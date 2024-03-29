package com.bills.service;

import com.bills.model.Transaction;
import com.bills.model.TransactionFactory;
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
    private final BigDecimal maximumBillable;

    public TransactionService() {
        String maximumBillableEnv = System.getenv(MAXIMUM_BILLABLE);
        this.maximumBillable = maximumBillableEnv != null ? new BigDecimal(Integer.parseInt(maximumBillableEnv)) : null;
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
        Transaction transaction = TransactionFactory.getTransaction(imageText);
        if (isValidTransaction(transaction)) {
            return Optional.of(transaction);
        } else {
            Logger.error("Invalid Transaction: {}", transaction);
            Logger.error("Image text: {}", imageText);
            return Optional.empty();
        }
    }

    public List<Transaction> ensureTotalBillable(List<Transaction> transactions) {
        if (maximumBillable == null) {
            return transactions;
        }
        BigDecimal total = transactions.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (maximumBillable.compareTo(total) < 0) {
            Logger.error("Total Amount of Transaction: {} is higher than the limit of: {}", total, maximumBillable);
            transactions.sort(Comparator.comparing(Transaction::getAmount).reversed());
            return removeToEnsureBillableAmount(transactions, total);
        }
        return transactions;
    }

    private List<Transaction> removeToEnsureBillableAmount(List<Transaction> transactions, BigDecimal total) {
        while (maximumBillable.compareTo(total) < 0) {
            Transaction transaction = transactions.get(0);
            total = total.subtract(transaction.getAmount());
            transactions.remove(0);
            Logger.info("Removed transaction: {}", transaction);
        }
        return transactions;
    }

    private boolean isValidTransaction(Transaction transaction) {
        return transaction != null &&
                transaction.getName() != null &&
                transaction.getDate() != null &&
                transaction.getAmount() != null &&
                transaction.getCuit() != null;
    }

}
