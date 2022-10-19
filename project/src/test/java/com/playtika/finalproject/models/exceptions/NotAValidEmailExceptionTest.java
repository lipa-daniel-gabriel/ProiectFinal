package com.playtika.finalproject.models.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;

public class NotAValidEmailExceptionTest {
    @Test
    public void test() {
        assertThrows(NotAValidEmailException.class, () -> {throw new NotAValidEmailException("test message");})
                .getMessage().equals("test message");
    }
}
