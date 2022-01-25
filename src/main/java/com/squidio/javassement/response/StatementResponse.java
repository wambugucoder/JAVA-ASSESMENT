package com.squidio.javassement.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

public class StatementResponse implements Serializable {
    @JsonProperty("accountNumber")
    private String accountNumber;
    @JsonProperty("description")
    private String description;
    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("date")
    private LocalDate date;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) throws NoSuchAlgorithmException {
        this.accountNumber = new Account().hashAccountNumber(accountNumber);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = Double.parseDouble(amount);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = LocalDate.parse(date);
    }
}
