package com.playtika.finalproject.controllers;

import com.playtika.finalproject.dtos.PlayerDto;
import com.playtika.finalproject.dtos.SignUpRequest;
import com.playtika.finalproject.models.Player;
import com.playtika.finalproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public PlayerDto registerNewUser(@RequestBody SignUpRequest signUpRequest) {
        Player player = userService.signUp(signUpRequest);
        PlayerDto response = new PlayerDto();
        response.setEmail(player.getEmail());
        response.setUsername(player.getUsername());
        return response;
    }
}
