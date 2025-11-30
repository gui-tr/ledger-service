package com.app.api.dto;

import com.app.transaction.Currency;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
public class AccountBalance {
    private String accountNo;
    private Currency baseCcy;
    private BigDecimal balance;
}
