package com.trading.service;

import com.trading.domain.OrderType;
import com.trading.modal.Order;
import com.trading.modal.User;
import com.trading.modal.Wallet;
import com.trading.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WalletServiceImp implements WalletService {
    private final WalletRepository walletRepository;
    @Autowired
    public WalletServiceImp(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Wallet getUserWallet(User user) {
        Wallet wallet = walletRepository.findByUserId(user.getId());
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
        }
        return wallet;
    }

    @Override
    public Wallet addBalance(Wallet wallet, Long amount) {
        BigDecimal balance = wallet.getBalance();
        BigDecimal newBalance = balance.add(BigDecimal.valueOf(amount));
        wallet.setBalance(newBalance);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findWalletById(Long id) throws Exception {
        Optional<Wallet> wallet = walletRepository.findById(id);
        if (wallet.isPresent()) {
            return wallet.get();
        }
        throw new Exception("Wallet Not Found");
    }

    @Override
    public Wallet walletToWalletTrasfer(User sender, Wallet receiverWallet, Long amount) throws Exception {
        Wallet senderWallet = getUserWallet(sender);
        if (senderWallet.getBalance().compareTo(BigDecimal.valueOf(amount))<0){
            throw new Exception("Insufficient Balance");
        }
        BigDecimal senderBalance = senderWallet.getBalance().subtract(BigDecimal.valueOf(amount));
        senderWallet.setBalance(senderBalance);
        walletRepository.save(senderWallet);
        BigDecimal receiverBalance = receiverWallet.getBalance().add(BigDecimal.valueOf(amount));
        receiverWallet.setBalance(receiverBalance);
        walletRepository.save(receiverWallet);
        return senderWallet;
    }

    @Override
    public Wallet payOrderPayment(Order order, User user) throws Exception {
        Wallet senderWallet = getUserWallet(user);
        if (order.getOrderType().equals(OrderType.BUY)){
            BigDecimal newBalance = senderWallet.getBalance().subtract(order.getPrice());
            if (newBalance.compareTo(order.getPrice())<0){
                throw new Exception("Insufficient funds");
            }
            senderWallet.setBalance(newBalance);
        }
        else {
            BigDecimal newBalance = senderWallet.getBalance().add(order.getPrice());
            senderWallet.setBalance(newBalance);
        }
        walletRepository.save(senderWallet);
        return senderWallet;
    }
}
