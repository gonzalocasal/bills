package com.bills.util;

import java.math.BigDecimal;

public abstract class Constants {

    private Constants(){}

    public static final String AWS_S3_BILLS_BUCKET = "bills-gc";
    public static final String COLUMN_SEPARATOR = ";";
    public static final String EXTENSION_CSV = ".csv";
    public static final String DATE_FORMAT_FOLDER = "yyyy-MM";
    public static final String DATE_FORMAT_TRANSACTION = "dd/MM/yyyy";
    public static final String SANTANDER_TRANSACTION_ID_TEXT = "Suc. Origen";
    public static final BigDecimal MAXIMUM_BILLABLE = new BigDecimal(270000);
    public static final int PAGE_SEGMENTATION_MODE = 4;

}
