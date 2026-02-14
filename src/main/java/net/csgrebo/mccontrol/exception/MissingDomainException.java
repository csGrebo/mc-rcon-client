package net.csgrebo.mccontrol.exception;

public class MissingDomainException extends RuntimeException {

    public enum Reason {
        EMPTY_ARG("No Domain Passed"),
        MISSING_FILE("Domain File Does Not Exist"),
        NO_LIST_FOUND("Unable To Load Domain List"),
        UNREGISTERED_DOMAIN("Domain Not Registered"),
        INVALID_CONFIGURATION("Domain Configuration Invalid"),
        ;

        private final String message;

        Reason(String message) {
            this.message = message;
        }

        String getMessage() {
            return message;
        }
    }

    private final Reason reason;
    private final String domainName;

    public MissingDomainException(Reason reason) {
        super(reason.getMessage());
        this.reason = reason;
        this.domainName = null;
    }

    public MissingDomainException(Reason reason, String domainName) {
        super(reason.getMessage());
        this.reason = reason;
        this.domainName = domainName;
    }

    public Reason getReason() {
        return reason;
    }

    public String getDomainName() {
        return domainName;
    }
}
