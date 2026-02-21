package com.trustAnchor.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IngestionService {
    UUID ingestFile(MultipartFile file);

    String calculateCheckSum(MultipartFile file);
}
