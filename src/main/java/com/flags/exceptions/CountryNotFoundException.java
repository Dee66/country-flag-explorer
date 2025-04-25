package com.flags.exceptions;

public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(String name) {
        super("Country not found: " + name);
    }
}