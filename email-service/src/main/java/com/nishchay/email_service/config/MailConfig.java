package com.nishchay.email_service.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
	
	@Value("${spring.mail.port}")
	private int SMTP_PORT;
	
	@Value("${spring.mail.host}")
	private String SMTP_HOST;
	
	@Value("${spring.mail.username}")
	private String SMTP_USERNAME;
	
	@Value("${spring.mail.password}")
	private String SMTP_PASSWORD;
	
	
	
	@Bean
	public JavaMailSender javaMailSender() {
		
		JavaMailSenderImpl javaMailSender=new JavaMailSenderImpl();
		
		javaMailSender.setHost(SMTP_HOST);
		javaMailSender.setPort(SMTP_PORT);
		
		javaMailSender.setUsername(SMTP_USERNAME);
		javaMailSender.setPassword(SMTP_PASSWORD);
		
		
		Properties properties=javaMailSender.getJavaMailProperties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.debug","true");
		
		
		return javaMailSender;
		
	}

}
