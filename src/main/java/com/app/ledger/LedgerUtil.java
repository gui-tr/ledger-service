package com.app.ledger;

import com.app.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

public class LedgerUtil {

    public static String generateAccountId() {
        final Random random = new Random();
        StringBuilder builder = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }

    public static BigDecimal getBalance(List<Transaction> txns) {
        BigDecimal balance = new BigDecimal(0);
        for (Transaction txn : txns) {
            balance = balance.add(txn.getAmount());
        }
        return balance;
    }

}
