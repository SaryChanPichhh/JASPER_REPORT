package com.aureumgrand.report.infrastructure.jasper;

import com.aureumgrand.report.application.dto.ReportDataDto;
import com.aureumgrand.report.domain.exception.TemplateNotFoundException;
import com.aureumgrand.report.domain.model.ReportFormat;
import com.aureumgrand.report.domain.model.ReportResult;
import com.aureumgrand.report.infrastructure.jasper.datasource.JRBeanDataSourceFactory;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentReceivedDataSource;
import net.sf.jasperreports.engine.JRDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JasperReportGeneratorTest {

    @Autowired
    private JasperReportGenerator reportGenerator;

    @Autowired
    private JasperTemplateLoader templateLoader;

    @Autowired
    private JRBeanDataSourceFactory dataSourceFactory;

    @Autowired
    private com.aureumgrand.report.application.usecase.pos.PosSaleInvoiceUseCase posSaleInvoiceUseCase;

    @Autowired
    private com.aureumgrand.report.application.usecase.pos.PosPoListingUseCase posPoListingUseCase;

    @Autowired
    private com.aureumgrand.report.application.usecase.pos.CustomerOrderUseCase customerOrderUseCase;

    @Autowired
    private com.aureumgrand.report.application.usecase.pos.PosPurchaseOrderUseCase posPurchaseOrderUseCase;

    @Autowired
    private com.aureumgrand.report.application.usecase.pos.SaleListingMovementUseCase saleListingMovementUseCase;

    @Autowired
    private com.aureumgrand.report.application.usecase.pos.SaleListingUseCase saleListingUseCase;

    @Autowired
    private com.aureumgrand.report.application.usecase.pos.DailyClosingDetailUseCase dailyClosingDetailUseCase;

    @Autowired
    private com.aureumgrand.report.application.usecase.pos.DailyClosingInventoryUseCase dailyClosingInventoryUseCase;

    @Autowired
    private com.aureumgrand.report.application.usecase.pos.InventoryAuditUseCase inventoryAuditUseCase;

    @Autowired
    private com.aureumgrand.report.application.usecase.pos.AdjustmentHistoryUseCase adjustmentHistoryUseCase;

    @Autowired
    private com.aureumgrand.report.application.usecase.pos.SaleListingSummaryUseCase saleListingSummaryUseCase;

    private List<ReportDataDto> sampleItems;
    private Map<String, Object> sampleParams;

    @BeforeEach
    void setUp() {
        templateLoader.clearCache();

        sampleItems = new ArrayList<>();
        sampleItems.add(new ReportDataDto("ROOM-101", "Deluxe Ocean View (2 Nights)", 2, 150.0, 300.0, Date.valueOf(LocalDate.now()), "Accommodation"));
        sampleItems.add(new ReportDataDto("DINING-01", "Apsara Royal Dinner Set", 2, 45.0, 90.0, Date.valueOf(LocalDate.now()), "Dining"));
        sampleItems.add(new ReportDataDto("SPA-02", "Herbal Aromatherapy Massage", 1, 60.0, 60.0, Date.valueOf(LocalDate.now()), "Spa"));

        sampleParams = new HashMap<>();
        sampleParams.put("InvoiceNo", "INV-TEST-2026");
        sampleParams.put("CustomerName", "សុខ ចាន់ដារា (Sok Chandara)");
        sampleParams.put("CustomerPhone", "+855 12 999 888");
        sampleParams.put("InvoiceDate", Date.valueOf(LocalDate.now()));
        sampleParams.put("DueDate", Date.valueOf(LocalDate.now().plusDays(3)));
        sampleParams.put("CashierName", "សុជាតិ (Socheat)");
        sampleParams.put("Mounth", "September 2026");
        sampleParams.put("BranchName", "Aureum Grand Hotel & Luxury Suites");
        sampleParams.put("TaxRate", 0.10);
    }

    @Test
    @DisplayName("Should generate PDF invoice successfully")
    void testGeneratePdfInvoice() {
        JRDataSource dataSource = dataSourceFactory.create(sampleItems);

        ReportResult result = reportGenerator.generate("invoice_template", sampleParams, dataSource, ReportFormat.PDF);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "PDF content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        assertEquals("application/pdf", result.getContentType());
        assertTrue(result.getFileName().endsWith(".pdf"));
    }

    @Test
    @DisplayName("Should generate Excel XLSX report successfully")
    void testGenerateXlsxReport() {
        JRDataSource dataSource = dataSourceFactory.create(sampleItems);

        ReportResult result = reportGenerator.generate("sales_summary", sampleParams, dataSource, ReportFormat.XLSX);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "XLSX content should not be empty");
        assertEquals(ReportFormat.XLSX, result.getFormat());
        assertTrue(result.getFileName().endsWith(".xlsx"));
    }

    @Test
    @DisplayName("Should generate Word DOCX report successfully")
    void testGenerateDocxReport() {
        JRDataSource dataSource = dataSourceFactory.create(sampleItems);

        ReportResult result = reportGenerator.generate("invoice_template", sampleParams, dataSource, ReportFormat.DOCX);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "DOCX content should not be empty");
        assertEquals(ReportFormat.DOCX, result.getFormat());
        assertTrue(result.getFileName().endsWith(".docx"));
    }

    @Test
    @DisplayName("Should generate CSV report successfully")
    void testGenerateCsvReport() {
        JRDataSource dataSource = dataSourceFactory.create(sampleItems);

        ReportResult result = reportGenerator.generate("sales_summary", sampleParams, dataSource, ReportFormat.CSV);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "CSV content should not be empty");
        assertEquals(ReportFormat.CSV, result.getFormat());
        assertEquals("text/csv", result.getContentType());
        assertTrue(result.getFileName().endsWith(".csv"));
    }

    @Test
    @DisplayName("Should cache compiled template after first load")
    void testTemplateCaching() {
        assertEquals(0, templateLoader.getCacheSize());

        JRDataSource ds1 = dataSourceFactory.create(sampleItems);
        reportGenerator.generate("sales_summary", sampleParams, ds1, ReportFormat.PDF);

        assertEquals(1, templateLoader.getCacheSize(), "Template should be cached after first execution");

        JRDataSource ds2 = dataSourceFactory.create(sampleItems);
        reportGenerator.generate("sales_summary", sampleParams, ds2, ReportFormat.PDF);

        assertEquals(1, templateLoader.getCacheSize(), "Subsequent calls should use cache without increasing size");
    }

    @Test
    @DisplayName("Should throw TemplateNotFoundException for non-existent template")
    void testNonExistentTemplate() {
        JRDataSource dataSource = dataSourceFactory.createEmpty();

        assertThrows(TemplateNotFoundException.class, () ->
                reportGenerator.generate("unknown_nonexistent_template", sampleParams, dataSource, ReportFormat.PDF)
        );
    }

    @Test
    @DisplayName("Should compile invoice_template.jrxml to .jasper file")
    void testCompileInvoiceToJasper() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/invoice/invoice_template.jrxml");
        java.io.File jasperFile = new java.io.File("src/main/resources/reports/invoice/invoice_template.jasper");
        net.sf.jasperreports.engine.JasperCompileManager.compileReportToFile(jrxmlFile.getAbsolutePath(), jasperFile.getAbsolutePath());
        assertTrue(jasperFile.exists(), "Compiled .jasper file should exist");
        assertTrue(jasperFile.length() > 0, "Compiled .jasper file should not be empty");
    }

    @Test
    @DisplayName("Should compile SaleInvoice80Report.jrxml")
    void testCompileSaleInvoice80Report() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/SaleInvoice80Report.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate SaleInvoice80Report via PosSaleInvoiceUseCase successfully")
    void testGeneratePosSaleInvoice80Report() {
        var command = new com.aureumgrand.report.application.dto.pos.PosSaleInvoiceDto();
        command.setReportName("SaleInvoice80Report");
        command.setFormat("PDF");
        command.setShopName("Aureum Grand Coffee");
        command.setTransRef("INV-2026-0089");
        command.setTransDate("10/01/2026 04:05 PM");
        command.setCustomerName("John Doe");
        command.setSubTotal("6.75");
        command.setTransValue("6.75");
        command.setCurrencySymbol("$");
        command.setField1("123 Market St, Suite 400");
        command.setField2("+1 (555) 019-2834");
        var payment = new PaymentReceivedDataSource("10.00", "$");
        command.setAmountReceive(payment);

        var item1 = new com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleInvoiceDataSource();
        item1.setItemCode("DRK-001");
        item1.setItemDesc("Iced Latte");
        item1.setQty(2);
        item1.setUnitPrice(new java.math.BigDecimal("2.50"));
        item1.setDiscount(java.math.BigDecimal.ZERO);
        item1.setTotalPrice(new java.math.BigDecimal("5.00"));

        var item2 = new com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleInvoiceDataSource();
        item2.setItemCode("BAK-002");
        item2.setItemDesc("Butter Croissant");
        item2.setQty(1);
        item2.setUnitPrice(new java.math.BigDecimal("1.75"));
        item2.setDiscount(java.math.BigDecimal.ZERO);
        item2.setTotalPrice(new java.math.BigDecimal("1.75"));

        command.setItems(List.of(item1, item2));

        ReportResult result = posSaleInvoiceUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/SaleInvoice80Report.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/SaleInvoice80Report.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile PosPoListingReport.jrxml")
    void testCompilePosPoListingReport() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/PosPoListingReport.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate PosPoListingReport via PosPoListingUseCase successfully")
    void testGeneratePosPoListingReport() {
        var command = new com.aureumgrand.report.application.dto.pos.PosPoListingDto();
        command.setReportName("PosPoListingReport");
        command.setFormat("PDF");
        command.setStartDate(LocalDate.of(2026, 10, 1));
        command.setEndDate(LocalDate.of(2026, 10, 5));
        command.setBranchName("Aureum Grand Hotel & Luxury Suites");
        command.setCurrencySymbol("$");

        var order1 = new com.aureumgrand.report.infrastructure.jasper.datasource.pos.PosPoListingDataSource();
        order1.setReceiveDate(LocalDate.of(2026, 10, 1));

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PosPoListing.builder()
                .time("08:30 AM")
                .warehouse("Main Kitchen")
                .supplierCode("SUP-001")
                .supplierName("Angkor Food Supplies")
                .supplierNameKH("ក្រុមហ៊ុន អង្គរ ផ្គត់ផ្គង់អាហារ")
                .transRef("PO-2026-0101")
                .itemCode("BEV-001")
                .itemDesc("Premium Arabica Coffee Beans 1kg")
                .itemDescKH("គ្រាប់កាហ្វេអារ៉ាប៊ីកា ១គក")
                .qty(10)
                .cost(new java.math.BigDecimal("12.50"))
                .totalCost(new java.math.BigDecimal("125.00"))
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PosPoListing.builder()
                .time("09:15 AM")
                .warehouse("Main Kitchen")
                .supplierCode("SUP-001")
                .supplierName("Angkor Food Supplies")
                .supplierNameKH("ក្រុមហ៊ុន អង្គរ ផ្គត់ផ្គង់អាហារ")
                .transRef("PO-2026-0101")
                .itemCode("DRY-002")
                .itemDesc("Pure Whole Milk 1L")
                .itemDescKH("ទឹកដោះគោសុទ្ធ ១លីត្រ")
                .qty(24)
                .cost(new java.math.BigDecimal("1.80"))
                .totalCost(new java.math.BigDecimal("43.20"))
                .build();

        order1.setItems(List.of(item1, item2));

        var order2 = new com.aureumgrand.report.infrastructure.jasper.datasource.pos.PosPoListingDataSource();
        order2.setReceiveDate(LocalDate.of(2026, 10, 2));

        var item3 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PosPoListing.builder()
                .time("02:00 PM")
                .warehouse("Beverage Store")
                .supplierCode("SUP-003")
                .supplierName("Mekong Beverage Co.")
                .supplierNameKH("ក្រុមហ៊ុន ភេសជ្ជៈមេគង្គ")
                .transRef("PO-2026-0105")
                .itemCode("BEV-045")
                .itemDesc("Vanilla Syrup 750ml")
                .itemDescKH("ស៊ីរ៉ូរសជាតិវ៉ានីឡា ៧៥០មល")
                .qty(6)
                .cost(new java.math.BigDecimal("8.50"))
                .totalCost(new java.math.BigDecimal("51.00"))
                .build();

        order2.setItems(List.of(item3));

        command.setOrders(List.of(order1, order2));

        ReportResult result = posPoListingUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/PosPoListingReport.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/PosPoListingReport.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile CustomerOrderReport.jrxml")
    void testCompileCustomerOrderReport() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/CustomerOrderReport.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate CustomerOrderReport via CustomerOrderUseCase successfully")
    void testGenerateCustomerOrderReport() {
        var command = new com.aureumgrand.report.application.dto.pos.InvoiceItemDto();
        command.setReportName("CustomerOrderReport");
        command.setFormat("PDF");
        command.setCustomerName("លោក សុខ ចាន់ដារ៉ា (Mr. Sok Chandara)");
        command.setDates("01/10/2026 - 15/10/2026");
        command.setBranchName("Aureum Grand Hotel & Luxury Suites");
        command.setCurrencySymbol("$");

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.InvoiceItemDataSource.builder()
                .transRef("INV-2026-0042")
                .invoicDate("01/10/2026")
                .itemCode("DRK-001")
                .itemDesc("Iced Caramel Macchiato")
                .price1(new java.math.BigDecimal("3.50"))
                .qty(2)
                .discountPrice(new java.math.BigDecimal("0.50"))
                .finalPrice(new java.math.BigDecimal("3.00"))
                .total(new java.math.BigDecimal("6.00"))
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.InvoiceItemDataSource.builder()
                .transRef("INV-2026-0042")
                .invoicDate("01/10/2026")
                .itemCode("BAK-005")
                .itemDesc("Almond Croissant")
                .price1(new java.math.BigDecimal("2.75"))
                .qty(1)
                .discountPrice(java.math.BigDecimal.ZERO)
                .finalPrice(new java.math.BigDecimal("2.75"))
                .total(new java.math.BigDecimal("2.75"))
                .build();

        var item3 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.InvoiceItemDataSource.builder()
                .transRef("INV-2026-0089")
                .invoicDate("05/10/2026")
                .itemCode("FOD-012")
                .itemDesc("Club Sandwich with French Fries")
                .price1(new java.math.BigDecimal("7.50"))
                .qty(2)
                .discountPrice(new java.math.BigDecimal("0.75"))
                .finalPrice(new java.math.BigDecimal("6.75"))
                .total(new java.math.BigDecimal("13.50"))
                .build();

        command.setItems(List.of(item1, item2, item3));

        ReportResult result = customerOrderUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/CustomerOrderReport.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/CustomerOrderReport.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile CustomerOrder80Report.jrxml")
    void testCompileCustomerOrder80Report() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/CustomerOrder80Report.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate CustomerOrder80Report via CustomerOrderUseCase successfully")
    void testGenerateCustomerOrder80Report() {
        var command = new com.aureumgrand.report.application.dto.pos.InvoiceItemDto();
        command.setReportName("CustomerOrder80Report");
        command.setFormat("PDF");
        command.setCustomerName("Mr. Sok Chandara");
        command.setDates("01/10/2026 - 15/10/2026");
        command.setBranchName("Aureum Grand Hotel & Luxury Suites");
        command.setCurrencySymbol("$");

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.InvoiceItemDataSource.builder()
                .transRef("INV-2026-0042")
                .invoicDate("01/10/2026")
                .itemCode("DRK-001")
                .itemDesc("Iced Caramel Macchiato")
                .price1(new java.math.BigDecimal("3.50"))
                .qty(2)
                .discountPrice(new java.math.BigDecimal("0.50"))
                .finalPrice(new java.math.BigDecimal("3.00"))
                .total(new java.math.BigDecimal("6.00"))
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.InvoiceItemDataSource.builder()
                .transRef("INV-2026-0042")
                .invoicDate("01/10/2026")
                .itemCode("BAK-005")
                .itemDesc("Almond Croissant")
                .price1(new java.math.BigDecimal("2.75"))
                .qty(1)
                .discountPrice(java.math.BigDecimal.ZERO)
                .finalPrice(new java.math.BigDecimal("2.75"))
                .total(new java.math.BigDecimal("2.75"))
                .build();

        var item3 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.InvoiceItemDataSource.builder()
                .transRef("INV-2026-0089")
                .invoicDate("05/10/2026")
                .itemCode("FOD-012")
                .itemDesc("Club Sandwich with French Fries")
                .price1(new java.math.BigDecimal("7.50"))
                .qty(2)
                .discountPrice(new java.math.BigDecimal("0.75"))
                .finalPrice(new java.math.BigDecimal("6.75"))
                .total(new java.math.BigDecimal("13.50"))
                .build();

        command.setItems(List.of(item1, item2, item3));

        ReportResult result = customerOrderUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/CustomerOrder80Report.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/CustomerOrder80Report.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile PosPurchaseOrderReport.jrxml")
    void testCompilePosPurchaseOrderReport() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/PosPurchaseOrderReport.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate PosPurchaseOrderReport via PosPurchaseOrderUseCase successfully")
    void testGeneratePosPurchaseOrderReport() {
        var command = new com.aureumgrand.report.application.dto.pos.PosPurchaseOrderDto();
        command.setReportName("PosPurchaseOrderReport");
        command.setFormat("PDF");
        command.setCode("PO-2026-0088");
        command.setShopName("Aureum Grand Hotel & Luxury Suites");
        command.setSupplier("Angkor Food Supplies Co., Ltd");
        command.setWarehouse("Central Kitchen & Store");
        command.setReceiveBy("លោក ចាន់ សុភាព (Mr. Chan Sopheap)");
        command.setChecker("លោកស្រី ម៉ៅ សុខា (Mrs. Mao Sokha)");
        command.setDescription("ការផ្គត់ផ្គង់ភេសជ្ជៈ និងគ្រឿងទេសប្រចាំខែ (Monthly Beverage & Supplies)");
        command.setNote("សូមដឹកជញ្ជូនទំនិញតាមកាលបរិច្ឆេទកំណត់ និងពិនិត្យគុណភាពជាមុន");
        command.setStartDate(java.time.LocalDateTime.of(2026, 10, 1, 8, 0));
        command.setEndDate(java.time.LocalDateTime.of(2026, 10, 15, 17, 0));
        command.setReceiveDate(java.time.LocalDateTime.of(2026, 10, 5, 14, 0));
        command.setCheckDate(java.time.LocalDateTime.of(2026, 10, 2, 9, 30));
        command.setCurrencySymbol("$");

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PurchaseOrderDataSource.builder()
                .itemCode("BEV-001")
                .itemDesc("Premium Arabica Coffee Beans (1kg)")
                .category("Beverages")
                .qty(10)
                .cost(new java.math.BigDecimal("15.50"))
                .totalCost(new java.math.BigDecimal("155.00"))
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PurchaseOrderDataSource.builder()
                .itemCode("BEV-002")
                .itemDesc("Twinings Earl Grey Tea (100 bags)")
                .category("Beverages")
                .qty(5)
                .cost(new java.math.BigDecimal("12.00"))
                .totalCost(new java.math.BigDecimal("60.00"))
                .build();

        var item3 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PurchaseOrderDataSource.builder()
                .itemCode("DRY-015")
                .itemDesc("Cambodian Organic Jasmine Rice (25kg)")
                .category("Dry Goods")
                .qty(8)
                .cost(new java.math.BigDecimal("26.50"))
                .totalCost(new java.math.BigDecimal("212.00"))
                .build();

        var item4 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PurchaseOrderDataSource.builder()
                .itemCode("DAI-008")
                .itemDesc("Anchor Unsalted Butter (5kg)")
                .category("Dairy")
                .qty(4)
                .cost(new java.math.BigDecimal("38.00"))
                .totalCost(new java.math.BigDecimal("152.00"))
                .build();

        command.setItems(List.of(item1, item2, item3, item4));

        ReportResult result = posPurchaseOrderUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/PosPurchaseOrderReport.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/PosPurchaseOrderReport.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile SaleListingMovementReport.jrxml")
    void testCompileSaleListingMovementReport() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/SaleListingMovementReport.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate SaleListingMovementReport via SaleListingMovementUseCase successfully")
    void testGenerateSaleListingMovementReport() {
        var command = new com.aureumgrand.report.application.dto.pos.SaleListingMovementDto();
        command.setReportName("SaleListingMovementReport");
        command.setFormat("PDF");
        command.setShopName("Aureum Grand Hotel & Luxury Suites");
        command.setStartDate(java.time.LocalDateTime.of(2026, 10, 1, 0, 0));
        command.setEndDate(java.time.LocalDateTime.of(2026, 10, 31, 23, 59));
        command.setCurrencySymbol("$");

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingMovementDataSource.builder()
                .itemCode("BEV-001")
                .itemDesc("Premium Arabica Coffee Beans (1kg)")
                .totalPurchaseQty(100)
                .totalPurchaseCost(new java.math.BigDecimal("1200.00"))
                .totalSaleQty(75)
                .totalSaleCost(new java.math.BigDecimal("900.00"))
                .totalRemainQty(25)
                .totalRemainCost(new java.math.BigDecimal("300.00"))
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingMovementDataSource.builder()
                .itemCode("BEV-002")
                .itemDesc("Twinings Earl Grey Tea (100 bags)")
                .totalPurchaseQty(60)
                .totalPurchaseCost(new java.math.BigDecimal("720.00"))
                .totalSaleQty(40)
                .totalSaleCost(new java.math.BigDecimal("480.00"))
                .totalRemainQty(20)
                .totalRemainCost(new java.math.BigDecimal("240.00"))
                .build();

        var item3 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingMovementDataSource.builder()
                .itemCode("DRY-015")
                .itemDesc("Cambodian Organic Jasmine Rice (25kg)")
                .totalPurchaseQty(80)
                .totalPurchaseCost(new java.math.BigDecimal("2120.00"))
                .totalSaleQty(50)
                .totalSaleCost(new java.math.BigDecimal("1325.00"))
                .totalRemainQty(30)
                .totalRemainCost(new java.math.BigDecimal("795.00"))
                .build();

        var item4 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingMovementDataSource.builder()
                .itemCode("DAI-008")
                .itemDesc("Anchor Unsalted Butter (5kg)")
                .totalPurchaseQty(30)
                .totalPurchaseCost(new java.math.BigDecimal("1140.00"))
                .totalSaleQty(22)
                .totalSaleCost(new java.math.BigDecimal("836.00"))
                .totalRemainQty(8)
                .totalRemainCost(new java.math.BigDecimal("304.00"))
                .build();

        command.setItems(List.of(item1, item2, item3, item4));

        ReportResult result = saleListingMovementUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/SaleListingMovementReport.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/SaleListingMovementReport.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile SaleListingByDateReport.jrxml")
    void testCompileSaleListingReport() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/SaleListingByDateReport.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate SaleListingReport via SaleListingUseCase successfully")
    void testGenerateSaleListingReport() {
        var command = new com.aureumgrand.report.application.dto.pos.SaleListingDto();
        command.setReportName("SaleListingReport");
        command.setFormat("PDF");
        command.setShopName("Aureum Grand Hotel & Luxury Suites");
        command.setStartDate(java.time.LocalDateTime.of(2026, 10, 1, 0, 0));
        command.setEndDate(java.time.LocalDateTime.of(2026, 10, 31, 23, 59));
        command.setCurrencySymbol("$");

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0101")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 9, 15))
                .customerName("លោក សុខ សាន (Mr. Sok San)")
                .seller("គឹម ឡុង (Kim Long)")
                .total(new java.math.BigDecimal("125.00"))
                .cost(new java.math.BigDecimal("75.00"))
                .profit(new java.math.BigDecimal("50.00"))
                .paymentStatus("PAID")
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0102")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 11, 30))
                .customerName("Jane Watson")
                .seller("ម៉ៅ សុខា (Mao Sokha)")
                .total(new java.math.BigDecimal("68.50"))
                .cost(new java.math.BigDecimal("35.00"))
                .profit(new java.math.BigDecimal("33.50"))
                .paymentStatus("PAID")
                .build();

        var item3 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0103")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 14, 20))
                .customerName("David Miller")
                .seller("គឹម ឡុង (Kim Long)")
                .total(new java.math.BigDecimal("210.00"))
                .cost(new java.math.BigDecimal("120.00"))
                .profit(new java.math.BigDecimal("90.00"))
                .paymentStatus("PAID")
                .build();

        var item4 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0104")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 16, 45))
                .customerName("Walk-in Guest")
                .seller("ចាន់ ធារី (Chan Theary)")
                .total(new java.math.BigDecimal("45.00"))
                .cost(new java.math.BigDecimal("22.00"))
                .profit(new java.math.BigDecimal("23.00"))
                .paymentStatus("CREDIT")
                .build();

        command.setItems(List.of(item1, item2, item3, item4));

        ReportResult result = saleListingUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/SaleListingReport.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/SaleListingReport.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile DailyClosingInventoryDetailA4Report.jrxml")
    void testCompileDailyClosingInventoryDetailA4Report() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/DailyClosingInventoryDetailA4Report.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate DailyClosingInventoryDetailA4Report via DailyClosingDetailUseCase successfully")
    void testGenerateDailyClosingInventoryDetailA4Report() {
        var command = new com.aureumgrand.report.application.dto.pos.DailyClosingDetailDto();
        command.setReportName("DailyClosingInventoryDetailA4Report");
        command.setFormat("PDF");
        command.setShopName("Aureum Grand Hotel & Luxury Suites");
        command.setDuration("01/10/2026 - Morning Shift (06:00 - 14:00)");
        command.setPrintDate(java.time.LocalDateTime.of(2026, 10, 1, 14, 30));
        command.setSubtotal("450.00");
        command.setDiscountInvoice(new java.math.BigDecimal("15.00"));
        command.setVat("43.50");
        command.setExpense("20.00");
        command.setExpenseRiel("82,000");
        command.setCashChange("100.00");
        command.setExchangeRate("4,100");
        command.setTotalDollar("458.50");
        command.setTotalRiel("1,879,850");
        command.setCurrencySymbol("$");

        var closing1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDetailDataSource.builder()
                .seller("គឹម ឡុង (Kim Long)")
                .Dates("01/10/2026")
                .items(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("BEV-001")
                                .itemDesc("Iced Caffe Latte")
                                .categoryDesc("Beverages")
                                .qty(3)
                                .price(new java.math.BigDecimal("4.50"))
                                .discountPrice(java.math.BigDecimal.ZERO)
                                .finalPrice(new java.math.BigDecimal("4.50"))
                                .total(new java.math.BigDecimal("13.50"))
                                .build(),
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("BAK-002")
                                .itemDesc("Butter Croissant")
                                .categoryDesc("Bakery")
                                .qty(2)
                                .price(new java.math.BigDecimal("2.50"))
                                .discountPrice(new java.math.BigDecimal("0.50"))
                                .finalPrice(new java.math.BigDecimal("2.00"))
                                .total(new java.math.BigDecimal("4.00"))
                                .build()
                ))
                .Payments(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("CASH USD")
                                .totalReceived("120.00")
                                .currencySymbol("$")
                                .build(),
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("KHQR / ABA")
                                .totalReceived("250.00")
                                .currencySymbol("$")
                                .build()
                ))
                .build();

        var closing2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDetailDataSource.builder()
                .seller("ម៉ៅ សុខា (Mao Sokha)")
                .Dates("01/10/2026")
                .items(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("FOOD-010")
                                .itemDesc("Grilled Salmon Steak")
                                .categoryDesc("Main Course")
                                .qty(2)
                                .price(new java.math.BigDecimal("22.00"))
                                .discountPrice(java.math.BigDecimal.ZERO)
                                .finalPrice(new java.math.BigDecimal("22.00"))
                                .total(new java.math.BigDecimal("44.00"))
                                .build()
                ))
                .Payments(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("CREDIT CARD (VISA)")
                                .totalReceived("88.50")
                                .currencySymbol("$")
                                .build()
                ))
                .build();

        command.setClosings(List.of(closing1, closing2));

        ReportResult result = dailyClosingDetailUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/DailyClosingInventoryDetailA4Report.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/DailyClosingInventoryDetailA4Report.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile DailyClosingInventoryDetail80mmReport.jrxml")
    void testCompileDailyClosingInventoryDetail80mmReport() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/DailyClosingInventoryDetail80mmReport.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate DailyClosingInventoryDetail80mmReport via DailyClosingDetailUseCase successfully")
    void testGenerateDailyClosingInventoryDetail80mmReport() {
        var command = new com.aureumgrand.report.application.dto.pos.DailyClosingDetailDto();
        command.setReportName("DailyClosingInventoryDetail80mmReport");
        command.setFormat("PDF");
        command.setShopName("Aureum Grand Hotel & Luxury Suites");
        command.setDuration("01/10/2026 - Morning Shift (06:00 - 14:00)");
        command.setPrintDate(java.time.LocalDateTime.of(2026, 10, 1, 14, 30));
        command.setSubtotal("450.00");
        command.setDiscountInvoice(new java.math.BigDecimal("15.00"));
        command.setVat("43.50");
        command.setExpense("20.00");
        command.setExpenseRiel("82,000");
        command.setCashChange("100.00");
        command.setExchangeRate("4,100");
        command.setTotalDollar("458.50");
        command.setTotalRiel("1,879,850");
        command.setCurrencySymbol("$");

        var closing1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDetailDataSource.builder()
                .seller("Kim Long")
                .Dates("01/10/2026")
                .items(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("BEV-001")
                                .itemDesc("Iced Caffe Latte")
                                .categoryDesc("Beverages")
                                .qty(3)
                                .price(new java.math.BigDecimal("4.50"))
                                .discountPrice(java.math.BigDecimal.ZERO)
                                .finalPrice(new java.math.BigDecimal("4.50"))
                                .total(new java.math.BigDecimal("13.50"))
                                .build(),
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("BAK-002")
                                .itemDesc("Butter Croissant")
                                .categoryDesc("Bakery")
                                .qty(2)
                                .price(new java.math.BigDecimal("2.50"))
                                .discountPrice(new java.math.BigDecimal("0.50"))
                                .finalPrice(new java.math.BigDecimal("2.00"))
                                .total(new java.math.BigDecimal("4.00"))
                                .build()
                ))
                .Payments(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("CASH USD")
                                .totalReceived("120.00")
                                .currencySymbol("$")
                                .build(),
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("KHQR / ABA")
                                .totalReceived("250.00")
                                .currencySymbol("$")
                                .build()
                ))
                .build();

        var closing2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDetailDataSource.builder()
                .seller("Mao Sokha")
                .Dates("01/10/2026")
                .items(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("FOOD-010")
                                .itemDesc("Grilled Salmon Steak")
                                .categoryDesc("Main Course")
                                .qty(2)
                                .price(new java.math.BigDecimal("22.00"))
                                .discountPrice(java.math.BigDecimal.ZERO)
                                .finalPrice(new java.math.BigDecimal("22.00"))
                                .total(new java.math.BigDecimal("44.00"))
                                .build()
                ))
                .Payments(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("CREDIT CARD (VISA)")
                                .totalReceived("88.50")
                                .currencySymbol("$")
                                .build()
                ))
                .build();

        command.setClosings(List.of(closing1, closing2));

        ReportResult result = dailyClosingDetailUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/DailyClosingInventoryDetail80mmReport.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/DailyClosingInventoryDetail80mmReport.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile DailyClosingInventoryByCategoryA4Report.jrxml")
    void testCompileDailyClosingInventoryByCategoryA4Report() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/DailyClosingInventoryByCategoryA4Report.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate DailyClosingInventoryByCategoryA4Report via DailyClosingDetailUseCase successfully")
    void testGenerateDailyClosingInventoryByCategoryA4Report() {
        var command = new com.aureumgrand.report.application.dto.pos.DailyClosingDetailDto();
        command.setReportName("DailyClosingInventoryByCategoryA4Report");
        command.setFormat("PDF");
        command.setShopName("Aureum Grand Hotel & Luxury Suites");
        command.setDuration("01/10/2026 - Morning Shift (06:00 - 14:00)");
        command.setPrintDate(java.time.LocalDateTime.of(2026, 10, 1, 14, 30));
        command.setSubtotal("450.00");
        command.setDiscountInvoice(new java.math.BigDecimal("15.00"));
        command.setVat("43.50");
        command.setExpense("20.00");
        command.setExpenseRiel("82,000");
        command.setCashChange("100.00");
        command.setExchangeRate("4,100");
        command.setTotalDollar("458.50");
        command.setTotalRiel("1,879,850");
        command.setCurrencySymbol("$");

        var closing1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDetailDataSource.builder()
                .seller("Kim Long")
                .Dates("01/10/2026")
                .items(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("BEV-001")
                                .itemDesc("Iced Caffe Latte")
                                .categoryCode(1)
                                .categoryDesc("Beverages")
                                .qty(3)
                                .price(new java.math.BigDecimal("4.50"))
                                .discountPrice(java.math.BigDecimal.ZERO)
                                .finalPrice(new java.math.BigDecimal("4.50"))
                                .total(new java.math.BigDecimal("13.50"))
                                .build(),
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("BAK-002")
                                .itemDesc("Butter Croissant")
                                .categoryCode(2)
                                .categoryDesc("Bakery & Pastry")
                                .qty(2)
                                .price(new java.math.BigDecimal("2.50"))
                                .discountPrice(new java.math.BigDecimal("0.50"))
                                .finalPrice(new java.math.BigDecimal("2.00"))
                                .total(new java.math.BigDecimal("4.00"))
                                .build()
                ))
                .Payments(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("CASH USD")
                                .totalReceived("120.00")
                                .currencySymbol("$")
                                .build(),
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("KHQR / ABA")
                                .totalReceived("250.00")
                                .currencySymbol("$")
                                .build()
                ))
                .build();

        var closing2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDetailDataSource.builder()
                .seller("Mao Sokha")
                .Dates("01/10/2026")
                .items(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("BEV-003")
                                .itemDesc("Cappuccino Grande")
                                .categoryCode(1)
                                .categoryDesc("Beverages")
                                .qty(2)
                                .price(new java.math.BigDecimal("4.50"))
                                .discountPrice(java.math.BigDecimal.ZERO)
                                .finalPrice(new java.math.BigDecimal("4.50"))
                                .total(new java.math.BigDecimal("9.00"))
                                .build(),
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("FOOD-010")
                                .itemDesc("Grilled Salmon Steak")
                                .categoryCode(3)
                                .categoryDesc("Main Course")
                                .qty(2)
                                .price(new java.math.BigDecimal("22.00"))
                                .discountPrice(java.math.BigDecimal.ZERO)
                                .finalPrice(new java.math.BigDecimal("22.00"))
                                .total(new java.math.BigDecimal("44.00"))
                                .build()
                ))
                .Payments(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("CREDIT CARD (VISA)")
                                .totalReceived("88.50")
                                .currencySymbol("$")
                                .build()
                ))
                .build();

        command.setClosings(List.of(closing1, closing2));

        ReportResult result = dailyClosingDetailUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/DailyClosingInventoryByCategoryA4Report.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/DailyClosingInventoryByCategoryA4Report.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile DailyClosingInventoryByCategory80Report.jrxml")
    void testCompileDailyClosingInventoryByCategory80Report() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/DailyClosingInventoryByCategory80Report.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate DailyClosingInventoryByCategory80Report via DailyClosingDetailUseCase successfully")
    void testGenerateDailyClosingInventoryByCategory80Report() {
        var command = new com.aureumgrand.report.application.dto.pos.DailyClosingDetailDto();
        command.setReportName("DailyClosingInventoryByCategory80Report");
        command.setFormat("PDF");
        command.setShopName("Aureum Grand Hotel & Luxury Suites");
        command.setDuration("01/10/2026 - Morning Shift (06:00 - 14:00)");
        command.setPrintDate(java.time.LocalDateTime.of(2026, 10, 1, 14, 30));
        command.setSubtotal("450.00");
        command.setDiscountInvoice(new java.math.BigDecimal("15.00"));
        command.setVat("43.50");
        command.setExpense("20.00");
        command.setExpenseRiel("82,000");
        command.setCashChange("100.00");
        command.setExchangeRate("4,100");
        command.setTotalDollar("458.50");
        command.setTotalRiel("1,879,850");
        command.setCurrencySymbol("$");

        var closing1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDetailDataSource.builder()
                .seller("Kim Long")
                .Dates("01/10/2026")
                .items(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("BEV-001")
                                .itemDesc("Iced Caffe Latte")
                                .categoryCode(1)
                                .categoryDesc("Beverages")
                                .qty(3)
                                .price(new java.math.BigDecimal("4.50"))
                                .discountPrice(java.math.BigDecimal.ZERO)
                                .finalPrice(new java.math.BigDecimal("4.50"))
                                .total(new java.math.BigDecimal("13.50"))
                                .build(),
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("BAK-002")
                                .itemDesc("Butter Croissant")
                                .categoryCode(2)
                                .categoryDesc("Bakery & Pastry")
                                .qty(2)
                                .price(new java.math.BigDecimal("2.50"))
                                .discountPrice(new java.math.BigDecimal("0.50"))
                                .finalPrice(new java.math.BigDecimal("2.00"))
                                .total(new java.math.BigDecimal("4.00"))
                                .build()
                ))
                .Payments(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("CASH USD")
                                .totalReceived("120.00")
                                .currencySymbol("$")
                                .build(),
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("KHQR / ABA")
                                .totalReceived("250.00")
                                .currencySymbol("$")
                                .build()
                ))
                .build();

        var closing2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDetailDataSource.builder()
                .seller("Mao Sokha")
                .Dates("01/10/2026")
                .items(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("BEV-003")
                                .itemDesc("Cappuccino Grande")
                                .categoryCode(1)
                                .categoryDesc("Beverages")
                                .qty(2)
                                .price(new java.math.BigDecimal("4.50"))
                                .discountPrice(java.math.BigDecimal.ZERO)
                                .finalPrice(new java.math.BigDecimal("4.50"))
                                .total(new java.math.BigDecimal("9.00"))
                                .build(),
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("FOOD-010")
                                .itemDesc("Grilled Salmon Steak")
                                .categoryCode(3)
                                .categoryDesc("Main Course")
                                .qty(2)
                                .price(new java.math.BigDecimal("22.00"))
                                .discountPrice(java.math.BigDecimal.ZERO)
                                .finalPrice(new java.math.BigDecimal("22.00"))
                                .total(new java.math.BigDecimal("44.00"))
                                .build()
                ))
                .Payments(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("CREDIT CARD (VISA)")
                                .totalReceived("88.50")
                                .currencySymbol("$")
                                .build()
                ))
                .build();

        command.setClosings(List.of(closing1, closing2));

        ReportResult result = dailyClosingDetailUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/DailyClosingInventoryByCategory80Report.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/DailyClosingInventoryByCategory80Report.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile DailyClosingInventoryA4Report.jrxml")
    void testCompileDailyClosingInventoryReport() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/DailyClosingInventoryA4Report.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate DailyClosingInventoryA4Report via DailyClosingInventoryUseCase successfully")
    void testGenerateDailyClosingInventoryReport() {
        var command = new com.aureumgrand.report.application.dto.pos.DailyClosingDto();
        command.setReportName("DailyClosingInventoryA4Report");
        command.setFormat("PDF");
        command.setShopName("Aureum Grand Hotel & Luxury Suites");
        command.setSeller("សុខ ចាន់ដារា (Sok Chandara)");
        command.setDates("02-10-2026");
        command.setDuration("Morning Shift (06:00 - 14:00)");
        command.setPrintDate(java.time.LocalDateTime.of(2026, 10, 2, 14, 15));
        command.setSubtotal(new java.math.BigDecimal("380.00"));
        command.setDiscount(new java.math.BigDecimal("12.50"));
        command.setTotalPrice("367.50");
        command.setTotalDollar(new java.math.BigDecimal("367.50"));
        command.setTotalRiel(new java.math.BigDecimal("1506750"));
        command.setExpense("15.00");
        command.setExpenseRiel("61,500");
        command.setExchangeRate(new java.math.BigDecimal("4100"));
        command.setVat("36.75");
        command.setCashChange("50.00");
        command.setCurrencySymbol("$");

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDataSource.builder()
                .itemCode("BEV-101")
                .itemDesc("Cappuccino Large")
                .categoryCode("BEV")
                .categoryDesc("Beverages")
                .qty(10)
                .price(new java.math.BigDecimal("4.50"))
                .discountPrice(java.math.BigDecimal.ZERO)
                .finalPrice(new java.math.BigDecimal("4.50"))
                .total(new java.math.BigDecimal("45.00"))
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDataSource.builder()
                .itemCode("BEV-102")
                .itemDesc("Iced Matcha Green Tea")
                .categoryCode("BEV")
                .categoryDesc("Beverages")
                .qty(8)
                .price(new java.math.BigDecimal("5.00"))
                .discountPrice(new java.math.BigDecimal("0.50"))
                .finalPrice(new java.math.BigDecimal("4.50"))
                .total(new java.math.BigDecimal("36.00"))
                .build();

        var item3 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDataSource.builder()
                .itemCode("BAK-201")
                .itemDesc("Almond Croissant")
                .categoryCode("BAK")
                .categoryDesc("Bakery & Pastry")
                .qty(6)
                .price(new java.math.BigDecimal("3.50"))
                .discountPrice(java.math.BigDecimal.ZERO)
                .finalPrice(new java.math.BigDecimal("3.50"))
                .total(new java.math.BigDecimal("21.00"))
                .build();

        command.setItems(List.of(item1, item2, item3));

        var payment1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                .paymentType("CASH USD")
                .totalReceived("150.00")
                .currencySymbol("$")
                .build();
        var payment2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                .paymentType("KHQR / ABA")
                .totalReceived("217.50")
                .currencySymbol("$")
                .build();
        command.setPayments(List.of(payment1, payment2));

        var expense1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.ExpenseDataSource.builder()
                .expenseDesc("Fresh Ice Supply")
                .ExpenseBy("Sokha")
                .ExpenseAmount("15.00")
                .CurrencySymbol("$")
                .build();
        command.setExpenses(List.of(expense1));

        ReportResult result = dailyClosingInventoryUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/DailyClosingInventoryA4Report.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/DailyClosingInventoryA4Report.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile DailyClosingInventory80mmReport.jrxml")
    void testCompileDailyClosingInventory80Report() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/DailyClosingInventory80mmReport.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate DailyClosingInventory80Report via DailyClosingInventoryUseCase successfully")
    void testGenerateDailyClosingInventory80Report() {
        var command = new com.aureumgrand.report.application.dto.pos.DailyClosingDto();
        command.setReportName("DailyClosingInventory80mmReport");
        command.setFormat("PDF");
        command.setShopName("Aureum Grand Hotel & Luxury Suites");
        command.setSeller("Sok Chandara");
        command.setDates("02-10-2026");
        command.setDuration("Morning Shift (06:00 - 14:00)");
        command.setPrintDate(java.time.LocalDateTime.of(2026, 10, 2, 14, 15));
        command.setSubtotal(new java.math.BigDecimal("380.00"));
        command.setDiscount(new java.math.BigDecimal("12.50"));
        command.setTotalPrice("367.50");
        command.setTotalDollar(new java.math.BigDecimal("367.50"));
        command.setTotalRiel(new java.math.BigDecimal("1506750"));
        command.setExpense("15.00");
        command.setExpenseRiel("61,500");
        command.setExchangeRate(new java.math.BigDecimal("4100"));
        command.setVat("36.75");
        command.setCashChange("50.00");
        command.setCurrencySymbol("$");

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDataSource.builder()
                .itemCode("BEV-101")
                .itemDesc("Cappuccino Large")
                .categoryCode("BEV")
                .categoryDesc("Beverages")
                .qty(10)
                .price(new java.math.BigDecimal("4.50"))
                .discountPrice(java.math.BigDecimal.ZERO)
                .finalPrice(new java.math.BigDecimal("4.50"))
                .total(new java.math.BigDecimal("45.00"))
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDataSource.builder()
                .itemCode("BEV-102")
                .itemDesc("Iced Matcha Green Tea")
                .categoryCode("BEV")
                .categoryDesc("Beverages")
                .qty(8)
                .price(new java.math.BigDecimal("5.00"))
                .discountPrice(new java.math.BigDecimal("0.50"))
                .finalPrice(new java.math.BigDecimal("4.50"))
                .total(new java.math.BigDecimal("36.00"))
                .build();

        var item3 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDataSource.builder()
                .itemCode("BAK-201")
                .itemDesc("Almond Croissant")
                .categoryCode("BAK")
                .categoryDesc("Bakery & Pastry")
                .qty(6)
                .price(new java.math.BigDecimal("3.50"))
                .discountPrice(java.math.BigDecimal.ZERO)
                .finalPrice(new java.math.BigDecimal("3.50"))
                .total(new java.math.BigDecimal("21.00"))
                .build();

        command.setItems(List.of(item1, item2, item3));

        var payment1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                .paymentType("CASH USD")
                .totalReceived("150.00")
                .currencySymbol("$")
                .build();
        var payment2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                .paymentType("KHQR / ABA")
                .totalReceived("217.50")
                .currencySymbol("$")
                .build();
        command.setPayments(List.of(payment1, payment2));

        var expense1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.ExpenseDataSource.builder()
                .expenseDesc("Fresh Ice Supply")
                .ExpenseBy("Sokha")
                .ExpenseAmount("15.00")
                .CurrencySymbol("$")
                .build();
        command.setExpenses(List.of(expense1));

        ReportResult result = dailyClosingInventoryUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated 80mm report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/DailyClosingInventory80mmReport.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/DailyClosingInventory80mmReport.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile InventoryAuditReport.jrxml")
    void testCompileInventoryAuditReport() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/InventoryAuditReport.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate InventoryAuditReport via InventoryAuditUseCase successfully")
    void testGenerateInventoryAuditReport() {
        var command = new com.aureumgrand.report.application.dto.pos.InventoryDto();
        command.setReportName("InventoryAuditReport");
        command.setFormat("PDF");
        command.setShopName("Aureum Grand Hotel & Luxury Suites");
        command.setWarehouse("Central Wine & Beverage Cellar");
        command.setAuditor("Senior Auditor Sok Vichea");
        command.setPrintDate(java.time.LocalDateTime.of(2026, 10, 2, 10, 0));
        command.setShowCost(true);
        command.setShowSalePrice(true);
        command.setCurrencySymbol("$");

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.InventoryDataSource.builder()
                .category("Wines & Spirits")
                .itemCode("WIN-001")
                .itemBarcode("8841234567890")
                .itemDesc("Chateau Margaux 2015 Premier Grand Cru")
                .stock(12)
                .costPrice(new java.math.BigDecimal("450.00"))
                .salePrice(new java.math.BigDecimal("780.00"))
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.InventoryDataSource.builder()
                .category("Wines & Spirits")
                .itemCode("WIN-002")
                .itemBarcode("8841234567891")
                .itemDesc("Dom Perignon Vintage Champagne 750ml")
                .stock(24)
                .costPrice(new java.math.BigDecimal("180.00"))
                .salePrice(new java.math.BigDecimal("290.00"))
                .build();

        var item3 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.InventoryDataSource.builder()
                .category("Coffee & Tea")
                .itemCode("COF-101")
                .itemBarcode("8841234567892")
                .itemDesc("Arabica Premium Roasted Coffee Beans (1kg)")
                .stock(50)
                .costPrice(new java.math.BigDecimal("14.00"))
                .salePrice(new java.math.BigDecimal("22.00"))
                .build();

        command.setItems(List.of(item1, item2, item3));

        ReportResult result = inventoryAuditUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/InventoryAuditReport.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/InventoryAuditReport.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile AdjustmentHistoryReport.jrxml")
    void testCompileAdjustmentHistoryReport() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/AdjustmentHistoryReport.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate AdjustmentHistoryReport via AdjustmentHistoryUseCase successfully")
    void testGenerateAdjustmentHistoryReport() {
        var command = new com.aureumgrand.report.application.dto.pos.AdjustmentHistoryDto();
        command.setReportName("AdjustmentHistoryReport");
        command.setFormat("PDF");
        command.setShopName("Aureum Grand Hotel & Luxury Suites");
        command.setWarehouse("Main Food & Beverage Store");
        command.setStartDate("01-10-2026");
        command.setEndDate("31-10-2026");
        command.setAdjustedBy("Chhay Ly (Inventory Supervisor)");
        command.setPrintDate(java.time.LocalDateTime.of(2026, 10, 2, 11, 30));
        command.setShowCost(true);
        command.setCurrencySymbol("$");

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.AdjustmentHistoryDataSource.builder()
                .wareCode("WH-01")
                .warehouse("Main Kitchen Store")
                .adjustmentDate("02-10-2026 09:30")
                .adjustmentRef("ADJ-2026-001")
                .itemCode("ING-001")
                .itemBarCode("884111222333")
                .itemDesc("Australian Wagyu Beef Ribeye MB7+")
                .quantity(5)
                .cost(85.00)
                .total(425.00)
                .adjustedBy("Chhay Ly")
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.AdjustmentHistoryDataSource.builder()
                .wareCode("WH-01")
                .warehouse("Main Kitchen Store")
                .adjustmentDate("02-10-2026 10:15")
                .adjustmentRef("ADJ-2026-002")
                .itemCode("ING-002")
                .itemBarCode("884111222334")
                .itemDesc("Fresh Norwegian Salmon (Spoilage / Damage)")
                .quantity(-2)
                .cost(35.00)
                .total(-70.00)
                .adjustedBy("Chhay Ly")
                .build();

        var item3 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.AdjustmentHistoryDataSource.builder()
                .wareCode("WH-02")
                .warehouse("Lobby Bar Store")
                .adjustmentDate("02-10-2026 11:00")
                .adjustmentRef("ADJ-2026-003")
                .itemCode("WIN-105")
                .itemBarCode("884111222335")
                .itemDesc("Moet & Chandon Imperial Brut (Breakage)")
                .quantity(-1)
                .cost(65.00)
                .total(-65.00)
                .adjustedBy("Sokha")
                .build();

        command.setItems(List.of(item1, item2, item3));

        ReportResult result = adjustmentHistoryUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/AdjustmentHistoryReport.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/AdjustmentHistoryReport.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile SaleListingSummaryReport.jrxml")
    void testCompileSaleListingSummaryReport() throws Exception {
        java.io.File jrxmlFile = new java.io.File("src/main/resources/reports/SaleListingSummaryReport.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate SaleListingSummaryReport via SaleListingSummaryUseCase successfully")
    void testGenerateSaleListingSummaryReport() {
        var command = new com.aureumgrand.report.application.dto.pos.SaleListingSummaryDto();
        command.setReportName("SaleListingSummaryReport");
        command.setFormat("PDF");
        command.setShopName("Aureum Grand Hotel & Luxury Suites");
        command.setStartDate(java.time.LocalDateTime.of(2026, 10, 1, 0, 0));
        command.setEndDate(java.time.LocalDateTime.of(2026, 10, 31, 23, 59));
        command.setCurrencySymbol("$");

        var day1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingSummaryDataSource.builder()
                .date("01-10-2026")
                .salePrice(new java.math.BigDecimal("1250.00"))
                .discount(new java.math.BigDecimal("50.00"))
                .cost(new java.math.BigDecimal("480.00"))
                .deliveryFee(new java.math.BigDecimal("25.00"))
                .expense(new java.math.BigDecimal("45.00"))
                .build();

        var day2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingSummaryDataSource.builder()
                .date("02-10-2026")
                .salePrice(new java.math.BigDecimal("1890.50"))
                .discount(new java.math.BigDecimal("100.00"))
                .cost(new java.math.BigDecimal("720.00"))
                .deliveryFee(new java.math.BigDecimal("35.00"))
                .expense(new java.math.BigDecimal("60.00"))
                .build();

        var day3 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingSummaryDataSource.builder()
                .date("03-10-2026")
                .salePrice(new java.math.BigDecimal("2100.00"))
                .discount(new java.math.BigDecimal("75.00"))
                .cost(new java.math.BigDecimal("850.00"))
                .deliveryFee(new java.math.BigDecimal("40.00"))
                .expense(new java.math.BigDecimal("90.00"))
                .build();

        var day4 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingSummaryDataSource.builder()
                .date("04-10-2026")
                .salePrice(new java.math.BigDecimal("1650.00"))
                .discount(new java.math.BigDecimal("30.00"))
                .cost(new java.math.BigDecimal("610.00"))
                .deliveryFee(new java.math.BigDecimal("20.00"))
                .expense(new java.math.BigDecimal("50.00"))
                .build();

        command.setItems(List.of(day1, day2, day3, day4));

        ReportResult result = saleListingSummaryUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/SaleListingSummaryReport.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/SaleListingSummaryReport.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile SaleListingByTransRefReport.jrxml and SaleListingGroupByTransRefReport.jrxml")
    void testCompileSaleListingByTransRefReport() throws Exception {
        java.io.File jrxmlFile1 = new java.io.File("src/main/resources/reports/SaleListingByTransRefReport.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile1.getAbsolutePath());

        java.io.File jrxmlFile2 = new java.io.File("src/main/resources/reports/SaleListingGroupByTransRefReport.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile2.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate SaleListingByTransRefReport grouped by transRef successfully")
    void testGenerateSaleListingByTransRefReport() {
        var command = new com.aureumgrand.report.application.dto.pos.SaleListingDto();
        command.setReportName("SaleListingByTransRefReport");
        command.setFormat("PDF");
        command.setShopName("Aureum Grand Hotel & Luxury Suites");
        command.setStartDate(java.time.LocalDateTime.of(2026, 10, 1, 0, 0));
        command.setEndDate(java.time.LocalDateTime.of(2026, 10, 31, 23, 59));
        command.setCurrencySymbol("$");

        // Transaction 1: INV-2026-0101 (2 items)
        var item1a = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0101")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 9, 15))
                .customerName("លោក សុខ សាន (Mr. Sok San)")
                .seller("គឹម ឡុង (Kim Long)")
                .total(new java.math.BigDecimal("75.00"))
                .cost(new java.math.BigDecimal("45.00"))
                .profit(new java.math.BigDecimal("30.00"))
                .paymentStatus("PAID")
                .build();

        var item1b = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0101")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 9, 15))
                .customerName("លោក សុខ សាន (Mr. Sok San)")
                .seller("គឹម ឡុង (Kim Long)")
                .total(new java.math.BigDecimal("50.00"))
                .cost(new java.math.BigDecimal("30.00"))
                .profit(new java.math.BigDecimal("20.00"))
                .paymentStatus("PAID")
                .build();

        // Transaction 2: INV-2026-0102 (2 items)
        var item2a = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0102")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 11, 30))
                .customerName("Jane Watson")
                .seller("ម៉ៅ សុខា (Mao Sokha)")
                .total(new java.math.BigDecimal("40.00"))
                .cost(new java.math.BigDecimal("20.00"))
                .profit(new java.math.BigDecimal("20.00"))
                .paymentStatus("PAID")
                .build();

        var item2b = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0102")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 11, 30))
                .customerName("Jane Watson")
                .seller("ម៉ៅ សុខា (Mao Sokha)")
                .total(new java.math.BigDecimal("28.50"))
                .cost(new java.math.BigDecimal("15.00"))
                .profit(new java.math.BigDecimal("13.50"))
                .paymentStatus("PAID")
                .build();

        // Transaction 3: INV-2026-0103 (1 item)
        var item3 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0103")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 14, 20))
                .customerName("David Miller")
                .seller("គឹម ឡុង (Kim Long)")
                .total(new java.math.BigDecimal("210.00"))
                .cost(new java.math.BigDecimal("120.00"))
                .profit(new java.math.BigDecimal("90.00"))
                .paymentStatus("PAID")
                .build();

        command.setItems(List.of(item1a, item1b, item2a, item2b, item3));

        ReportResult result = saleListingUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/SaleListingByTransRefReport.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/SaleListingByTransRefReport.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Should compile SaleListingBySellerReport.jrxml and SaleListingGroupBySellerReport.jrxml")
    void testCompileSaleListingBySellerReport() throws Exception {
        java.io.File jrxmlFile1 = new java.io.File("src/main/resources/reports/SaleListingBySellerReport.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile1.getAbsolutePath());

        java.io.File jrxmlFile2 = new java.io.File("src/main/resources/reports/SaleListingGroupBySellerReport.jrxml");
        net.sf.jasperreports.engine.JasperCompileManager.compileReport(jrxmlFile2.getAbsolutePath());
    }

    @Test
    @DisplayName("Should generate SaleListingBySellerReport grouped by seller successfully")
    void testGenerateSaleListingBySellerReport() {
        var command = new com.aureumgrand.report.application.dto.pos.SaleListingDto();
        command.setReportName("SaleListingBySellerReport");
        command.setFormat("PDF");
        command.setShopName("Aureum Grand Hotel & Luxury Suites");
        command.setStartDate(java.time.LocalDateTime.of(2026, 10, 1, 0, 0));
        command.setEndDate(java.time.LocalDateTime.of(2026, 10, 31, 23, 59));
        command.setCurrencySymbol("$");

        // Seller 1: គឹម ឡុង (Kim Long) - 2 transactions
        var item1a = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0101")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 9, 15))
                .customerName("លោក សុខ សាន (Mr. Sok San)")
                .seller("គឹម ឡុង (Kim Long)")
                .total(new java.math.BigDecimal("125.00"))
                .cost(new java.math.BigDecimal("75.00"))
                .profit(new java.math.BigDecimal("50.00"))
                .deliveryFee(new java.math.BigDecimal("2.50"))
                .tips(new java.math.BigDecimal("5.00"))
                .paymentStatus("PAID")
                .build();

        var item1b = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0103")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 14, 20))
                .customerName("David Miller")
                .seller("គឹម ឡុង (Kim Long)")
                .total(new java.math.BigDecimal("210.00"))
                .cost(new java.math.BigDecimal("120.00"))
                .profit(new java.math.BigDecimal("90.00"))
                .deliveryFee(java.math.BigDecimal.ZERO)
                .tips(new java.math.BigDecimal("10.00"))
                .paymentStatus("PAID")
                .build();

        // Seller 2: ម៉ៅ សុខា (Mao Sokha) - 1 transaction
        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0102")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 11, 30))
                .customerName("Jane Watson")
                .seller("ម៉ៅ សុខា (Mao Sokha)")
                .total(new java.math.BigDecimal("68.50"))
                .cost(new java.math.BigDecimal("35.00"))
                .profit(new java.math.BigDecimal("33.50"))
                .deliveryFee(new java.math.BigDecimal("1.50"))
                .tips(new java.math.BigDecimal("2.00"))
                .paymentStatus("PAID")
                .build();

        // Seller 3: ចាន់ ធារី (Chan Theary) - 1 transaction
        var item3 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0104")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 16, 45))
                .customerName("Walk-in Guest")
                .seller("ចាន់ ធារី (Chan Theary)")
                .total(new java.math.BigDecimal("45.00"))
                .cost(new java.math.BigDecimal("22.00"))
                .profit(new java.math.BigDecimal("23.00"))
                .deliveryFee(java.math.BigDecimal.ZERO)
                .tips(java.math.BigDecimal.ZERO)
                .paymentStatus("CREDIT")
                .build();

        command.setItems(List.of(item1a, item1b, item2, item3));

        ReportResult result = saleListingUseCase.execute(command);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0, "Generated report content should not be empty");
        assertEquals(ReportFormat.PDF, result.getFormat());
        try {
            java.nio.file.Files.write(java.nio.file.Path.of("target/SaleListingBySellerReport.pdf"), result.getContent());
            java.nio.file.Files.write(java.nio.file.Path.of("src/main/resources/pdf/SaleListingBySellerReport.pdf"), result.getContent());
        } catch (Exception e) { e.printStackTrace(); }
    }
}
