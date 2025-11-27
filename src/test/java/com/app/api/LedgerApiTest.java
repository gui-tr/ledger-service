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
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class LedgerApiTest {

    public static final String ACCOUNT_NO_1 = "12345678";
    public static final Currency CURRENCY_ACCOUNT_1 = Currency.GBP;
    public static final String ACCOUNT_NO_2 = "987654321";
    public static final Currency CURRENCY_ACCOUNT_2 = Currency.USD;

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
        final var result = ledgerService.openNewAccount(CURRENCY_ACCOUNT_1);

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
        final var result = ledgerService.openNewAccount(CURRENCY_ACCOUNT_1);

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
        final var result = ledgerService.openNewAccount(CURRENCY_ACCOUNT_1);

        // deposit money into account
        ledgerService.depositIntoAccount(result.getAccountNo(), BigDecimal.valueOf(1000), Currency.GBP);
        ledgerService.depositIntoAccount(result.getAccountNo(), BigDecimal.valueOf(500), Currency.USD);

        given
            .contentType(ContentType.JSON)
            .pathParam("account", result.getAccountNo())
        .when()
            .get("/ledger/accounts/{account}/transactions")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("statusCode", equalTo("OK"))
            .body("data", hasSize(2));
    }

    @Test
    void getAllAccountBalances(RequestSpecification given) {
        // create new accounts first
        final var resultAcc1 = ledgerService.openNewAccount(CURRENCY_ACCOUNT_1);
        final var resultAcc2 = ledgerService.openNewAccount(CURRENCY_ACCOUNT_2);

        // deposit money into account 1 (total 1500)
        ledgerService.depositIntoAccount(resultAcc1.getAccountNo(), BigDecimal.valueOf(1000), Currency.GBP);
        ledgerService.depositIntoAccount(resultAcc1.getAccountNo(), BigDecimal.valueOf(500), Currency.USD);

        // deposit money into account 2 (total 400)
        ledgerService.depositIntoAccount(resultAcc2.getAccountNo(), BigDecimal.valueOf(300), Currency.GBP);
        ledgerService.depositIntoAccount(resultAcc2.getAccountNo(), BigDecimal.valueOf(100), Currency.USD);

        List<AccountBalance> balances = given
            .contentType(ContentType.JSON)
        .when()
            .get("/ledger/accounts/")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("statusCode", equalTo("OK"))
            .extract()
            .jsonPath()
            .getList("data", AccountBalance.class);

        final var amounts = balances.stream().map(AccountBalance::getBalance).toList();
        assertTrue(amounts.contains(BigDecimal.valueOf(1500)));
        assertTrue(amounts.contains(BigDecimal.valueOf(400)));
    }

    @Test
    void deposit() {
    }

    @Test
    void withdrawal() {
    }

    @Test
    void transfer() {
    }
}