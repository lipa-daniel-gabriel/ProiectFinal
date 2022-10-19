package com.playtika.finalproject.models;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Component
public class GameDatabaseAPI {
    public static final long API_CALL_TIMEOUT = 30;
    private final String clientID = "Client-ID";
    private final String clientToken = "your token";
    private final String authHeader = "Authorization";
    private final String authTokenBearer = "your auth token bearer";
    private final int[] playable_platforms = new int[]{6, 48, 49};
    private WebClient webClient = WebClient.create("https://api.igdb.com/v4/games");

    public List<Game> getGameByName(String name) {
        String queryBuilder = String.format("fields id,name,platforms,category; search \"%s\";", name);
        List<Game> test = webClient
                .post()
                .header(clientID, clientToken)
                .header(authHeader, authTokenBearer)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .bodyValue(queryBuilder)
                .retrieve()
                .bodyToFlux(Game.class)
                .collectList()
                .block(Duration.ofSeconds(API_CALL_TIMEOUT));
        Supplier<Integer> random = () -> playable_platforms[ThreadLocalRandom.current()
                .nextInt(3)];
        if (test != null && !test.isEmpty()) {
            test.forEach(game -> game.setPlatformType(PlatformType.valueOfPlatform(random.get())));
        }
        return test;
    }
}
