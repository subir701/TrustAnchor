package com.trustAnchor.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String , String > template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Using String serializers so we can read the keys/values in Redis Desktop Manager/CLI
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }

    @Bean
    @Primary
    public EmbeddingStore<TextSegment> customRedisEmbeddingStore() {
        return RedisEmbeddingStore.builder()
                .host("127.0.0.1")
                .port(6379)
                .dimension(768)
                .indexName("trustanchor-l2-index")
                .build();
    }
}
