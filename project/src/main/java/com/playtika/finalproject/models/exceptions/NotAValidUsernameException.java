package com.playtika.finalproject.models.exceptions;

public class NotAValidUsernameException extends RuntimeException {
    public NotAValidUsernameException(String username_is_not_valid) {
        super(username_is_not_valid);
    }
}
