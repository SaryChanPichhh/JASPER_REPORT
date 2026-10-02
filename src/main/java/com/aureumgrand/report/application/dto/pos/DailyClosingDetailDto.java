package com.aureumgrand.report.application.dto.pos;

import com.aureumgrand.report.application.dto.GenerateReportCommand;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDetailDataSource;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DailyClosingDetailDto extends GenerateReportCommand {
    private String shopName;
    private String shopImage;
    private LocalDateTime printDate;
    private String expense = "";
    private String expenseRiel = "";
    private String exchangeRate = "";
    private String vat = "";
    private String cashChange = "";
    private String duration = "";
    private BigDecimal discountInvoice;
    private BigDecimal totalAmountRiel = BigDecimal.ZERO;
    private BigDecimal totalAmountDollar = BigDecimal.ZERO;
    private String subtotal = "";
    private String totalRiel = "";
    private String totalDollar = "";
    private boolean isFiltering = false;
    private List<DailyClosingDetailDataSource> dailyClosings = new ArrayList<>();
    private List<ItemDataSource> items = new ArrayList<>();
    private List<PaymentDataSource> payments = new ArrayList<>();

    public DailyClosingDetailDto() {
        if (getReportName() == null || getReportName().isBlank()) {
            setReportName("DailyClosingInventoryDetailA4Report");
        }
    }

    public List<DailyClosingDetailDataSource> getClosings() {
        return dailyClosings;
    }

    public void setClosings(List<DailyClosingDetailDataSource> closings) {
        this.dailyClosings = closings;
    }

    public List<DailyClosingDetailDataSource> getDetails() {
        return dailyClosings;
    }

    public void setDetails(List<DailyClosingDetailDataSource> details) {
        this.dailyClosings = details;
    }
}
