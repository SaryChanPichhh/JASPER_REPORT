package com.aureumgrand.report.application.usecase.pos;

import com.aureumgrand.report.application.dto.pos.PosPurchaseOrderDto;
import com.aureumgrand.report.domain.model.ReportFormat;
import com.aureumgrand.report.domain.model.ReportResult;
import com.aureumgrand.report.domain.port.ReportGenerator;
import com.aureumgrand.report.infrastructure.jasper.datasource.JRBeanDataSourceFactory;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.PurchaseOrderDataSource;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class PosPurchaseOrderUseCase {
    private static final Logger log = LoggerFactory.getLogger(PosPurchaseOrderUseCase.class);
    private final ReportGenerator reportGenerator;
    private final JRBeanDataSourceFactory beanDataSourceFactory;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ReportResult execute(PosPurchaseOrderDto command) {
        if (command.getReportName() == null || command.getReportName().isBlank()) {
            command.setReportName("PosPurchaseOrderReport");
        }
        log.info("Executing Purchase Order report generation for template: '{}', format: '{}'",
                command.getReportName(), command.getFormat());
        ReportFormat format = command.resolveFormat();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ShopName", command.getShopName() != null && !command.getShopName().isBlank()
                ? command.getShopName()
                : "Aureum Grand Hotel & Luxury Suites");
        parameters.put("ShopImage", command.getShopImage() != null ? command.getShopImage() : "");
        parameters.put("PoCode", command.getCode() != null && !command.getCode().isBlank()
                ? command.getCode()
                : "PO-" + System.currentTimeMillis());
        parameters.put("Supplier", command.getSupplier() != null && !command.getSupplier().isBlank()
                ? command.getSupplier()
                : "N/A");
        parameters.put("Warehouse", command.getWarehouse() != null && !command.getWarehouse().isBlank()
                ? command.getWarehouse()
                : "Main Warehouse");
        parameters.put("ReceiveBy", command.getReceiveBy() != null ? command.getReceiveBy() : "");
        parameters.put("ReceiveDate", command.getReceiveDate() != null
                ? command.getReceiveDate().format(DATE_TIME_FORMATTER)
                : "");
        parameters.put("Checker", command.getChecker() != null ? command.getChecker() : "");
        parameters.put("CheckDate", command.getCheckDate() != null
                ? command.getCheckDate().format(DATE_TIME_FORMATTER)
                : "");
        parameters.put("StartDate", command.getStartDate() != null
                ? command.getStartDate().format(DATE_FORMATTER)
                : "");
        parameters.put("EndDate", command.getEndDate() != null
                ? command.getEndDate().format(DATE_FORMATTER)
                : "");
        parameters.put("PrintDate", command.getPrintDate() != null
                ? command.getPrintDate().format(DATE_TIME_FORMATTER)
                : LocalDateTime.now().format(DATE_TIME_FORMATTER));
        parameters.put("Description", command.getDescription() != null ? command.getDescription() : "");
        parameters.put("Note", command.getNote() != null ? command.getNote() : "");
        parameters.put("CurrencySymbol", command.getCurrencySymbol() != null && !command.getCurrencySymbol().isBlank()
                ? command.getCurrencySymbol()
                : (command.getExchangeSign() != null && !command.getExchangeSign().isBlank() ? command.getExchangeSign() : "$"));
        parameters.put("ReportTitle", "PURCHASE ORDER");
        parameters.put("Field1", command.getField1() != null ? command.getField1() : "");
        parameters.put("Field2", command.getField2() != null ? command.getField2() : "");
        parameters.put("Field3", command.getField3() != null ? command.getField3() : "");

        List<PurchaseOrderDataSource> items = command.getItems() != null ? command.getItems() : List.of();
        for (PurchaseOrderDataSource item : items) {
            if (item.getTotalCost() == null || item.getTotalCost().compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal cost = item.getCost() != null ? item.getCost() : BigDecimal.ZERO;
                item.setTotalCost(cost.multiply(BigDecimal.valueOf(item.getQty())));
            }
        }

        JRDataSource dataSource = items.isEmpty()
                ? beanDataSourceFactory.createEmpty()
                : beanDataSourceFactory.create(items);

        ReportResult result = reportGenerator.generate(command.getReportName(), parameters, dataSource, format);
        log.info("Purchase Order Report '{}' generated successfully. Size: {} bytes", result.getFileName(), result.getSize());
        return result;
    }
}
