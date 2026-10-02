package com.aureumgrand.report.infrastructure.jasper.datasource.pos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDataSource {
    private String supplier;
    private String invoiceNumber;
    private String category;
    private LocalDate invoiceDate;
    private String itemCode;
    private String itemDesc;
    private String itemImage;
    private byte[] imageByte;
    private int qty;
    private BigDecimal cost;
    private BigDecimal totalCost;
    @Builder.Default
    private BigDecimal exchangeRate = BigDecimal.ZERO;

    public BigDecimal getTotalCost() {
        if (totalCost != null) {
            return totalCost;
        }
        if (cost != null) {
            return cost.multiply(BigDecimal.valueOf(qty));
        }
        return BigDecimal.ZERO;
    }

    public String getFormattedInvoiceDate() {
        return invoiceDate != null ? invoiceDate.toString() : "";
    }
}
