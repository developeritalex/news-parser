package org.example.consumer.exception;

public class NonretryableException extends RuntimeException {
    public NonretryableException(String message) {
        super(message);
    }

    public NonretryableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonretryableException(Throwable cause) {
        super(cause);
    }
}
