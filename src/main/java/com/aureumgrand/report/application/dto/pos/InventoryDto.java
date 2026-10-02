package com.aureumgrand.report.application.dto.pos;

import com.aureumgrand.report.application.dto.GenerateReportCommand;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.InventoryDataSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class InventoryDto extends GenerateReportCommand {
    private String shopName;
    private String shopImage;
    private LocalDateTime printDate;
    private String warehouse;
    private String auditor;
    private String currencySymbol = "$";
    private boolean isShowCost = true;
    private boolean isShowSalePrice = true;
    private List<InventoryDataSource> items = new ArrayList<>();

    public InventoryDto() {
        if (getReportName() == null || getReportName().isBlank()) {
            setReportName("InventoryAuditReport");
        }
    }
}
