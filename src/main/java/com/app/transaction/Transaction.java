package com.app.transaction;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
public class Transaction {
    private String id;
    private String accountId;
    private Type type;
    private BigDecimal amount;
    private Currency currency;
    private LocalDateTime timestamp;
}
