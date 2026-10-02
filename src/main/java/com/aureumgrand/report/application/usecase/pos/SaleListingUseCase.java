package com.aureumgrand.report.application.usecase.pos;

import com.aureumgrand.report.application.dto.pos.SaleListingDto;
import com.aureumgrand.report.domain.model.ReportFormat;
import com.aureumgrand.report.domain.model.ReportResult;
import com.aureumgrand.report.domain.port.ReportGenerator;
import com.aureumgrand.report.infrastructure.jasper.datasource.JRBeanDataSourceFactory;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource;
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
public class SaleListingUseCase {
    private static final Logger log = LoggerFactory.getLogger(SaleListingUseCase.class);
    private final ReportGenerator reportGenerator;
    private final JRBeanDataSourceFactory beanDataSourceFactory;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ReportResult execute(SaleListingDto command) {
        if (command.getReportName() == null || command.getReportName().isBlank()) {
            command.setReportName("SaleListingReport");
        } else if ("pos/salelistingbytransref".equalsIgnoreCase(command.getReportName())
                || "pos/salelisting-by-transref".equalsIgnoreCase(command.getReportName())
                || "pos/salelistinggroupbytransref".equalsIgnoreCase(command.getReportName())
                || "salelistingbytransref".equalsIgnoreCase(command.getReportName())
                || "salelistinggroupbytransref".equalsIgnoreCase(command.getReportName())) {
            command.setReportName("SaleListingByTransRefReport");
        } else if ("pos/salelistingbyseller".equalsIgnoreCase(command.getReportName())
                || "pos/salelisting-by-seller".equalsIgnoreCase(command.getReportName())
                || "pos/salelistinggroupbyseller".equalsIgnoreCase(command.getReportName())
                || "salelistingbyseller".equalsIgnoreCase(command.getReportName())
                || "salelistinggroupbyseller".equalsIgnoreCase(command.getReportName())
                || "SaleListingGroupBySellerReport".equalsIgnoreCase(command.getReportName())) {
            command.setReportName("SaleListingBySellerReport");
        }
        log.info("Executing Sale Listing report generation for template: '{}', format: '{}'",
                command.getReportName(), command.getFormat());
        ReportFormat format = command.resolveFormat();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ShopName", command.getShopName() != null && !command.getShopName().isBlank()
                ? command.getShopName()
                : "Aureum Grand Hotel & Luxury Suites");
        parameters.put("ShopImage", command.getShopImage() != null ? command.getShopImage() : "");
        parameters.put("StartDate", command.getStartDate() != null
                ? command.getStartDate().format(DATE_FORMATTER)
                : "");
        parameters.put("EndDate", command.getEndDate() != null
                ? command.getEndDate().format(DATE_FORMATTER)
                : "");
        parameters.put("PrintDate", command.getPrintDate() != null
                ? command.getPrintDate().format(DATE_TIME_FORMATTER)
                : LocalDateTime.now().format(DATE_TIME_FORMATTER));
        parameters.put("CurrencySymbol", command.getCurrencySymbol() != null && !command.getCurrencySymbol().isBlank()
                ? command.getCurrencySymbol()
                : "$");
        String reportTitle = "SALES TRANSACTION LISTING REPORT";
        if (command.getReportName().contains("TransRef")) {
            reportTitle = "SALES TRANSACTION LISTING REPORT (GROUPED BY TRANS REF)";
        } else if (command.getReportName().contains("Seller")) {
            reportTitle = "SALES TRANSACTION LISTING REPORT (GROUPED BY SELLER)";
        }
        parameters.put("ReportTitle", reportTitle);

        List<SaleListingDataSource> items = command.getItems() != null ? command.getItems() : List.of();
        for (SaleListingDataSource item : items) {
            if (item.getTotal() == null) {
                item.setTotal(BigDecimal.ZERO);
            }
            if (item.getCost() == null) {
                item.setCost(BigDecimal.ZERO);
            }
            if (item.getProfit() == null) {
                item.setProfit(item.getTotal().subtract(item.getCost()));
            }
        }

        JRDataSource dataSource = items.isEmpty()
                ? beanDataSourceFactory.createEmpty()
                : beanDataSourceFactory.create(items);

        ReportResult result = reportGenerator.generate(command.getReportName(), parameters, dataSource, format);
        log.info("Sale Listing Report '{}' generated successfully. Size: {} bytes", result.getFileName(), result.getSize());
        return result;
    }
}
