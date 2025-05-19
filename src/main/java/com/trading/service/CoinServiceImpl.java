
package com.trading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    private CoinGeckoClient coinGeckoClient;

    @Value("${coingecko.api.key}")
    private String API_KEY;

    @Value("${coingecko.free.plan.retry.delay:30000}")
    private long retryDelay;

    @Value("${coingecko.max.retries:3}")
    private int maxRetries;

    @Override
    @Cacheable(value = "coinList", key = "#page")
    public List<CoinDTO> getCoinList(int page) throws Exception {
        int retries = 0;
        Exception lastException = null;

        while (retries < maxRetries) {
            try {
                return coinGeckoClient.getCoinList(API_KEY, "usd", 10, page);
            } catch (Exception e) {
                lastException = e;
                log.warn("Error fetching coin list (attempt {}/{}): {}", retries + 1, maxRetries, e.getMessage());

                if (e.getMessage().contains("free plan") ||
                        (e instanceof feign.FeignException &&
                                ((feign.FeignException) e).status() == 429)) {
                    // Rate limit error, wait before retrying
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new Exception("Retry interrupted", ie);
                    }
                    retries++;
                } else {
                    // Not a rate limit error, don't retry
                    break;
                }
            }
        }

        log.error("Error fetching coin list after {} retries: ", maxRetries, lastException);
        throw new Exception("Unable to fetch coin data. CoinGecko API limits reached. Please try again later.");
    }

    @Override
    @Cacheable(value = "marketChart", key = "#coinId + '-' + #days")
    public String getMarketChart(String coinId, int days) throws Exception {
        int retries = 0;
        Exception lastException = null;

        while (retries < maxRetries) {
            try {
                return coinGeckoClient.getMarketChart(API_KEY, coinId, "usd", days);
            } catch (Exception e) {
                lastException = e;
                log.warn("Error fetching market chart (attempt {}/{}): {}", retries + 1, maxRetries, e.getMessage());

                if (e.getMessage().contains("free plan") ||
                        (e instanceof feign.FeignException &&
                                ((feign.FeignException) e).status() == 429)) {
                    // Rate limit error, wait before retrying
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new Exception("Retry interrupted", ie);
                    }
                    retries++;
                } else {
                    // Not a rate limit error, don't retry
                    break;
                }
            }
        }

        log.error("Error fetching market chart after {} retries: ", maxRetries, lastException);
        throw new Exception("Unable to fetch market data. CoinGecko API limits reached. Please try again later.");
    }

    @Override
    @Cacheable(value = "coinDetails", key = "#coinId")
    public String getCoinDetails(String coinId) throws JsonProcessingException {
        // First check if data exists in the database
        try {
            Optional<Coin> coinOptional = coinRepository.findById(Long.valueOf(coinId));
            if (coinOptional.isPresent()) {
                return objectMapper.writeValueAsString(coinOptional.get());
            }
        } catch (NumberFormatException e) {
            log.warn("Non-numeric coinId: {}, trying to fetch from API directly", coinId);
        }

        // Fetch data from the API with retry mechanism
        int retries = 0;
        Exception lastException = null;

        while (retries < maxRetries) {
            try {
                String response = coinGeckoClient.getCoinDetails(API_KEY, coinId);

                try {
                    // Only try to save to DB if coinId is numeric
                    Coin coin = objectMapper.readValue(response, Coin.class);
                    coinRepository.save(coin);
                } catch (Exception e) {
                    log.warn("Could not save coin to database: {}", e.getMessage());
                    // Continue even if saving to DB fails
                }

                return response;
            } catch (Exception e) {
                lastException = e;
                log.warn("Error fetching coin details (attempt {}/{}): {}", retries + 1, maxRetries, e.getMessage());

                if (e.getMessage().contains("free plan") ||
                        (e instanceof feign.FeignException &&
                                ((feign.FeignException) e).status() == 429)) {
                    // Rate limit error, wait before retrying
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new JsonProcessingException("Retry interrupted") {};
                    }
                    retries++;
                } else {
                    // Not a rate limit error, don't retry
                    break;
                }
            }
        }

        log.error("Error fetching coin details after {} retries", maxRetries, lastException);
        throw new JsonProcessingException("Unable to fetch coin details. CoinGecko API limits reached.") {};
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
            log.error("Error searching coin: {}", e.getMessage());
            return "{\"error\": \"Unable to search coins at this time. API limits may have been reached.\"}";
        }
    }

    @Override
    @Cacheable(value = "top50Coins")
    public String getTop50CoinsByMarketCapRank() {
        try {
            return coinGeckoClient.getTop50CoinsByMarketCapRank(API_KEY, "usd", 50, 1);
        } catch (Exception e) {
            log.error("Error fetching top 50 coins: {}", e.getMessage());
            return "{\"error\": \"Unable to fetch top coins at this time. API limits may have been reached.\"}";
        }
    }

    @Override
    @Cacheable(value = "trendingCoins")
    public String getTreadingCoins() {
        try {
            return coinGeckoClient.getTrendingCoins(API_KEY);
        } catch (Exception e) {
            log.error("Error fetching trending coins: {}", e.getMessage());
            return "{\"error\": \"Unable to fetch trending coins at this time. API limits may have been reached.\"}";
        }
    }
}