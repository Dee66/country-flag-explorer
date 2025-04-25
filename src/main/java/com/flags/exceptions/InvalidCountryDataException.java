package com.flags.exceptions;

public class InvalidCountryDataException extends RuntimeException {
    public InvalidCountryDataException(String message) {
        super(message);
    }
}