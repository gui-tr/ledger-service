package com.app.ledger;

import com.app.account.Account;
import com.app.account.AccountBalance;
import com.app.account.AccountRepository;
import com.app.exception.LedgerExceptions;
import com.app.transaction.*;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.app.ledger.LedgerUtil.generateAccountId;
import static com.app.ledger.LedgerUtil.getBalance;
import static com.app.transaction.FxRate.convert;

@Singleton
public class LedgerService {

    @Inject
    AccountRepository accountRepository;


    // Create a new Account (in different ccys)
    public Account openNewAccount(Currency baseCcy) {
        return accountRepository.insert(Account.builder()
                        .accountNo(generateAccountId())
                        .baseCcy(baseCcy)
                        .build());
    }

    // Get all accounts with their balances
    public List<AccountBalance> getAllAccounts() {

        final var accountList = accountRepository.findAll();
        if (accountList.isEmpty()) {
            throw new LedgerExceptions.NoAccountFoundException();
        }

        final var accountBalanceList = new ArrayList<AccountBalance>();

        accountList.forEach(account -> {
            accountBalanceList.add(AccountBalance.builder()
                    .accountNo(account.getAccountNo())
                    .balance(getBalance(account.getTransactions()))
                    .build());
        });

        return accountBalanceList;
    }



    // Delete an Account
    // - cannot delete an account if balance is positive
    public boolean deleteAccount(String accountNo) {
        final var account = getAccountFromRepo(accountNo);

        if (getBalance(account.getTransactions()).compareTo(BigDecimal.ZERO) > 0) {
            throw new LedgerExceptions.PositiveBalanceException(accountNo);
        }

        return accountRepository.deleteByAccountNo(accountNo);
    }

    // Get account balance
    // - accounts should exist
    // - should be sum of all transactions
    public AccountBalance getAccountBalance(String accountNo) {
        final var account = getAccountFromRepo(accountNo);

        return AccountBalance.builder()
                .accountNo(accountNo)
                .balance(getBalance(account.getTransactions()))
                .build();
    }

    // Deposit money into Account (in different ccys)
    // - accounts should exist
    // - convert amount into Account ccy
    public Transaction depositIntoAccount(String accountNo, BigDecimal amount, Currency currency) {

        final var account = getAccountFromRepo(accountNo);

        final var convertedAmount = convert(currency, account.getBaseCcy(), amount);

        final var transaction = Transaction.builder()
                .accountNo(account.getAccountNo())
                .type(Type.DEPOSIT)
                .amount(convertedAmount)
                .currency(currency)
                .timestamp(LocalDateTime.now())
                .build();

        account.getTransactions().add(transaction);
        return transaction;
    }

    // Withdraw money from Account
    // - accounts should exist
    // - withdraw only in Account ccy
    // - withdraw only possible if funds available (no overdraft possible)
    public Transaction withdrawFromAccount(String accountNo, BigDecimal amount) {

        final var account = getAccountFromRepo(accountNo);

        if (getBalance(account.getTransactions()).subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new LedgerExceptions.InsufficientFundsException(accountNo);
        }

        final var transaction = Transaction.builder()
                .accountNo(account.getAccountNo())
                .type(Type.WITHDRAWAL)
                .amount(amount.negate())
                .currency(account.getBaseCcy())
                .timestamp(LocalDateTime.now())
                .build();

        account.getTransactions().add(transaction);
        return transaction;
    }



    // Transfer money from an Account to Another
    // - both accounts should exist
    // - transfer is possible only if funds available
    // - transfers are done from base ccy to base ccy
    public List<Transaction> transferMoney(String fromAccountNo, String toAccountNo, BigDecimal amount) {

        // get accounts
        final var fromAccount = getAccountFromRepo(fromAccountNo);
        final var toAccount = getAccountFromRepo(toAccountNo);

        if (getBalance(fromAccount.getTransactions()).subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new LedgerExceptions.InsufficientFundsException(fromAccountNo);
        }

        // build and add transfer OUT to account
        final var transactionFrom = Transaction.builder()
                .accountNo(fromAccount.getAccountNo())
                .type(Type.TRANSFER_OUT)
                .amount(amount)
                .currency(fromAccount.getBaseCcy())
                .timestamp(LocalDateTime.now())
                .build();
        fromAccount.getTransactions().add(transactionFrom);

        // build and add transfer IN to account
        final var convertedAmount = convert(fromAccount.getBaseCcy(), toAccount.getBaseCcy(), amount);
        final var transactionTo = Transaction.builder()
                .accountNo(fromAccount.getAccountNo())
                .type(Type.TRANSFER_IN)
                .amount(convertedAmount)
                .currency(fromAccount.getBaseCcy())
                .timestamp(LocalDateTime.now())
                .build();
        fromAccount.getTransactions().add(transactionTo);

        return List.of(transactionFrom, transactionTo);
    }


    private Account getAccountFromRepo(String accountNo) {
        return accountRepository.findAll().stream()
                .filter(acc -> acc.getAccountNo().equals(accountNo))
                .findFirst()
                .orElseThrow(() -> new LedgerExceptions.AccountNotFoundException(accountNo));
    }

}
