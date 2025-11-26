package com.app.api;

import com.app.account.Account;
import com.app.exception.LedgerBaseException;
import com.app.ledger.LedgerService;
import com.app.transaction.Currency;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.inject.Inject;

import java.math.BigDecimal;

import static com.app.api.LedgerApi.BASE_URL;

@Controller(BASE_URL)
public class LedgerApi {

    static final String BASE_URL = "/ledger";
    static final String ACCOUNT_PATH = "/account";

    @Inject
    LedgerService ledgerService;


    // ACCOUNT ENDPOINTS
    @Post(ACCOUNT_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Open new account")
    public HttpResponse<ApiResponse<Account>> openNewAccount(@QueryValue Currency baseCcy) {
        final var account = ledgerService.openNewAccount(baseCcy);

        ApiResponse<Account> response = ApiResponse.<Account>builder()
                .statusCode(HttpStatus.OK)
                .message("Account successfully created")
                .data(account)
                .build();

        return HttpResponse.ok(response);
    }


    @Delete(ACCOUNT_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete an account")
    public HttpResponse<ApiResponse<Void>> deleteAccount(@QueryValue String accountNo) {

        ledgerService.deleteAccount(accountNo);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("Account successfully deleted")
                .data(null)
                .build();

        return HttpResponse.ok(response);
    }


    @Get(ACCOUNT_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get account balance")
    public HttpResponse<ApiResponse<BigDecimal>> getAccountBalance(@QueryValue String accountNo) {

        final var balance = ledgerService.getAccountBalance(accountNo);

        ApiResponse<BigDecimal> response = ApiResponse.<BigDecimal>builder()
                .statusCode(HttpStatus.OK)
                .message("Account successfully deleted")
                .data(balance)
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
