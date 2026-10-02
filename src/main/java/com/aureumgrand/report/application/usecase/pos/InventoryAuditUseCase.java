package com.aureumgrand.report.application.usecase.pos;

import com.aureumgrand.report.application.dto.pos.InventoryDto;
import com.aureumgrand.report.domain.model.ReportFormat;
import com.aureumgrand.report.domain.model.ReportResult;
import com.aureumgrand.report.domain.port.ReportGenerator;
import com.aureumgrand.report.infrastructure.jasper.datasource.JRBeanDataSourceFactory;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.InventoryDataSource;
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
public class InventoryAuditUseCase {
    private static final Logger log = LoggerFactory.getLogger(InventoryAuditUseCase.class);
    private final ReportGenerator reportGenerator;
    private final JRBeanDataSourceFactory beanDataSourceFactory;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public ReportResult execute(InventoryDto command) {
        if (command.getReportName() == null || command.getReportName().isBlank()) {
            command.setReportName("InventoryAuditReport");
        }
        log.info("Executing Inventory Audit report generation for template: '{}', format: '{}'",
                command.getReportName(), command.getFormat());
        ReportFormat format = command.resolveFormat();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ShopName", command.getShopName() != null && !command.getShopName().isBlank()
                ? command.getShopName()
                : "Aureum Grand Hotel & Luxury Suites");
        parameters.put("ShopImage", command.getShopImage() != null ? command.getShopImage() : "");
        parameters.put("ReportTitle", "INVENTORY AUDIT & STOCK COUNT REPORT");
        parameters.put("Warehouse", command.getWarehouse() != null && !command.getWarehouse().isBlank()
                ? command.getWarehouse()
                : "Main Store / All Warehouses");
        parameters.put("Auditor", command.getAuditor() != null && !command.getAuditor().isBlank()
                ? command.getAuditor()
                : "Internal Audit Team");
        parameters.put("PrintDate", command.getPrintDate() != null
                ? command.getPrintDate().format(DATE_TIME_FORMATTER)
                : LocalDateTime.now().format(DATE_TIME_FORMATTER));
        parameters.put("CurrencySymbol", command.getCurrencySymbol() != null && !command.getCurrencySymbol().isBlank()
                ? command.getCurrencySymbol()
                : "$");
        parameters.put("IsShowCost", command.isShowCost());
        parameters.put("IsShowSalePrice", command.isShowSalePrice());

        // Prepare line items
        List<InventoryDataSource> items = command.getItems() != null
                ? new ArrayList<>(command.getItems())
                : new ArrayList<>();
        for (InventoryDataSource item : items) {
            prepareItem(item);
        }
        if (!items.isEmpty()) {
            items.sort(Comparator.comparing(
                    item -> item.getCategory() != null ? item.getCategory() : ""
            ));
        }

        JRDataSource dataSource = !items.isEmpty()
                ? beanDataSourceFactory.create(items)
                : beanDataSourceFactory.createEmpty();

        ReportResult result = reportGenerator.generate(command.getReportName(), parameters, dataSource, format);
        log.info("Inventory Audit Report '{}' generated successfully. Size: {} bytes", result.getFileName(), result.getSize());
        return result;
    }

    private void prepareItem(InventoryDataSource item) {
        BigDecimal cost = item.getCostPrice() != null ? item.getCostPrice() : BigDecimal.ZERO;
        BigDecimal sale = item.getSalePrice() != null ? item.getSalePrice() : BigDecimal.ZERO;
        int qty = item.getStock();

        if (item.getTotalCost() == null || item.getTotalCost().compareTo(BigDecimal.ZERO) == 0) {
            item.setTotalCost(cost.multiply(BigDecimal.valueOf(qty)));
        }
        if (item.getTotalSale() == null || item.getTotalSale().compareTo(BigDecimal.ZERO) == 0) {
            item.setTotalSale(sale.multiply(BigDecimal.valueOf(qty)));
        }
    }
}
