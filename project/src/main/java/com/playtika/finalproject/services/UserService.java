package com.playtika.finalproject.services;

import com.playtika.finalproject.dtos.LoginResponse;
import com.playtika.finalproject.dtos.SignUpRequest;
import com.playtika.finalproject.exceptions.MySecurityException;
import com.playtika.finalproject.models.Player;
import com.playtika.finalproject.models.Role;
import com.playtika.finalproject.models.RoleType;
import com.playtika.finalproject.repositories.RolesRepository;
import com.playtika.finalproject.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.logging.Logger;

@Service
public class UserService implements UserDetailsService {


    Logger logger = Logger.getLogger(UserService.class.toString());

    private final PasswordEncoder passwordEncoder;
    private final PlayerRepository playerRepository;
    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    AuthenticationManager authenticationManager;


    RolesRepository rolesRepository;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, PlayerRepository playerRepository,RolesRepository rolesRepository) {
        this.passwordEncoder = passwordEncoder;
        this.playerRepository = playerRepository;
        this.rolesRepository = rolesRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player player = playerRepository.findByUsername(username);
        if (player == null) {
            throw new MySecurityException("Username does not exist");
        }
        return org.springframework.security.core.userdetails.User.builder()
                .username(player.getUsername())
                .password(player.getPassword())
                .authorities(player.getRoles())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    public LoginResponse login(String username, String password) {

        logger.info("Login check credentials");

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        Player player = playerRepository.findByUsername(username);
        player.setOnline(true);
        if (player.isLocked()) {
            throw new MySecurityException("Player is currently locked");
        }
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setEmail(player.getEmail());
        loginResponse.setUsername(player.getUsername());
        loginResponse.setAccessToken(jwtTokenService.createJwtToken(player));

        logger.info("Logger ok");

        return loginResponse;
    }

    public Player signUp(SignUpRequest signUpRequest) {
        logger.info("Start signup");
        Player player = playerRepository.findByUsername(signUpRequest.getUsername());
        if (player != null) {
            throw new MySecurityException("Username not available");
        }
        player = playerRepository.findByEmail(signUpRequest.getEmail());
        if (player != null) {
            throw new MySecurityException("Email not available");
        }
        player = new Player();
        player.setUsername(signUpRequest.getUsername());
        player.setEmail(signUpRequest.getEmail());
        player.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        Role defaultRole = rolesRepository.getRoleByName(RoleType.ROLE_USER.toString());
        HashSet<Role> initialRoles = new HashSet<>();
        initialRoles.add(defaultRole);
        player.setRoles(initialRoles);
        playerRepository.saveAndFlush(player);
        logger.info("User registered");
        return player;
    }
}
