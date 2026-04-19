package com.nishchay.orderservice.service.impl;

import com.nishchay.commonlib.dto.order.OrderDTO;
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
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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

        return null;
    }

    @Override
    public OrderResponseDto checkOrderStatusByOrderId(String orderId) {
        return null;
    }

    @Override
    public OrderResponseDto updateOrderStatus(String orderId, int version) {
        return null;
    }

    @Override
    public OrderDTO cancelOrder(String orderId, Long userId) {
        return null;
    }

    @Override
    public List<OrderResponseDtoWithOutOrderItem> getAllOrders(Long userId, int page, int size) {
        return List.of();
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

    private ProductVariantResponseDto findMatchingVariant(List<ProductVariantResponseDto> variants, Long variantId) {
        if (variants == null) return null;
        return variants.stream()
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElse(null);
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

}
