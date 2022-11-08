package com.bills.model;

import java.util.List;
import java.util.stream.IntStream;

import static com.bills.model.TransactionFactory.parseAmount;
import static com.bills.model.TransactionFactory.parseCuit;
import static com.bills.model.TransactionFactory.parseDate;
import static com.bills.model.TransactionFactory.parseName;
import static com.bills.util.Constants.BRUBANK_LINES_PIVOT;

public class BrubankTransaction extends Transaction{

    public BrubankTransaction(List<String> lines) {

        int index = IntStream.range(0, lines.size())
                .filter(i -> lines.get(i).equalsIgnoreCase(BRUBANK_LINES_PIVOT))
                .findFirst().orElse(0);

        this.setName(parseName(lines.get(index + 1)));
        this.setDate(parseDate(lines.get(index + 2)));
        this.setAmount(parseAmount(lines.get(index + 3)));
        this.setCuit(parseCuit(lines.get(lines.size() - 4)));
    }
}
