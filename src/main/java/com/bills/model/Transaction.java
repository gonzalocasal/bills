package com.bills.model;

import org.tinylog.Logger;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Transaction {

    private String name;
    private BigDecimal amount;
    private Date date;
    private String cuit;

    protected static String parseName(String name) {
        StringBuilder capitalizeName = new StringBuilder();
        for (String w : name.toLowerCase().split("\\s")) {
            String first = w.substring(0,1);
            String afterFirst = w.substring(1);
            capitalizeName.append(first.toUpperCase()).append(afterFirst).append(" ");
        }
        return capitalizeName.toString().trim();
    }

    protected static Date parseDate(SimpleDateFormat format, String dateStr) {
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            Logger.error("Error parsing {} as date.", dateStr);
            return null;
        }
    }

    protected static BigDecimal parseAmount(String amountStr) {
        try {
            amountStr = amountStr
                    .replace(".", "")
                    .replace("$", "")
                    .replace(",", "").trim();

            String decimal = amountStr.substring(amountStr.length() - 2);
            String integer = amountStr.substring(0, amountStr.length() - 2);

            return new BigDecimal(integer + "." + decimal);
        } catch (Exception e) {
            Logger.error("Error parsing {} as amount.", amountStr);
        }
        return null;
    }

    protected static String parseCuit(String cuitStr) {
        cuitStr = cuitStr.replaceAll("\\D","").trim();
        String type = cuitStr.substring(0, 2);
        String dni = cuitStr.substring(2, cuitStr.length() - 1);
        String lastNum = cuitStr.substring(cuitStr.length() - 1);
        return type + "-" + dni + "-" + lastNum;
    }

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
