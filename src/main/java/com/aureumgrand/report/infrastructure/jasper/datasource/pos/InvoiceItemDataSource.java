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
public class InvoiceItemDataSource {
    private String transRef;
    private String invoicDate;
    private String itemCode;
    private String itemDesc;
    private BigDecimal price1;
    private int qty;
    private BigDecimal finalPrice;
    private BigDecimal discountPrice;
    private BigDecimal total;
    private BigDecimal discountInvoice;

    public String getInvoiceDate() {
        return invoicDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoicDate = invoiceDate;
    }
}
