package com.nishchay.productservice.redis;

import com.nishchay.productservice.dto.product.ProductCacheDto;
import com.nishchay.productservice.entity.Product;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRedis {
    private static String HASH_KEY="Products";

    private RedisTemplate<String, ProductCacheDto> redisTemplate;
    private ModelMapper modelMapper;

    public ProductRedis(RedisTemplate redisTemplate,ModelMapper modelMapper){
        this.redisTemplate=redisTemplate;
        this.modelMapper=modelMapper;
    }

    public void save(Product product){
        try{
            if(product.getImageUrl()!=null){
                ProductCacheDto productCacheDto=modelMapper.map(product,ProductCacheDto.class);
                redisTemplate.opsForHash().put(HASH_KEY,product.getId(),productCacheDto);
                redisTemplate.expire(HASH_KEY, java.time.Duration.ofHours(1));
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to save the product in REdis " + e.getMessage());
        }
    }

    public ProductCacheDto findByProductId(String id){
        try{
            return (ProductCacheDto) redisTemplate.opsForHash().get(HASH_KEY,id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch the product from Redis " + e.getMessage());
        }
    }

    public void deleteByProductId(String id){
        try{
            redisTemplate.opsForHash().delete(HASH_KEY,id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete the product from Redis " + e.getMessage());
        }
    }
}
