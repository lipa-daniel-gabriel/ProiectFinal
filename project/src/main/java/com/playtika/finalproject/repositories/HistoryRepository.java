package com.playtika.finalproject.repositories;

import com.playtika.finalproject.models.PlayedHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<PlayedHistory, Long> {

}
