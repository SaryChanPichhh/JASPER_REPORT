package com.aureumgrand.report.application.usecase.pos;

import com.aureumgrand.report.application.dto.pos.PosPoListingDto;
import com.aureumgrand.report.domain.model.ReportFormat;
import com.aureumgrand.report.domain.model.ReportResult;
import com.aureumgrand.report.domain.port.ReportGenerator;
import com.aureumgrand.report.infrastructure.jasper.datasource.JRBeanDataSourceFactory;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.PosPoListing;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.PosPoListingDataSource;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class PosPoListingUseCase {
    private static final Logger log = LoggerFactory.getLogger(PosPoListingUseCase.class);
    private final ReportGenerator reportGenerator;
    private final JRBeanDataSourceFactory beanDataSourceFactory;

    public ReportResult execute(PosPoListingDto command) {
        if (command.getReportName() == null || command.getReportName().isBlank()) {
            command.setReportName("PosPoListingReport");
        }
        log.info("Executing PO listing report generation for template: '{}', format: '{}'",
                command.getReportName(), command.getFormat());
        ReportFormat format = command.resolveFormat();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("StartDate", command.getStartDate() != null ? command.getStartDate().toString() : "");
        parameters.put("EndDate", command.getEndDate() != null ? command.getEndDate().toString() : "");
        parameters.put("BranchName", command.getBranchName() != null ? command.getBranchName() : "Aureum Grand Hotel & Luxury Suites");
        parameters.put("CurrencySymbol", command.getCurrencySymbol() != null ? command.getCurrencySymbol() : "$");
        parameters.put("ReportTitle", "PURCHASE ORDER LISTING REPORT");

        // Flatten orders into a linear list of PO items
        List<PosPoListing> flatItems = new ArrayList<>();
        if (command.getOrders() != null) {
            for (PosPoListingDataSource order : command.getOrders()) {
                if (order.getItems() != null) {
                    for (PosPoListing item : order.getItems()) {
                        if (item.getReceiveDate() == null) {
                            item.setReceiveDate(order.getReceiveDate());
                        }
                        if (item.getTotalCost() == null) {
                            BigDecimal cost = item.getCost() != null ? item.getCost() : BigDecimal.ZERO;
                            item.setTotalCost(cost.multiply(BigDecimal.valueOf(item.getQty())));
                        }
                        flatItems.add(item);
                    }
                }
            }
        }

        if (!flatItems.isEmpty()) {
            flatItems.sort((a, b) -> {
                if (a.getReceiveDate() == null && b.getReceiveDate() == null) return 0;
                if (a.getReceiveDate() == null) return 1;
                if (b.getReceiveDate() == null) return -1;
                return a.getReceiveDate().compareTo(b.getReceiveDate());
            });
        }

        JRDataSource dataSource = flatItems.isEmpty()
                ? beanDataSourceFactory.createEmpty()
                : beanDataSourceFactory.create(flatItems);

        ReportResult result = reportGenerator.generate(command.getReportName(), parameters, dataSource, format);
        log.info("PO Listing Report '{}' generated successfully. Size: {} bytes", result.getFileName(), result.getSize());
        return result;
    }
}
