package com.bills.util;

import org.tinylog.Logger;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Duration;
import java.time.Instant;

public class S3ClientProvider {

    private static S3Client s3Client;

    private S3ClientProvider() {
    }

    public static S3Client getS3Client() {
        if (s3Client == null) {
            Instant start = Instant.now();
            Region region = Region.US_EAST_1;
            s3Client = S3Client.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(region)
                    .build();
            Logger.info("Client built completed in: {}ms.", Duration.between(start, Instant.now()).toMillis());
        }
        return s3Client;
    }
}