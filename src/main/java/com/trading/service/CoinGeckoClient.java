package com.trading.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.trading.config.FeignClientConfig;
import com.trading.modal.CoinDTO;

import java.util.List;

@FeignClient(name = "coingecko", url = "${coingecko.api.url}", configuration = FeignClientConfig.class)
public interface CoinGeckoClient {

    @GetMapping("/coins/markets")
    List<CoinDTO> getCoinList(
            @RequestHeader("x-cg-api-key") String apiKey,
            @RequestParam("vs_currency") String vsCurrency,
            @RequestParam("per_page") int perPage,
            @RequestParam("page") int page);

    @GetMapping("/coins/{id}/market_chart")
    String getMarketChart(
            @RequestHeader("x-cg-api-key") String apiKey,
            @PathVariable("id") String id,
            @RequestParam("vs_currency") String vsCurrency,
            @RequestParam("days") int days);

    @GetMapping("/coins/{id}")
    String getCoinDetails(
            @RequestHeader("x-cg-api-key") String apiKey,
            @PathVariable("id") String id);

    @GetMapping("/search")
    String searchCoin(
            @RequestHeader("x-cg-api-key") String apiKey,
            @RequestParam("query") String query);

    @GetMapping("/coins/markets")
    String getTop50CoinsByMarketCapRank(
            @RequestHeader("x-cg-api-key") String apiKey,
            @RequestParam("vs_currency") String vsCurrency,
            @RequestParam("per_page") int perPage,
            @RequestParam("page") int page);

    @GetMapping("/search/trending")
    String getTrendingCoins(
            @RequestHeader("x-cg-api-key") String apiKey);
}