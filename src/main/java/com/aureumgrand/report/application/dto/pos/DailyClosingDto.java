package com.aureumgrand.report.application.dto.pos;

import com.aureumgrand.report.application.dto.GenerateReportCommand;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDataSource;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.ExpenseDataSource;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DailyClosingDto extends GenerateReportCommand {
    private String shopName;
    private String shopImage;
    private String seller;
    private String dates;
    private LocalDateTime printDate;
    private String duration;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private String totalPrice;
    private BigDecimal totalRiel;
    private BigDecimal totalDollar;
    private String expense;
    private String expenseRiel;
    private BigDecimal exchangeRate = BigDecimal.ZERO;
    private String vat = "";
    private String cashChange = "";
    private String currencySymbol = "$";
    private List<DailyClosingDataSource> items = new ArrayList<>();
    private List<PaymentDataSource> payments = new ArrayList<>();
    private List<ExpenseDataSource> expenses = new ArrayList<>();

    public DailyClosingDto() {
        if (getReportName() == null || getReportName().isBlank()) {
            setReportName("DailyClosingInventoryA4Report");
        }
    }
}
