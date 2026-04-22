package com.nishchay.orderservice.redis;


import com.nishchay.commonlib.dto.order.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class OrderRedis {

    private static final String HASH_KEY="Order";
    private static final Logger LOGGER= org.slf4j.LoggerFactory.getLogger(OrderRedis.class);
    private final RedisTemplate redisTemplate;

    public void save(OrderDTO order){
        try{
            redisTemplate.opsForHash().put(HASH_KEY,order.getOrderId(),order);
            redisTemplate.expire(HASH_KEY, Duration.ofHours(1));

        }catch (Exception e){
            throw new RuntimeException(" Failoed to save the order in redis, with id {} "+ order.getOrderId());

        }
    }

    public OrderDTO findByOrderId(String id){
        try {
            return (OrderDTO) redisTemplate.opsForHash().get(HASH_KEY, id);
        }catch (Exception e){
            LOGGER.warn(" Failed to find the order in redis, with id {} ", id);
            return null;
        }
    }

    public void deleteByOrderId(String id){
        try{
            redisTemplate.opsForHash().delete(HASH_KEY,id);
        }catch (Exception e){
            LOGGER.warn(" Failed to delete the order in redis, with id {} ", id);
        }
    }
}
