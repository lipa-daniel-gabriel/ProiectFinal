package com.playtika.finalproject.services;

import com.playtika.finalproject.dtos.PlayerUpdateDetailsDTO;
import com.playtika.finalproject.exceptions.MySecurityException;
import com.playtika.finalproject.models.*;
import com.playtika.finalproject.repositories.GameRepository;
import com.playtika.finalproject.repositories.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class PlayerServiceTest {
    PlayerRepository playerRepository = mock(PlayerRepository.class);
    GameRepository gameRepository = mock(GameRepository.class);
    HistoryService historyService = mock(HistoryService.class);
    PlayerService playerService;
    Player player;
    Game game;
    Game game2;

    @BeforeEach
    public void setup() {
        reset(playerRepository);
        reset(gameRepository);
        reset(historyService);
        playerService = new PlayerService(playerRepository, gameRepository, historyService);
        game = new Game();
        game.setId(1);
        game.setName("Game1");
        game.setCategory("0");
        game.setPlatformType(PlatformType.PC);
        game2 = new Game();
        game2.setId(2);
        game2.setName("Game2");
        game2.setCategory("1");
        game2.setPlatformType(PlatformType.PS);
        player = new Player();
        player.setId(1);
        player.setGame(game);
        player.setLocked(false);
        player.setUsername("player");
        player.setEmail("player@gmail.com");
        player.setPassword("password123");
        player.setAge(20);
        player.setOnline(true);
        Role role = new Role();
        role.setId(1);
        role.setName(RoleType.ROLE_USER.name());
        role.setUser(List.of(player));
        player.setRoles(Set.of(role));
        player.setGameStartTime(LocalDateTime.now());
        Set<Game> gameSet = new HashSet<>();
        gameSet.add(game);
        player.setFavouriteGames(gameSet);
        player.setFriends(new HashSet<>());
    }

    @Test
    public void updateAgeNameAndEmailTest() {
        PlayerUpdateDetailsDTO dto = new PlayerUpdateDetailsDTO();
        dto.setAge(30);
        dto.setEmail("dto@gmail.com");
        dto.setUsername("datatransferobject");
        when(playerRepository.findById(any())).thenReturn(Optional.ofNullable(player));
        when(playerRepository.saveAndFlush(any())).thenReturn(player);
        Optional<Player> playerOptional = playerService.updateAgeNameAndEmail(1, dto);

        assertThat(playerOptional).isPresent()
                .get()
                .isEqualTo(player)
                .hasSameHashCodeAs(player);
    }

    @Test
    public void addGameToFavoriteTest() {
        when(playerRepository.findByUsername(any())).thenReturn(player);
        when(gameRepository.findByName(any())).thenReturn(game2);

        assertThat(playerService.addGameToFavorite("Game1", "player")).isEmpty();
        Optional<Game> gameOptional = playerService.addGameToFavorite("Game2", "player");
        assertThat(gameOptional).isPresent()
                .get()
                .isEqualTo(game2)
                .hasSameHashCodeAs(game2);

        when(gameRepository.findByName(any())).thenReturn(null);
        Optional<Game> gameOptional1 = playerService.addGameToFavorite("Game3", "player");
        assertThat(gameOptional1).isEmpty();
    }

    @Test
    public void getPossiblePlayersPageTest() {
        Pageable pageable = PageRequest.of(0, 8);
        Player player1 = new Player();
        player1.setFavouriteGames(Set.of(game));

        when(playerRepository.findAll()).thenReturn(List.of(player));
        when(playerRepository.findByUsername(any())).thenReturn(player1);
        assertThat(playerService.getPossiblePlayersPage(pageable, "player")).isPresent();
    }

    @Test
    public void getFavouriteGamesTest() {
        when(playerRepository.findByUsername(any())).thenReturn(player);

        Optional<Set<Game>> returnedGames = playerService.getFavoriteGames("player");

        assertThat(returnedGames).isPresent();
        Set<Game> games = returnedGames.get();
        assertThat(games).contains(game)
                .hasSize(1);
    }

    @Test
    public void addFriendToFriendListTest() {
        String currentPlayer = "player";
        String friend = "friend1";
        Player player1 = new Player();
        player1.setUsername("friend1");
        player1.setFriends(new HashSet<>());
        when(playerRepository.findByUsername(currentPlayer)).thenReturn(player);
        when(playerRepository.findByUsername(friend)).thenReturn(player1);

        assertThat(playerService.addFriendToFriendList(friend, currentPlayer)).isPresent()
                .get()
                .isEqualTo(true);

        when(playerRepository.findByUsername(currentPlayer)).thenReturn(player);
        when(playerRepository.findByUsername(friend)).thenReturn(null);

        assertThat(playerService.addFriendToFriendList(friend, currentPlayer)).isEmpty();
    }

    @Test
    public void getFriendsTest() {
        when(playerRepository.findByUsername("player")).thenReturn(player);

        Optional<List<Player>> optionalPlayers = playerService.getFriends("player");
        Player player1 = new Player();
        assertThat(optionalPlayers).isEmpty();
        player.getFriends()
                .add(player1);
        optionalPlayers = playerService.getFriends("player");
        assertThat(optionalPlayers).isPresent();
        List<Player> gameList = optionalPlayers.get();
        assertThat(gameList).hasSize(1)
                .contains(player1);
    }

    @Test
    public void getTeammateTest() {
        Player player1 = new Player();
        player1.setOnline(true);
        Set<Game> games = new HashSet<>();
        player1.setFavouriteGames(games);
        player.setHistory(new ArrayList<>());
        player1.setHistory(new ArrayList<>());

        when(playerRepository.findByUsername(any())).thenReturn(player);
        when(playerRepository.findAll()).thenReturn(List.of(player1));

        assertThrows(MySecurityException.class, () -> playerService.getTeammate("player"));

        games.add(game);

        Optional<Player> optionalPlayer = playerService.getTeammate("player");
        assertThat(optionalPlayer).isPresent()
                .get()
                .isEqualTo(player1);
    }

    @Test
    public void getOnlinePlayersTest() {
        when(playerRepository.getOnlinePlayers()).thenReturn(List.of(player));

        Optional<List<Player>> optionalPlayers = playerService.getOnlinePlayers("player");
        assertThat(optionalPlayers).isPresent();
        List<Player> playerList = optionalPlayers.get();
        assertThat(playerList).hasSize(1)
                .containsExactly(player);
    }

    @Test
    public void getPlayedHistory() {
        PlayedHistory playedHistory = new PlayedHistory();
        playedHistory.setPlayerName("testname");
        playedHistory.setGameName("testgame");
        playedHistory.setId(1);
        player.setHistory(List.of(playedHistory));
        when(playerRepository.findByUsername(any())).thenReturn(player);
        Optional<List<PlayedHistory>> optionalHistory = playerService.getPlayedHistory("player");
        assertThat(optionalHistory).isPresent();
        List<PlayedHistory> playedHistories = optionalHistory.get();
        assertThat(playedHistories).hasSize(1)
                .containsExactly(playedHistory);
        player.setHistory(null);
        optionalHistory = playerService.getPlayedHistory("player");
        assertThat(optionalHistory).isEmpty();
    }

}
