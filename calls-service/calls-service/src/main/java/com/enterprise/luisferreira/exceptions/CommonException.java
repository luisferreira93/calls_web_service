package com.enterprise.luisferreira.exceptions;

public class CommonException extends Exception {

    private final CommonExceptionConstants reason;

    /**
     * Creates an instance of exception.
     *
     * @param reason  The reason that cause this exception.
     * @param message The detail message.
     */
    public CommonException(final CommonExceptionConstants reason, final String message) {

        super(message);

        this.reason = reason;
    }

    /**
     * Returns the reason that cause this exception.
     *
     * @return ExceptionConstants value.
     */
    public CommonExceptionConstants getReason() {
        return this.reason;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("CommonException [");
        if (this.reason != null) {
            builder.append("reason=");
            builder.append(this.reason);
        }
        builder.append("]");
        return builder.toString();
    }

}

