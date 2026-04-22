package com.nishchay.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    private String redisHost;

    private int redisPort;

    @Bean
    public JedisConnectionFactory connectionFactory(){
        RedisStandaloneConfiguration configuration=new RedisStandaloneConfiguration();
        configuration.setHostName(redisHost);
        configuration.setPort(redisPort);
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String,Object> redisTemplate(){
        RedisTemplate<String,Object> template=new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setKeySerializer(new StringRedisSerializer());

        RedisSerializer<Object> jsonSerializer = RedisSerializer.json();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }


}
