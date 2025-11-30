package com.app.ledger;

import com.app.account.Account;
import com.app.api.dto.AccountBalance;
import com.app.account.AccountRepository;
import com.app.api.dto.TransactionDTO;
import com.app.api.mapper.TransactionMapper;
import com.app.ledger.exception.LedgerExceptions;
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


    // Open a new account
    public AccountBalance openNewAccount(Currency baseCcy) {
        final var account = accountRepository.insert(Account.builder()
                        .accountNo(generateAccountId())
                        .baseCcy(baseCcy)
                        .build());

        return AccountBalance.builder()
                .accountNo(account.getAccountNo())
                .baseCcy(account.getBaseCcy())
                .balance(getBalance(account.getTransactions()))
                .build();
    }

    // Get all accounts and their balances
    public List<AccountBalance> getAllAccounts() {

        final var accountList = accountRepository.findAll();
        if (accountList.isEmpty()) {
            throw new LedgerExceptions.NoAccountFoundException();
        }

        final var accountBalanceList = new ArrayList<AccountBalance>();

        accountList.forEach(account -> {
            accountBalanceList.add(AccountBalance.builder()
                    .accountNo(account.getAccountNo())
                    .baseCcy(account.getBaseCcy())
                    .balance(getBalance(account.getTransactions()))
                    .build());
        });

        return accountBalanceList;
    }

    // Delete an account
    public boolean deleteAccount(String accountNo) {
        final var account = getAccountFromRepo(accountNo);

        if (getBalance(account.getTransactions()).compareTo(BigDecimal.ZERO) > 0) {
            throw new LedgerExceptions.PositiveBalanceException(accountNo);
        }

        return accountRepository.deleteByAccountNo(accountNo);
    }

    // Get account balance
    public AccountBalance getAccountBalance(String accountNo) {
        final var account = getAccountFromRepo(accountNo);

        return AccountBalance.builder()
                .accountNo(accountNo)
                .baseCcy(account.getBaseCcy())
                .balance(getBalance(account.getTransactions()))
                .build();
    }

    // Get account transaction history
    public List<TransactionDTO> getTransactionHistory(String accountNo) {
        final var account = getAccountFromRepo(accountNo);
        return account.getTransactions().stream()
                .map(TransactionMapper::toDTO)
                .toList();
    }

    // Deposit money into an account
    public TransactionDTO depositIntoAccount(String accountNo, BigDecimal amount, Currency currency) {

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
        return TransactionMapper.toDTO(transaction);
    }

    // Withdraw money from Account
    public TransactionDTO withdrawFromAccount(String accountNo, BigDecimal amount) {

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
        return TransactionMapper.toDTO(transaction);
    }

    // Transfer money between accounts
    public List<TransactionDTO> transferMoney(String fromAccountNo, String toAccountNo, BigDecimal amount) {
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
                .amount(amount.negate())
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

        toAccount.getTransactions().add(transactionTo);
        return List.of(
                TransactionMapper.toDTO(transactionFrom),
                TransactionMapper.toDTO(transactionTo)
        );
    }


    private Account getAccountFromRepo(String accountNo) {
        return accountRepository.findAll().stream()
                .filter(acc -> acc.getAccountNo().equals(accountNo))
                .findFirst()
                .orElseThrow(() -> new LedgerExceptions.AccountNotFoundException(accountNo));
    }
}
