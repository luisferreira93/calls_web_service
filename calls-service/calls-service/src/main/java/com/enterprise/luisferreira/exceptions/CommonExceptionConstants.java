package com.enterprise.luisferreira.exceptions;

import javax.ws.rs.core.Response.Status;

public enum CommonExceptionConstants {

    /**
     * Thrown to indicate that a method has been passed an illegal or inappropriate argument.
     */
    ILLEGAL_ARGUMENT_EXCEPTION(Status.BAD_REQUEST),

    /**
     * A paginated request has hit an out of bounds index.
     */
    PAGINATED_REQUEST_OUT_OF_BOUNDS_INDEX(Status.BAD_REQUEST),

    /**
     * Thrown if a content does not exist.
     */
    CALL_NOT_FOUND(Status.NO_CONTENT),

    /**
     * Thrown if a date inserted is in the wrong format.
     */
    WRONG_FORMAT(Status.BAD_REQUEST);


    private final Status statusCode;

    CommonExceptionConstants(final Status statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Return {@link CommonExceptionConstants} respective {@link Status} code.
     *
     * @return {@link Status} The {@link CommonExceptionConstants} respective {@link Status} code.
     */
    public Status getStatusCode() {
        return this.statusCode;
    }


}
