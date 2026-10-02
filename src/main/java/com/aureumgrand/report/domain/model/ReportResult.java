package com.aureumgrand.report.domain.model;

public class ReportResult {
    private final byte[] content;
    private final String fileName;
    private final ReportFormat format;
    private final String contentType;
    private final long size;

    public ReportResult(byte[] content, String fileName, ReportFormat format) {
        this.content = content != null ? content : new byte[0];
        this.fileName = fileName;
        this.format = format;
        this.contentType = format != null ? format.getContentType() : "application/octet-stream";
        this.size = this.content.length;
    }

    public byte[] getContent() {
        return content;
    }

    public String getFileName() {
        return fileName;
    }

    public ReportFormat getFormat() {
        return format;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }
}
