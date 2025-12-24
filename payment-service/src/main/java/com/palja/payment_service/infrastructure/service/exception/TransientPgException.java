package com.palja.payment_service.infrastructure.service.exception;

public class TransientPgException extends RuntimeException {
    public TransientPgException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransientPgException(String message) {
        super(message);
    }
}
