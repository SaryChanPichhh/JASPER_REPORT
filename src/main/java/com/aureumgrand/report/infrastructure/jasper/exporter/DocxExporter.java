package com.aureumgrand.report.infrastructure.jasper.exporter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleDocxExporterConfiguration;
import net.sf.jasperreports.export.SimpleDocxReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class DocxExporter {

    public byte[] export(JasperPrint jasperPrint) throws JRException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimpleDocxReportConfiguration reportConfig = new SimpleDocxReportConfiguration();
        reportConfig.setFlexibleRowHeight(true);
        reportConfig.setFramesAsNestedTables(true);
        exporter.setConfiguration(reportConfig);

        SimpleDocxExporterConfiguration exportConfig = new SimpleDocxExporterConfiguration();
        exportConfig.setMetadataAuthor("Aureum Grand Hotel & Luxury Suites");
        exporter.setConfiguration(exportConfig);

        exporter.exportReport();
        return outputStream.toByteArray();
    }
}
