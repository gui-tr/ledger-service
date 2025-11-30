package com.app.api.mapper;

import com.app.api.dto.TransactionDTO;
import com.app.transaction.Transaction;

public class TransactionMapper {
    public static TransactionDTO toDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}
