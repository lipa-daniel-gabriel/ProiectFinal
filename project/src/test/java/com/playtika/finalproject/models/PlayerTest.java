package com.playtika.finalproject.models;

import com.playtika.finalproject.dtos.PlayerDto;
import com.playtika.finalproject.dtos.PlayerUpdateDetailsDTO;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

import org.springframework.security.core.parameters.P;

import java.util.HashSet;
import java.util.Set;

public class PlayerTest {

    static Game game1 = new Game();
    static Game game2 = new Game();
    static Game game3 = new Game();
    static Player player = new Player();
    static Set<Game> otherGames = new HashSet<>();
    static Set<Game> favoriteGames = new HashSet<>();
    static PlayerUpdateDetailsDTO playerUpdateDetailsDTO = new PlayerUpdateDetailsDTO();


    @BeforeAll
    public static void init() {
        game1.setName("game1");
        game2.setName("game1");
        game3.setName("game3");
        otherGames.add(game1);
        otherGames.add(game2);
        favoriteGames.add(game2);
        favoriteGames.add(game3);
        player.setFavouriteGames(favoriteGames);
        playerUpdateDetailsDTO.setAge(10);
        playerUpdateDetailsDTO.setUsername("Mihai");
        playerUpdateDetailsDTO.setEmail("mihai@email.com");
    }


    @Test

    public void withModifiesTest() {
        player.withModifiesBy(playerUpdateDetailsDTO);
        assertEquals("Player username not the same as PlayerDTO", playerUpdateDetailsDTO.getUsername(), player.getUsername());
        assertEquals("Player age not the same as PlayerDTO", playerUpdateDetailsDTO.getAge(), player.getAge());
        assertEquals("Player email not the same as PlayerDTO", playerUpdateDetailsDTO.getEmail(), player.getEmail());
    }

    @Test
    public void checkIfListHasCommonGames() {
        otherGames.add(game1);
        otherGames.add(game2);
        favoriteGames.add(game2);
        favoriteGames.add(game3);
        player.setFavouriteGames(favoriteGames);
        player.setFavouriteGames(favoriteGames);
        boolean response = player.checkIfListHasCommonGames(otherGames);
        Assertions.assertEquals(true, response, "Not common");
    }

    @Test
    public void getFirstCommonGame() {
        otherGames.add(game1);
        favoriteGames.add(game2);
        player.setFavouriteGames(favoriteGames);
        Game response = player.getFirstCommonGame(player);
        assertEquals("Not the first game", "game1", response.getName());
    }
}
