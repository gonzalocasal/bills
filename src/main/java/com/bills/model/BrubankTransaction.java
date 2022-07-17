package com.bills.model;

import org.tinylog.Logger;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.bills.util.Constants.DATE_FORMAT_TRANSACTION;

public class BrubankTransaction extends Transaction{

    public BrubankTransaction(List<String> lines) {
        super();
        if (lines.isEmpty()) {
            return;
        }
        try {
            SimpleDateFormat dateFormatTransaction = new SimpleDateFormat(DATE_FORMAT_TRANSACTION);
            this.setDate(parseDate(dateFormatTransaction, lines.get(1)));
            this.setName(parseName(lines.get(3)));
            this.setAmount(parseAmount(lines.get(5)));
            this.setCuit(parseCuit(lines.get(lines.size() - 3)));
        } catch (RuntimeException exception) {
            Logger.error("Error occurred trying to generate transaction with: {}", lines);
        }
    }
}
