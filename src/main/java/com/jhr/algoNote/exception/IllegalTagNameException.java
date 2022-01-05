package com.jhr.algoNote.exception;

public class IllegalTagNameException extends RuntimeException {

    public IllegalTagNameException() {
    }

    public IllegalTagNameException(String message) {
        super(message);
    }

    public IllegalTagNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalTagNameException(Throwable cause) {
        super(cause);
    }

    public IllegalTagNameException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
