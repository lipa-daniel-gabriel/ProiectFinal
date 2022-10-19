package com.playtika.finalproject.models.exceptions;

public class NotAValidEmailException extends RuntimeException {
    public NotAValidEmailException(String email_is_not_valid) {
        super(email_is_not_valid);
    }
}
