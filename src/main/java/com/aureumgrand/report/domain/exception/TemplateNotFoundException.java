package com.aureumgrand.report.domain.exception;

public class TemplateNotFoundException extends RuntimeException {
    public TemplateNotFoundException(String message) {
        super(message);
    }
    public TemplateNotFoundException(String templateName, String details) {
        super("Report template '" + templateName + "' not found. " + details);
    }
}
