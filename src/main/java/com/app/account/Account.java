package com.app.account;

import com.app.transaction.Currency;
import com.app.transaction.Transaction;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
public class Account {
    private String id;
    private String accountNo;
    private Currency baseCcy;
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
}
