package com.bills;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.bills.model.Transaction;
import com.bills.processor.S3Processor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Application implements RequestHandler<Void, String> {

    public static void main(String[] args) {
        new S3Processor().processTransactions();
    }

    @Override
    public String handleRequest(Void input, Context context) {
        Instant start = Instant.now();
        List<Transaction> transactions = new S3Processor().processTransactions();
        return String.format("Process Completed in: %d ms. %n Transactions parsed: %s",
                Duration.between(start, Instant.now()).toMillis(), transactions);
    }

}