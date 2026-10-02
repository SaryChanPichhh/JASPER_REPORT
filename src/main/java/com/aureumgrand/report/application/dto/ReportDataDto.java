package com.aureumgrand.report.application.dto;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

public class ReportDataDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemCode;
    private String description;
    private Integer quantity;
    private Double unitPrice;
    private Double amount;
    private Date date;
    private String category;
    private String notes;

    public ReportDataDto() {
    }

    public ReportDataDto(String itemCode, String description, Integer quantity, Double unitPrice, Double amount, Date date, String category) {
        this.itemCode = itemCode;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount != null ? amount : (quantity != null && unitPrice != null ? quantity * unitPrice : 0.0);
        this.date = date;
        this.category = category;
    }

    public static ReportDataDto of(String itemCode, String description, Integer quantity, Double unitPrice, LocalDate localDate, String category) {
        Date sqlDate = localDate != null ? Date.valueOf(localDate) : null;
        Double calculatedAmount = (quantity != null && unitPrice != null) ? quantity * unitPrice : 0.0;
        return new ReportDataDto(itemCode, description, quantity, unitPrice, calculatedAmount, sqlDate, category);
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        recalculateAmount();
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
        recalculateAmount();
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    private void recalculateAmount() {
        if (this.amount == null && this.quantity != null && this.unitPrice != null) {
            this.amount = this.quantity * this.unitPrice;
        }
    }
}
