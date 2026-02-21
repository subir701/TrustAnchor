package com.trustAnchor.exception;

public class IngestionFailedException extends TrustAnchorException{
    public IngestionFailedException(String message, Throwable cause) {
        super(message);
    }
}
