package com.aureumgrand.report.application.usecase;

import com.aureumgrand.report.application.dto.GenerateReportCommand;
import com.aureumgrand.report.domain.model.ReportResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ScheduleReportUseCase {

    private static final Logger log = LoggerFactory.getLogger(ScheduleReportUseCase.class);

    private final GenerateReportUseCase generateReportUseCase;

    public ScheduleReportUseCase(GenerateReportUseCase generateReportUseCase) {
        this.generateReportUseCase = generateReportUseCase;
    }

    @Async("reportTaskExecutor")
    public CompletableFuture<ReportResult> executeAsync(String jobId, GenerateReportCommand command) {
        log.info("Starting background async report job [{}] for template '{}'", jobId, command.getReportName());
        try {
            ReportResult result = generateReportUseCase.execute(command);
            log.info("Background async report job [{}] completed successfully ({} bytes)", jobId, result.getSize());
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Background async report job [{}] failed: {}", jobId, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public String submit(GenerateReportCommand command) {
        String jobId = UUID.randomUUID().toString();
        log.info("Enqueued async report generation with Job ID: {}", jobId);
        executeAsync(jobId, command);
        return jobId;
    }
}
