package com.aureumgrand.report.application.usecase.pos;

import com.aureumgrand.report.application.dto.pos.AdjustmentHistoryDto;
import com.aureumgrand.report.domain.model.ReportFormat;
import com.aureumgrand.report.domain.model.ReportResult;
import com.aureumgrand.report.domain.port.ReportGenerator;
import com.aureumgrand.report.infrastructure.jasper.datasource.JRBeanDataSourceFactory;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.AdjustmentHistoryDataSource;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AdjustmentHistoryUseCase {
    private static final Logger log = LoggerFactory.getLogger(AdjustmentHistoryUseCase.class);
    private final ReportGenerator reportGenerator;
    private final JRBeanDataSourceFactory beanDataSourceFactory;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public ReportResult execute(AdjustmentHistoryDto command) {
        if (command.getReportName() == null || command.getReportName().isBlank()) {
            command.setReportName("AdjustmentHistoryReport");
        }
        log.info("Executing Adjustment History report generation for template: '{}', format: '{}'",
                command.getReportName(), command.getFormat());
        ReportFormat format = command.resolveFormat();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ShopName", command.getShopName() != null && !command.getShopName().isBlank()
                ? command.getShopName()
                : "Aureum Grand Hotel & Luxury Suites");
        parameters.put("ShopImage", command.getShopImage() != null ? command.getShopImage() : "");
        parameters.put("ReportTitle", "INVENTORY ADJUSTMENT HISTORY REPORT");
        parameters.put("Warehouse", command.getWarehouse() != null && !command.getWarehouse().isBlank()
                ? command.getWarehouse()
                : "All Warehouses");
        parameters.put("AdjustedBy", command.getAdjustedBy() != null && !command.getAdjustedBy().isBlank()
                ? command.getAdjustedBy()
                : "All Personnel");
        parameters.put("StartDate", command.getStartDate() != null && !command.getStartDate().isBlank()
                ? command.getStartDate()
                : "All");
        parameters.put("EndDate", command.getEndDate() != null && !command.getEndDate().isBlank()
                ? command.getEndDate()
                : "All");
        parameters.put("PrintDate", command.getPrintDate() != null
                ? command.getPrintDate().format(DATE_TIME_FORMATTER)
                : LocalDateTime.now().format(DATE_TIME_FORMATTER));
        parameters.put("CurrencySymbol", command.getCurrencySymbol() != null && !command.getCurrencySymbol().isBlank()
                ? command.getCurrencySymbol()
                : "$");
        parameters.put("IsShowCost", command.isShowCost());

        List<AdjustmentHistoryDataSource> items = command.getItems() != null
                ? new ArrayList<>(command.getItems())
                : new ArrayList<>();
        for (AdjustmentHistoryDataSource item : items) {
            prepareItem(item);
        }
        if (!items.isEmpty()) {
            items.sort(Comparator.<AdjustmentHistoryDataSource, String>comparing(
                    item -> item.getWarehouse() != null && !item.getWarehouse().isBlank()
                            ? item.getWarehouse()
                            : (item.getWareCode() != null && !item.getWareCode().isBlank() ? item.getWareCode() : "Main Warehouse"),
                    String.CASE_INSENSITIVE_ORDER
            ));
        }

        JRDataSource dataSource = !items.isEmpty()
                ? beanDataSourceFactory.create(items)
                : beanDataSourceFactory.createEmpty();

        ReportResult result = reportGenerator.generate(command.getReportName(), parameters, dataSource, format);
        log.info("Adjustment History Report '{}' generated successfully. Size: {} bytes", result.getFileName(), result.getSize());
        return result;
    }

    private void prepareItem(AdjustmentHistoryDataSource item) {
        if (item.getTotal() == 0.0 && item.getCost() != 0.0 && item.getQuantity() != 0) {
            item.setTotal(item.getCost() * item.getQuantity());
        }
    }
}
