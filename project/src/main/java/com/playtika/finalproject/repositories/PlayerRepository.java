package com.playtika.finalproject.repositories;

import com.playtika.finalproject.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Player findByUsername(String username);

    Player findByEmail(String email);

    boolean existsByUsername(String username);

    long deleteByUsername(String username);

    void delete(Player player);

    @Query("FROM players x WHERE x.isOnline=true")
    List<Player> getOnlinePlayers();

    List<Player> getAllById(long id);
}
