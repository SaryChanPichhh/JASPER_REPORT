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
public class PaymentDataSource {
    private String paymentType;
    private String totalReceived;
    @Builder.Default
    private String currencySymbol = "$";
}
