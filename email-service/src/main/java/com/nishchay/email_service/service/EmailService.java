package com.nishchay.email_service.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bouncycastle.util.encoders.UTF8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.nishchay.commonlib.dto.ApiResponce;
import com.nishchay.commonlib.dto.order.OrderDTO;
import com.nishchay.commonlib.dto.order.OrderEventDTO;
import com.nishchay.commonlib.dto.order.OrderItemDTO;
import com.nishchay.email_service.dto.ProductStockResponseDto;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	
	
	private final JavaMailSender mailSender;
	
	private final ProductApiClient apiClient;
	
	private static final Logger logger= LoggerFactory.getLogger(EmailService.class);
	
	public void sendOrderConfirmationEmail(OrderEventDTO orderDTO) throws MessagingException, IOException {
		try {
			
			String template=loadTemplate("templates/email-template.html");
			List<OrderItemDTO> orderItems=orderDTO.getOrderDTO().getOrderItems();
			StringBuilder orderItemBuilder=new StringBuilder();
			BigDecimal amount=BigDecimal.valueOf(0);
			
			NumberFormat currencyFormate=NumberFormat.getCurrencyInstance(Locale.US);
			
			for(OrderItemDTO item:orderItems) {
				ResponseEntity<ApiResponce<ProductStockResponseDto>> productResponseEntity=apiClient.getProductById(item.getProductId());
				if (!productResponseEntity.getStatusCode().is2xxSuccessful()) {
		            logger.warn("Product API returned an error status: {} for product ID: {}", 
		                        productResponseEntity.getStatusCode(), item.getProductId());
		            continue; // Skip to the next item
		        }
				ApiResponce<ProductStockResponseDto> productResponse=productResponseEntity.getBody();
				
				if(productResponse== null) {
					logger.warn("products not found with id{}",item.getProductId());
					return;
					
				}
				BigDecimal totalPrice = productResponse.getData().getProductResponseDto().getPrice().multiply(amount);

                amount = amount.add(totalPrice);
                String formattedUnitPrice = currencyFormate.format(productResponse.getData().getProductResponseDto().getPrice());
                String formattedTotalPrice = currencyFormate.format(totalPrice);
                
                orderItemBuilder.append("<tr>")
                .append("<td><img width='100' height='100' src='").append(productResponse.getData().getProductResponseDto().getImageUrl()).append("' alt='Product Image'/></td>")
                .append("<td>").append(productResponse.getData().getProductResponseDto().getName()).append("</td>")
                .append("<td>").append(item.getQuantity()).append("</td>")
                .append("<td>").append(formattedUnitPrice).append("</td>")
                .append("<td>").append(formattedTotalPrice).append("</td>")
                .append("</tr>");
                
                
                
				
			}
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Error fetching product with ID {}: {}", item.getProductId(), e.getMessage());
            // Optionally, you can continue or rethrow the exception based on requirements
            continue;

		}
		
		String orderItemsHtml = orderItemBuilder.toString();
        String formattedGrandTotal = currencyFormate.format(amount);

        // Prepare variables for template replacement
        Map<String, String> variables = Map.of(
                "customerName", order.getEmail(),
                "orderId", order.getOrderDTO().getOrderId(),
                "orderDate", order.getOrderDTO().getCreatedAt().toString(),
                "orderItems", orderItemsHtml,
                "grandTotal", formattedGrandTotal,
                "actionUrl", "https://yourapp.com/orders/" + order.getOrderDTO().getOrderId()
        );

	
		
	}
	
	private String loadTemplate(String path) throws IOException{
		ClassPathResource resource=new ClassPathResource(path);
		
		StringBuilder sb= new StringBuilder();
		
		try(BufferedReader reader=new BufferedReader((new InputStreamReader(resource.getInputStream(),StandardCharsets.UTF_8)))) {
			String line;
			while((line=reader.readLine())!=null) {
				sb.append(line).append("\n");
			}
		}
		return sb.toString();

	}
	
	private String replacePlaceholders(String template, Map<String, String> variables){
        String result = template;
        for(Map.Entry<String, String> entry : variables.entrySet()){
            String placeholder = "{" + entry.getKey() + "}";
            result = result.replace(placeholder, entry.getValue());
        }
        return result;
    }

}
