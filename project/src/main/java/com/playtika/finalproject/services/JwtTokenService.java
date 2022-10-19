package com.playtika.finalproject.services;

import com.playtika.finalproject.models.Player;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;


@Getter
@Service
public class JwtTokenService {

    private final String secretKey = "My secret key!";
    private final int tokenValidityInHours = 6;

    @Autowired
    UserService userService;
    private String secretKeyBase64;

    @PostConstruct
    public void initBean() {
        this.secretKeyBase64 = Base64.getEncoder()
                .encodeToString(secretKey.getBytes());
    }

    public String createJwtToken(Player player) {
        Claims claims = Jwts.claims()
                .setSubject(player.getUsername());
        claims.put("auth", player.getRoles()
                .stream()
                .map(s -> new SimpleGrantedAuthority(s.getAuthority()))
                .collect(Collectors.toList()));
        Date date = new Date();
        Date tokenExpireDate = new Date(System.currentTimeMillis() + tokenValidityInHours * 3600 * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(player.getUsername())
                .setExpiration(tokenExpireDate)
                .setIssuedAt(date)
                .signWith(SignatureAlgorithm.HS256, secretKeyBase64)
                .compact();
    }

    public Authentication validateUser(String token) {
        UserDetails userDetails = this.userService.loadUserByUsername(this.getUsernameFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKeyBase64)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getTokenFromHttpRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return token == null ? null : token.split(" ")[1];
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKeyBase64)
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

