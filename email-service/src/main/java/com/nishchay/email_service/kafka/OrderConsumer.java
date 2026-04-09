package com.nishchay.email_service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.nishchay.commonlib.dto.order.OrderEventDTO;
import com.nishchay.email_service.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderConsumer {
	
	private final EmailService emailService;
	
	private static final Logger LOGGER=LoggerFactory.getLogger(OrderConsumer.class);
	
	
	@KafkaListener(topics="${spring.kafka.order-topic.name}",groupId = "${spring.kafka.consumer.group-id}")
	public void consume(OrderEventDTO orderEvent) {
		try {
			LOGGER.info("Recieved Order in kafka from order servic {}",orderEvent.toString());
			emailService.sendOrderConfirmationEmail(orderEvent);
		}catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn("Failed to recieve order from order service from kafka {}",e.getMessage());
		}
		
	}

}
