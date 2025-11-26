package com.app.transaction;

import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.util.List;

@Singleton
public class FxRate {

    public record FxRatePair(Currency from, Currency to, double rate) {}

    private static final List<FxRatePair> FX_RATES = List.of(
            new FxRatePair(Currency.GBP, Currency.USD, 1.414),
            new FxRatePair(Currency.GBP, Currency.EUR, 1.124),
            new FxRatePair(Currency.USD, Currency.GBP, 0.765),
            new FxRatePair(Currency.USD, Currency.EUR, 0.876),
            new FxRatePair(Currency.EUR, Currency.GBP, 0.676),
            new FxRatePair(Currency.EUR, Currency.USD, 1.158)
    );

    public static BigDecimal convert(Currency from, Currency to, BigDecimal amount) {
        if (from.equals(to)) {
            return amount;
        } else {
            return FX_RATES.stream()
                    .filter(pair -> pair.from().equals(from) && pair.to().equals(to))
                    .map(pair -> amount.multiply(BigDecimal.valueOf(pair.rate())))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No rate found from {}" + from + " to {} " + to));
        }
    }
}
