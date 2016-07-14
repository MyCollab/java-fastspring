package com.fastspring;

/**
 * @author Hai Phuc Nguyen
 * @since 1.0.0
 */
public class OperationException extends RuntimeException {
    private int errorCode;

    public OperationException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return String.format("OperationException{errorCode=%d, message=%s}", errorCode, getMessage());
    }
}
