package com.ecommerce.application.exceptions;

import lombok.NoArgsConstructor;

import java.io.Serial;


@NoArgsConstructor
public class APIException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public APIException(String message) {
        super(message);
    }
}
