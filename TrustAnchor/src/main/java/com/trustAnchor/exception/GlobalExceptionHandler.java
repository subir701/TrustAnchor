package com.trustAnchor.exception;

import com.trustAnchor.util.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StorageQuotaExceededException.class)
    public ResponseEntity<ErrorDetails> handleQuota(StorageQuotaExceededException exception){
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new ErrorDetails("QUOTA_EXCEEDED", exception.getMessage()));
    }

    @ExceptionHandler(IngestionFailedException.class)
    public ResponseEntity<ErrorDetails> handleIngestion(IngestionFailedException exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDetails("PROCESSING_ERROR", exception.getMessage()));
    }
}
