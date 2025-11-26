package com.app.ledger;

import com.app.account.Account;
import com.app.account.AccountRepository;
import com.app.exception.LedgerExceptions;
import com.app.transaction.Currency;
import com.app.transaction.FxRate;
import com.app.transaction.TransactionRepository;
import io.micronaut.runtime.http.scope.RequestScope;
import jakarta.inject.Inject;

import java.math.BigDecimal;

import static com.app.ledger.LedgerUtil.generateAccountId;
import static com.app.ledger.LedgerUtil.getBalance;

@RequestScope
public class LedgerService {

    @Inject
    AccountRepository accountRepository;

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    FxRate fxRate;


    // Create a new Account (in different ccys)
    public Account openNewAccount(Currency baseCcy) {
        return accountRepository.insert(Account.builder()
                        .accountNo(generateAccountId())
                        .baseCcy(baseCcy)
                        .build());
    }

    // Delete an Account
    // - cannot delete an account if balance is positive
    public boolean deleteAccount(String accountNo) {
        final var account = accountRepository.findAll().stream()
                .filter(acc -> acc.getAccountNo().equals(accountNo))
                .findFirst()
                .orElseThrow(() -> new LedgerExceptions.AccountNotFoundException(accountNo));

        if (getBalance(account.getTransactions()).compareTo(BigDecimal.ZERO) > 0) {
            throw new LedgerExceptions.PositiveBalanceException(accountNo);
        }

        return accountRepository.deleteByAccountNo(accountNo);
    }

    // Get account balance
    // - accounts should exist
    // - should be sum of all transactions
    public BigDecimal getAccountBalance(String accountNo) {
        final var account = accountRepository.findAll().stream()
                .filter(acc -> acc.getAccountNo().equals(accountNo))
                .findFirst()
                .orElseThrow(() -> new LedgerExceptions.AccountNotFoundException(accountNo));

        return getBalance(account.getTransactions());
    }

    // Deposit money into Account (in different ccys)
    // - accounts should exist
    // - convert amount into Account ccy

    // Withdraw money from Account
    // - accounts should exist
    // - withdraw only in Account ccy
    // - withdraw only possible if funds available (no overdraft possible)

    // Transfer money from an Account to Another
    // - both accounts should exist
    // - transfer is possible only if funds available


}
