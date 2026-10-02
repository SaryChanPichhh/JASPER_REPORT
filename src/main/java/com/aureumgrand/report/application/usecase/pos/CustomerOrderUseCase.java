package com.aureumgrand.report.application.usecase.pos;

import com.aureumgrand.report.application.dto.pos.InvoiceItemDto;
import com.aureumgrand.report.domain.model.ReportFormat;
import com.aureumgrand.report.domain.model.ReportResult;
import com.aureumgrand.report.domain.port.ReportGenerator;
import com.aureumgrand.report.infrastructure.jasper.datasource.JRBeanDataSourceFactory;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.InvoiceItemDataSource;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CustomerOrderUseCase {
    private static final Logger log = LoggerFactory.getLogger(CustomerOrderUseCase.class);
    private final ReportGenerator reportGenerator;
    private final JRBeanDataSourceFactory beanDataSourceFactory;

    public ReportResult execute(InvoiceItemDto command) {
        if (command.getReportName() == null || command.getReportName().isBlank()) {
            command.setReportName("CustomerOrderReport");
        } else if ("CustomerOrder80".equalsIgnoreCase(command.getReportName())
                || "CustomerOrder-80".equalsIgnoreCase(command.getReportName())
                || "CustomerOrder80mm".equalsIgnoreCase(command.getReportName())
                || "CustomerOrder80mmReport".equalsIgnoreCase(command.getReportName())) {
            command.setReportName("CustomerOrder80Report");
        }
        log.info("Executing customer order report generation for template: '{}', format: '{}'",
                command.getReportName(), command.getFormat());
        ReportFormat format = command.resolveFormat();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("CustomerName", command.getCustomerName() != null && !command.getCustomerName().isBlank()
                ? command.getCustomerName()
                : "Valued Customer");
        parameters.put("Dates", command.getDates() != null ? command.getDates() : "");
        parameters.put("BranchName", command.getBranchName() != null ? command.getBranchName() : "Aureum Grand Hotel & Luxury Suites");
        parameters.put("CurrencySymbol", command.getCurrencySymbol() != null ? command.getCurrencySymbol() : "$");
        parameters.put("ReportTitle", "CUSTOMER ORDER REPORT");

        List<InvoiceItemDataSource> items = command.getItems() != null
                ? new ArrayList<>(command.getItems())
                : new ArrayList<>();
        for (InvoiceItemDataSource item : items) {
            BigDecimal price = item.getPrice1() != null ? item.getPrice1() : BigDecimal.ZERO;
            BigDecimal disc = item.getDiscountPrice() != null ? item.getDiscountPrice() : BigDecimal.ZERO;
            if (item.getFinalPrice() == null) {
                item.setFinalPrice(price.subtract(disc));
            }
            if (item.getTotal() == null) {
                item.setTotal(item.getFinalPrice().multiply(BigDecimal.valueOf(item.getQty())));
            }
        }

        if (!items.isEmpty()) {
            items.sort(Comparator.comparing(
                    item -> item.getTransRef() != null ? item.getTransRef() : ""
            ));
        }

        JRDataSource dataSource = items.isEmpty()
                ? beanDataSourceFactory.createEmpty()
                : beanDataSourceFactory.create(items);

        ReportResult result = reportGenerator.generate(command.getReportName(), parameters, dataSource, format);
        log.info("Customer Order Report '{}' generated successfully. Size: {} bytes", result.getFileName(), result.getSize());
        return result;
    }
}
