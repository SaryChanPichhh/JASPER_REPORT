package com.aureumgrand.report.presentation.rest;

import com.aureumgrand.report.application.dto.pos.InvoiceItemDto;
import com.aureumgrand.report.application.dto.pos.PosPoListingDto;
import com.aureumgrand.report.application.dto.pos.PosSaleInvoiceDto;
import com.aureumgrand.report.application.dto.pos.AdjustmentHistoryDto;
import com.aureumgrand.report.application.dto.pos.DailyClosingDetailDto;
import com.aureumgrand.report.application.dto.pos.DailyClosingDto;
import com.aureumgrand.report.application.dto.pos.InventoryDto;
import com.aureumgrand.report.application.dto.pos.PosPurchaseOrderDto;
import com.aureumgrand.report.application.dto.pos.SaleListingDto;
import com.aureumgrand.report.application.dto.pos.SaleListingMovementDto;
import com.aureumgrand.report.application.dto.pos.SaleListingSummaryDto;
import com.aureumgrand.report.application.usecase.pos.AdjustmentHistoryUseCase;
import com.aureumgrand.report.application.usecase.pos.CustomerOrderUseCase;
import com.aureumgrand.report.application.usecase.pos.DailyClosingDetailUseCase;
import com.aureumgrand.report.application.usecase.pos.DailyClosingInventoryUseCase;
import com.aureumgrand.report.application.usecase.pos.InventoryAuditUseCase;
import com.aureumgrand.report.application.usecase.pos.PosPoListingUseCase;
import com.aureumgrand.report.application.usecase.pos.PosPurchaseOrderUseCase;
import com.aureumgrand.report.application.usecase.pos.PosSaleInvoiceUseCase;
import com.aureumgrand.report.application.usecase.pos.SaleListingMovementUseCase;
import com.aureumgrand.report.application.usecase.pos.SaleListingSummaryUseCase;
import com.aureumgrand.report.application.usecase.pos.SaleListingUseCase;
import com.aureumgrand.report.domain.model.ReportResult;
import com.aureumgrand.report.infrastructure.jasper.JasperTemplateLoader;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/reports")
@AllArgsConstructor
public class ReportController {
    private final PosSaleInvoiceUseCase posSaleInvoiceUseCase;
    private final PosPoListingUseCase posPoListingUseCase;
    private final CustomerOrderUseCase customerOrderUseCase;
    private final PosPurchaseOrderUseCase posPurchaseOrderUseCase;
    private final SaleListingMovementUseCase saleListingMovementUseCase;
    private final SaleListingUseCase saleListingUseCase;
    private final DailyClosingDetailUseCase dailyClosingDetailUseCase;
    private final DailyClosingInventoryUseCase dailyClosingInventoryUseCase;
    private final InventoryAuditUseCase inventoryAuditUseCase;
    private final AdjustmentHistoryUseCase adjustmentHistoryUseCase;
    private final SaleListingSummaryUseCase saleListingSummaryUseCase;

    @PostMapping(path = "/customerorder")
    public ResponseEntity<byte[]> customerOrder(@Valid @RequestBody InvoiceItemDto request) {
        var result = customerOrderUseCase.execute(request);
        var fileName = request.getReportName() != null && !request.getReportName().isBlank()
                ? request.getReportName() + result.getFormat().getFileExtension()
                : result.getFileName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .contentLength(result.getSize())
                .body(result.getContent());
    }
    @PostMapping(path = "/pos/pospolisting")
    public ResponseEntity<byte[]> posPoListing(@Valid @RequestBody PosPoListingDto request) {
        var result = posPoListingUseCase.execute(request);
        var fileName = request.getReportName() != null && !request.getReportName().isBlank()
                ? request.getReportName() + result.getFormat().getFileExtension()
                : result.getFileName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .contentLength(result.getSize())
                .body(result.getContent());
    }

