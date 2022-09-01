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
        List<String> lines = List.of("07:38 -|I 6“ Q}",
                "Recibiste dinero de",
                "Juan Jose Perez",
                "5 dejulio de 2022 - 20:43",
                "$ 137.279,94",
                "@ Transferencia recibida",
                "Numero de transaccio’n 275719528",
                "Banco origen . Rebanking",
                "CBU / Alias 4150999718001925160029",
                "CUIT 20-35728585-3",
                "Destino Caja de ahorro en pesos",
                "Exportar ﬂ]",
                " ");
        Transaction transaction = TransactionFactory.getTransaction(lines);
        if (transaction != null) {
            Assert.assertEquals("Juan Jose Perez", transaction.getName());
            Assert.assertEquals("20-35728585-3", transaction.getCuit());
            Assert.assertEquals(new BigDecimal("137279.94"), transaction.getAmount());
            Assert.assertEquals(new SimpleDateFormat(DATE_FORMAT_TRANSACTION).parse("05/07/2022"), transaction.getDate());
        }
    }

}
