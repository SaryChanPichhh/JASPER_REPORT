package com.aureumgrand.report.application.dto.pos;

import com.aureumgrand.report.application.dto.GenerateReportCommand;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.SaleListingMovementDataSource;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SaleListingMovementDto extends GenerateReportCommand {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime printDate;
    private String shopName;
    private String shopImage;
    private List<SaleListingMovementDataSource> items = new ArrayList<>();

    public SaleListingMovementDto() {
        if (getReportName() == null || getReportName().isBlank()) {
            setReportName("SaleListingMovementReport");
        }
    }
}
