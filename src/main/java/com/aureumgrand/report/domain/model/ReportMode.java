package com.aureumgrand.report.domain.model;

import lombok.Getter;

@Getter
public enum ReportMode {
    NORMAL(1),
    DELIVERY(2);
    private final int value;
    ReportMode(int value){
        this.value = value;
    }
}
