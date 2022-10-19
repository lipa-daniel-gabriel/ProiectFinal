package com.playtika.finalproject.services;

import com.playtika.finalproject.models.Player;
import com.playtika.finalproject.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    private final PlayerRepository playerRepository;

    @Autowired
    public AdminService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Optional<Player> lockOrUnlockUser(long id, boolean b) {
        Optional<Player> player = playerRepository.findById(id);
        Player player1 = new Player();
        if (player.isPresent()) {
            player1 = player.get();
            player1.setLocked(b);
            playerRepository.save(player1);
        } else return Optional.empty();
        return Optional.of(player1);
    }
}
