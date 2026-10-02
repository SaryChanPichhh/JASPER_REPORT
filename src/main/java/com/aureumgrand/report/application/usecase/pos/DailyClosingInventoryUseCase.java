package com.aureumgrand.report.application.usecase.pos;

import com.aureumgrand.report.application.dto.pos.DailyClosingDto;
import com.aureumgrand.report.domain.model.ReportFormat;
import com.aureumgrand.report.domain.model.ReportResult;
import com.aureumgrand.report.domain.port.ReportGenerator;
import com.aureumgrand.report.infrastructure.jasper.datasource.JRBeanDataSourceFactory;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDataSource;
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
public class DailyClosingInventoryUseCase {
    private static final Logger log = LoggerFactory.getLogger(DailyClosingInventoryUseCase.class);
    private final ReportGenerator reportGenerator;
    private final JRBeanDataSourceFactory beanDataSourceFactory;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public ReportResult execute(DailyClosingDto command) {
        if (command.getReportName() == null || command.getReportName().isBlank()) {
            command.setReportName("DailyClosingInventoryA4Report");
        } else if ("DailyClosingInventory80Report".equalsIgnoreCase(command.getReportName())
                || "DailyClosingInventory-80".equalsIgnoreCase(command.getReportName())
                || "DailyClosingInventory80".equalsIgnoreCase(command.getReportName())) {
            command.setReportName("DailyClosingInventory80mmReport");
        }
        log.info("Executing Daily Closing Inventory report generation for template: '{}', format: '{}'",
                command.getReportName(), command.getFormat());
        ReportFormat format = command.resolveFormat();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ShopName", command.getShopName() != null && !command.getShopName().isBlank()
                ? command.getShopName()
                : "Aureum Grand Hotel & Luxury Suites");
        parameters.put("ShopImage", command.getShopImage() != null ? command.getShopImage() : "");
        parameters.put("ReportTitle", "DAILY CLOSING INVENTORY REPORT");
        parameters.put("Seller", command.getSeller() != null && !command.getSeller().isBlank()
                ? command.getSeller()
                : "General Cashier");
        parameters.put("Dates", command.getDates() != null && !command.getDates().isBlank()
                ? command.getDates()
                : "");
        parameters.put("Duration", command.getDuration() != null && !command.getDuration().isBlank()
                ? command.getDuration()
                : "All Shifts");
        parameters.put("PrintDate", command.getPrintDate() != null
                ? command.getPrintDate().format(DATE_TIME_FORMATTER)
                : LocalDateTime.now().format(DATE_TIME_FORMATTER));

        parameters.put("Subtotal", command.getSubtotal() != null
                ? command.getSubtotal().toString()
                : "0.00");
        parameters.put("Discount", command.getDiscount() != null
                ? command.getDiscount().toString()
                : "0.00");
        parameters.put("DiscountInvoice", command.getDiscount() != null
                ? command.getDiscount().toString()
                : "0.00");
        parameters.put("TotalPrice", command.getTotalPrice() != null && !command.getTotalPrice().isBlank()
                ? command.getTotalPrice()
                : (command.getTotalDollar() != null ? command.getTotalDollar().toString() : "0.00"));
        parameters.put("TotalDollar", command.getTotalDollar() != null
                ? command.getTotalDollar().toString()
                : "0.00");
        parameters.put("TotalRiel", command.getTotalRiel() != null
                ? command.getTotalRiel().toString()
                : "0");
        parameters.put("Expense", command.getExpense() != null && !command.getExpense().isBlank()
                ? command.getExpense()
                : "0.00");
        parameters.put("ExpenseRiel", command.getExpenseRiel() != null && !command.getExpenseRiel().isBlank()
                ? command.getExpenseRiel()
                : "0");
        parameters.put("ExchangeRate", command.getExchangeRate() != null && command.getExchangeRate().compareTo(BigDecimal.ZERO) > 0
                ? command.getExchangeRate().toString()
                : "4,100");
        parameters.put("Vat", command.getVat() != null && !command.getVat().isBlank()
                ? command.getVat()
                : "0.00");
        parameters.put("CashChange", command.getCashChange() != null && !command.getCashChange().isBlank()
                ? command.getCashChange()
                : "0.00");
        parameters.put("CurrencySymbol", command.getCurrencySymbol() != null && !command.getCurrencySymbol().isBlank()
                ? command.getCurrencySymbol()
                : "$");

        List<DailyClosingDataSource> items = command.getItems() != null
                ? new ArrayList<>(command.getItems())
                : new ArrayList<>();

        if (command.getReportName() != null && command.getReportName().contains("ByCategory")) {
            parameters.put("ReportTitle", "DAILY CLOSING INVENTORY BY CATEGORY REPORT");
            if (!items.isEmpty()) {
                items.sort(Comparator.comparing(
                        item -> item.getCategoryCode() != null ? String.valueOf(item.getCategoryCode()) : ""
                ));
            }
        }

        // Subdatasources for payments and expenses
        JRDataSource paymentDataSource = command.getPayments() != null && !command.getPayments().isEmpty()
                ? beanDataSourceFactory.create(command.getPayments())
                : beanDataSourceFactory.createEmpty();
        parameters.put("PaymentDataSource", paymentDataSource);

        JRDataSource expenseDataSource = command.getExpenses() != null && !command.getExpenses().isEmpty()
                ? beanDataSourceFactory.create(command.getExpenses())
                : beanDataSourceFactory.createEmpty();
        parameters.put("ExpenseDataSource", expenseDataSource);

        // Prepare line items
        if (!items.isEmpty()) {
            String defaultSeller = command.getSeller() != null && !command.getSeller().isBlank()
                    ? command.getSeller()
                    : "General Cashier";
            for (DailyClosingDataSource item : items) {
                if (item.getSeller() == null || item.getSeller().isBlank()) {
                    item.setSeller(defaultSeller);
                }
                prepareItem(item);
            }
        }

        JRDataSource dataSource = !items.isEmpty()
                ? beanDataSourceFactory.create(items)
                : beanDataSourceFactory.createEmpty();

        ReportResult result = reportGenerator.generate(command.getReportName(), parameters, dataSource, format);
        log.info("Daily Closing Inventory Report '{}' generated successfully. Size: {} bytes", result.getFileName(), result.getSize());
        return result;
    }

    private void prepareItem(DailyClosingDataSource item) {
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
