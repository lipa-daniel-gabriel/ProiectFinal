package com.playtika.finalproject.repositories;

import com.playtika.finalproject.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    boolean findAllByName(String name);

    Game findByName(String name);

    void deleteById(long id);

}
