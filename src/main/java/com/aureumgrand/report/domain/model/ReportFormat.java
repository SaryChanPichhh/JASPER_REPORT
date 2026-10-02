package com.aureumgrand.report.domain.model;

public enum ReportFormat {
    PDF("application/pdf", ".pdf"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx"),
    CSV("text/csv", ".csv");

    private final String contentType;
    private final String fileExtension;

    ReportFormat(String contentType, String fileExtension) {
        this.contentType = contentType;
        this.fileExtension = fileExtension;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public static ReportFormat fromString(String format) {
        if (format == null || format.isBlank()) {
            return PDF;
        }
        try {
            return ReportFormat.valueOf(format.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PDF;
        }
    }
}
