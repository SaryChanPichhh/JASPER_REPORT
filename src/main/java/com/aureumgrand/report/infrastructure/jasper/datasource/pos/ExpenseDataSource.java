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
public class ExpenseDataSource {
    private String expenseDesc;
    private String ExpenseBy;
    private String ExpenseAmount;
    @Builder.Default
    private String CurrencySymbol = "$";

    public String getExpenseBy() {
        return ExpenseBy;
    }

    public String getExpenseAmount() {
        return ExpenseAmount;
    }

    public String getCurrencySymbol() {
        return CurrencySymbol;
    }
}
