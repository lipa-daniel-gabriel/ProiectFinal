package com.playtika.finalproject.controllers;

import com.playtika.finalproject.dtos.LoginRequest;
import com.playtika.finalproject.dtos.LoginResponse;
import com.playtika.finalproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getLoginPage() {
        String message = "Login page with postman";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest.getUsername(), loginRequest.getPassword());
    }
}
