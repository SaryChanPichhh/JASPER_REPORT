package com.aureumgrand.report.application.dto.pos;

import com.aureumgrand.report.application.dto.GenerateReportCommand;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.PosPoListingDataSource;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PosPoListingDto extends GenerateReportCommand {
    private LocalDate startDate;
    private LocalDate endDate;
    private String branchName = "Aureum Grand Hotel & Luxury Suites";
    private List<PosPoListingDataSource> Orders = new ArrayList<>();

    public PosPoListingDto() {
        if (getReportName() == null || getReportName().isBlank()) {
            setReportName("PosPoListingReport");
        }
    }

    public List<PosPoListingDataSource> getOrders() {
        return Orders;
    }

    public void setOrders(List<PosPoListingDataSource> orders) {
        this.Orders = orders;
    }
}
