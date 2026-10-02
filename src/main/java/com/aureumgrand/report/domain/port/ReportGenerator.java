package com.aureumgrand.report.domain.port;

import com.aureumgrand.report.domain.model.ReportFormat;
import com.aureumgrand.report.domain.model.ReportResult;
import net.sf.jasperreports.engine.JRDataSource;

import java.util.Map;

public interface ReportGenerator {
    ReportResult generate(String templateName, Map<String, Object> parameters,
                          JRDataSource dataSource, ReportFormat format);
}
