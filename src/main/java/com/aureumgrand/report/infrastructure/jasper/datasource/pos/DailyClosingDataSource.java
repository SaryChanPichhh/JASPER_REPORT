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
public class DailyClosingDataSource {
    private String itemCode;
    private String itemDesc;
    private int qty;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private BigDecimal finalPrice;
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;
    @Builder.Default
    private String categoryCode = "";
    @Builder.Default
    private String categoryDesc = "";
    @Builder.Default
    private String seller = "";
}
