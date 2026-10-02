package com.aureumgrand.report.application.usecase;

import com.aureumgrand.report.application.dto.GenerateReportCommand;
import com.aureumgrand.report.domain.model.ReportFormat;
import com.aureumgrand.report.domain.model.ReportResult;
import com.aureumgrand.report.domain.port.ReportGenerator;
import com.aureumgrand.report.infrastructure.jasper.datasource.JRBeanDataSourceFactory;
import net.sf.jasperreports.engine.JRDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class GenerateReportUseCase {
    private static final Logger log = LoggerFactory.getLogger(GenerateReportUseCase.class);
    private final ReportGenerator reportGenerator;
    private final JRBeanDataSourceFactory beanDataSourceFactory;
    public GenerateReportUseCase(ReportGenerator reportGenerator, JRBeanDataSourceFactory beanDataSourceFactory) {
        this.reportGenerator = reportGenerator;
        this.beanDataSourceFactory = beanDataSourceFactory;
    }

    public ReportResult execute(GenerateReportCommand command) {
        log.info("Executing report generation for template: '{}', format: '{}'",
                command.getReportName(), command.getFormat());

        ReportFormat format = command.resolveFormat();
        Map<String, Object> parameters = new HashMap<>();

        JRDataSource dataSource = beanDataSourceFactory.create(new ArrayList<>()); ;
        ReportResult result = reportGenerator.generate(command.getReportName(), parameters, dataSource, format);

        log.info("Report '{}' generated successfully. Size: {} bytes", result.getFileName(), result.getSize());
        return result;
    }
}
