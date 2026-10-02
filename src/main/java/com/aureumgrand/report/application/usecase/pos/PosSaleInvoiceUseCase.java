package com.aureumgrand.report.application.usecase.pos;

import com.aureumgrand.report.application.dto.GenerateReportCommand;
import com.aureumgrand.report.application.dto.pos.PosSaleInvoiceDto;
import com.aureumgrand.report.application.usecase.GenerateReportUseCase;
import com.aureumgrand.report.domain.model.ReportFormat;
import com.aureumgrand.report.domain.model.ReportResult;
import com.aureumgrand.report.domain.port.ReportGenerator;
import com.aureumgrand.report.infrastructure.jasper.datasource.JRBeanDataSourceFactory;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class PosSaleInvoiceUseCase {
    private static final Logger log = LoggerFactory.getLogger(GenerateReportUseCase.class);
    private final ReportGenerator reportGenerator;
    private final JRBeanDataSourceFactory beanDataSourceFactory;
    public ReportResult execute(PosSaleInvoiceDto command) {
        log.info("Executing report generation for template: '{}', format: '{}'",
                command.getReportName(), command.getFormat());
        ReportFormat format = command.resolveFormat();
        Map<String, Object> parameters = new HashMap<>();

        if (command.getItems() != null && !command.getItems().isEmpty()) {
            parameters.put("Items", beanDataSourceFactory.create(command.getItems()));
        } else {
            parameters.put("Items", beanDataSourceFactory.createEmpty());
        }

        JRDataSource dataSource = beanDataSourceFactory.create(java.util.List.of(command));
        ReportResult result = reportGenerator.generate(command.getReportName(), parameters, dataSource, format);
        log.info("Report '{}' generated successfully. Size: {} bytes", result.getFileName(), result.getSize());
        return result;
    }
}
