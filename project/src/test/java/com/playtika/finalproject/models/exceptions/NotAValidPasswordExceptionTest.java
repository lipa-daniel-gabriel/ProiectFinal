package com.playtika.finalproject.models.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;

public class NotAValidPasswordExceptionTest {

    @Test
    public void test() {
        assertThrows(NotAValidPasswordException.class, () -> {throw new NotAValidPasswordException("test message");})
                .getMessage().equals("test message");
    }
}
