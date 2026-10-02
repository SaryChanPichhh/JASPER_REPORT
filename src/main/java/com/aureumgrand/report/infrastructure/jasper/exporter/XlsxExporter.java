package com.aureumgrand.report.infrastructure.jasper.exporter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class XlsxExporter {

    public byte[] export(JasperPrint jasperPrint) throws JRException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
        reportConfig.setOnePagePerSheet(false);
        reportConfig.setRemoveEmptySpaceBetweenRows(true);
        reportConfig.setDetectCellType(true);
        reportConfig.setWhitePageBackground(false);
        exporter.setConfiguration(reportConfig);

        SimpleXlsxExporterConfiguration exportConfig = new SimpleXlsxExporterConfiguration();
        exportConfig.setMetadataAuthor("Aureum Grand Hotel & Luxury Suites");
        exporter.setConfiguration(exportConfig);

        exporter.exportReport();
        return outputStream.toByteArray();
    }
}
