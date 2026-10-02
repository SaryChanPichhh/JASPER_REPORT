# Aureum Grand Report Service

A production-ready JasperReports reporting microservice built with **Spring Boot 3** and **JasperReports 7.0**, following **Clean Architecture** principles.

> 📖 **Comprehensive Architecture Guide:** See [REPORT_PROCESS_GUIDE.md](REPORT_PROCESS_GUIDE.md).  
> 🚀 **API Integration Guide for Frontend & Backend Devs:** See [API_DOCUMENTATION.md](API_DOCUMENTATION.md).

---

## Architecture Overview

```
src/main/java/com/aureumgrand/report/
├── domain/                         # Enterprise business rules
│   ├── model/                      # Report domain models & enums
│   │   ├── ReportRequest.java
│   │   ├── ReportFormat.java       # PDF, XLSX, DOCX, CSV
│   │   └── ReportResult.java
│   ├── port/                       # Domain ports (interfaces)
│   │   ├── ReportGenerator.java
│   │   └── ReportRepository.java   # Dynamic DB-driven templates
│   └── exception/                  # Domain exceptions
│       ├── ReportGenerationException.java
│       └── TemplateNotFoundException.java
│
├── application/                    # Application business rules
│   ├── usecase/                    # Orchestrates business workflows
│   │   ├── GenerateReportUseCase.java
│   │   ├── PreviewReportUseCase.java
│   │   └── ScheduleReportUseCase.java
│   └── dto/                        # Application data transfer objects
│       ├── GenerateReportCommand.java
│       └── ReportDataDto.java
│
├── infrastructure/                 # Frameworks, drivers & adapters
│   ├── jasper/
│   │   ├── JasperReportGenerator.java    # Implements ReportGenerator
│   │   ├── JasperTemplateLoader.java     # Caching + classpath/DB resolution
│   │   ├── datasource/
│   │   │   ├── JRBeanDataSourceFactory.java
│   │   │   └── JRJdbcDataSourceFactory.java
│   │   └── exporter/
│   │       ├── PdfExporter.java          # JRPdfExporter
│   │       ├── XlsxExporter.java         # JRXlsxExporter (POI)
│   │       ├── DocxExporter.java         # JRDocxExporter (POI)
│   │       └── CsvExporter.java          # JRCsvExporter
│   ├── persistence/
│   │   ├── ReportTemplateEntity.java     # JPA dynamic template entity
│   │   ├── ReportTemplateJpaRepository.java
│   │   └── ReportRepositoryImpl.java     # Implements ReportRepository port
│   └── config/
│       └── JasperConfig.java             # Thread pool & async configuration
│
├── presentation/                   # Controllers & presentation layer
│   └── rest/
│       ├── ReportController.java
│       └── advice/GlobalExceptionHandler.java
│
└── ReportServiceApplication.java
```

---

## Features

1. **Clean Architecture Layering**: Strict separation between Domain, Application Use Cases, Infrastructure Adapters, and Presentation REST endpoints.
2. **JasperReports 7.0 & Spring Boot 3**: Fully modernized stack using Jakarta EE, OpenPDF/iText, and Apache POI 5.2.5.
3. **Multi-Format Export**:
   - `PDF` (inline preview or attachment)
   - `XLSX` (Excel with auto row height, type detection, single sheet)
   - `DOCX` (Word document with nested tables and flexible row heights)
   - `CSV` (Comma-separated UTF-8 values)
4. **Khmer Font & Unicode Support**: Bundled Khmer font families (`Khmer OS`, `Khmer OS Muol`, `Khmer OS Battambang`, `Khmer OS Siemreap`) registered via `jasperreports_extension.properties` and `fonts.xml` with `Identity-H` embedding for PDF export.
5. **Template Caching & Resolution**: Fast in-memory template caching via `ConcurrentHashMap<String, JasperReport>`, with automatic fallback between DB dynamic templates, precompiled `.jasper`, and source `.jrxml`.
6. **Asynchronous & Batch Scheduling**: Heavy report generation executed in the background through Spring `@Async` and configured `ThreadPoolTaskExecutor`.

---

## Getting Started

### Prerequisites
- **Java 17, 21, or 23** installed
- No global Maven installation required (includes `./mvnw`)

### Build and Test
```bash
# Clean and compile
./mvnw clean compile

# Run all unit and integration tests
./mvnw test
```

### Run the Application
```bash
./mvnw spring-boot:run
```
The application will start on `http://localhost:8080`.

---

## REST API Endpoints

### 1. Generate Report
- **URL**: `POST /api/v1/reports/generate`
- **Body**:
```json
{
  "templateName": "invoice_template",
  "format": "PDF",
  "parameters": {
    "InvoiceNo": "INV-2026-0001",
    "CustomerName": "សុខ សាន (Sok San)",
    "CustomerPhone": "+855 12 345 678",
    "CashierName": "សុជាតិ (Socheat)"
  },
  "items": [
    {
      "itemCode": "ROOM-DELUXE",
      "description": "Deluxe Suite (2 Nights)",
      "quantity": 2,
      "unitPrice": 120.00
    }
  ]
}
```

### 2. Preview Report (Inline PDF)
- **URL**: `POST /api/v1/reports/preview`
- Displays PDF directly in browser (`Content-Disposition: inline`).

### 3. Schedule Async Report
- **URL**: `POST /api/v1/reports/schedule`
- Returns an async job ID immediately (`202 Accepted`).

### 4. Sample Endpoints
- **Sample Invoice**: `GET /api/v1/reports/sample/invoice?format=PDF` (supports `PDF`, `XLSX`, `DOCX`, `CSV`)
- **Sample Sales Summary**: `GET /api/v1/reports/sample/sales-summary?format=XLSX`
- **Clear Template Cache**: `POST /api/v1/reports/cache/clear`
