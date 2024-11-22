package com.trading.controller;

import com.trading.modal.Order;
import com.trading.modal.User;
import com.trading.modal.Wallet;
import com.trading.modal.WalletTransaction;
import com.trading.service.UserService;
import com.trading.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    private final WalletService walletService;
    private final UserService userService;

    @Autowired
    public WalletController(WalletService walletService, UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }

    @GetMapping("/api/wallet/")
    public ResponseEntity<Wallet> getUserWallet(@RequestHeader("Authorization") String token) {
        User user = userService.findUserByJwtToken(token);
        Wallet wallet = walletService.getUserWallet(user);
        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PostMapping("/api/wallet/{walletId}/transfer")
    private ResponseEntity<Wallet> walletToWalletTransfer(@RequestHeader("Authorization") String token,
                                                          @PathVariable Long walletId,
                                                          @RequestBody WalletTransaction walletTransaction
    ) throws Exception {
        User senderUser = userService.findUserByJwtToken(token);
        Wallet receiverWallet = walletService.findWalletById(walletId);
        Wallet wallet = walletService.walletToWalletTrasfer(senderUser, receiverWallet, walletTransaction.getAmount());
        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PostMapping("/api/wallet/order/{orderId}/pay")
    private ResponseEntity<Wallet> payOrderPayment(@RequestHeader("Authorization") String token,
                                                          @PathVariable Long orderId
    ) throws Exception {
        User user = userService.findUserByJwtToken(token);
        Order order = or
    }
}
