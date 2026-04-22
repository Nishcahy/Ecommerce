package com.nishchay.orderservice.kafka;

import com.nishchay.commonlib.dto.order.OrderEventDTO;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component

public class OrderProducer {
    private static final Logger LOGGER= LoggerFactory.getLogger(OrderProducer.class);

    private final NewTopic newTopic;

    private final KafkaTemplate<String, OrderEventDTO> kafkaTemplate;

    public OrderProducer(NewTopic newTopic, KafkaTemplate<String, OrderEventDTO> kafkaTemplate) {
        this.newTopic = newTopic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(OrderEventDTO orderEventDTO){
        LOGGER.info(String.format("Order event sent -> %s",orderEventDTO.toString()));

        Message<OrderEventDTO> msg= MessageBuilder.withPayload(orderEventDTO)
                .setHeader(KafkaHeaders.TOPIC,newTopic.name())
                .build();
    }
}
