package com.bills.model;

import java.util.List;

import static com.bills.model.TransactionFactory.parseAmount;
import static com.bills.model.TransactionFactory.parseCuit;
import static com.bills.model.TransactionFactory.parseDateBrubank;
import static com.bills.model.TransactionFactory.parseName;

public class BrubankTransaction extends Transaction{

    public BrubankTransaction(List<String> lines) {
        this.setDate(parseDateBrubank(lines.get(3)));
        this.setName(parseName(lines.get(2)));
        this.setAmount(parseAmount(lines.get(4)));
        this.setCuit(parseCuit(lines.get(lines.size() - 4)));
    }
}
