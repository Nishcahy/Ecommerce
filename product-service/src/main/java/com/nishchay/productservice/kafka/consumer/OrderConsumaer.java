package com.nishchay.productservice.kafka.consumer;


import com.nishchay.productservice.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderConsumaer {

    private final Logger logger= LoggerFactory.getLogger(OrderConsumaer.class);
    private final ProductVariantService productVariantService;


}
