package net.csgrebo.mccontrol.exception;

public class ImproperCommandException extends RuntimeException {

    public ImproperCommandException() {
    }

    public ImproperCommandException(String message) {
        super(message);
    }

    public ImproperCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImproperCommandException(Throwable cause) {
        super(cause);
    }

    protected ImproperCommandException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
