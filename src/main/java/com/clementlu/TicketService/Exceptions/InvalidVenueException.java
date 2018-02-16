package com.clementlu.ticketservice.Exceptions;

/**
 * @author      Clement Lu <clementlu@live.com>
 * @version     1.0
 * @since       1.0
 */
public class InvalidVenueException extends Exception {

    public InvalidVenueException() {
    }

    public InvalidVenueException(String message) {
        super(message);
    }

    public InvalidVenueException(Throwable cause) {
        super(cause);
    }

    public InvalidVenueException(String message, Throwable cause) {
        super(message, cause);
    }
}