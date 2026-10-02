package com.aureumgrand.report.infrastructure.jasper.datasource.pos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleListingDataSource {
    private String transRef;
    private LocalDateTime transDate;
    private String formattedTransDate;
    public LocalTime getTransHour() {
        return transDate != null ? transDate.toLocalTime() : null;
    }
    public LocalDate getTransDay() {
        return transDate != null ? transDate.toLocalDate() : null;
    }
    public String getFormattedTransDate() {
        if (formattedTransDate != null && !formattedTransDate.isBlank()) {
            return formattedTransDate;
        }
        return transDate != null ? transDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) : "";
    }
    private String seller;
    private String customerName;
    private BigDecimal total;
    private BigDecimal cost;
    private BigDecimal profit;
    @Builder.Default
    private BigDecimal deliveryFee = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal tips = BigDecimal.ZERO;
    @Builder.Default
    private String paymentStatus = "";
    public BigDecimal getProfit() {
        if (profit != null) {
            return profit;
        }
        if (total != null && cost != null) {
            return total.subtract(cost);
        }
        return total != null ? total : BigDecimal.ZERO;
    }
}
