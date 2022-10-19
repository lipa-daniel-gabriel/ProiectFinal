package com.playtika.finalproject.services;

import com.playtika.finalproject.dtos.PlayerUpdateDetailsDTO;
import com.playtika.finalproject.exceptions.MySecurityException;
import com.playtika.finalproject.models.Game;
import com.playtika.finalproject.models.PlayedHistory;
import com.playtika.finalproject.models.Player;
import com.playtika.finalproject.repositories.GameRepository;
import com.playtika.finalproject.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final HistoryService historyService;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, GameRepository gameRepository, HistoryService historyService) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.historyService = historyService;
    }

    private  Map<Player, Game> getPlayersWithSamePreferences(List<Player> onlinePlayers, Player player, List<Player> availablePlayers) {
        Map<Player, Game> commonGames = new HashMap<>();
        for (Player p : onlinePlayers) {
            if (player.equals(p)) {
                continue;
            }
            if (player.checkIfListHasCommonGames(p.getFavouriteGames())) {
                commonGames.put(p, player.getFirstCommonGame(p));
            }
        }
        return commonGames;
    }

    public Optional<Player> updateAgeNameAndEmail(long id, PlayerUpdateDetailsDTO playerDto) {
        return playerRepository.findById(id)
                .map(player -> player.withModifiesBy(playerDto))
                .map(playerRepository::saveAndFlush);
    }

    public Optional<Game> addGameToFavorite(String gameName, String username) {
        Player player = playerRepository.findByUsername(username);
        for (Game game : player.getFavouriteGames()) {
            if (game.getName()
                    .equals(gameName)) {
                return Optional.empty();
            }
        }
        Game game = gameRepository.findByName(gameName);
        if (game != null && player.getFavouriteGames()
                .add(game)) {
            playerRepository.saveAndFlush(player);
            return Optional.of(game);
        }
        return Optional.empty();
    }

    public Optional<Page<Player>> getPossiblePlayersPage(Pageable pageable, String username) {
        List<Player> onlinePlayers = playerRepository.findAll().stream().filter(Player::isOnline).collect(Collectors.toList());
        Player player = playerRepository.findByUsername(username);
        onlinePlayers.remove(player);
        List<Player> availablePlayers = new ArrayList<>();

        for (Player p : onlinePlayers) {
            if (player.checkIfListHasCommonGames(p.getFavouriteGames())) {
                availablePlayers.add(p);
            }
        }
        availablePlayers.remove(player);
        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), availablePlayers.size());
        return Optional.of(new PageImpl<>(availablePlayers.subList(start, end), pageable, availablePlayers.size()));
    }

    public Optional<Set<Game>> getFavoriteGames(String name) {
        Optional<Player> player = Optional.ofNullable(playerRepository.findByUsername(name));
        Player player1 = new Player();
        if (player.isPresent()) {
            player1 = player.get();
        }
        return Optional.ofNullable(player1.getFavouriteGames());
    }

    public Optional<Boolean> addFriendToFriendList(String friendUsername, String currentPlayerUsername) {
        Player friend = playerRepository.findByUsername(friendUsername);
        Player currentPlayer = playerRepository.findByUsername(currentPlayerUsername);
        Set<Player> currentPlayerFriends = currentPlayer.getFriends();
        if (friend != null) {
            currentPlayer.getFriends()
                    .add(friend);
            friend.getFriends()
                    .add(currentPlayer);
            playerRepository.save(friend);
            playerRepository.save(currentPlayer);
            return Optional.of(true);
        } else return Optional.empty();
    }

    public Optional<List<Player>> getFriends(String name) {
        Set<Player> friends = playerRepository.findByUsername(name)
                .getFriends();
        List<Player> players = new ArrayList<>(friends);
        if (!friends.isEmpty()) {
            return Optional.of(players);
        }
        return Optional.empty();
    }

    public Optional<Player> getTeammate(String username) {
        Player player = playerRepository.findByUsername(username);
        PlayedHistory playedHistory = new PlayedHistory();
        List<Player> onlinePlayers = playerRepository.findAll()
                .stream()
                .filter(Player::isOnline)
                .collect(Collectors.toList());
        List<Player> availablePlayers = new ArrayList<>();
        Map<Player, Game> playersWithCommonGames = getPlayersWithSamePreferences(onlinePlayers, player,
                availablePlayers);
        if (playersWithCommonGames.isEmpty()) {
            throw new MySecurityException("No players online with common games found");
        }
        Player player1 = playersWithCommonGames.keySet().stream().findFirst()
                .orElseThrow(() -> new MySecurityException("Player with common game not found"));
        Game game = playersWithCommonGames.get(player1);
        if (player1.getGameStartTime() == null) {
            player1.setGameStartTime(LocalDateTime.MIN);
        }

        long seconds = ChronoUnit.SECONDS.between(player1.getGameStartTime(), LocalDateTime.now());
        if (seconds > Player.GAME_SESSION_DURATION) {
            player1.setGameStartTime(LocalDateTime.now());
            player.setGameStartTime(LocalDateTime.now());
            player.setGame(game);
            player1.setGame(game);
            playedHistory.setPlayerName(player.getUsername());
            playedHistory.setGameName(player.getGame()
                    .getName());
            player1.getHistory()
                    .add(playedHistory);
            historyService.saveHistory(playedHistory);
            playedHistory = new PlayedHistory();
            playedHistory.setGameName(player1.getGame()
                    .getName());
            playedHistory.setPlayerName(player1.getUsername());
            player.getHistory()
                    .add(playedHistory);
            historyService.saveHistory(playedHistory);
            playerRepository.save(player);
            playerRepository.save(player1);
        } else throw new UnsupportedOperationException();
        return Optional.of(player1);
    }

    public Optional<List<Player>> getOnlinePlayers(String name) {
        return Optional.ofNullable(playerRepository.getOnlinePlayers());
    }

    public Optional<List<PlayedHistory>> getPlayedHistory(String username) {
        Optional<Player> player = Optional.of(playerRepository.findByUsername(username));
        return player.map(Player::getHistory);
    }
}
