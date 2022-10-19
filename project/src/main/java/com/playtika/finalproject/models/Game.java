package com.playtika.finalproject.models;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "games")
@Table(name = "games")
public class Game {
    @Id
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(name = "game_category")
    private String category;
    @Column(name = "platform_type")
    private PlatformType platformType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;
        Game game = (Game) o;
        return getName().equals(game.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
