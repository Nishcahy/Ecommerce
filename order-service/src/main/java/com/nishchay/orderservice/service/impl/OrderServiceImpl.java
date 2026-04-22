package com.nishchay.orderservice.service.impl;

import com.nishchay.commonlib.dto.order.OrderDTO;
import com.nishchay.commonlib.dto.order.OrderEventDTO;
import com.nishchay.commonlib.dto.order.OrderItemDTO;
import com.nishchay.orderservice.dto.OrderRequestDto;
import com.nishchay.orderservice.dto.OrderResponseDto;
import com.nishchay.orderservice.dto.OrderResponseDtoWithOutOrderItem;
import com.nishchay.orderservice.dto.product.ProductResponseDto;
import com.nishchay.orderservice.dto.product_variant.ProductVariantResponseDto;
import com.nishchay.orderservice.entity.Order;
import com.nishchay.orderservice.entity.OrderItem;
import com.nishchay.orderservice.entity.OrderStatus;
import com.nishchay.orderservice.exception.OrderException;
import com.nishchay.orderservice.kafka.OrderProducer;
import com.nishchay.orderservice.redis.OrderRedis;
import com.nishchay.orderservice.repository.OrderRepository;
import com.nishchay.orderservice.service.OrderService;
import com.nishchay.orderservice.service.ProductAPIClient;
import com.nishchay.orderservice.service.state.OrderContext;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER= LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    private final ModelMapper modelMapper;
    private final ProductAPIClient productAPIClient;

    private final OrderRedis orderRedis;


    @Override
    public OrderDTO placeOrder(OrderRequestDto orderRequestDto, Long userId, String email) {
        try {
            OrderDTO newOrder = createOrderDTO(orderRequestDto, userId);
            validateStockAndPrice(orderRequestDto, newOrder);

            Order createdOrder = saveOrder(newOrder, orderRequestDto);
            orderRedis.save(modelMapper.map(createdOrder, OrderDTO.class));


            sendOrderEvent(createdOrder, orderRequestDto.getPaymentMethod(), email);

            return modelMapper.map(createdOrder, OrderDTO.class);
        } catch (OrderException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to create order: {}", e.getMessage(), e);
            throw new OrderException("Failed to create order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public OrderResponseDto checkOrderStatusByOrderId(String orderId) {
        OrderDTO cachedOrder = orderRedis.findByOrderId(orderId);
        OrderDTO order;

        if (cachedOrder != null) {
            order = cachedOrder;
            LOGGER.info("Order retrieved from Redis cache.");
        } else {

            order = modelMapper.map(orderRepository.findById(orderId).orElse(null), OrderDTO.class);
            if (order != null) {
                orderRedis.save(order);
                LOGGER.info("Order retrieved from DB and saved to Redis cache.");
            }
        }

        if (order != null) {
            OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
            OrderResponseDto orderResponseDto = new OrderResponseDto();
            orderResponseDto.setOrderDTO(orderDTO);
            return orderResponseDto;
        }
        return null;
    }

    @Override
    public OrderResponseDto updateOrderStatus(String orderId, int version) {

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            if (order.getVersion() != version) {
                throw new OptimisticLockException("Version conflict!");
            }

            OrderContext context = new OrderContext(order);
            context.handleStateChange(order);

            Order savedOrder = orderRepository.save(order);
            OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);

            orderRedis.save(orderDTO);

            OrderResponseDto orderResponseDto = new OrderResponseDto();
            orderResponseDto.setOrderDTO(orderDTO);

            return orderResponseDto;
        }
        return null;
    }

    @Override
    public OrderDTO cancelOrder(String orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(OrderStatus.CANCELLED.getLabel());

        }
        OrderDTO savedOrderDTO = modelMapper.map(orderRepository.save(order), OrderDTO.class);
        orderRedis.save(savedOrderDTO);
        return savedOrderDTO;
    }

    @Override
    public List<OrderResponseDtoWithOutOrderItem> getAllOrders(Long userId, int page, int size) {
        return List.of();
    }



    private void sendOrderEvent(Order createdOrder, String paymentMethod, String email) {
        OrderEventDTO orderEvent = createOrderEvent(createdOrder, paymentMethod, email);
        orderProducer.sendMessage(orderEvent);
    }

    private OrderEventDTO createOrderEvent(Order createdOrder, String paymentMethod, String email) {
        OrderDTO createdOrderDto = modelMapper.map(createdOrder, OrderDTO.class);
        OrderEventDTO orderEvent = new OrderEventDTO();
        orderEvent.setOrderDTO(createdOrderDto);
        orderEvent.setStatus("PENDING");
        orderEvent.setMessage("Order status is in pending state");
//        orderEvent.setPaymentMethod(paymentMethod);
        orderEvent.setEmail(email);
        return orderEvent;
    }

    private void validateStockAndPrice(OrderRequestDto orderRequestDTO, OrderDTO newOrder) {
        Set<String> productIds = extractProductIds(orderRequestDTO.getOrderItems());
        List<ProductResponseDto> products = fetchProducts(productIds);

        if (products.size() != productIds.size()) {
            throw new OrderException("Variants not found for ids: " + productIds, HttpStatus.BAD_REQUEST);
        }

        Map<String, ProductResponseDto> productsMap = products.stream()
                .collect(Collectors.toMap(ProductResponseDto::getId, Function.identity()));

        for (OrderItemDTO orderItem : orderRequestDTO.getOrderItems()) {
            ProductResponseDto product = productsMap.get(orderItem.getProductId());
            if (product == null) {
                throw new OrderException("Not found product!", HttpStatus.NOT_FOUND);
            }

            //* UPDATE PRICE AND VALIDATE STOCK
            updatePriceAndValidateStock(orderItem, product);
        }
    }

    private Order saveOrder(OrderDTO newOrder, OrderRequestDto orderRequestDto) {
        Order order = modelMapper.map(newOrder, Order.class);

        BigDecimal amount = BigDecimal.valueOf(0);

        for (OrderItemDTO orderItemDTO : orderRequestDto.getOrderItems()) {
            BigDecimal itemTotal = orderItemDTO.getPrice().multiply(BigDecimal.valueOf(orderItemDTO.getQuantity()));
            amount = amount.add(itemTotal);
        }
        if(orderRequestDto.getOrderId()!=null){
            order.setOrderId(orderRequestDto.getOrderId());
        }else{
            order.setOrderId(UUID.randomUUID().toString());
        }


        order.setStatus(OrderStatus.PENDING.getLabel());
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.setOrder(order);
        }
        return orderRepository.save(order);
    }
    private ProductVariantResponseDto findMatchingVariant(List<ProductVariantResponseDto> variants, Long variantId) {
        if (variants == null) return null;
        return variants.stream()
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElse(null);
    }

    private OrderDTO createOrderDTO(OrderRequestDto orderRequestDto, Long userId) {
        OrderDTO newOrder = modelMapper.map(orderRequestDto, OrderDTO.class);
        newOrder.setUserId(userId);
        return newOrder;
    }

    private Set<String> extractProductIds(List<OrderItemDTO> orderItems) {
        return orderItems.stream()
                .map(OrderItemDTO::getProductId)
                .collect(Collectors.toSet());
    }

    private List<ProductResponseDto> fetchProducts(Set<String> productIds) {
        return productAPIClient.getProductsByIds(productIds).getBody().getData();
    }

    private void updatePriceAndValidateStock(OrderItemDTO orderItem, ProductResponseDto product) {
        ProductVariantResponseDto selectedVariant = findMatchingVariant(product.getVariants(), orderItem.getId());
        if (selectedVariant != null) {
            if (selectedVariant.getStockQuantity() < orderItem.getQuantity()) {
                throw new OrderException("Insufficient stock for variantId: " + selectedVariant.getId(), HttpStatus.BAD_REQUEST);
            }
            if(selectedVariant.getPrice() != null){
                orderItem.setPrice(selectedVariant.getPrice());
                return;
            }
            orderItem.setPrice(product.getPrice());
        }
    }
}


