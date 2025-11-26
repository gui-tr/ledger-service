package com.app.api;

import io.micronaut.http.HttpStatus;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Serdeable
public class ApiResponse<T> {
    private HttpStatus statusCode;
    private String message;
    private T data;
}