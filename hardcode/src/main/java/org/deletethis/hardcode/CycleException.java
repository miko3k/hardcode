package org.deletethis.hardcode;

public class CycleException extends HardcodeException {
    public CycleException() {
    }

    public CycleException(String message) {
        super(message);
    }

    public CycleException(String message, Throwable cause) {
        super(message, cause);
    }

    public CycleException(Throwable cause) {
        super(cause);
    }
}
