package com.bills.util;

import com.bills.model.Transaction;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.bills.util.Constants.COLUMN_SEPARATOR;
import static com.bills.util.Constants.DATE_FORMAT_TRANSACTION;

public class CSVGenerator {

    private final SimpleDateFormat transactionDateFormatter = new SimpleDateFormat(DATE_FORMAT_TRANSACTION);

    public String generateCSVStringLines(List<Transaction> transactions) {
        transactions.sort(Comparator.comparing(Transaction::getDate));
        NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
        BigDecimal totalAmount = BigDecimal.ZERO;
        StringBuilder csvLinesBuilder = new StringBuilder();
        appendHeader(csvLinesBuilder);
        for (Transaction transaction : transactions) {
            totalAmount = totalAmount.add(transaction.getAmount());
            appendLine(csvLinesBuilder, dollarFormat, transaction);
        }
        appendTotalAmount(csvLinesBuilder, dollarFormat, totalAmount);

        return csvLinesBuilder.toString();
    }

    private void appendHeader(StringBuilder csvLinesBuilder) {
        csvLinesBuilder.append("Fecha")
                .append(COLUMN_SEPARATOR)
                .append("Nombre")
                .append(COLUMN_SEPARATOR)
                .append("CUIT")
                .append(COLUMN_SEPARATOR)
                .append("Monto")
                .append(System.lineSeparator());
    }

    private void appendLine(StringBuilder csvLinesBuilder, NumberFormat dollarFormat, Transaction transaction) {
        String dollarFormatted = dollarFormat.format(transaction.getAmount());
        csvLinesBuilder.append(transactionDateFormatter.format(transaction.getDate()))
                .append(COLUMN_SEPARATOR)
                .append(transaction.getName())
                .append(COLUMN_SEPARATOR)
                .append(transaction.getCuit())
                .append(COLUMN_SEPARATOR)
                .append(dollarFormatted.replace(dollarFormatted.substring(0,2) ,"$ "))
                .append(System.lineSeparator());
    }

    private void appendTotalAmount(StringBuilder csvLinesBuilder, NumberFormat dollarFormat, BigDecimal totalAmount) {
        String dollarFormatted = dollarFormat.format(totalAmount);
        csvLinesBuilder.append(COLUMN_SEPARATOR)
                .append(COLUMN_SEPARATOR)
                .append(COLUMN_SEPARATOR)
                .append(dollarFormatted.replace(dollarFormatted.substring(0,2) ,"$ "));
    }

}
