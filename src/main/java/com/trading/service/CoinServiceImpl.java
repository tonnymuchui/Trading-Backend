package com.trading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.modal.Coin;
import com.trading.modal.CoinDTO;
import com.trading.repository.CoinRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CoinServiceImpl implements CoinService {

    @Autowired
    private CoinRepository coinRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CoinGeckoClient coinGeckoClient; // Use Feign Client

    @Value("${coingecko.api.key}")
    private String API_KEY;

    @Override
    @Cacheable(value = "coinList", key = "#page")
    public List<CoinDTO> getCoinList(int page) throws Exception {
        try {
            return coinGeckoClient.getCoinList(API_KEY, "usd", 10, page);
        } catch (Exception e) {
            log.error("Error fetching coin list: ", e);
            throw new Exception("Please wait for some time because you are using the free plan.");
        }
    }

    @Override
    @Cacheable(value = "marketChart", key = "#coinId + '-' + #days")
    public String getMarketChart(String coinId, int days) throws Exception {
        try {
            return coinGeckoClient.getMarketChart(API_KEY, coinId, "usd", days);
        } catch (Exception e) {
            log.error("Error fetching market chart: ", e);
            throw new Exception("You are using the free plan.");
        }
    }

    @Override
    @Cacheable(value = "coinDetails", key = "#coinId")
    public String getCoinDetails(String coinId) throws JsonProcessingException {
        // Check if data exists in the database
        Optional<Coin> coinOptional = coinRepository.findById(Long.valueOf(coinId));
        if (coinOptional.isPresent()) {
            return objectMapper.writeValueAsString(coinOptional.get());
        }

        // Fetch data from the API
        String response = coinGeckoClient.getCoinDetails(API_KEY, coinId);
        Coin coin = objectMapper.readValue(response, Coin.class);

        // Save to the database
        coinRepository.save(coin);

        return response;
    }

    @Override
    public Coin findById(String coinId) throws Exception {
        Optional<Coin> optionalCoin = coinRepository.findById(Long.valueOf(coinId));
        if (optionalCoin.isEmpty()) {
            throw new Exception("Invalid coin id");
        }
        return optionalCoin.get();
    }

    @Override
    @Cacheable(value = "searchCoin", key = "#keyword")
    public String searchCoin(String keyword) {
        try {
            return coinGeckoClient.searchCoin(API_KEY, keyword);
        } catch (Exception e) {
            log.error("Error searching coin: ", e);
            return null; // Or return a default response
        }
    }

    @Override
    @Cacheable(value = "top50Coins")
    public String getTop50CoinsByMarketCapRank() {
        try {
            return coinGeckoClient.getTop50CoinsByMarketCapRank(API_KEY, "usd", 50, 1);
        } catch (Exception e) {
            log.error("Error fetching top 50 coins: ", e);
            return null; // Or return a default response
        }
    }

    @Override
    @Cacheable(value = "trendingCoins")
    public String getTreadingCoins() {
        try {
            return coinGeckoClient.getTrendingCoins(API_KEY);
        } catch (Exception e) {
            log.error("Error fetching trending coins: ", e);
            return null; // Or return a default response
        }
    }
}