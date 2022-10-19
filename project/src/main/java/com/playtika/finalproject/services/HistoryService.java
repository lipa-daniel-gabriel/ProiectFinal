package com.playtika.finalproject.services;

import com.playtika.finalproject.models.PlayedHistory;
import com.playtika.finalproject.repositories.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;

    @Autowired
    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void saveHistory(PlayedHistory playedHistory) {
        historyRepository.saveAndFlush(playedHistory);
    }
}
