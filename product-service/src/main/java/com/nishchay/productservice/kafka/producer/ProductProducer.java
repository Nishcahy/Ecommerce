package com.nishchay.productservice.kafka.producer;

import com.nishchay.commonlib.dto.product.ProductEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductProducer {

    private static final Logger logger = LoggerFactory.getLogger(ProductProducer.class);
    private final NewTopic newTopic;
    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public void sendMessage(ProductEvent productEvent){
        logger.info(String.format("Message sending... -> %s", productEvent.toString()));

        Message<ProductEvent> msg= MessageBuilder.withPayload(productEvent)
                .setHeader(KafkaHeaders.TOPIC, newTopic.name())
                .build();
        kafkaTemplate.send(msg);
    }

    public void sendDeleteProductMessage(ProductEvent productEvent){
        logger.info("sending to delete product with id: "+productEvent.toString());

        Message<ProductEvent> msg=MessageBuilder.withPayload(productEvent)
                .setHeader(KafkaHeaders.TOPIC, newTopic.name())
                .build();
        kafkaTemplate.send(msg);
    }
}