    @PostMapping(path = "/pos/saleinvoice")
    public ResponseEntity<byte[]> PosSaleInvoice(@Valid @RequestBody PosSaleInvoiceDto request){
        var result = posSaleInvoiceUseCase.execute(request);
        var fileName = request.getReportName() != null && !request.getReportName().isBlank()
                ? request.getReportName() + result.getFormat().getFileExtension()
                : result.getFileName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .contentLength(result.getSize())
                .body(result.getContent());
    }

    @PostMapping(path = "/pos/purchaseorder")
    public ResponseEntity<byte[]> posPurchaseOrder(@Valid @RequestBody PosPurchaseOrderDto request) {
        var result = posPurchaseOrderUseCase.execute(request);
        var fileName = request.getReportName() != null && !request.getReportName().isBlank()
                ? request.getReportName() + result.getFormat().getFileExtension()
                : result.getFileName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .contentLength(result.getSize())
                .body(result.getContent());
    }

    @PostMapping(path = "/pos/salelistingmovement" )
    public ResponseEntity<byte[]> saleListingMovement(@Valid @RequestBody SaleListingMovementDto request) {
        var result = saleListingMovementUseCase.execute(request);
        var fileName = request.getReportName() != null && !request.getReportName().isBlank()
                ? request.getReportName() + result.getFormat().getFileExtension()
                : result.getFileName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .contentLength(result.getSize())
                .body(result.getContent());
    }

    @PostMapping(path ="/pos/salelisting")
    public ResponseEntity<byte[]> saleListing(@Valid @RequestBody SaleListingDto request) {
        var result = saleListingUseCase.execute(request);
        var fileName = request.getReportName() != null && !request.getReportName().isBlank()
                ? request.getReportName() + result.getFormat().getFileExtension()
                : result.getFileName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .contentLength(result.getSize())
                .body(result.getContent());
    }

    @PostMapping(path = "/dailyclosinginventorydetail")
    public ResponseEntity<byte[]> dailyClosingDetail(@Valid @RequestBody DailyClosingDetailDto request) {
        var result = dailyClosingDetailUseCase.execute(request);
        var fileName = request.getReportName() != null && !request.getReportName().isBlank()
                ? request.getReportName() + result.getFormat().getFileExtension()
                : result.getFileName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .contentLength(result.getSize())
                .body(result.getContent());
    }
    @PostMapping(path = "/dailyclosinginventory")
    public ResponseEntity<byte[]> dailyClosingInventory(@Valid @RequestBody DailyClosingDto request) {
        var result = dailyClosingInventoryUseCase.execute(request);
        var fileName = request.getReportName() != null && !request.getReportName().isBlank()
                ? request.getReportName() + result.getFormat().getFileExtension()
                : result.getFileName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .contentLength(result.getSize())
                .body(result.getContent());
    }
    @PostMapping(path = "/pos/inventoryaudit")
    public ResponseEntity<byte[]> inventoryAudit(@Valid @RequestBody InventoryDto request) {
        var result = inventoryAuditUseCase.execute(request);
        var fileName = request.getReportName() != null && !request.getReportName().isBlank()
                ? request.getReportName() + result.getFormat().getFileExtension()
                : result.getFileName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .contentLength(result.getSize())
                .body(result.getContent());
    }

    @PostMapping(path = "/pos/adjustment-history")
    public ResponseEntity<byte[]> adjustmentHistory(@Valid @RequestBody AdjustmentHistoryDto request) {
        var result = adjustmentHistoryUseCase.execute(request);
        var fileName = request.getReportName() != null && !request.getReportName().isBlank()
                ? request.getReportName() + result.getFormat().getFileExtension()
                : result.getFileName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .contentLength(result.getSize())
                .body(result.getContent());
    }

    @PostMapping(path = "/pos/salelistingsummary")
    public ResponseEntity<byte[]> saleListingSummary(@Valid @RequestBody SaleListingSummaryDto request) {
        var result = saleListingSummaryUseCase.execute(request);
        var fileName = request.getReportName() != null && !request.getReportName().isBlank()
                ? request.getReportName().replace("/", "-") + result.getFormat().getFileExtension()
                : result.getFileName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .contentLength(result.getSize())
                .body(result.getContent());
    }
}
