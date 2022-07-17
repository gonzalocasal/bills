package com.bills.model;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.bills.util.Constants.DATE_FORMAT_TRANSACTION;

public class TransactionTest {


    @Test
    public void testParseBrubankTransaction() throws ParseException {
        List<String> lines = List.of("Fecha", "01/06/2022 12:24 -03:00", "Origen",
                "Juan Jose Perez", "Monto", "$ 75,477,41", "NUmero de Transaccion", "173848763",
                "Banco", "Brubank", "CBU /A|ias", "juanalegre.bru.5615", "CUIT", "20-55660297—5",
                "Destino", "Caja de ahorro en pesos");

        BrubankTransaction brubankTransaction = new BrubankTransaction(lines);

        Assert.assertEquals("Juan Jose Perez", brubankTransaction.getName());
        Assert.assertEquals("20-55660297-5", brubankTransaction.getCuit());
        Assert.assertEquals(new BigDecimal("75477.41"), brubankTransaction.getAmount());
        Assert.assertEquals(new SimpleDateFormat(DATE_FORMAT_TRANSACTION).parse("01/06/2022"), brubankTransaction.getDate());
    }

}
