package com.trading.service;

import com.trading.domain.OrderType;
import com.trading.modal.Coin;
import com.trading.modal.Order;
import com.trading.modal.OrderItem;
import com.trading.modal.User;

import java.util.List;

public interface OrderService {
    Order createOrder(User user, OrderItem orderItem, OrderType orderType);
    Order getOrderById(Long id);
    List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol);
    Order processOrder(Coin coin, OrderType orderType,double quality, User user) throws Exception;
}
