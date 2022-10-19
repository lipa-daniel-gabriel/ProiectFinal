package com.playtika.finalproject.services;

import com.playtika.finalproject.dtos.SignUpRequest;
import com.playtika.finalproject.exceptions.MySecurityException;
import com.playtika.finalproject.models.*;
import com.playtika.finalproject.repositories.PlayerRepository;
import com.playtika.finalproject.repositories.RolesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    PlayerRepository playerRepository = mock(PlayerRepository.class);
    JwtTokenService jwtTokenService = mock(JwtTokenService.class);
    @MockBean
    AuthenticationManager authenticationManager;
    RolesRepository rolesRepository = mock(RolesRepository.class);
    UserService userService = new UserService(passwordEncoder, playerRepository,rolesRepository);
    Player player;

    @BeforeEach
    public void setup() {
        reset(passwordEncoder);
        reset(playerRepository);
        reset(jwtTokenService);
        reset(rolesRepository);
        Set<Role> roles = new HashSet<>();
        Role role = new Role(RoleType.ROLE_USER);
        Game game = new Game(1,"Game","0", PlatformType.PC);
        Set<Game> gameSet = new HashSet<>();
        gameSet.add(game);
        Player player1 = new Player();
        Set<Player> playerSet = new HashSet<>();
        playerSet.add(player1);
        player = new Player(false,true,1,"adminul","adminul@gmail.com","Parola123",20, roles, LocalDateTime.now(),gameSet,playerSet,game, new ArrayList<>());
    }

    @Test
    public void loadUserByUsernameTest() {
        when(playerRepository.findByUsername(anyString())).thenReturn(null);

        assertThrows(MySecurityException.class, () -> userService.loadUserByUsername("player"));

        when(playerRepository.findByUsername(anyString())).thenReturn(player);

        UserDetails userDetails = userService.loadUserByUsername("adminul");

        assertThat(userDetails.getUsername()).isEqualTo(player.getUsername());
    }

    @Test
    public void loginTest() {
        when(playerRepository.findByUsername(anyString())).thenReturn(player);
        player.setLocked(true);
        assertThrows(NullPointerException.class, () -> userService.login("adminul","Parola123"));
    }

    @Test
    public void signUpTest() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("adminul@gmail.com");
        signUpRequest.setPassword("parola123");
        signUpRequest.setUsername("adminul");
        when(playerRepository.findByUsername(anyString())).thenReturn(player);
        assertThrows(MySecurityException.class, () -> userService.signUp(signUpRequest));
        when(playerRepository.findByEmail(anyString())).thenReturn(player);
        assertThrows(MySecurityException.class, () -> userService.signUp(signUpRequest));
        when(playerRepository.findByUsername(anyString())).thenReturn(null);
        when(playerRepository.findByEmail(anyString())).thenReturn(null);
        Role role = new Role(RoleType.ROLE_USER);

        when(rolesRepository.getRoleByName(anyString())).thenReturn(role);
        when(passwordEncoder.encode(anyString())).thenReturn("Parola123");
        Player player1 = userService.signUp(signUpRequest);

        assertThat(player1).extracting("username","email","password").containsSequence("adminul","adminul@gmail.com",passwordEncoder.encode("Parola123"));
    }
}
