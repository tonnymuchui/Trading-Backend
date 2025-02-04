package com.trading.service;

import com.trading.exception.WalletException;
import com.trading.modal.Order;
import com.trading.modal.User;
import com.trading.modal.Wallet;

import java.math.BigDecimal;

public interface WalletService {


    Wallet getUserWallet(User user) throws WalletException;

    public Wallet addBalanceToWallet(Wallet wallet, Long money) throws Exception;

    public Wallet findWalletById(Long id) throws WalletException;

    public Wallet walletToWalletTransfer(User sender,Wallet receiverWallet, Long amount) throws WalletException;

    public Wallet payOrderPayment(Order order, User user) throws WalletException;



}
