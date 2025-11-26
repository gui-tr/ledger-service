package com.app.exception;

import io.micronaut.http.HttpStatus;

public class LedgerExceptions {

    public static class AccountNotFoundException extends LedgerBaseException {
        public AccountNotFoundException(String accountNo) {
            super("Account not found with no: " + accountNo, HttpStatus.NOT_FOUND);
        }
    }

    public static class InsufficientFundsException extends LedgerBaseException {
        public InsufficientFundsException(String accountNo) {
            super("Insufficient funds for account no: " + accountNo, HttpStatus.BAD_REQUEST);
        }
    }

    public static class PositiveBalanceException extends LedgerBaseException {
        public PositiveBalanceException(String accountNo) {
            super("Cannot delete positive balance for account no: " + accountNo +
                    "\nPlease empty account first.", HttpStatus.BAD_REQUEST);
        }
    }


}
