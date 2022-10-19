package com.playtika.finalproject.controllers;

import com.playtika.finalproject.models.Game;
import com.playtika.finalproject.models.Player;
import com.playtika.finalproject.services.AdminService;
import com.playtika.finalproject.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.security.RolesAllowed;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/admin")
@RolesAllowed("ROLE_ADMIN")
public class AdminController {

    private final GameService gameService;
    private final AdminService adminService;

    @Autowired
    public AdminController(GameService gameService, AdminService adminService) {
        this.gameService = gameService;
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<String> adminPage() {
        return new ResponseEntity<>("Hello admin", HttpStatus.OK);
    }

    @GetMapping(value = "/games", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Game>> getAllGames() {
        return gameService.getAllGames()
                .map(player -> new ResponseEntity<>(player, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @PostMapping(value = "/addGame", consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Game> addGame(@RequestBody String gameName) {
        Optional<Game> game = gameService.findGame(gameName);
        return game
                .map(gameFound -> new ResponseEntity<>(gameFound, HttpStatus.OK))
                .orElseGet(() -> {
                    CompletableFuture<List<Game>> futureGame = gameService.addGame(gameName);
                    try {
                        List<Game> foundGames = futureGame.get();
                        System.out.println(foundGames);
                        if (foundGames != null && foundGames.size() > 0) {
                            gameService.addGame(foundGames.get(0));
                            return new ResponseEntity<>(foundGames.get(0), HttpStatus.OK);
                        }
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @GetMapping("/populate")
    public void populateGames() {
        gameService.populateGameDatabase(200);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> deleteGameById(@PathVariable long id) {
        return gameService.deleteById(id) ?
                new ResponseEntity<>(HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping(value = "/lockUser/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> lockOrUnlockUser(@PathVariable long id, @RequestParam(name = "boolean") boolean b) {
        return adminService.lockOrUnlockUser(id, b)
                .map(player -> new ResponseEntity<>(player, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping(value = "/easteregg", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> randomQuote() {
        String quote = WebClient.create("https://api.kanye.rest/")
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(3));
        if (quote == null || quote.isEmpty()) {
            return new ResponseEntity<>("The endpoint was unable to fetch a quote", HttpStatus.REQUEST_TIMEOUT);
        }
        quote = quote.replace("{", "");
        quote = quote.replace("}", "");
        return new ResponseEntity<>(quote + " - Kanye West", HttpStatus.OK);
    }
}

