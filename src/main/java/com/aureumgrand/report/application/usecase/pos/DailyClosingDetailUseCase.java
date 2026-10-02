package com.aureumgrand.report.application.usecase.pos;

import com.aureumgrand.report.application.dto.pos.DailyClosingDetailDto;
import com.aureumgrand.report.domain.model.ReportFormat;
import com.aureumgrand.report.domain.model.ReportResult;
import com.aureumgrand.report.domain.port.ReportGenerator;
import com.aureumgrand.report.infrastructure.jasper.datasource.JRBeanDataSourceFactory;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDetailDataSource;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class DailyClosingDetailUseCase {
    private static final Logger log = LoggerFactory.getLogger(DailyClosingDetailUseCase.class);
    private final ReportGenerator reportGenerator;
    private final JRBeanDataSourceFactory beanDataSourceFactory;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public ReportResult execute(DailyClosingDetailDto command) {
        if (command.getReportName() == null || command.getReportName().isBlank()
                || "DailyClosingDetailReport".equalsIgnoreCase(command.getReportName())) {
            command.setReportName("DailyClosingInventoryDetailA4Report");
        }
        log.info("Executing Daily Closing Detail report generation for template: '{}', format: '{}'",
                command.getReportName(), command.getFormat());
        ReportFormat format = command.resolveFormat();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ShopName", command.getShopName() != null && !command.getShopName().isBlank()
                ? command.getShopName()
                : "Aureum Grand Hotel & Luxury Suites");
        parameters.put("ShopImage", command.getShopImage() != null ? command.getShopImage() : "");
        parameters.put("PrintDate", command.getPrintDate() != null
                ? command.getPrintDate().format(DATE_TIME_FORMATTER)
                : LocalDateTime.now().format(DATE_TIME_FORMATTER));
        parameters.put("Duration", command.getDuration() != null && !command.getDuration().isBlank()
                ? command.getDuration()
                : "All Shifts");
        parameters.put("Subtotal", command.getSubtotal() != null && !command.getSubtotal().isBlank()
                ? command.getSubtotal()
                : "0.00");
        parameters.put("DiscountInvoice", command.getDiscountInvoice() != null
                ? command.getDiscountInvoice().toString()
                : "0.00");
        parameters.put("Vat", command.getVat() != null && !command.getVat().isBlank()
                ? command.getVat()
                : "0.00");
        parameters.put("Expense", command.getExpense() != null && !command.getExpense().isBlank()
                ? command.getExpense()
                : "0.00");
        parameters.put("ExpenseRiel", command.getExpenseRiel() != null && !command.getExpenseRiel().isBlank()
                ? command.getExpenseRiel()
                : "0");
        parameters.put("CashChange", command.getCashChange() != null && !command.getCashChange().isBlank()
                ? command.getCashChange()
                : "0.00");
        parameters.put("ExchangeRate", command.getExchangeRate() != null && !command.getExchangeRate().isBlank()
                ? command.getExchangeRate()
                : "4,100");
        parameters.put("TotalDollar", command.getTotalDollar() != null && !command.getTotalDollar().isBlank()
                ? command.getTotalDollar()
                : (command.getTotalAmountDollar() != null ? command.getTotalAmountDollar().toString() : "0.00"));
        parameters.put("TotalRiel", command.getTotalRiel() != null && !command.getTotalRiel().isBlank()
                ? command.getTotalRiel()
                : (command.getTotalAmountRiel() != null ? command.getTotalAmountRiel().toString() : "0"));
        parameters.put("CurrencySymbol", command.getCurrencySymbol() != null && !command.getCurrencySymbol().isBlank()
                ? command.getCurrencySymbol()
                : "$");
        if (command.getReportName() != null && command.getReportName().contains("ByCategory")) {
            parameters.put("ReportTitle", "DAILY CLOSING INVENTORY BY CATEGORY REPORT");
        } else {
            parameters.put("ReportTitle", "DAILY CLOSING DETAIL REPORT");
        }

        // Flatten items and collect payments
        List<ItemDataSource> flatItems = new ArrayList<>();
        List<PaymentDataSource> allPayments = new ArrayList<>();

        List<DailyClosingDetailDataSource> closings = command.getClosings() != null && !command.getClosings().isEmpty()
                ? command.getClosings()
                : (command.getDetails() != null && !command.getDetails().isEmpty() ? command.getDetails() : List.of());

        if (!closings.isEmpty()) {
            for (DailyClosingDetailDataSource closing : closings) {
                String seller = closing.getSeller() != null && !closing.getSeller().isBlank()
                        ? closing.getSeller()
                        : "Cashier";
                if (closing.getItems() != null) {
                    for (ItemDataSource item : closing.getItems()) {
                        if (item.getSeller() == null || item.getSeller().isBlank()) {
                            item.setSeller(seller);
                        }
                        prepareItem(item);
                        flatItems.add(item);
                    }
                }
                if (closing.getPayments() != null) {
                    allPayments.addAll(closing.getPayments());
                }
            }
        } else if (command.getItems() != null && !command.getItems().isEmpty()) {
            for (ItemDataSource item : command.getItems()) {
                if (item.getSeller() == null || item.getSeller().isBlank()) {
                    item.setSeller("Cashier");
                }
                prepareItem(item);
                flatItems.add(item);
            }
            if (command.getPayments() != null) {
                allPayments.addAll(command.getPayments());
            }
        }

        JRDataSource paymentDataSource = allPayments.isEmpty()
                ? beanDataSourceFactory.createEmpty()
                : beanDataSourceFactory.create(allPayments);
        parameters.put("PaymentDataSource", paymentDataSource);

        if (command.getReportName() != null && command.getReportName().contains("ByCategory")) {
            flatItems.sort(Comparator.comparing(
                    item -> item.getCategoryCode() != null ? String.valueOf(item.getCategoryCode()) : ""
            ));
        }

        JRDataSource dataSource = flatItems.isEmpty()
                ? beanDataSourceFactory.createEmpty()
                : beanDataSourceFactory.create(flatItems);

        ReportResult result = reportGenerator.generate(command.getReportName(), parameters, dataSource, format);
        log.info("Daily Closing Detail Report '{}' generated successfully. Size: {} bytes", result.getFileName(), result.getSize());
        return result;
    }

    private void prepareItem(ItemDataSource item) {
        BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
        BigDecimal disc = item.getDiscountPrice() != null ? item.getDiscountPrice() : BigDecimal.ZERO;
        if (item.getFinalPrice() == null) {
            item.setFinalPrice(price.subtract(disc));
        }
        if (item.getTotal() == null || item.getTotal().compareTo(BigDecimal.ZERO) == 0) {
            item.setTotal(item.getFinalPrice().multiply(BigDecimal.valueOf(item.getQty())));
        }
    }
}
