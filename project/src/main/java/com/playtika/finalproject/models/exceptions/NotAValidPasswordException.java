package com.playtika.finalproject.models.exceptions;

public class NotAValidPasswordException extends RuntimeException {
    public NotAValidPasswordException(String password_is_not_valid) {
        super(password_is_not_valid);
    }
}
