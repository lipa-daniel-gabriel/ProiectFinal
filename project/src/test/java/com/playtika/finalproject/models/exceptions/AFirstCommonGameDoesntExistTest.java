package com.playtika.finalproject.models.exceptions;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;
public class AFirstCommonGameDoesntExistTest {

    @Test
    public void test() {
        assertThrows(AFirstCommonGameDoesntExist.class, () -> {throw new AFirstCommonGameDoesntExist("test message");})
                .getMessage().equals("test message");
    }
}
