package com.haripriya.haripriya_backend.exception;

public class AuthenticationException extends CustomException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
