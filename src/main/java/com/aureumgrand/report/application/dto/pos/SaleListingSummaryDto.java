package com.aureumgrand.report.application.dto.pos;

import com.aureumgrand.report.application.dto.GenerateReportCommand;
import com.aureumgrand.report.domain.model.ReportMode;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingSummaryDataSource;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SaleListingSummaryDto extends GenerateReportCommand {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime printDate;
    private String shopName;
    private String shopImage;
    private ReportMode reportMode = ReportMode.NORMAL;

    @JsonAlias({"items", "data"})
    private List<SaleListingSummaryDataSource> data = new ArrayList<>();

    private String exchangeSign = "$";

    public SaleListingSummaryDto() {
        if (getReportName() == null || getReportName().isBlank()) {
            setReportName("SaleListingSummaryReport");
        }
    }

    public List<SaleListingSummaryDataSource> getItems() {
        return this.data;
    }

    public void setItems(List<SaleListingSummaryDataSource> items) {
        this.data = items;
    }
}
