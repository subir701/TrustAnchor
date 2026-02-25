package com.trustAnchor.service;


import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheServiceImpl implements RedisCacheService{

    private final RedisTemplate<String,String > redisTemplate;
    private static final String CACHE_PREFIX = "trustanchor:l1:";
    private final EmbeddingStore<TextSegment> redisEmbeddingStore; // Injected Bean
    private final EmbeddingModel embeddingModel;

    @Override
    public String checkL1Cache(String query) {
        String key = generateKey(query);
        String cachedResponse = redisTemplate.opsForValue().get(key);

        if(cachedResponse != null){
            log.info("L1 Cache Hit for query hash: {}", key);
        }
        return cachedResponse;
    }

    @Override
    public void createPair(String query, String response) {
        String key = generateKey(query);
        //Storing with a 24-hour TTL to save RAM
        redisTemplate.opsForValue().set(key,response, 24, TimeUnit.HOURS);
        log.debug("L1 Cache Entry Created: {}", key);
    }

    @Override
    public String checkL2Cache(String query) {
        // 1. Turn query into vector
        Embedding queryEmbedding = embeddingModel.embed(query).content();

        // 2. Search Redis with a similarity threshold (0.95 = 95%)
        List<EmbeddingMatch<TextSegment>> matches = redisEmbeddingStore.findRelevant(queryEmbedding, 1, 0.95);

        if (!matches.isEmpty()) {
            return matches.get(0).embedded().text();
        }
        return null;
    }

    private String generateKey(String input){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Converting bytes to Hex String
            StringBuilder hexString = new StringBuilder();
            for(byte b : hashBytes){
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1)hexString.append('0');
                hexString.append(hex);
            }
            return CACHE_PREFIX + hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not found", e);
            throw new RuntimeException("Internal Security Error");
        }
    }
}
