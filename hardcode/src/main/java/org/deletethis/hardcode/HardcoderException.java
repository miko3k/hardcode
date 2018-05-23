package org.deletethis.hardcode;

public class HardcoderException extends RuntimeException {
    public HardcoderException() {
    }

    public HardcoderException(String message) {
        super(message);
    }

    public HardcoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public HardcoderException(Throwable cause) {
        super(cause);
    }

    public HardcoderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
