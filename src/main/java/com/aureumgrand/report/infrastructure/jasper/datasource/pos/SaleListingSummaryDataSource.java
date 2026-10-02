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
public class SaleListingSummaryDataSource {
    private String date;
    private BigDecimal salePrice;
    private BigDecimal discount;
    private BigDecimal cost;
    @Builder.Default
    private BigDecimal deliveryFee = BigDecimal.ZERO;
    private BigDecimal expense;
    private BigDecimal netSale;
    private BigDecimal netProfit;

    public BigDecimal getNetSale() {
        if (netSale != null) {
            return netSale;
        }
        BigDecimal gross = salePrice != null ? salePrice : BigDecimal.ZERO;
        BigDecimal disc = discount != null ? discount : BigDecimal.ZERO;
        return gross.subtract(disc);
    }

    public BigDecimal getNetProfit() {
        if (netProfit != null) {
            return netProfit;
        }
        BigDecimal net = getNetSale();
        BigDecimal del = deliveryFee != null ? deliveryFee : BigDecimal.ZERO;
        BigDecimal c = cost != null ? cost : BigDecimal.ZERO;
        BigDecimal exp = expense != null ? expense : BigDecimal.ZERO;
        return net.add(del).subtract(c).subtract(exp);
    }
}
