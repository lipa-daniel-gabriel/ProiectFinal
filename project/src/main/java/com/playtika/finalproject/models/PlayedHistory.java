package com.playtika.finalproject.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "played_history")
@Table(name = "played_history")
@Getter
@Setter
public class PlayedHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "player_name", nullable = false)
    private String playerName;
    @Column(name = "game_name", nullable = false)
    private String gameName;
}
