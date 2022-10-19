package com.playtika.finalproject.services;

import com.playtika.finalproject.models.PlayedHistory;
import com.playtika.finalproject.repositories.HistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class HistoryServiceTest {

    HistoryRepository historyRepository;
    PlayedHistory playedHistory = new PlayedHistory();

    @BeforeEach
    public void setup() {
        historyRepository = mock(HistoryRepository.class);
        playedHistory.setId(1);
        playedHistory.setGameName("game");
        playedHistory.setPlayerName("player");
    }

    @Test
    public void saveHistoryTest() {
        HistoryService historyService = new HistoryService(historyRepository);
        historyService.saveHistory(playedHistory);
        verify(historyRepository, times(1)).saveAndFlush(playedHistory);
    }

}
