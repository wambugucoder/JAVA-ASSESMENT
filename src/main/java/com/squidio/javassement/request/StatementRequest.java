package com.squidio.javassement.request;

import java.io.Serializable;
import java.time.LocalDate;

public class StatementRequest {
    private String accountId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Double fromAmount;
    private Double toAmount;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = LocalDate.parse(fromDate);
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = LocalDate.parse(toDate);
    }

    public Double getFromAmount() {
        return fromAmount;
    }

    public void setFromAmount(Double fromAmount) {
        this.fromAmount = fromAmount;
    }

    public Double getToAmount() {
        return toAmount;
    }

    public void setToAmount(Double toAmount) {
        this.toAmount = toAmount;
    }
}
