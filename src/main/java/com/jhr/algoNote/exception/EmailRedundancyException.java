package com.jhr.algoNote.exception;

/**
 * 이메일 중복시 발생하는 예외
 */
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
