package com.trustAnchor.service;

import com.trustAnchor.model.DocumentMetadata;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentProcessService {
    void processDocument(MultipartFile file, DocumentMetadata metadata);
}
