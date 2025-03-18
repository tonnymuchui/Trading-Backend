package com.trading.config;

import com.trading.service.CoinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CoinDataUpdater {

    @Autowired
    private CoinService coinService;

    @Scheduled(fixedRate = 3600000) // Update every hour
    public void updateCoinData() {
        try {
            coinService.getCoinList(1); // Fetch and save data
        } catch (Exception e) {
            log.error("Error updating coin data: ", e);
        }
    }
}
