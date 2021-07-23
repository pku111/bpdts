package com.example.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Could not find any user who lives in or within 50 miles of london");

    }

    public UserNotFoundException(Exception e) {
        super("Could not find any user who lives in or within 50 miles od london because of : " + e.getMessage(), e);
    }
}
