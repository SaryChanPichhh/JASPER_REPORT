package com.aureumgrand.report.application.usecase;

import com.aureumgrand.report.application.dto.GenerateReportCommand;
import com.aureumgrand.report.domain.model.ReportFormat;
import com.aureumgrand.report.domain.model.ReportResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PreviewReportUseCase {

    private static final Logger log = LoggerFactory.getLogger(PreviewReportUseCase.class);

    private final GenerateReportUseCase generateReportUseCase;

    public PreviewReportUseCase(GenerateReportUseCase generateReportUseCase) {
        this.generateReportUseCase = generateReportUseCase;
    }

    public ReportResult execute(GenerateReportCommand command) {
        log.info("Executing preview report for template: '{}'", command.getReportName());
        // Force PDF format for inline browser preview
        command.setFormat(ReportFormat.PDF.name());
        return generateReportUseCase.execute(command);
    }
}
