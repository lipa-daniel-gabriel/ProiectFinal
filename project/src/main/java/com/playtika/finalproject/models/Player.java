package com.playtika.finalproject.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.playtika.finalproject.dtos.PlayerUpdateDetailsDTO;
import com.playtika.finalproject.models.exceptions.AFirstCommonGameDoesntExist;
import com.playtika.finalproject.models.exceptions.NotAValidEmailException;
import com.playtika.finalproject.models.exceptions.NotAValidPasswordException;
import com.playtika.finalproject.models.exceptions.NotAValidUsernameException;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "players")
@Table()
public class Player {

    public static final long GAME_SESSION_DURATION = 10;
    @Column(name = "is_locked")
    boolean isLocked;
    @Column(name = "is_online")
    boolean isOnline;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column
    private int age;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(referencedColumnName = "id", name = "player_id"),
            inverseJoinColumns = @JoinColumn(referencedColumnName = "id", name = "role_id"))
    private Set<Role> roles;

    @DateTimeFormat
    @Column(name = "game_start_time")
    private LocalDateTime gameStartTime;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Game> favouriteGames;
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Player> friends;
    @OneToOne
    private Game game;
    @OneToMany
    private List<PlayedHistory> history;

    public Player withModifiesBy(PlayerUpdateDetailsDTO playerDto) {
        setUsername(playerDto.getUsername());
        setAge(playerDto.getAge());
        setEmail(playerDto.getEmail());
        return this;
    }

    public boolean checkIfListHasCommonGames(Set<Game> otherGames) {
        for (Game game : otherGames) {
            if (favouriteGames.contains(game)) {
                return true;
            }
        }
        return false;
    }

    public Game getFirstCommonGame(Player player) {
        for (Game game : player.getFavouriteGames()) {
            if (favouriteGames.contains(game)) {
                return game;
            }
        }
        throw new AFirstCommonGameDoesntExist("Doesn't have a first common game");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return Objects.equals(getUsername(), player.getUsername()) && Objects.equals(getEmail(), player.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getEmail());
    }


    private static final String USERNAME_PATTERN = "^[A-Za-z]\\w{4,29}$";
    private static final Pattern usernamePattern = Pattern.compile(USERNAME_PATTERN);

    private boolean validateUsername(String username) {
        Matcher matcher = usernamePattern.matcher(username);
        return matcher.matches();
    }

    public void setUsername(String username) {
        if (validateUsername(username)) {
            this.username = username;
        } else throw new NotAValidUsernameException("Username is not valid");
    }


    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private static final Pattern emailPatterns = Pattern.compile(EMAIL_PATTERN);

    private boolean validateEmail(String email) {
        Matcher matcher = emailPatterns.matcher(email);
        return matcher.matches();
    }

    public void setEmail(String email) {
        if (validateEmail(email)) {
            this.email = email;
        } else throw new NotAValidEmailException("Email is not valid");
    }

    private boolean validatePassword(String password) {
        return password.length() > 8 ;
    }

    public void setPassword(String password) {
        if (validatePassword(password)) {
            this.password = password;
        } else throw new NotAValidPasswordException("Password is not valid");
    }
}
