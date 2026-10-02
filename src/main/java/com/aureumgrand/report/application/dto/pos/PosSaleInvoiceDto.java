package com.aureumgrand.report.application.dto.pos;

import com.aureumgrand.report.application.dto.GenerateReportCommand;
import com.aureumgrand.report.domain.model.ReportMode;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentReceivedDataSource;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleInvoiceDataSource;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PosSaleInvoiceDto extends GenerateReportCommand {
    private String shopName;
    private String shopImage;
    private String customerName;
    private String transRef;
    private String transDate;
    private String note;
    private String subTotal;
    private String discountInvoice;
    private String transValue;
    private String exchangeRate;
    private String vat;
    private String transValueKH;
    private BigDecimal deliveryFee;
    private String field1 = "";
    private String field2 = "";
    private List<SaleInvoiceDataSource> items;
    private ReportMode reportMode = ReportMode.NORMAL;
    private PaymentReceivedDataSource amountReceive = new PaymentReceivedDataSource();
    // Printing preset
    private boolean showCashChange = false;
    private boolean showRowNum = false;
    private boolean showSubTotal = false;
    private boolean showDelivery = false;

}
