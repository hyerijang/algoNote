package com.jhr.algoNote.exception;

public class EmailRedundancyException extends RuntimeException {

    public EmailRedundancyException() {
    }

    public EmailRedundancyException(String message) {
        super(message);
    }

    public EmailRedundancyException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailRedundancyException(Throwable cause) {
        super(cause);
    }

    public EmailRedundancyException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
