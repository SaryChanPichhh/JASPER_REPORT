# JasperReports Project Structure (Spring Boot / Clean Architecture)

A production-ready structure for managing JasperReports in a Java/Spring Boot project, using Clean Architecture layering.

## Project Structure

```
report-service/
├── src/main/java/com/aureumgrand/report/
│   ├── domain/
│   │   ├── model/                  # Report request/response domain objects
│   │   │   ├── ReportRequest.java
│   │   │   ├── ReportFormat.java   # enum: PDF, XLSX, DOCX, CSV
│   │   │   └── ReportResult.java
│   │   └── port/                   # Interfaces (ports)
│   │       ├── ReportGenerator.java
│   │       └── ReportRepository.java   # if reports are DB-driven (dynamic templates)
│   │
│   ├── application/
│   │   ├── usecase/
│   │   │   ├── GenerateReportUseCase.java
│   │   │   ├── PreviewReportUseCase.java
│   │   │   └── ScheduleReportUseCase.java   # if async/batch reports
│   │   └── dto/
│   │       ├── GenerateReportCommand.java
│   │       └── ReportDataDto.java
│   │
│   ├── infrastructure/
│   │   ├── jasper/
│   │   │   ├── JasperReportGenerator.java   # implements ReportGenerator
│   │   │   ├── JasperTemplateLoader.java    # loads .jasper/.jrxml files
│   │   │   ├── datasource/
│   │   │   │   ├── JRBeanDataSourceFactory.java
│   │   │   │   └── JRJdbcDataSourceFactory.java
│   │   │   └── exporter/
│   │   │       ├── PdfExporter.java
│   │   │       ├── XlsxExporter.java
│   │   │       └── DocxExporter.java
│   │   ├── persistence/
│   │   │   └── ReportTemplateEntity.java   # if templates are DB-managed
│   │   └── config/
│   │       └── JasperConfig.java           # bean setup, cache config
│   │
│   ├── presentation/
│   │   └── rest/
│   │       ├── ReportController.java
│   │       └── advice/GlobalExceptionHandler.java
│   │
│   └── ReportServiceApplication.java
│
├── src/main/resources/
│   ├── reports/
│   │   ├── invoice/
│   │   │   ├── invoice_template.jrxml
│   │   │   └── invoice_template.jasper   # compiled (or compile at build time)
│   │   ├── sales-summary/
│   │   │   └── sales_summary.jrxml
│   │   └── subreports/
│   │       └── item_details_subreport.jrxml
│   ├── fonts/                       # Khmer font support (important for bilingual reports)
│   │   └── KhmerOS.ttf
│   └── application.yml
│
└── src/test/java/.../jasper/
    └── JasperReportGeneratorTest.java
```

## Key Design Decisions

### 1. Keep `.jrxml` as source of truth, compile at build time

Use the `jasperreports-maven-plugin` (or Gradle equivalent) to auto-compile `.jrxml` → `.jasper` during build, rather than committing compiled files or compiling at runtime (slow, fragile).

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>jasperreports-plugin</artifactId>
    <version>2.3</version>
    <configuration>
        <sourceDirectory>src/main/resources/reports</sourceDirectory>
        <outputDirectory>target/classes/reports</outputDirectory>
    </configuration>
</plugin>
```

### 2. Separate "template" from "data source" from "exporter"

This is the core of a clean Jasper setup — `JasperTemplateLoader` finds the `.jasper` file, a `DataSourceFactory` builds the `JRDataSource` (bean collection, JDBC connection, or JSON), and an `Exporter` handles output format. Keeps `GenerateReportUseCase` format-agnostic.

### 3. Cache compiled reports

Loading/compiling `.jasper` files repeatedly is expensive. Use a `ConcurrentHashMap<String, JasperReport>` cache in `JasperTemplateLoader`, keyed by template name.

### 4. Khmer font handling (relevant for Aureum Grand)

JasperReports needs explicit font extension registration for Khmer glyphs to render in PDF export — bundle the font as a `jasperreports_extension.properties` + `fonts.xml` in resources, not just rely on system fonts.

### 5. Subreports for repeated sections

Line items, letterheads, footers — keep as separate `.jrxml` subreports referenced via `REPORT_DIR` parameter, so they're reusable across invoice/receipt/summary templates.

### 6. Async for large reports

For heavy reports (e.g. monthly sales across all hotel branches), route `ScheduleReportUseCase` through a job queue (Spring `@Async` + a job status table) instead of blocking the HTTP thread.

## Sample Core Interface

```java
public interface ReportGenerator {
    ReportResult generate(String templateName, Map<String, Object> parameters, 
                           JRDataSource dataSource, ReportFormat format);
}
```
