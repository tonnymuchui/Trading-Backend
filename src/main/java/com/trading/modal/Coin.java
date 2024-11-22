package com.trading.modal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Coin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private String name;
    private String image;

    @Column(name = "current_price", precision = 19, scale = 4)
    private double currentPrice;

    @Column(name = "market_cap", precision = 19, scale = 2)
    private BigDecimal marketCap;

    private Integer marketCapRank;

    @Column(name = "fully_diluted_valuation", precision = 19, scale = 2)
    private BigDecimal fullyDilutedValuation;

    @Column(name = "total_volume", precision = 19, scale = 2)
    private BigDecimal totalVolume;

    @Column(name = "high_24h", precision = 19, scale = 4)
    private BigDecimal high24h;

    @Column(name = "low_24h", precision = 19, scale = 4)
    private BigDecimal low24h;

    @Column(name = "price_change_24h", precision = 19, scale = 4)
    private BigDecimal priceChange24h;

    @Column(name = "price_change_percentage_24h", precision = 5, scale = 2)
    private BigDecimal priceChangePercentage24h;

    @Column(name = "market_cap_change_24h", precision = 19, scale = 2)
    private BigDecimal marketCapChange24h;

    @Column(name = "market_cap_change_percentage_24h", precision = 5, scale = 2)
    private BigDecimal marketCapChangePercentage24h;

    @Column(name = "circulating_supply", precision = 19, scale = 4)
    private BigDecimal circulatingSupply;

    @Column(name = "total_supply", precision = 19, scale = 4)
    private BigDecimal totalSupply;

    @Column(name = "max_supply", precision = 19, scale = 4)
    private BigDecimal maxSupply;

    @Column(name = "ath", precision = 19, scale = 4)
    private BigDecimal ath;

    @Column(name = "ath_change_percentage", precision = 5, scale = 2)
    private BigDecimal athChangePercentage;

    private LocalDateTime athDate;

    @Column(name = "atl", precision = 19, scale = 4)
    private BigDecimal atl;

    @Column(name = "atl_change_percentage", precision = 5, scale = 2)
    private BigDecimal atlChangePercentage;

    private LocalDateTime atlDate;
    private String roi;
    private LocalDateTime lastUpdated;
}
