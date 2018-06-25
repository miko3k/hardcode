package org.deletethis.hardcode;

public class ConfigMismatchException extends HardcodeException {
    public ConfigMismatchException() {
    }

    public ConfigMismatchException(String message) {
        super(message);
    }

    public ConfigMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigMismatchException(Throwable cause) {
        super(cause);
    }
}
