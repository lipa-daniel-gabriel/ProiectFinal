package com.playtika.finalproject.models.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;

public class NotAValidPlatformNumberTest {
    @Test
    public void test() {
        assertThrows(NotAValidPlatformNumber.class, () -> {throw new NotAValidPlatformNumber("test message");})
                .getMessage().equals("test message");
    }
}
