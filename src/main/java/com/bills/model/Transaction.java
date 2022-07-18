package com.bills.model;

import java.math.BigDecimal;
import java.util.Date;

public abstract class Transaction {

    private String name;
    private BigDecimal amount;
    private Date date;
    private String cuit;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", cuit='" + cuit + '\'' +
                '}';
    }
}
