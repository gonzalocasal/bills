package com.bills.model;

import org.tinylog.Logger;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.bills.util.Constants.DATE_FORMAT_TRANSACTION;
import static com.bills.util.Constants.SANTANDER_TRANSACTION_ID_TEXT;

public class TransactionFactory {

    private TransactionFactory(){}

    public static Transaction getTransaction(List<String> lines) {
        if (lines.isEmpty()) {
            return null;
        }
        if (lines.stream().anyMatch(l -> l.contains(SANTANDER_TRANSACTION_ID_TEXT))) {
            return new SantanderTransaction(lines);
        } else {
            return new BrubankTransaction(lines);
        }
    }

    public static String parseName(String name) {
        StringBuilder capitalizeName = new StringBuilder();
        for (String w : name.toLowerCase().split("\\s")) {
            String first = w.substring(0,1);
            String afterFirst = w.substring(1);
            capitalizeName.append(first.toUpperCase()).append(afterFirst).append(" ");
        }
        return capitalizeName.toString().trim();
    }

    public static Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat(DATE_FORMAT_TRANSACTION).parse(dateStr);
        } catch (ParseException e) {
            Logger.error("Error parsing {} as date.", dateStr);
            return null;
        }
    }

    public static BigDecimal parseAmount(String amountStr) {
        try {
            amountStr = amountStr
                    .replace(".", "")
                    .replace("$", "")
                    .replace(",", "")
                    .replace("O", "0").trim();

            String decimal = amountStr.substring(amountStr.length() - 2);
            String integer = amountStr.substring(0, amountStr.length() - 2);

            return new BigDecimal(integer + "." + decimal);
        } catch (Exception e) {
            Logger.error("Error parsing {} as amount.", amountStr);
        }
        return null;
    }

    public static String parseCuit(String cuitStr) {
        cuitStr = cuitStr.replaceAll("\\D","").trim();
        String type = cuitStr.substring(0, 2);
        String dni = cuitStr.substring(2, cuitStr.length() - 1);
        String lastNum = cuitStr.substring(cuitStr.length() - 1);
        return type + "-" + dni + "-" + lastNum;
    }

    public static BigDecimal parseAmountSantander(String amountStr) {
        amountStr = amountStr.replace("Importe ", "");
        return parseAmount(amountStr);
    }

    public static String parseCuitSantander(String cuitStr) {
        String[] split = cuitStr.split("/");
        cuitStr = split[split.length - 1];
        return parseCuit(cuitStr);
    }

    public static Date parseDateSantander(String dateStr) {
        String[] split = dateStr.replace("Fecha y hora ", "").split("/");
        String[] dateArray = new String[3];
        for (int i = 0; i < 3; i++) {
            dateArray[i] = split[i].substring(0, 2);
        }
        dateArray[2] = "20" + dateArray[2];
        return parseDate(String.join("/", dateArray));
    }

    public static String parseNameSantander(String name) {
        return parseName(name.split("/")[0].split(" DE ")[1]);
    }

    public static Date parseDateBrubank(String dateStr) {
        String[] split = dateStr.split(" - ")[0].replace("de", "").split("\\s+");
        String month = String.valueOf(monthNameToNumber(split[1]));
        return parseDate(String.format("%s/%s/%s", split[0], month, split[2]));
    }

    private static Integer monthNameToNumber(String monthName) {
        try {
            Date date = new SimpleDateFormat("MMMM", new Locale("es", "ES")).parse(monthName);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.MONTH) + 1;
        } catch (ParseException e) {
            Logger.error("Error parsing month name: {}", monthName);
            return null;
        }
    }

}
