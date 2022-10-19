package com.playtika.finalproject.models.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;

public class NotAValidUsernameExceptionTest {
    @Test
    public void test() {
        assertThrows(NotAValidUsernameException.class, () -> {throw new NotAValidUsernameException("test message");})
                .getMessage().equals("test message");
    }
}
