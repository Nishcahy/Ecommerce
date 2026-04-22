package com.nishchay.orderservice.controller;



import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nishchay.commonlib.dto.ApiResponce;
import com.nishchay.commonlib.dto.order.OrderDTO;
import com.nishchay.orderservice.dto.OrderRequestDto;
import com.nishchay.orderservice.dto.OrderResponseDto;
import com.nishchay.orderservice.dto.UserDto;
import com.nishchay.orderservice.exception.OrderException;
import com.nishchay.orderservice.service.AuthenticationAPIClient;
import com.nishchay.orderservice.service.OrderService;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
	
	private final OrderService orderService;
	
	private final AuthenticationAPIClient authenticationAPIClient;
	
	@PostMapping
	public ResponseEntity<ApiResponce<?>> placeOrder(@RequestBody OrderRequestDto order,HttpServletRequest request){
		try {
			String cookie=request.getHeader(HttpHeaders.COOKIE);
			
			ApiResponce<UserDto> user= authenticationAPIClient.getCurrentUser(cookie).getBody();
			if(user!=null && user.getData()!=null) {
				return new ResponseEntity<>(new ApiResponce<>(orderService.placeOrder(order, user.getData().getId(), user.getData().getEmail()),HttpStatus.CREATED.value()),HttpStatus.CREATED);
			}
			 return new ResponseEntity<>(new ApiResponce<>("User not found!", HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
		}catch(OrderException e) {
			return new  ResponseEntity<>(new ApiResponce<>(e.getMessage(), e.getStatus().value()), e.getStatus());
			
		}catch(Exception e) {
			return new ResponseEntity<>(new ApiResponce<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PostMapping("/cancel/{orderId}")
    public ResponseEntity<ApiResponce<?>> cancelOrder(@PathVariable("orderId") String orderId, HttpServletRequest request) {
        try {
            
            String cookie = request.getHeader(HttpHeaders.COOKIE);

            ApiResponce<UserDto> user = authenticationAPIClient.getCurrentUser(cookie).getBody();

            if (user != null && user.getData() != null) {
                OrderDTO existingOrder = orderService.cancelOrder(orderId, user.getData().getId());

                if (existingOrder != null) {
                    return new ResponseEntity<>(new ApiResponce<>(existingOrder, HttpStatus.OK.value()), HttpStatus.OK);
                }
                return new ResponseEntity<>(new ApiResponce<>("Order not found!", HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
            }
           
            return new ResponseEntity<>(new ApiResponce<>("Unauthorized request!", HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            
            return new ResponseEntity<>(new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	 @GetMapping("/{orderId}")
	    public ResponseEntity<ApiResponce<?>> getOrderStatus(@PathVariable("orderId") String orderId) {
	        try {
	            
	            OrderResponseDto existingOrder = orderService.checkOrderStatusByOrderId(orderId);

	            if (existingOrder != null) {
	                return new ResponseEntity<>(new ApiResponce<>(existingOrder, HttpStatus.OK.value()), HttpStatus.OK);
	            }
	            
	            return new ResponseEntity<>(new ApiResponce<>("Order not found!", HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
	        } catch (Exception e) {
	            
	            return new ResponseEntity<>(new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	 
	 @GetMapping
     public ResponseEntity<ApiResponce<?>> getOrders(HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
         try {
            
             String cookie = request.getHeader(HttpHeaders.COOKIE);

    
             ApiResponce<UserDto> user = authenticationAPIClient.getCurrentUser(cookie).getBody();
             if(user!=null && user.getData()!=null) {
            	 return new ResponseEntity<>(new ApiResponce<>(orderService.getAllOrders(user.getData().getId(), page, size), HttpStatus.OK.value()), HttpStatus.OK);
             }

             return new ResponseEntity<>(new ApiResponce<>("User not Authenticated.... Please Login", HttpStatus.OK.value()), HttpStatus.OK);
         } catch (Exception e) {
             return new ResponseEntity<>(new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
         }
     }
	 
	 @PatchMapping("/update/status/{orderId}")
	    public ResponseEntity<ApiResponce<?>> updateOrderStatus(@PathVariable("orderId") String orderId, @RequestHeader(HttpHeaders.IF_MATCH) int version) {
	        try {

	            OrderResponseDto orderDTO = orderService.updateOrderStatus(orderId, version);
	            return orderDTO != null ? new ResponseEntity<>(new ApiResponce<>(orderDTO, HttpStatus.OK.value()), HttpStatus.OK)
	                    : new ResponseEntity<>(new ApiResponce<>("Not found order!", HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
	        } catch (IllegalStateException | OptimisticLockException e) {
	        
	            return new ResponseEntity<>(new ApiResponce<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
	        } catch (Exception e) {
	           
	            return new ResponseEntity<>(new ApiResponce<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	 }
	 
	 
	 

}
