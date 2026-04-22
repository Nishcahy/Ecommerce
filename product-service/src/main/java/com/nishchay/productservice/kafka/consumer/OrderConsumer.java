package com.nishchay.productservice.kafka.consumer;


import com.nishchay.commonlib.dto.order.OrderDTO;
import com.nishchay.commonlib.dto.order.OrderEventDTO;
import com.nishchay.commonlib.dto.order.OrderItemDTO;
import com.nishchay.productservice.entity.ProductVariant;
import com.nishchay.productservice.exception.InsufficientStockException;
import com.nishchay.productservice.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderConsumer {

    private final Logger logger= LoggerFactory.getLogger(OrderConsumer.class);
    private final ProductVariantService productVariantService;

    @KafkaListener(topics = "${spring.kafka.create-order-topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void consume(OrderEventDTO orderEventDTO){
        logger.info("Recived Order event");
        OrderDTO orderDTO=orderEventDTO.getOrderDTO();
        Set<Long> variantIds=orderDTO.getOrderItems().stream().map(OrderItemDTO::getId).collect(Collectors.toSet());

        Map<Long, ProductVariant> variantMap=productVariantService.getProductVariantByIds(variantIds).stream()
                .collect(Collectors.toMap(ProductVariant::getId, variant->variant));
        for (OrderItemDTO orderItem : orderDTO.getOrderItems()) {
            updateStockForVariant(orderItem, variantMap);
        }

        logger.info("Successfully processed OrderEvent for orderId: {}", orderDTO.getOrderId());
    }

    private void updateStockForVariant(OrderItemDTO orderItemDTO,Map<Long,ProductVariant> variantMap){
        Long variantId=orderItemDTO.getId();
        ProductVariant productVariant=variantMap.get(variantId);

        if(productVariant==null){
            logger.error("Product variant not found for id: "+variantId);
            throw new RuntimeException("Product variant not found "+variantId);
        }

        int requiredQuantity=orderItemDTO.getQuantity();
        int currentStock=productVariant.getStockQuantity();
        int updatedStock=currentStock-requiredQuantity;

        if(updatedStock<0){
            logger.error("Insufficient stock for variant id: "+variantId);
            throw new InsufficientStockException("Insufficient stock for variant id: "+variantId);
        }
        productVariant.setStockQuantity(updatedStock);
        productVariantService.saveProductVariant(productVariant);
        logger.info("Updated stock for variant id: "+variantId+" current stock: "+currentStock+" required quantity: "+requiredQuantity+" updated stock: "+updatedStock);
    }





}
