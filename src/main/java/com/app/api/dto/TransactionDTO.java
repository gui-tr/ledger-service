package com.app.api.dto;

import com.app.transaction.Currency;
import com.app.transaction.Type;
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
public class TransactionDTO {
    private String id;
    private Type type;
    private BigDecimal amount;
    private Currency currency;
    private LocalDateTime timestamp;
}
