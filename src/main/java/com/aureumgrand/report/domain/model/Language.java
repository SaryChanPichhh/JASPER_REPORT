package com.aureumgrand.report.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum Language {
    KM(1,"KM"),ZH_CH(3,"ZH_CN"),ZH_TW(4,"ZH_TW"),ENG(2,"ENG");
    private final int value;
    private final String description;
    Language(int value,String description){
        this.value = value;
        this.description = description;
    }
}
