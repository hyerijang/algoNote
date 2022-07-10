package com.jhr.algoNote.exception;

/**
 * DB에 중복되는 태그 이름을 저장하려 했을 때 발생하는 예외
 */
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
