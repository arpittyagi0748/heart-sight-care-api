package com.haripriya.haripriya_backend.exception;

public class ValidationException extends CustomException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
