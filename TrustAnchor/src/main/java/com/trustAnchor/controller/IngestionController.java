package com.trustAnchor.controller;

import com.trustAnchor.service.IngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/trustanchor/documents")
@RequiredArgsConstructor
public class IngestionController {
    private final IngestionService ingestionService;

    /**
     * Uploads a document for RAG processing.
     * Returns 202 ACCEPTED because the embedding process happens asynchronously.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> uploadFile(@RequestParam("file") MultipartFile file) {
        UUID documentId = ingestionService.ingestFile(file);

        return ResponseEntity
                .accepted()
                .body(documentId);
    }
}
