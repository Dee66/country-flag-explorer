package com.flags.exceptions;

public class CountryAlreadyExistsException extends RuntimeException {
    public CountryAlreadyExistsException(String name) {
        super("Country already exists: " + name);
    }
}