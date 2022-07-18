package com.bills.model;

import java.util.List;

import static com.bills.model.TransactionFactory.parseAmountSantander;
import static com.bills.model.TransactionFactory.parseCuitSantander;
import static com.bills.model.TransactionFactory.parseDateSantander;
import static com.bills.model.TransactionFactory.parseNameSantander;

public class SantanderTransaction extends Transaction{

    public SantanderTransaction(List<String> lines) {
        this.setDate(parseDateSantander(lines.get(7)));
        this.setName(parseNameSantander(lines.get(4)));
        this.setAmount(parseAmountSantander(lines.get(10)));
        this.setCuit(parseCuitSantander(lines.get(5)));
    }
}
