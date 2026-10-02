package com.aureumgrand.report.infrastructure.jasper;

import com.aureumgrand.report.domain.exception.ReportGenerationException;
import com.aureumgrand.report.domain.exception.TemplateNotFoundException;
import com.aureumgrand.report.domain.model.ReportFormat;
import com.aureumgrand.report.domain.model.ReportResult;
import com.aureumgrand.report.domain.port.ReportGenerator;
import com.aureumgrand.report.infrastructure.jasper.exporter.CsvExporter;
import com.aureumgrand.report.infrastructure.jasper.exporter.DocxExporter;
import com.aureumgrand.report.infrastructure.jasper.exporter.PdfExporter;
import com.aureumgrand.report.infrastructure.jasper.exporter.XlsxExporter;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class JasperReportGenerator implements ReportGenerator {
    private static final Logger log = LoggerFactory.getLogger(JasperReportGenerator.class);
    private final JasperTemplateLoader templateLoader;
    private final PdfExporter pdfExporter;
    private final XlsxExporter xlsxExporter;
    private final DocxExporter docxExporter;
    private final CsvExporter csvExporter;
    @Override
    public ReportResult generate(String templateName, Map<String, Object> parameters,
                                 JRDataSource dataSource, ReportFormat format) {
        if (format == null) {
            format = ReportFormat.PDF;
        }
        try {
            log.debug("Loading JasperReport template: {}", templateName);
            JasperReport jasperReport = templateLoader.loadTemplate(templateName);

            Map<String, Object> fillParameters = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
            JRDataSource effectiveDataSource = dataSource != null ? dataSource : new JREmptyDataSource();

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, fillParameters, effectiveDataSource);

            // Auto-trim page height for continuous roll paper / POS receipts
            if (jasperReport.isIgnorePagination() && !jasperPrint.getPages().isEmpty()) {
                net.sf.jasperreports.engine.JRPrintPage page = jasperPrint.getPages().get(0);
                int maxBottom = 0;
                for (net.sf.jasperreports.engine.JRPrintElement element : page.getElements()) {
                    int bottom = element.getY() + element.getHeight();
                    if (bottom > maxBottom) {
                        maxBottom = bottom;
                    }
                }
                if (maxBottom > 0) {
                    jasperPrint.setPageHeight(maxBottom + 20);
                }
            }

            byte[] exportedContent = exportReport(jasperPrint, format);

            String baseFileName = extractBaseFileName(templateName);
            String fileName = baseFileName + format.getFileExtension();

            return new ReportResult(exportedContent, fileName, format);

        } catch (ReportGenerationException | TemplateNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to generate report '{}' in format {}: {}", templateName, format, e.getMessage(), e);
            throw new ReportGenerationException("Failed to generate report for template '" + templateName + "': " + e.getMessage(), e);
        }
    }

    private byte[] exportReport(JasperPrint jasperPrint, ReportFormat format) throws Exception {
        return switch (format) {
            case PDF -> pdfExporter.export(jasperPrint);
            case XLSX -> xlsxExporter.export(jasperPrint);
            case DOCX -> docxExporter.export(jasperPrint);
            case CSV -> csvExporter.export(jasperPrint);
        };
    }

    private String extractBaseFileName(String templateName) {
        String name = templateName.replace("\\", "/");
        if (name.contains("/")) {
            name = name.substring(name.lastIndexOf('/') + 1);
        }
        return name.replaceAll("\\.(jrxml|jasper)$", "");
    }
}
