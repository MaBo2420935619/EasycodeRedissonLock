package com.mabo.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;

@Configuration
public class RedissonConfig {

    public RedissonConfig() {
    }
   /**
    * @Author mabo
    * @Description   获取redis连接对象
    */
   @Value("${spring.redis.host}")
    private String redisIp;

    @Value("${spring.redis.port}")
    private String redisPort;
    @Bean()
    public RedissonClient redisson() throws IOException {
//         1.创建配置
        Config config = new Config();
//         2.根据 Config 创建出 RedissonClient 示例。
        config.useSingleServer().setAddress("redis://"+redisIp+":"+redisPort);
        return Redisson.create(config);
    }

}
