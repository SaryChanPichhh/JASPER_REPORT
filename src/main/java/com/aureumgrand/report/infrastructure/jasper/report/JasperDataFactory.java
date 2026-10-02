package com.aureumgrand.report.infrastructure.jasper.report;

import com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleInvoiceDataSource;

import java.math.BigDecimal;
import java.util.List;

public class JasperDataFactory {
    public static List<SaleInvoiceDataSource> createBeanCollection() {

        return List.of(
                createItem(
                        "ITEM001",
                        "Coca Cola",
                        2,
                        "1.50",
                        "0",
                        "3.00"
                ),
                createItem(
                        "ITEM002",
                        "Pepsi",
                        3,
                        "1.25",
                        "0",
                        "3.75"
                )
        );
    }

    private static SaleInvoiceDataSource createItem(
            String itemCode,
            String itemDesc,
            int qty,
            String unitPrice,
            String discount,
            String totalPrice) {

        var item = new SaleInvoiceDataSource();

        item.setItemCode(itemCode);
        item.setItemDesc(itemDesc);
        item.setQty(qty);
        item.setUnitPrice(new BigDecimal(unitPrice));
        item.setDiscount(new BigDecimal(discount));
        item.setTotalPrice(new BigDecimal(totalPrice));

        return item;
    }
}
