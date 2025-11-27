package com.app.api;

import com.app.account.Account;
import com.app.account.AccountBalance;
import com.app.exception.LedgerBaseException;
import com.app.ledger.LedgerService;
import com.app.transaction.Currency;
import com.app.transaction.Transaction;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;

import static com.app.api.LedgerApi.BASE_URL;

@Controller(BASE_URL)
public class LedgerApi {

    static final String BASE_URL = "/ledger";
    static final String ACCOUNTS = "/accounts";
    static final String ACCOUNT = ACCOUNTS + "/{account}";
    static final String BALANCE = ACCOUNT + "/balance";
    static final String TRANSACTIONS = ACCOUNT + "/transactions";
    static final String DEPOSIT = ACCOUNT + "/deposit";
    static final String WITHDRAWAL = ACCOUNT + "/withdrawal";
    static final String TRANSFER = "/transfer";

    @Inject
    LedgerService ledgerService;


    @Post(ACCOUNTS)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Open new account")
    public HttpResponse<ApiResponse<Account>> openNewAccount(@QueryValue Currency baseCcy) {

        final var account = ledgerService.openNewAccount(baseCcy);

        ApiResponse<Account> response =
                ApiResponse.<Account>builder()
                .statusCode(HttpStatus.OK)
                .message("Account successfully created")
                .data(account)
                .build();

        return HttpResponse.ok(response);
    }


    @Delete(ACCOUNT)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete an account")
    public HttpResponse<ApiResponse<Void>> deleteAccount(@PathVariable String account) {

        ledgerService.deleteAccount(account);

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("Account successfully deleted")
                .data(null)
                .build();

        return HttpResponse.ok(response);
    }


    @Get(BALANCE)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get account balance")
    public HttpResponse<ApiResponse<AccountBalance>> getAccountBalance(@PathVariable String account) {

        final var accountBalance = ledgerService.getAccountBalance(account);

        ApiResponse<AccountBalance> response =
                ApiResponse.<AccountBalance>builder()
                .statusCode(HttpStatus.OK)
                .message("Account balance successfully retrieved")
                .data(accountBalance)
                .build();

        return HttpResponse.ok(response);
    }

    // get account transaction history
    @Get(TRANSACTIONS)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get account transactions history")
    public HttpResponse<ApiResponse<List<Transaction>>> getTransactionHistory(@PathVariable String account) {

        final var transactions = ledgerService.getTransactionHistory(account);

        ApiResponse<List<Transaction>> response =
                ApiResponse.<List<Transaction>>builder()
                        .statusCode(HttpStatus.OK)
                        .message("Transaction history successfully retrieved")
                        .data(transactions)
                        .build();

        return HttpResponse.ok(response);
    }


    @Get(ACCOUNTS)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all accounts and their balances")
    public HttpResponse<ApiResponse<List<AccountBalance>>> getAllAccountBalances() {

        final var accountBalances = ledgerService.getAllAccounts();

        ApiResponse<List<AccountBalance>> response =
                ApiResponse.<List<AccountBalance>>builder()
                        .statusCode(HttpStatus.OK)
                        .message("Accounts successfully retrieved")
                        .data(accountBalances)
                        .build();

        return HttpResponse.ok(response);
    }


    @Post(DEPOSIT)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Deposit money into account")
    public HttpResponse<ApiResponse<Transaction>> deposit(
            @PathVariable String account,
            @QueryValue BigDecimal amount,
            @QueryValue Currency currency) {

        final var transaction = ledgerService.depositIntoAccount(account, amount, currency);

        ApiResponse<Transaction> response =
                ApiResponse.<Transaction>builder()
                .statusCode(HttpStatus.OK)
                .message("Deposit successful")
                .data(transaction)
                .build();

        return HttpResponse.ok(response);
    }


    @Post(WITHDRAWAL)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Withdraw money from account")
    public HttpResponse<ApiResponse<Transaction>> withdrawal(
            @PathVariable String account,
            @QueryValue BigDecimal amount) {

        final var transaction = ledgerService.withdrawFromAccount(account, amount);

        ApiResponse<Transaction> response =
                ApiResponse.<Transaction>builder()
                .statusCode(HttpStatus.OK)
                .message("Withdrawal successful")
                .data(transaction)
                .build();

        return HttpResponse.ok(response);
    }


    @Post(TRANSFER)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Transfer money between accounts")
    public HttpResponse<ApiResponse<List<Transaction>>> transfer(
            @QueryValue String fromAccount,
            @QueryValue String toAccount,
            @QueryValue BigDecimal amount) {

        final var transactions = ledgerService.transferMoney(fromAccount, toAccount, amount);

        ApiResponse<List<Transaction>> response =
                ApiResponse.<List<Transaction>>builder()
                .statusCode(HttpStatus.OK)
                .message("Transfer successful")
                .data(transactions)
                .build();

        return HttpResponse.ok(response);
    }



    // Global ledger exception handler
    @Error(global = true, exception = LedgerBaseException.class)
    public HttpResponse<ApiResponse<Void>> handleLedgerException(LedgerBaseException ex) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(ex.getStatus())
                .message(ex.getMessage())
                .build();

        return HttpResponse.status(ex.getStatus()).body(response);
    }

}
