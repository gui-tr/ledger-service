package com.app.ledger.exception;

import io.micronaut.http.HttpStatus;
import lombok.Getter;

@Getter
public abstract class LedgerBaseException extends RuntimeException {
    private final HttpStatus status;

    protected LedgerBaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}

