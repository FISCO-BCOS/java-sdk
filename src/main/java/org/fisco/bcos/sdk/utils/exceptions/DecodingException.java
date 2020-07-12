package org.fisco.bcos.sdk.utils.exceptions;

public class DecodingException extends RuntimeException {
    public DecodingException(String message) {
        super(message);
    }

    public DecodingException(String message, Throwable cause) {
        super(message, cause);
    }

}
