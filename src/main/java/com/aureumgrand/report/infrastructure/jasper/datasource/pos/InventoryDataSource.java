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
public class InventoryDataSource {
    private String category;
    private String itemCode;
    private String itemBarcode;
    private String itemDesc;
    private int stock;
    private BigDecimal costPrice;
    private BigDecimal exchangeRate;
    private BigDecimal salePrice;
    @Builder.Default
    private BigDecimal totalCost = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal totalSale = BigDecimal.ZERO;
}
