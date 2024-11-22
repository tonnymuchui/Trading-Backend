package com.trading.service;

import com.trading.modal.Coin;

import java.util.List;

public interface CoinService {
    List<Coin> getAllCoinList(int page) throws Exception;
    String getMarketChart(String coinId, int days) throws Exception;
    String getCoinDetails(String coinId) throws Exception;
    Coin findById(String coinId) throws Exception;
    String SearchCoin(String keyword) throws Exception;
    String getTop50CoinsByMarketRank() throws Exception;
    String getTrendingCoinsByMarketRank() throws Exception;
}
