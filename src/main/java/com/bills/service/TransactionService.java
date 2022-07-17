package com.bills.service;

import com.bills.model.BrubankTransaction;
import com.bills.model.Transaction;
import com.bills.util.TextExtractor;
import org.tinylog.Logger;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.bills.util.Constants.MAXIMUM_BILLABLE;

public class TransactionService {

    private final TextExtractor textExtractor;

    public TransactionService() {
        this.textExtractor = new TextExtractor();
    }

    public List<Transaction> parseTransactions(List<ResponseInputStream<GetObjectResponse>> filesDownloaded) {
        Instant start = Instant.now();
        List<Transaction> transactions = new ArrayList<>();
        filesDownloaded.parallelStream().forEach(f -> generateTransaction(f).ifPresent(transactions::add));
        Logger.info("Transactions parsed in: {}ms.", Duration.between(start, Instant.now()).toMillis());
        return transactions;
    }

    public Optional<Transaction> generateTransaction(ResponseInputStream<GetObjectResponse> s3object) {
        List<String> imageText = textExtractor.getImageText(s3object);
        BrubankTransaction transaction = new BrubankTransaction(imageText);
        if (isValidTransaction(transaction)) {
            return Optional.of(transaction);
        } else {
            Logger.error("Invalid Transaction: {}", transaction);
            Logger.error("Image text: {}", imageText);
            return Optional.empty();
        }
    }

    public List<Transaction> ensureTotalBillable(List<Transaction> transactions) {
        BigDecimal total = transactions.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (MAXIMUM_BILLABLE.compareTo(total) < 0) {
            Logger.error("Total Amount of Transaction: {} is higher than the limit of: {}", total, MAXIMUM_BILLABLE);
            transactions.sort(Comparator.comparing(Transaction::getAmount).reversed());
            return removeToEnsureBillableAmount(transactions, total);
        }
        return transactions;
    }

    private List<Transaction> removeToEnsureBillableAmount(List<Transaction> transactions, BigDecimal total) {
        while (MAXIMUM_BILLABLE.compareTo(total) < 0) {
            Transaction transaction = transactions.get(0);
            total = total.subtract(transaction.getAmount());
            transactions.remove(0);
            Logger.info("Removed transaction: {}", transaction);
        }
        return transactions;
    }

    private boolean isValidTransaction(Transaction transaction) {
        return transaction.getName() != null && transaction.getDate() != null &&
                transaction.getAmount() != null && transaction.getCuit() != null;
    }

}
