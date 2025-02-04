package com.trading.repository;

import com.trading.modal.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AssetsRepository extends JpaRepository<Asset,Long> {
    public List<Asset> findByUserId(Long userId);

    Asset findByUserIdAndCoinId(Long userId, String coinId);

    Asset findByIdAndUserId(Long assetId, Long userId);

//    Optional<Asset> findByUserIdAndCoin_SymbolAndPortfolioId(Long userId, String symbol, Long portfolioId);
}