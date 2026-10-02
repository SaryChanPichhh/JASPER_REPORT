package com.aureumgrand.report.application.dto;

import com.aureumgrand.report.domain.model.DecimalFormatting;
import com.aureumgrand.report.domain.model.Language;
import com.aureumgrand.report.domain.model.ReportFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GenerateReportCommand implements Serializable {
    private String dbCode;
    private String format = "PDF";
    @NotBlank(message = "Report name must not be blank")
    private String reportName;
    private String connection;
    private Language Language;
    private String currencySymbol;
    private String subCurrencySymbol;
    private DecimalFormatting DecimalPrecision;
    private DecimalFormatting SubDecimalPrecision;
    public GenerateReportCommand() {
    }
    public ReportFormat resolveFormat() {
        return ReportFormat.fromString(this.format);
    }
}
