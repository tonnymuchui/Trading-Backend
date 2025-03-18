package com.trading.service;

import com.trading.modal.CoinDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "coingecko-client", url = "${coingecko.api.url}")
public interface CoinGeckoClient {

    @GetMapping("/coins/markets")
    List<CoinDTO> getCoinList(
            @RequestHeader("x-cg-demo-api-key") String apiKey,
            @RequestParam("vs_currency") String vsCurrency,
            @RequestParam("per_page") int perPage,
            @RequestParam("page") int page
    );

    @GetMapping("/coins/{coinId}/market_chart")
    String getMarketChart(
            @RequestHeader("x-cg-demo-api-key") String apiKey,
            @PathVariable String coinId,
            @RequestParam("vs_currency") String vsCurrency,
            @RequestParam("days") int days
    );

    @GetMapping("/coins/{coinId}")
    String getCoinDetails(
            @RequestHeader("x-cg-demo-api-key") String apiKey,
            @PathVariable String coinId
    );

    @GetMapping("/search")
    String searchCoin(
            @RequestHeader("x-cg-demo-api-key") String apiKey,
            @RequestParam("query") String query
    );

    @GetMapping("/coins/markets")
    String getTop50CoinsByMarketCapRank(
            @RequestHeader("x-cg-demo-api-key") String apiKey,
            @RequestParam("vs_currency") String vsCurrency,
            @RequestParam("per_page") int perPage,
            @RequestParam("page") int page
    );

    @GetMapping("/search/trending")
    String getTrendingCoins(
            @RequestHeader("x-cg-demo-api-key") String apiKey
    );
}