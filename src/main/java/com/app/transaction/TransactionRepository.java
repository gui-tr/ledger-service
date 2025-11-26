package com.app.transaction;

import jakarta.inject.Singleton;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class TransactionRepository {

    private final Map<String, Transaction> map = new ConcurrentHashMap<>();

    public Transaction upsert(Transaction transaction) {
        if (transaction.getId() == null) {
            transaction.setId(UUID.randomUUID().toString());
        }
        map.put(transaction.getId(), transaction);
        return transaction;
    }

    public Optional<Transaction> findById(String id) {
        return Optional.ofNullable(map.get(id));
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(map.values());
    }

    public void delete(Long id) {
        map.remove(id);
    }
}
