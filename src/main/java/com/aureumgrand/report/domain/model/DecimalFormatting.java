package com.aureumgrand.report.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum DecimalFormatting {
    Standard(
            0,
            "{0:#,###,##0}"
    ),
    OneDecimalPrecision(
            1,
            "{0:# ### ##0.#}"
    ),
    TwoDecimalPrecision(
            2,
            "{0:# ### ##0.##}"
    ),
    ThreeDecimalPrecision(
            3,
            "{0:# ### ##0.###}"
    ),

    FourDecimalPrecision(
            4,
            "{0:# ### ##0.####}"
    ),

    FiveDecimalPrecision(
            5,
            "{0:# ### ##0.#####}"
    ),

    SixDecimalPrecision(
            6,
            "{0:# ### ##0.######}"
    ),

    OneDecimalWithTrailingZero(
            11,
            "{0:# ### ##0.0}"
    ),

    TwoDecimalWithTrailingZero(
            12,
            "{0:# ### ##0.00}"
    ),

    ThreeDecimalWithTrailingZero(
            13,
            "{0:# ### ##0.000}"
    ),

    FourDecimalWithTrailingZero(
            14,
            "{0:# ### ##0.0000}"
    ),

    FiveDecimalWithTrailingZero(
            15,
            "{0:# ### ##0.00000}"
    ),

    SixDecimalWithTrailingZero(
            16,
            "{0:# ### ##0.000000}"
    );

    private final int value;
    private final String description;

    DecimalFormatting(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
