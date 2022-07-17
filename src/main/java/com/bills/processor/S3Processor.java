package com.bills.processor;

import com.bills.model.Transaction;
import com.bills.service.TransactionService;
import com.bills.util.CSVGenerator;
import com.bills.util.S3ClientProvider;
import org.tinylog.Logger;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.bills.util.Constants.AWS_S3_BILLS_BUCKET;
import static com.bills.util.Constants.DATE_FORMAT_FOLDER;
import static com.bills.util.Constants.EXTENSION_CSV;

public class S3Processor {

    private final S3Client s3Client;
    private final TransactionService transactionService;

    public S3Processor() {
        s3Client = S3ClientProvider.getS3Client();
        transactionService = new TransactionService();
    }

    public List<Transaction> processTransactions() {
        List<ResponseInputStream<GetObjectResponse>> filesDownloaded = downloadImageFiles(s3Client);
        List<Transaction> transactions = transactionService.parseTransactions(filesDownloaded);
        transactions = transactionService.ensureTotalBillable(transactions);

        String csvStringLines = new CSVGenerator().generateCSVStringLines(transactions);
        String csvFileName = LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_FOLDER)) + EXTENSION_CSV;
        uploadResult(s3Client, csvStringLines, csvFileName);
        return transactions;
    }

    private List<ResponseInputStream<GetObjectResponse>> downloadImageFiles(S3Client s3Client) {
        Instant start = Instant.now();
        ListObjectsV2Request listObjectsReqManual = ListObjectsV2Request.builder()
                .bucket(AWS_S3_BILLS_BUCKET)
                .build();
        ListObjectsV2Response bucketContent = s3Client.listObjectsV2(listObjectsReqManual);
        Logger.info("List completed in: {}ms.", Duration.between(start, Instant.now()).toMillis());

        start = Instant.now();
        List<ResponseInputStream<GetObjectResponse>> filesDownloaded = new ArrayList<>();
        bucketContent.contents().parallelStream().forEach(content -> {
            if (content.key().contains(EXTENSION_CSV)) {
                return;
            }
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(AWS_S3_BILLS_BUCKET)
                    .key(content.key())
                    .build();

            filesDownloaded.add(s3Client.getObject(getObjectRequest));
        });
        Logger.info("Files Download completed in: {}ms.", Duration.between(start, Instant.now()).toMillis());
        return filesDownloaded;
    }

    private void uploadResult(S3Client s3Client, String csvStringLines, String csvFileName) {
        Instant start = Instant.now();
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(AWS_S3_BILLS_BUCKET)
                .key(csvFileName)
                .build();
        s3Client.putObject(objectRequest, RequestBody.fromString(csvStringLines));
        Logger.info("Upload completed in: {}ms.", Duration.between(start, Instant.now()).toMillis());
    }

}
