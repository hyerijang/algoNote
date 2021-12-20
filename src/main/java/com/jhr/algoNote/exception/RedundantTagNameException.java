package com.jhr.algoNote.exception;

public class RedundantTagNameException extends RuntimeException {

    public RedundantTagNameException() {
        super();
    }

    public RedundantTagNameException(String message) {
        super(message);
    }

    public RedundantTagNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedundantTagNameException(Throwable cause) {
        super(cause);
    }
}
