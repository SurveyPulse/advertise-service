package com.example.advertise_service.config;

import com.example.advertise_service.dto.response.AdvertisementSummaryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@Configuration
public class RedisConfig {

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(
            @Value("${spring.redis.host}") String host,
            @Value("${spring.redis.port}") int port) {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                                                                            .commandTimeout(Duration.ofSeconds(3))
                                                                            .shutdownTimeout(Duration.ZERO)
                                                                            .build();
        return new LettuceConnectionFactory(
                new org.springframework.data.redis.connection.RedisStandaloneConfiguration(host, port),
                clientConfig
        );
    }

    @Bean
    public ReactiveRedisOperations<String, List<AdvertisementSummaryResponse>> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory) {

        // Jackson JSON 직렬화기
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        StringRedisSerializer keySerializer = new StringRedisSerializer();

        // Object용 SerializationPair을 List<…>용으로 캐스팅
        @SuppressWarnings("unchecked")
        RedisSerializationContext.SerializationPair<List<AdvertisementSummaryResponse>> valuePair =
                (RedisSerializationContext.SerializationPair<List<AdvertisementSummaryResponse>>)
                        (RedisSerializationContext.SerializationPair<?>)
                                RedisSerializationContext.SerializationPair.fromSerializer(serializer);

        // 컨텍스트 빌드
        RedisSerializationContext<String, List<AdvertisementSummaryResponse>> context =
                RedisSerializationContext.<String, List<AdvertisementSummaryResponse>>newSerializationContext(keySerializer)
                                         .value(valuePair)
                                         .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
