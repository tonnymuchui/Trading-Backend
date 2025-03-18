package com.trading.service;

import com.trading.modal.CoinDTO;
import com.trading.response.ApiResponse;
import org.json.JSONException;

public interface ChatBotService {
    ApiResponse getCoinDetails(String coinName);

    CoinDTO getCoinByName(String coinName);

    String simpleChat(String prompt) throws JSONException;
}
