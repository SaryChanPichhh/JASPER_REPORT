package com.aureumgrand.report.application.dto.pos;

import com.aureumgrand.report.application.dto.GenerateReportCommand;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.AdjustmentHistoryDataSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AdjustmentHistoryDto extends GenerateReportCommand {
    private boolean isShowCost = true;
    private String shopName;
    private String shopImage;
    private String warehouse;
    private String startDate;
    private String endDate;
    private String adjustedBy;
    private String currencySymbol = "$";
    private LocalDateTime printDate;
    private List<AdjustmentHistoryDataSource> items = new ArrayList<>();
    public AdjustmentHistoryDto() {
        if (getReportName() == null || getReportName().isBlank()) {
            setReportName("AdjustmentHistoryReport");
        }
    }
}
