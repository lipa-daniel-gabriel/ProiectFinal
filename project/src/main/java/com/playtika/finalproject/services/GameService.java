package com.playtika.finalproject.services;

import com.playtika.finalproject.models.Game;
import com.playtika.finalproject.models.GameDatabaseAPI;
import com.playtika.finalproject.models.PlatformType;
import com.playtika.finalproject.models.Player;
import com.playtika.finalproject.repositories.GameRepository;
import com.playtika.finalproject.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Service
public class GameService {

    private final GameRepository gameRepository;

    private final GameDatabaseAPI gameDatabaseAPI;

    private final PlayerRepository playerRepository;

    @Autowired
    public GameService(GameRepository gameRepository, GameDatabaseAPI gameDatabaseAPI, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.gameDatabaseAPI = gameDatabaseAPI;
        this.playerRepository = playerRepository;
    }

    public Optional<List<Game>> getAllGames() {
        List<Game> games = gameRepository.findAll();
        if (!games.isEmpty()) {
            return Optional.of(games);
        }
        return Optional.empty();
    }

    public Optional<Game> findGame(String gameName) {
        return Optional.ofNullable(gameRepository.findByName(gameName));
    }

    public void addGame(Game game) {
        gameRepository.saveAndFlush(game);
    }

    @Async
    public CompletableFuture<List<Game>> addGame(String gameName) {
        return CompletableFuture.completedFuture(gameDatabaseAPI.getGameByName(gameName));
    }

    public void populateGameDatabase(int amount) {
        List<Game> test = WebClient.create("https://api.igdb.com/v4/games")
                .post()
                .header("Client-ID", "your id")
                .header("Authorization", "your token bearer")
                .bodyValue(String.format("fields id,name,platforms,category; limit %d;", amount))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToFlux(Game.class)
                .collectList()
                .block(Duration.ofSeconds(30));
        int[] platforms = new int[]{6, 48, 49};
        Supplier<Integer> random = () -> platforms[ThreadLocalRandom.current()
                .nextInt(3)];
        test.forEach(x -> x.setPlatformType(PlatformType.valueOfPlatform(random.get())));
        gameRepository.saveAllAndFlush(test);
    }

    public boolean deleteById(long id) {
        List<Player> players = playerRepository.findAll();
        Optional<Game> game = gameRepository.findById(id);
        for (Player player : players) {
            game.ifPresent(value -> player.getFavouriteGames().remove(value));
        }
        if (id > 0) {
            gameRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
