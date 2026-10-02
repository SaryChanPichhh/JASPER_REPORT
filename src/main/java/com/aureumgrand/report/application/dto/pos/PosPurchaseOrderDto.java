package com.aureumgrand.report.application.dto.pos;

import com.aureumgrand.report.application.dto.GenerateReportCommand;
import com.aureumgrand.report.infrastructure.jasper.datasource.pos.PurchaseOrderDataSource;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PosPurchaseOrderDto extends GenerateReportCommand {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime printDate;
    private String shopName;
    private String shopImage;
    private String checker;
    private LocalDateTime checkDate;
    private String exchangeSign;
    private String note;
    private String description;
    private LocalDateTime receiveDate;
    private String receiveBy;
    private String warehouse;
    private String supplier;
    private String code;
    private List<PurchaseOrderDataSource> items = new ArrayList<>();
    private String field1 = "";
    private String field2 = "";
    private String field3 = "";
    private String field4 = "";
    private String field5 = "";
    private String field6 = "";
    private String field7 = "";
    private String field8 = "";
    private String field9 = "";
    public PosPurchaseOrderDto() {
        if (getReportName() == null || getReportName().isBlank()) {
            setReportName("PosPurchaseOrderReport");
        }
    }
}
