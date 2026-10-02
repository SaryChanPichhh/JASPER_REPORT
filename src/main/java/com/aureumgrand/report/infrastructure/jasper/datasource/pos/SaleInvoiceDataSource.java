package com.aureumgrand.report.infrastructure.jasper.datasource.pos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class SaleInvoiceDataSource {
    private String itemCode;
    private String itemDesc;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal totalPrice;

    // Backward compatibility getters for uppercase property names if needed
    public String getItemCode() { return itemCode; }
    public String getItemDesc() { return itemDesc; }
    public Integer getQty() { return qty; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getTotalPrice() { return totalPrice; }
}

