package com.app.api;

import com.app.account.AccountBalance;
import com.app.ledger.LedgerService;
import com.app.transaction.Currency;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class LedgerApiTest {

    @Inject
    LedgerService ledgerService;


    @Test
    void openNewAccount(RequestSpecification given) {
        given
            .contentType(ContentType.JSON)
            .queryParam("baseCcy", "GBP")
        .when()
            .post("/ledger/accounts")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("statusCode", equalTo("OK"))
            .body("message", equalTo("Account successfully created"))
            .body("data.baseCcy", equalTo("GBP"))
            .body("data.accountNo", notNullValue());
    }

    @Test
    void deleteAccount(RequestSpecification given) {
        // create new account first
        final var result = ledgerService.openNewAccount(Currency.GBP);

        // call endpoint
        given
            .contentType(ContentType.JSON)
            .pathParam("account", result.getAccountNo())
        .when()
            .delete("/ledger/accounts/{account}")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("message", equalTo("Account successfully deleted"));
    }

    @Test
    void getAccountBalance(RequestSpecification given) {
        // create new account first
        final var result = ledgerService.openNewAccount(Currency.GBP);

        // call endpoint
        given
            .contentType(ContentType.JSON)
            .pathParam("account", result.getAccountNo())
        .when()
            .get("/ledger/accounts/{account}/balance")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("statusCode", equalTo("OK"))
            .body("message", equalTo("Account balance successfully retrieved"))
            .body("data.baseCcy", equalTo(result.getBaseCcy().toString()))
            .body("data.accountNo", equalTo(result.getAccountNo()));
    }

    @Test
    void getTransactionHistory(RequestSpecification given) {
        // create new account first
        final var result = ledgerService.openNewAccount(Currency.GBP);

        // deposit money into account
        ledgerService.depositIntoAccount(result.getAccountNo(), BigDecimal.valueOf(1000), Currency.GBP);
        ledgerService.depositIntoAccount(result.getAccountNo(), BigDecimal.valueOf(500), Currency.USD);

        // call endpoint
        given
            .contentType(ContentType.JSON)
            .pathParam("account", result.getAccountNo())
        .when()
            .get("/ledger/accounts/{account}/transactions")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("statusCode", equalTo("OK"))
            .body("message", equalTo("Transaction history successfully retrieved"))
            .body("data", hasSize(2));
    }

    @Test
    void getAllAccountBalances(RequestSpecification given) {
        // create new accounts first
        final var resultAcc1 = ledgerService.openNewAccount(Currency.GBP);
        final var resultAcc2 = ledgerService.openNewAccount(Currency.GBP);

        // deposit money into account 1 (total 1500)
        ledgerService.depositIntoAccount(resultAcc1.getAccountNo(), BigDecimal.valueOf(1000), Currency.GBP);
        ledgerService.depositIntoAccount(resultAcc1.getAccountNo(), BigDecimal.valueOf(500), Currency.GBP);

        // deposit money into account 2 (total 400)
        ledgerService.depositIntoAccount(resultAcc2.getAccountNo(), BigDecimal.valueOf(300), Currency.GBP);
        ledgerService.depositIntoAccount(resultAcc2.getAccountNo(), BigDecimal.valueOf(100), Currency.GBP);

        // expected balances
        final var expectedBalanceList = List.of(
                AccountBalance.builder()
                        .accountNo(resultAcc1.getAccountNo())
                        .baseCcy(Currency.GBP)
                        .balance(BigDecimal.valueOf(1500))
                        .build(),
                AccountBalance.builder()
                        .accountNo(resultAcc2.getAccountNo())
                        .baseCcy(Currency.GBP)
                        .balance(BigDecimal.valueOf(400))
                        .build());

        // call endpoint
        given
            .contentType(ContentType.ANY)
        .when()
            .get("/ledger/accounts/")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("statusCode", equalTo("OK"))
            .extract()
            .jsonPath()
            .getList("data", AccountBalance.class);

        final var amounts = expectedBalanceList.stream().map(AccountBalance::getBalance).toList();
        assertTrue(amounts.contains(BigDecimal.valueOf(1500)));
        assertTrue(amounts.contains(BigDecimal.valueOf(400)));
    }

    @Test
    void deposit(RequestSpecification given) {

        // create new account first
        final var result = ledgerService.openNewAccount(Currency.GBP);

        // call endpoint
        given
            .contentType(ContentType.JSON)
            .pathParam("account", result.getAccountNo())
            .queryParam("amount", BigDecimal.valueOf(1000))
            .queryParam("currency", Currency.GBP)
        .when()
            .post("/ledger/accounts/{account}/deposit")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("statusCode", equalTo("OK"))
            .body("message", equalTo("Deposit successful"));

        // control value in repo
        final var account = ledgerService.getTransactionHistory(result.getAccountNo());
        assertEquals(BigDecimal.valueOf(1000), account.getFirst().getAmount());
    }

    @Test
    void withdrawal(RequestSpecification given) {
        // create new account and add money first
        final var account = ledgerService.openNewAccount(Currency.GBP);
        ledgerService.depositIntoAccount(account.getAccountNo(), BigDecimal.valueOf(1000), Currency.GBP);

        // verify deposit
        final var newAccountBalance = ledgerService.getAccountBalance(account.getAccountNo()).getBalance();
        assertEquals(BigDecimal.valueOf(1000), newAccountBalance);

        // call endpoint
        given
            .contentType(ContentType.JSON)
            .pathParam("account", account.getAccountNo())
            .queryParam("amount", BigDecimal.valueOf(500))
        .when()
            .post("/ledger/accounts/{account}/withdrawal")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("statusCode", equalTo("OK"))
            .body("message", equalTo("Withdrawal successful"));

        final var balanceAfterWithdrawal = ledgerService.getAccountBalance(account.getAccountNo()).getBalance();
        assertEquals(BigDecimal.valueOf(500), balanceAfterWithdrawal);
    }

    @Test
    void transfer(RequestSpecification given) {
        // create new accounts first
        final var resultAcc1 = ledgerService.openNewAccount(Currency.GBP);
        final var resultAcc2 = ledgerService.openNewAccount(Currency.GBP);

        // deposit money into accounts
        ledgerService.depositIntoAccount(resultAcc1.getAccountNo(), BigDecimal.valueOf(1000), Currency.GBP);
        ledgerService.depositIntoAccount(resultAcc2.getAccountNo(), BigDecimal.valueOf(1000), Currency.GBP);

        // call endpoint
        given
            .contentType(ContentType.JSON)
            .queryParam("fromAccount", resultAcc1.getAccountNo())
            .queryParam("toAccount", resultAcc2.getAccountNo())
            .queryParam("amount", BigDecimal.valueOf(1000))
        .when()
            .post("/ledger/transfer")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("statusCode", equalTo("OK"))
            .body("message", equalTo("Transfer successful"))
            .body("data", hasSize(2));

        // verify transfer
        final var acc1Balance = ledgerService.getAccountBalance(resultAcc1.getAccountNo()).getBalance();
        final var acc2Balance = ledgerService.getAccountBalance(resultAcc2.getAccountNo()).getBalance();

        assertEquals(BigDecimal.valueOf(0), acc1Balance);
        assertEquals(BigDecimal.valueOf(2000), acc2Balance);
    }
}
