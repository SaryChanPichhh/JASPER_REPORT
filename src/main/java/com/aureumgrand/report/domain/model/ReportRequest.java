package com.aureumgrand.report.domain.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportRequest {
    private final String templateName;
    private final ReportFormat format;
    private final Map<String, Object> parameters;
    private final List<?> data;

    public ReportRequest(String templateName, ReportFormat format, Map<String, Object> parameters, List<?> data) {
        this.templateName = templateName;
        this.format = format != null ? format : ReportFormat.PDF;
        this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
        this.data = data != null ? List.copyOf(data) : Collections.emptyList();
    }

    public String getTemplateName() {
        return templateName;
    }

    public ReportFormat getFormat() {
        return format;
    }

    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public List<?> getData() {
        return data;
    }
}
