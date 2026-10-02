package com.aureumgrand.report.presentation.rest;

import com.aureumgrand.report.application.dto.pos.PosPoListingDto;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.PosPoListing;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.PosPoListingDataSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/reports/pos/pospolisting should return PDF report attachment")
    void testPosPoListingEndpoint() throws Exception {
        var dto = new PosPoListingDto();
        dto.setReportName("PosPoListingReport");
        dto.setFormat("PDF");
        dto.setStartDate(LocalDate.of(2026, 10, 1));
        dto.setEndDate(LocalDate.of(2026, 10, 10));

        var order = new PosPoListingDataSource();
        order.setReceiveDate(LocalDate.of(2026, 10, 1));
        var item = PosPoListing.builder()
                .time("10:00 AM")
                .warehouse("Main Kitchen")
                .supplierCode("SUP-01")
                .supplierName("Angkor Food")
                .transRef("PO-001")
                .itemCode("ITM-01")
                .itemDesc("Coffee Beans")
                .qty(5)
                .cost(new BigDecimal("10.00"))
                .totalCost(new BigDecimal("50.00"))
                .build();
        order.setItems(List.of(item));
        dto.setOrders(List.of(order));

        mockMvc.perform(post("/api/v1/reports/pos/pospolisting")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/customerorder should return PDF report attachment")
    void testCustomerOrderEndpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.InvoiceItemDto();
        dto.setReportName("CustomerOrderReport");
        dto.setFormat("PDF");
        dto.setCustomerName("Jane Doe");
        dto.setDates("01/10/2026 - 31/10/2026");

        var item = com.aureumgrand.report.infrastructure.jasper.datasource.pos.InvoiceItemDataSource.builder()
                .transRef("INV-1001")
                .invoicDate("10/01/2026")
                .itemCode("ITM-01")
                .itemDesc("Latte")
                .price1(new BigDecimal("3.00"))
                .qty(2)
                .discountPrice(BigDecimal.ZERO)
                .finalPrice(new BigDecimal("3.00"))
                .total(new BigDecimal("6.00"))
                .build();
        dto.setItems(List.of(item));

        mockMvc.perform(post("/api/v1/reports/customerorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/customerorder80 should return 80mm PDF report attachment")
    void testCustomerOrder80Endpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.InvoiceItemDto();
        dto.setReportName("CustomerOrder80Report");
        dto.setFormat("PDF");
        dto.setCustomerName("Jane Doe");
        dto.setDates("01/10/2026 - 31/10/2026");

        var item = com.aureumgrand.report.infrastructure.jasper.datasource.pos.InvoiceItemDataSource.builder()
                .transRef("INV-1001")
                .invoicDate("10/01/2026")
                .itemCode("ITM-01")
                .itemDesc("Latte")
                .price1(new BigDecimal("3.00"))
                .qty(2)
                .discountPrice(BigDecimal.ZERO)
                .finalPrice(new BigDecimal("3.00"))
                .total(new BigDecimal("6.00"))
                .build();
        dto.setItems(List.of(item));

        mockMvc.perform(post("/api/v1/reports/customerorder80")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/pos/customerorder80")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/pos/purchaseorder should return PDF report attachment")
    void testPosPurchaseOrderEndpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.PosPurchaseOrderDto();
        dto.setReportName("PosPurchaseOrderReport");
        dto.setFormat("PDF");
        dto.setCode("PO-2026-0001");
        dto.setShopName("Aureum Grand Hotel & Luxury Suites");
        dto.setSupplier("Angkor Food Supplies");
        dto.setWarehouse("Main Kitchen Warehouse");
        dto.setReceiveBy("Sok San");
        dto.setChecker("Kim Chhay");
        dto.setDescription("Monthly Beverage & Kitchen Supplies");
        dto.setNote("Please deliver during business hours 8am - 5pm");
        dto.setStartDate(java.time.LocalDateTime.of(2026, 10, 1, 8, 0));
        dto.setEndDate(java.time.LocalDateTime.of(2026, 10, 10, 17, 0));
        dto.setReceiveDate(java.time.LocalDateTime.of(2026, 10, 5, 14, 0));
        dto.setCheckDate(java.time.LocalDateTime.of(2026, 10, 2, 9, 30));

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PurchaseOrderDataSource.builder()
                .itemCode("BEV-001")
                .itemDesc("Premium Arabica Coffee Beans (1kg)")
                .category("Beverages")
                .qty(10)
                .cost(new BigDecimal("15.50"))
                .totalCost(new BigDecimal("155.00"))
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PurchaseOrderDataSource.builder()
                .itemCode("DRY-002")
                .itemDesc("Organic Jasmine Rice (25kg)")
                .category("Dry Goods")
                .qty(5)
                .cost(new BigDecimal("28.00"))
                .totalCost(new BigDecimal("140.00"))
                .build();

        dto.setItems(List.of(item1, item2));

        mockMvc.perform(post("/api/v1/reports/pos/purchaseorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/pos/salelistingmovement should return PDF report attachment")
    void testSaleListingMovementEndpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.SaleListingMovementDto();
        dto.setReportName("SaleListingMovementReport");
        dto.setFormat("PDF");
        dto.setShopName("Aureum Grand Hotel & Luxury Suites");
        dto.setStartDate(java.time.LocalDateTime.of(2026, 10, 1, 0, 0));
        dto.setEndDate(java.time.LocalDateTime.of(2026, 10, 31, 23, 59));
        dto.setCurrencySymbol("$");

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingMovementDataSource.builder()
                .itemCode("BEV-001")
                .itemDesc("Premium Arabica Coffee Beans (1kg)")
                .totalPurchaseQty(100)
                .totalPurchaseCost(new BigDecimal("1200.00"))
                .totalSaleQty(75)
                .totalSaleCost(new BigDecimal("900.00"))
                .totalRemainQty(25)
                .totalRemainCost(new BigDecimal("300.00"))
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingMovementDataSource.builder()
                .itemCode("DRY-002")
                .itemDesc("Organic Jasmine Rice (25kg)")
                .totalPurchaseQty(50)
                .totalPurchaseCost(new BigDecimal("1250.00"))
                .totalSaleQty(30)
                .totalSaleCost(new BigDecimal("750.00"))
                .totalRemainQty(20)
                .totalRemainCost(new BigDecimal("500.00"))
                .build();

        dto.setItems(List.of(item1, item2));

        mockMvc.perform(post("/api/v1/reports/pos/salelistingmovement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/salelistingmovement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/pos-salelistingmovement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/pos/salelisting should return PDF report attachment")
    void testSaleListingEndpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.SaleListingDto();
        dto.setReportName("SaleListingReport");
        dto.setFormat("PDF");
        dto.setShopName("Aureum Grand Hotel & Luxury Suites");
        dto.setStartDate(java.time.LocalDateTime.of(2026, 10, 1, 0, 0));
        dto.setEndDate(java.time.LocalDateTime.of(2026, 10, 31, 23, 59));
        dto.setCurrencySymbol("$");

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0001")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 10, 30))
                .customerName("Sok San")
                .seller("Cashier 01")
                .total(new BigDecimal("120.00"))
                .cost(new BigDecimal("70.00"))
                .profit(new BigDecimal("50.00"))
                .paymentStatus("PAID")
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0002")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 11, 45))
                .customerName("John Smith")
                .seller("Cashier 02")
                .total(new BigDecimal("85.50"))
                .cost(new BigDecimal("45.00"))
                .profit(new BigDecimal("40.50"))
                .paymentStatus("PAID")
                .build();

        dto.setItems(List.of(item1, item2));

        mockMvc.perform(post("/api/v1/reports/pos/salelisting")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/pos-dailyclosingdetail should return PDF report attachment")
    void testDailyClosingDetailEndpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.DailyClosingDetailDto();
        dto.setReportName("DailyClosingInventoryDetailA4Report");
        dto.setFormat("PDF");
        dto.setShopName("Aureum Grand Hotel & Luxury Suites");
        dto.setDuration("Morning Shift");
        dto.setSubtotal("250.00");
        dto.setTotalDollar("250.00");

        var closing = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDetailDataSource.builder()
                .seller("Cashier 01")
                .Dates("01/10/2026")
                .items(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("BEV-01")
                                .itemDesc("Espresso")
                                .categoryDesc("Coffee")
                                .qty(2)
                                .price(new BigDecimal("3.50"))
                                .finalPrice(new BigDecimal("3.50"))
                                .total(new BigDecimal("7.00"))
                                .build()
                ))
                .Payments(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("CASH")
                                .totalReceived("7.00")
                                .currencySymbol("$")
                                .build()
                ))
                .build();
        dto.setClosings(List.of(closing));

        mockMvc.perform(post("/api/v1/reports/pos-dailyclosingdetail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/pos/dailyclosingdetail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/dailyclosinginventorydetaila4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/dailyclosinginventorydetail80mm should return 80mm PDF report attachment")
    void testDailyClosingInventoryDetail80mmEndpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.DailyClosingDetailDto();
        dto.setReportName("DailyClosingInventoryDetail80mmReport");
        dto.setFormat("PDF");
        dto.setShopName("Aureum Grand Hotel & Luxury Suites");
        dto.setDuration("Morning Shift");
        dto.setSubtotal("250.00");
        dto.setTotalDollar("250.00");

        var closing = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDetailDataSource.builder()
                .seller("Cashier 01")
                .Dates("01/10/2026")
                .items(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("BEV-01")
                                .itemDesc("Espresso")
                                .categoryDesc("Coffee")
                                .qty(2)
                                .price(new BigDecimal("3.50"))
                                .finalPrice(new BigDecimal("3.50"))
                                .total(new BigDecimal("7.00"))
                                .build()
                ))
                .Payments(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("CASH")
                                .totalReceived("7.00")
                                .currencySymbol("$")
                                .build()
                ))
                .build();
        dto.setClosings(List.of(closing));

        mockMvc.perform(post("/api/v1/reports/dailyclosinginventorydetail80mm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/pos/dailyclosinginventorydetail80mm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/dailyclosinginventorybycategorya4 should return PDF report attachment")
    void testDailyClosingInventoryByCategoryA4Endpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.DailyClosingDetailDto();
        dto.setReportName("DailyClosingInventoryByCategoryA4Report");
        dto.setFormat("PDF");
        dto.setShopName("Aureum Grand Hotel & Luxury Suites");
        dto.setDuration("Morning Shift");
        dto.setSubtotal("250.00");
        dto.setTotalDollar("250.00");

        var closing = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDetailDataSource.builder()
                .seller("Cashier 01")
                .Dates("01/10/2026")
                .items(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("BEV-01")
                                .itemDesc("Espresso")
                                .categoryCode(1)
                                .categoryDesc("Coffee")
                                .qty(2)
                                .price(new BigDecimal("3.50"))
                                .finalPrice(new BigDecimal("3.50"))
                                .total(new BigDecimal("7.00"))
                                .build()
                ))
                .Payments(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("CASH")
                                .totalReceived("7.00")
                                .currencySymbol("$")
                                .build()
                ))
                .build();
        dto.setClosings(List.of(closing));

        mockMvc.perform(post("/api/v1/reports/dailyclosinginventorybycategorya4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/pos/dailyclosinginventorybycategory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/dailyclosinginventorybycategory80 should return 80mm PDF report attachment")
    void testDailyClosingInventoryByCategory80Endpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.DailyClosingDetailDto();
        dto.setReportName("DailyClosingInventoryByCategory80Report");
        dto.setFormat("PDF");
        dto.setShopName("Aureum Grand Hotel & Luxury Suites");
        dto.setDuration("Morning Shift");
        dto.setSubtotal("250.00");
        dto.setTotalDollar("250.00");

        var closing = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDetailDataSource.builder()
                .seller("Cashier 01")
                .Dates("01/10/2026")
                .items(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.ItemDataSource.builder()
                                .itemCode("BEV-01")
                                .itemDesc("Espresso")
                                .categoryCode(1)
                                .categoryDesc("Coffee")
                                .qty(2)
                                .price(new BigDecimal("3.50"))
                                .finalPrice(new BigDecimal("3.50"))
                                .total(new BigDecimal("7.00"))
                                .build()
                ))
                .Payments(List.of(
                        com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                                .paymentType("CASH")
                                .totalReceived("7.00")
                                .currencySymbol("$")
                                .build()
                ))
                .build();
        dto.setClosings(List.of(closing));

        mockMvc.perform(post("/api/v1/reports/dailyclosinginventorybycategory80")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/pos/dailyclosinginventorybycategory80")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/dailyclosinginventory should return PDF report attachment")
    void testDailyClosingInventoryEndpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.DailyClosingDto();
        dto.setReportName("DailyClosingInventoryA4Report");
        dto.setFormat("PDF");
        dto.setShopName("Aureum Grand Hotel & Luxury Suites");
        dto.setSeller("Sok Chandara");
        dto.setDates("02-10-2026");
        dto.setDuration("Morning Shift");
        dto.setSubtotal(new BigDecimal("100.00"));
        dto.setTotalDollar(new BigDecimal("100.00"));

        var item = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDataSource.builder()
                .itemCode("BEV-001")
                .itemDesc("Latte")
                .categoryDesc("Coffee")
                .qty(5)
                .price(new BigDecimal("4.00"))
                .finalPrice(new BigDecimal("4.00"))
                .total(new BigDecimal("20.00"))
                .build();
        dto.setItems(List.of(item));

        var payment = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                .paymentType("CASH")
                .totalReceived("20.00")
                .currencySymbol("$")
                .build();
        dto.setPayments(List.of(payment));

        var expense = com.aureumgrand.report.infrastructure.jasper.datasource.pos.ExpenseDataSource.builder()
                .expenseDesc("Cleaning materials")
                .ExpenseBy("Sokha")
                .ExpenseAmount("5.00")
                .CurrencySymbol("$")
                .build();
        dto.setExpenses(List.of(expense));

        mockMvc.perform(post("/api/v1/reports/dailyclosinginventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/pos/dailyclosinginventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/dailyclosinginventory80 should return 80mm PDF report attachment")
    void testDailyClosingInventory80Endpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.DailyClosingDto();
        dto.setReportName("DailyClosingInventory80Report");
        dto.setFormat("PDF");
        dto.setShopName("Aureum Grand Hotel & Luxury Suites");
        dto.setSeller("Sok Chandara");
        dto.setDates("02-10-2026");
        dto.setDuration("Morning Shift");
        dto.setSubtotal(new BigDecimal("100.00"));
        dto.setTotalDollar(new BigDecimal("100.00"));

        var item = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDataSource.builder()
                .itemCode("BEV-001")
                .itemDesc("Latte")
                .categoryDesc("Coffee")
                .qty(5)
                .price(new BigDecimal("4.00"))
                .finalPrice(new BigDecimal("4.00"))
                .total(new BigDecimal("20.00"))
                .build();
        dto.setItems(List.of(item));

        var payment = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                .paymentType("CASH")
                .totalReceived("20.00")
                .currencySymbol("$")
                .build();
        dto.setPayments(List.of(payment));

        var expense = com.aureumgrand.report.infrastructure.jasper.datasource.pos.ExpenseDataSource.builder()
                .expenseDesc("Cleaning materials")
                .ExpenseBy("Sokha")
                .ExpenseAmount("5.00")
                .CurrencySymbol("$")
                .build();
        dto.setExpenses(List.of(expense));

        mockMvc.perform(post("/api/v1/reports/dailyclosinginventory80")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/pos/dailyclosinginventory80")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/dailyclosinginventory with DailyClosingInventoryByCategory80Report and A4 should succeed")
    void testDailyClosingInventoryByCategoryViaDailyClosingInventoryEndpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.DailyClosingDto();
        dto.setReportName("DailyClosingInventoryByCategory80Report");
        dto.setFormat("PDF");
        dto.setShopName("Aureum Grand Hotel & Luxury Suites");
        dto.setSeller("Sok Chandara");
        dto.setDates("02-10-2026");
        dto.setDuration("Morning Shift");
        dto.setSubtotal(new BigDecimal("100.00"));
        dto.setDiscount(new BigDecimal("5.00"));
        dto.setTotalDollar(new BigDecimal("95.00"));

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDataSource.builder()
                .itemCode("BEV-001")
                .itemDesc("Latte")
                .categoryCode("BEV")
                .categoryDesc("Beverages")
                .qty(5)
                .price(new BigDecimal("4.00"))
                .discountPrice(new BigDecimal("0.50"))
                .finalPrice(new BigDecimal("3.50"))
                .total(new BigDecimal("17.50"))
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.DailyClosingDataSource.builder()
                .itemCode("FOD-001")
                .itemDesc("Croissant")
                .categoryCode("FOOD")
                .categoryDesc("Bakery")
                .qty(2)
                .price(new BigDecimal("3.00"))
                .discountPrice(BigDecimal.ZERO)
                .finalPrice(new BigDecimal("3.00"))
                .total(new BigDecimal("6.00"))
                .build();

        dto.setItems(List.of(item1, item2));

        var payment = com.aureumgrand.report.infrastructure.jasper.datasource.pos.PaymentDataSource.builder()
                .paymentType("CASH")
                .totalReceived("23.50")
                .currencySymbol("$")
                .build();
        dto.setPayments(List.of(payment));

        // Test 80mm via /dailyclosinginventory
        mockMvc.perform(post("/api/v1/reports/dailyclosinginventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        // Test A4 via /dailyclosinginventory
        dto.setReportName("DailyClosingInventoryByCategoryA4Report");
        mockMvc.perform(post("/api/v1/reports/dailyclosinginventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/pos/inventoryaudit should return PDF report attachment")
    void testInventoryAuditEndpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.InventoryDto();
        dto.setReportName("InventoryAuditReport");
        dto.setFormat("PDF");
        dto.setShopName("Aureum Grand Hotel & Luxury Suites");
        dto.setWarehouse("Main Kitchen Store");
        dto.setShowCost(true);
        dto.setShowSalePrice(true);

        var item = com.aureumgrand.report.infrastructure.jasper.datasource.pos.InventoryDataSource.builder()
                .category("Dry Goods")
                .itemCode("DRY-01")
                .itemBarcode("884000111222")
                .itemDesc("Jasmine Rice 25kg")
                .stock(10)
                .costPrice(new BigDecimal("22.50"))
                .salePrice(new BigDecimal("30.00"))
                .build();
        dto.setItems(List.of(item));

        mockMvc.perform(post("/api/v1/reports/pos/inventoryaudit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/inventoryaudit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/pos-inventoryaudit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/pos/adjustment-history should return PDF report attachment")
    void testAdjustmentHistoryEndpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.AdjustmentHistoryDto();
        dto.setReportName("AdjustmentHistoryReport");
        dto.setFormat("PDF");
        dto.setShopName("Aureum Grand Hotel & Luxury Suites");
        dto.setWarehouse("Main Kitchen");
        dto.setStartDate("01-10-2026");
        dto.setEndDate("31-10-2026");
        dto.setShowCost(true);

        var item = com.aureumgrand.report.infrastructure.jasper.datasource.pos.AdjustmentHistoryDataSource.builder()
                .wareCode("WH-01")
                .warehouse("Main Kitchen")
                .adjustmentDate("02-10-2026 10:00")
                .adjustmentRef("ADJ-001")
                .itemCode("ING-01")
                .itemBarCode("884001")
                .itemDesc("Olive Oil 5L")
                .quantity(2)
                .cost(25.0)
                .total(50.0)
                .adjustedBy("Chef Dara")
                .build();
        dto.setItems(List.of(item));

        mockMvc.perform(post("/api/v1/reports/pos/adjustment-history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/pos-adjustment-history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/adjustment-history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/pos/salelistingsummary should return PDF report attachment")
    void testSaleListingSummaryEndpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.SaleListingSummaryDto();
        dto.setReportName("SaleListingSummaryReport");
        dto.setFormat("PDF");
        dto.setShopName("Aureum Grand Hotel & Luxury Suites");
        dto.setStartDate(java.time.LocalDateTime.of(2026, 10, 1, 0, 0));
        dto.setEndDate(java.time.LocalDateTime.of(2026, 10, 31, 23, 59));
        dto.setCurrencySymbol("$");

        var item = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingSummaryDataSource.builder()
                .date("01-10-2026")
                .salePrice(new BigDecimal("1500.00"))
                .discount(new BigDecimal("50.00"))
                .cost(new BigDecimal("600.00"))
                .deliveryFee(new BigDecimal("30.00"))
                .expense(new BigDecimal("40.00"))
                .build();
        dto.setItems(List.of(item));

        mockMvc.perform(post("/api/v1/reports/pos/salelistingsummary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/pos-salelistingsummary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/salelistingsummary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/pos/salelistingbytransref should return PDF report attachment grouped by transRef")
    void testSaleListingByTransRefEndpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.SaleListingDto();
        dto.setReportName("SaleListingByTransRefReport");
        dto.setFormat("PDF");
        dto.setShopName("Aureum Grand Hotel & Luxury Suites");
        dto.setStartDate(java.time.LocalDateTime.of(2026, 10, 1, 0, 0));
        dto.setEndDate(java.time.LocalDateTime.of(2026, 10, 31, 23, 59));
        dto.setCurrencySymbol("$");

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0001")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 10, 30))
                .customerName("Sok San")
                .seller("Cashier 01")
                .total(new BigDecimal("120.00"))
                .cost(new BigDecimal("70.00"))
                .profit(new BigDecimal("50.00"))
                .paymentStatus("PAID")
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0001")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 10, 30))
                .customerName("Sok San")
                .seller("Cashier 01")
                .total(new BigDecimal("80.00"))
                .cost(new BigDecimal("40.00"))
                .profit(new BigDecimal("40.00"))
                .paymentStatus("PAID")
                .build();

        dto.setItems(List.of(item1, item2));

        mockMvc.perform(post("/api/v1/reports/pos/salelistingbytransref")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/pos/salelistinggroupbytransref")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/salelistingbytransref")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("POST /api/v1/reports/pos/salelistingbyseller should return PDF report attachment grouped by seller")
    void testSaleListingBySellerEndpoint() throws Exception {
        var dto = new com.aureumgrand.report.application.dto.pos.SaleListingDto();
        dto.setReportName("SaleListingBySellerReport");
        dto.setFormat("PDF");
        dto.setShopName("Aureum Grand Hotel & Luxury Suites");
        dto.setStartDate(java.time.LocalDateTime.of(2026, 10, 1, 0, 0));
        dto.setEndDate(java.time.LocalDateTime.of(2026, 10, 31, 23, 59));
        dto.setCurrencySymbol("$");

        var item1 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0001")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 10, 30))
                .customerName("Sok San")
                .seller("Cashier 01")
                .total(new BigDecimal("120.00"))
                .cost(new BigDecimal("70.00"))
                .profit(new BigDecimal("50.00"))
                .paymentStatus("PAID")
                .build();

        var item2 = com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource.builder()
                .transRef("INV-2026-0002")
                .transDate(java.time.LocalDateTime.of(2026, 10, 1, 11, 00))
                .customerName("Jane Watson")
                .seller("Cashier 02")
                .total(new BigDecimal("80.00"))
                .cost(new BigDecimal("40.00"))
                .profit(new BigDecimal("40.00"))
                .paymentStatus("PAID")
                .build();

        dto.setItems(List.of(item1, item2));

        mockMvc.perform(post("/api/v1/reports/pos/salelistingbyseller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/pos/salelistinggroupbyseller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(post("/api/v1/reports/salelistingbyseller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }
}
