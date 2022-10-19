package com.playtika.finalproject.services;

import com.playtika.finalproject.models.Game;
import com.playtika.finalproject.models.GameDatabaseAPI;
import com.playtika.finalproject.models.PlatformType;
import com.playtika.finalproject.repositories.GameRepository;
import com.playtika.finalproject.repositories.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GameServiceTest {
    GameRepository gameRepository = mock(GameRepository.class);
    PlayerRepository playerRepository = mock(PlayerRepository.class);
    GameDatabaseAPI gameDatabaseAPI = mock(GameDatabaseAPI.class);
    GameService gameService;
    List<Game> gameList;
    Game game1;
    Game game2;
    Game game3;

    @BeforeEach
    public void setup() {
        reset(gameRepository);
        reset(playerRepository);
        reset(gameDatabaseAPI);
        gameList = new ArrayList<>();
        game1 = new Game();
        game1.setId(1);
        game1.setName("Game1");
        game1.setCategory("0");
        game1.setPlatformType(PlatformType.PC);
        game2 = new Game();
        game2.setId(2);
        game2.setName("Game2");
        game2.setCategory("1");
        game2.setPlatformType(PlatformType.PS);
        game2 = new Game();
        game2.setId(3);
        game2.setName("Game3");
        game2.setCategory("2");
        game2.setPlatformType(PlatformType.XBOX);
        gameList.add(game1);
        gameList.add(game2);
        gameList.add(game3);
        gameService = new GameService(gameRepository, gameDatabaseAPI, playerRepository);
    }

    @Test
    public void getAllGamesTest() {
        when(gameRepository.findAll()).thenReturn(gameList);

        Optional<List<Game>> returnedList = gameService.getAllGames();

        assertThat(returnedList).isPresent();
        assertThat(returnedList.get()).hasSize(3)
                .contains(game1, game2, game3)
                .containsExactlyElementsOf(gameList);

        when(gameRepository.findAll()).thenReturn(new ArrayList<>());

        returnedList = gameService.getAllGames();
        assertThat(returnedList).isEmpty();
    }

    @Test
    public void findGameTest() {
        when(gameRepository.findByName("Game1")).thenReturn(game1);

        Optional<Game> returnedGame = gameService.findGame("Game1");
        assertThat(returnedGame).isPresent()
                .get()
                .isEqualTo(game1)
                .hasSameHashCodeAs(game1);
    }

    @Test
    public void addGameTest() {
        gameService.addGame(game1);

        verify(gameRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void addGameByNameTest() throws ExecutionException, InterruptedException {
        when(gameDatabaseAPI.getGameByName("Game1")).thenReturn(gameList);

        CompletableFuture<List<Game>> returnedList = gameService.addGame("Game1");

        assertThat(returnedList.get())
                .isNotNull()
                .hasSize(3)
                .containsExactlyElementsOf(gameList);
    }

    @Test
    public void populateDatabaseTest() {
        ArgumentCaptor<Integer> valueCapture = ArgumentCaptor.forClass(Integer.class);
        gameService = mock(GameService.class);
        doNothing().when(gameService)
                .populateGameDatabase(valueCapture.capture());

        gameService.populateGameDatabase(100);

        assertEquals(100, valueCapture.getValue());
    }

    @Test
    public void deleteByIdTest() {
        doNothing().when(gameRepository)
                .deleteById(any());

        boolean value = gameService.deleteById(1L);

        assertThat(value).isTrue();

        value = gameService.deleteById(-1);
        assertThat(value).isFalse();
    }
}
