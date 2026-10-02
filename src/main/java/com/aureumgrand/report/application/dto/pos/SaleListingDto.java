package com.aureumgrand.report.application.dto.pos;

import com.aureumgrand.report.application.dto.GenerateReportCommand;
import com.aureumgrand.report.domain.model.ReportMode;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingDataSource;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SaleListingDto extends GenerateReportCommand {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime printDate;
    private String shopName;
    private String shopImage;
    private ReportMode reportMode = ReportMode.NORMAL;

    @JsonAlias({"items", "data"})
    private List<SaleListingDataSource> items = new ArrayList<>();

    public SaleListingDto() {
        if (getReportName() == null || getReportName().isBlank()) {
            setReportName("SaleListingReport");
        }
    }

    public List<SaleListingDataSource> getData() {
        return this.items;
    }

    public void setData(List<SaleListingDataSource> data) {
        this.items = data;
    }
}
