package com.trading;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.trading.modal.CoinDTO;
import com.trading.service.CoinServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class CoinServiceImplTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8089))
            .build();

    @Autowired
    private CoinServiceImpl coinService;

    @BeforeEach
    void setUp() {
        wireMock.stubFor(get(urlPathEqualTo("/coins/markets"))
                .withQueryParam("vs_currency", equalTo("usd"))
                .withQueryParam("per_page", equalTo("10"))
                .withQueryParam("page", equalTo("1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\": \"bitcoin\", \"symbol\": \"btc\", \"name\": \"Bitcoin\"}]")));
    }

    @Test
    void testGetCoinList() throws Exception {
        List<CoinDTO> coins = coinService.getCoinList(1);
        assertNotNull(coins);
    }
}