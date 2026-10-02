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
public class PosPoListing {
    private LocalDate receiveDate;
    @Builder.Default
    private String time = "";
    private String warehouse;
    private String supplierCode;
    private String supplierName;
    private String supplierNameKH;
    private String transRef;
    private String itemCode;
    private String itemDesc;
    private String itemDescKH;
    private int qty;
    private BigDecimal cost;
    private BigDecimal totalCost;
    @Builder.Default
    private BigDecimal exchangeRate = BigDecimal.ONE;

    public String getFormattedReceiveDate() {
        return receiveDate != null ? receiveDate.toString() : "";
    }
}
