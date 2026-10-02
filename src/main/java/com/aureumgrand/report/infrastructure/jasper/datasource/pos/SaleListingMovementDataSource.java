package com.aureumgrand.report.infrastructure.jasper.datasource.pos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleListingMovementDataSource {
    private String itemCode;
    private String itemDesc;
    private int totalPurchaseQty;
    private BigDecimal totalPurchaseCost;
    private int totalSaleQty;
    private BigDecimal totalSaleCost;
    private int totalRemainQty;
    private BigDecimal totalRemainCost;
}
