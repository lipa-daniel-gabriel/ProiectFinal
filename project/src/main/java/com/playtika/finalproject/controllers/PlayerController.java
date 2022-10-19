package com.playtika.finalproject.controllers;

import com.playtika.finalproject.dtos.PlayerUpdateDetailsDTO;
import com.playtika.finalproject.models.Game;
import com.playtika.finalproject.models.PlayedHistory;
import com.playtika.finalproject.models.Player;
import com.playtika.finalproject.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RolesAllowed({"ADMIN", "USER"})
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }
    @RolesAllowed("ADMIN")
    @PatchMapping(value = "{id}/patch", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Player> updateNameAgeAndEmail(@PathVariable long id,
                                                        @RequestBody PlayerUpdateDetailsDTO playerDto) {
        return playerService.updateAgeNameAndEmail(id, playerDto)
                .map(player -> new ResponseEntity<>(player, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @PostMapping(value = "/addToFavorite", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Game> addGameToFavorite(@RequestParam(name = "gameName") String gameName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return playerService.addGameToFavorite(gameName, authentication.getName())
                .map(player -> new ResponseEntity<>(player, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping(value = "/pages/smart")
    public ResponseEntity<Page<Player>> getPossiblePlayers(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return playerService.getPossiblePlayersPage(pageable, authentication.getName())
                .map(player -> new ResponseEntity<>(player, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping(value = "/favoriteGames", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Game>> getFavoriteGames() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return playerService.getFavoriteGames(authentication.getName())
                .map(player -> new ResponseEntity<>(player, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping(value = "/getFriends", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Player>> getFriends() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return playerService.getFriends(authentication.getName())
                .map(player -> new ResponseEntity<>(player, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping(value = "/getFriends/online", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Player>> getOnlineFriends() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return playerService.getOnlinePlayers(authentication.getName())
                .map(player -> new ResponseEntity<>(player, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @PostMapping(value = "/addFriends", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> addFriend(@RequestParam(name = "username") String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return playerService.addFriendToFriendList(username, authentication.getName())
                .map(player -> new ResponseEntity<>(player, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PlayedHistory>> getPlayedHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return playerService.getPlayedHistory(authentication.getName())
                .map(player -> new ResponseEntity<>(player, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping(value = "/findTeammate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> findTeammate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return playerService.getTeammate(authentication.getName())
                .map(x -> new ResponseEntity<>(x, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
