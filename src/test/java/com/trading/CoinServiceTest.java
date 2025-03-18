package com.trading;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.modal.Coin;
import com.trading.modal.CoinDTO;
import com.trading.repository.CoinRepository;
import com.trading.service.CoinService;
import com.trading.service.CoinServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "spring.flyway.enabled=false")

class CoinServiceTest {

    private CoinServiceImpl coinService;

    @MockBean
    private CoinRepository coinRepository;

    @MockBean
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        coinService = new CoinServiceImpl();
        ReflectionTestUtils.setField(coinService, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(coinService, "coinRepository", coinRepository);
        ReflectionTestUtils.setField(coinService, "API_KEY", "test-api-key");
        ReflectionTestUtils.setField(coinService, "API_URL", "https://api.coingecko.com/api/v3");
        ReflectionTestUtils.setField(coinService, "restTemplate", restTemplate);
    }

    @Test
    void getCoinList_ShouldReturnListOfCoins() throws Exception {
        // Prepare test data
        String mockResponse = """
            [
                {
                    "id": "bitcoin",
                    "symbol": "btc",
                    "name": "Bitcoin",
                    "current_price": 50000.00,
                    "market_cap": 1000000000,
                    "market_cap_rank": 1,
                    "total_volume": 500000000,
                    "price_change_percentage_24h": 2.5,
                    "market_cap_change_percentage_24h": 2.0,
                    "circulating_supply": 19000000,
                    "total_supply": 21000000,
                    "image": "https://example.com/bitcoin.png"
                },
                {
                    "id": "ethereum",
                    "symbol": "eth",
                    "name": "Ethereum",
                    "current_price": 3000.00,
                    "market_cap": 500000000,
                    "market_cap_rank": 2,
                    "total_volume": 250000000,
                    "price_change_percentage_24h": 1.5,
                    "market_cap_change_percentage_24h": 1.0,
                    "circulating_supply": 120000000,
                    "total_supply": 0,
                    "image": "https://example.com/ethereum.png"
                }
            ]
            """;

        // Mock RestTemplate response
        when(restTemplate.exchange(
                anyString(),
                any(),
                any(),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // Execute test
        List<CoinDTO> result = coinService.getCoinList(1);

        // Verify results
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify first coin
        CoinDTO bitcoin = result.get(0);
        assertEquals("bitcoin", bitcoin.getId());
        assertEquals("btc", bitcoin.getSymbol());
        assertEquals("Bitcoin", bitcoin.getName());
        assertEquals(50000.00, bitcoin.getCurrentPrice());
        assertEquals(1000000000L, bitcoin.getMarketCap());
        assertEquals(1, bitcoin.getMarketCapRank());

        // Verify second coin
        CoinDTO ethereum = result.get(1);
        assertEquals("ethereum", ethereum.getId());
        assertEquals("eth", ethereum.getSymbol());
        assertEquals("Ethereum", ethereum.getName());
        assertEquals(3000.00, ethereum.getCurrentPrice());
    }

    @Test
    void getCoinList_ShouldHandleRateLimitError() {
        // Mock RestTemplate to throw rate limit exception
        when(restTemplate.exchange(
                anyString(),
                any(),
                any(),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));

        // Execute and verify
        Exception exception = assertThrows(Exception.class, () -> {
            coinService.getCoinList(1);
        });

        assertEquals("Please wait for some time because you are using the free plan.", exception.getMessage());
    }

    @Test
    void getCoinList_ShouldHandleInvalidJsonResponse() {
        // Mock RestTemplate to return invalid JSON
        when(restTemplate.exchange(
                anyString(),
                any(),
                any(),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>("invalid json", HttpStatus.OK));

        // Execute and verify
        Exception exception = assertThrows(Exception.class, () -> {
            coinService.getCoinList(1);
        });

        assertEquals("Please wait for some time because you are using the free plan.", exception.getMessage());
    }
}