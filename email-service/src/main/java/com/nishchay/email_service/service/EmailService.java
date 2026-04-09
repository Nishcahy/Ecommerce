package com.nishchay.email_service.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.nishchay.commonlib.dto.order.OrderDTO;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	
	
	private final JavaMailSender mailSender;
	
	private final ProductApiClient apiClient;
	
	private static final Logger logger= LoggerFactory.getLogger(EmailService.class);
	
	public void sendOrderConfirmationEmail(OrderDTO orderDTO) throws MessagingException, IOException {
		try {
			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("");
		}
		
		
	}

}
