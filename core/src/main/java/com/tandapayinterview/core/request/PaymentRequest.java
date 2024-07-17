package com.tandapayinterview.core.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Currency;

public record PaymentRequest(

        String id,
        @NotNull(message = "payment amount must be provided")
        Double amount,
        Currency currency,
        @NotNull(message = "recipient mssdn must be provided")
        String mobileNumber,
        String status,
        String reference
) {
}
