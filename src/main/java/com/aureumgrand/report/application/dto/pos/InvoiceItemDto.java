package com.aureumgrand.report.application.dto.pos;

import com.aureumgrand.report.application.dto.GenerateReportCommand;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.InvoiceItemDataSource;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InvoiceItemDto extends GenerateReportCommand {
    private String customerName;
    private String dates;
    private String branchName = "Aureum Grand Hotel & Luxury Suites";
    private List<InvoiceItemDataSource> items = new ArrayList<>();

    public InvoiceItemDto() {
        if (getReportName() == null || getReportName().isBlank()) {
            setReportName("CustomerOrderReport");
        }
    }
}
