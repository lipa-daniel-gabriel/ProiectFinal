package com.playtika.finalproject.services;

import com.playtika.finalproject.models.Player;
import com.playtika.finalproject.repositories.PlayerRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class AdminServiceTest {

    @Test
    public void testLockUnlockUser() {
        PlayerRepository playerRepository = mock(PlayerRepository.class);
        AdminService adminService = new AdminService(playerRepository);
        Player player = new Player();
        player.setId(1);
        player.setLocked(true);
        when(playerRepository.findById(any())).thenReturn(Optional.of(player));

        assertThat(adminService.lockOrUnlockUser(1, true))
                .contains(player)
                .get()
                .isExactlyInstanceOf(Player.class)
                .isEqualTo(player);
    }
}
