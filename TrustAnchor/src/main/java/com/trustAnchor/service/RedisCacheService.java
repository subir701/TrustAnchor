package com.trustAnchor.service;

public interface RedisCacheService {
    String checkL1Cache(String query);
    void createPair(String query, String response);
    String checkL2Cache(String query);
    void saveToL2Cache(String query, String response);
}
