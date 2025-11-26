package com.app.account;

import jakarta.inject.Singleton;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class AccountRepository {

    private final Map<String, Account> map = new ConcurrentHashMap<>();

    public Account insert(Account account) {
        if (account.getId() == null) {
            account.setId(UUID.randomUUID().toString());
        }
        map.put(account.getId(), account);
        return account;
    }

    public Optional<Account> findById(String id) {
        return Optional.ofNullable(map.get(id));
    }

    public List<Account> findAll() {
        return new ArrayList<>(map.values());
    }

    public boolean deleteByAccountNo(String accountNo) {
        return map.entrySet()
                .removeIf(account ->
                    account.getValue().getAccountNo().equals(accountNo));
    }
}

