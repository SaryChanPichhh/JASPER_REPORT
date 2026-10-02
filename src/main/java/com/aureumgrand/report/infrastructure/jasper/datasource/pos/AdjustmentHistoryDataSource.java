package com.aureumgrand.report.infrastructure.jasper.datasource.pos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdjustmentHistoryDataSource {
    @Builder.Default
    private String wareCode = "";
    @Builder.Default
    private String warehouse = "";
    @Builder.Default
    private String adjustmentDate = "";
    @Builder.Default
    private String adjustmentRef = "";
    @Builder.Default
    private String itemCode = "";
    @Builder.Default
    private String itemBarCode = "";
    @Builder.Default
    private String itemDesc = "";
    private int quantity;
    private double cost;
    private double total;
    @Builder.Default
    private String adjustedBy = "";
}
