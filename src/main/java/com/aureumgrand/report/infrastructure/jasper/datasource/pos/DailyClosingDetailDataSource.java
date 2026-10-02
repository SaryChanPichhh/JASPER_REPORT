package com.aureumgrand.report.infrastructure.jasper.datasource.pos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyClosingDetailDataSource {
    private String seller;
    private String Dates;
    @Builder.Default
    private List<ItemDataSource> items = new ArrayList<>();
    @Builder.Default
    private List<PaymentDataSource> Payments = new ArrayList<>();
}
