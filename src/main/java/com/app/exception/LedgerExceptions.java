package com.app.exception;

import io.micronaut.http.HttpStatus;

public class LedgerExceptions {

    public static class AccountNotFoundException extends LedgerBaseException {
        public AccountNotFoundException(String accountId) {
            super("Account not found with id: " + accountId, HttpStatus.NOT_FOUND);
        }
    }

    public static class InsufficientFundsException extends LedgerBaseException {
        public InsufficientFundsException(String accountId) {
            super("Insufficient funds for account id: " + accountId, HttpStatus.BAD_REQUEST);
        }
    }

    public static class PositiveBalanceException extends LedgerBaseException {
        public PositiveBalanceException(String accountId) {
            super("Cannot delete positive balance for account id: " + accountId +
                    "\nPlease empty account first.", HttpStatus.BAD_REQUEST);
        }
    }


}
