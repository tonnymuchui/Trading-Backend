package com.trading.service;

import com.trading.domain.OrderStatus;
import com.trading.domain.OrderType;
import com.trading.modal.Coin;
import com.trading.modal.Order;
import com.trading.modal.OrderItem;
import com.trading.modal.User;
import com.trading.repository.OrderItemRepository;
import com.trading.repository.OrderRepository;
import com.trading.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImp implements OrderService {
    private OrderRepository orderRepository;
    private WalletRepository walletRepository;
    private OrderItemRepository orderItemRepository;
    private WalletService walletService;

    @Autowired
    public OrderServiceImp(OrderRepository orderRepository, WalletRepository walletRepository, OrderItemRepository orderItemRepository, WalletService walletService) {
        this.orderRepository = orderRepository;
        this.walletRepository = walletRepository;
        this.orderItemRepository = orderItemRepository;
        this.walletService = walletService;
    }

    @Override
    public Order createOrder(User user, OrderItem orderItem, OrderType orderType) {
        double price = orderItem.getCoin().getCurrentPrice()*orderItem.getQuantity();
        Order order = new Order();
        order.setUser(user);
        order.setOrderItem(orderItem);
        order.setOrderType(orderType);
        order.setPrice(BigDecimal.valueOf(price));
        order.setLocalDateTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(()-> new RuntimeException("Order not found"));
    }

    @Override
    public List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol) {
        return orderRepository.findByUserId(userId);
    }
    private OrderItem createOrderItem(Coin coin,double quantity,double buyPrice,double sellPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCoin(coin);
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);
        return orderItemRepository.save(orderItem);
    }
    @Transactional
    public Order buyAsset(Coin coin,double quantity,User user) throws Exception {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }
        double buyPrice = coin.getCurrentPrice();
        OrderItem orderItem = createOrderItem(coin,quantity,buyPrice,0);
        Order order = createOrder(user, orderItem, OrderType.BUY);
        orderItem.setOrder(order);
        walletService.payOrderPayment(order,user);
        order.setOrderStatus(OrderStatus.SUCCESS);
        order.setOrderType(OrderType.BUY);
        Order savedOrder = orderRepository.save(order);
        return savedOrder;
    }
    @Transactional
    public Order sellAsset(Coin coin,double quantity,User user) throws Exception {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }
        double sellPrice = coin.getCurrentPrice();
        double buyPrice = assetToSell.getCurrentPrice();
        OrderItem orderItem = createOrderItem(coin,quantity,buyPrice,sellPrice);
        Order order = createOrder(user, orderItem, OrderType.SELL);
        orderItem.setOrder(order);
        if (assetToSell.getQuantity()){
            order.setOrderStatus(OrderStatus.SUCCESS);
            order.setOrderType(OrderType.SELL);
            Order savedOrder = orderRepository.save(order);
            walletService.payOrderPayment(order,user);
            Asset updatedAsset = assetService.updateAsset(assetToSell.getId(),-quantity);
            if (updatedAsset.getQuantity()*coin.getCurrentPrice()<=1){
                assetService.deleteAsset(updatedAsset.getId);
            }
            return savedOrder;
        } throw new Exception("Insufficient quantity to sell");

    }
    @Override
    @Transactional
    public Order processOrder(Coin coin, OrderType orderType, double quality, User user) throws Exception {
        if (orderType.equals(OrderType.BUY)){
            return buyAsset(coin,quality,user);
        } else if (orderType.equals(OrderType.SELL)) {
            return sellAsset(coin,quality,user);
        }
        throw new Exception("Invalid order type");
    }
}
