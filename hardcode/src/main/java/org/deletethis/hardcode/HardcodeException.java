package org.deletethis.hardcode;

public class HardcodeException extends RuntimeException {
    public HardcodeException() {
    }

    public HardcodeException(String message) {
        super(message);
    }

    public HardcodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HardcodeException(Throwable cause) {
        super(cause);
    }
}
