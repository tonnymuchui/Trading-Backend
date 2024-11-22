package com.trading.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.modal.Coin;
import com.trading.service.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coins")
public class CoinController {
    private CoinService coinService;
    private ObjectMapper objectMapper;

    @Autowired
    public CoinController(CoinService coinService, ObjectMapper objectMapper) {
    }
    @GetMapping("coin_list")
    ResponseEntity<List<Coin>> getAllCoins(@RequestParam("page") int page) throws Exception {
        List<Coin> coins = coinService.getAllCoinList(page);
        return new ResponseEntity<>(coins, HttpStatus.OK);
    }

    @GetMapping("/{coinId}/chart")
    ResponseEntity<JsonNode> getCoinChart(@PathVariable String coinId, @RequestParam("days") int days) throws Exception {
        String res = coinService.getMarketChart(coinId, days);
        JsonNode node = objectMapper.readTree(res);
        return new ResponseEntity<>(node, HttpStatus.ACCEPTED);
    }
    @GetMapping("search")
    ResponseEntity<JsonNode> searchCoin(@RequestParam("query") String query, @RequestParam("page") int page) throws Exception {
        String coin = coinService.SearchCoin(query);
        JsonNode jsonNode = objectMapper.readTree(coin);
        return ResponseEntity.ok(jsonNode);
    }
    @GetMapping("/top50")
    ResponseEntity<JsonNode> getTop50CoinsByMarketRank(@RequestParam("page") int page) throws Exception {
        String coin = coinService.getTop50CoinsByMarketRank();
        JsonNode jsonNode = objectMapper.readTree(coin);
        return ResponseEntity.ok(jsonNode);
    }
    @GetMapping("/treading")
    ResponseEntity<JsonNode> getTrendingCoinsByMarketRank(@RequestParam("page") int page) throws Exception {
        String coin = coinService.getTrendingCoinsByMarketRank();
        JsonNode jsonNode = objectMapper.readTree(coin);
        return ResponseEntity.ok(jsonNode);
    }
    @GetMapping("/details/{coinId}")
    ResponseEntity<JsonNode> getCoinDetails(@PathVariable String coinId) throws Exception {
        String res = coinService.getCoinDetails(coinId);
        JsonNode node = objectMapper.readTree(res);
        return ResponseEntity.ok(node);
    }
}
