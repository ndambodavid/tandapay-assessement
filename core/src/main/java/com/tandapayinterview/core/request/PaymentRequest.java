package com.tandapayinterview.core.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Currency;

public record PaymentRequest(

        String id,
        @NotNull(message = "payment amount must be provided")
        @DecimalMin(value = "10.00", inclusive = true, message = "Amount must be 10.00 or greater")
        @DecimalMax(value = "500000", inclusive = true, message = "Amount must be utmost 500000")
        @Digits(integer = 6, fraction = 2, message = "Amount must be a valid decimal number with up to 6 integer digits and 2 fractional digits")
        Float amount,
        @Pattern(regexp = "^254\\d{9}$", message = "Phone number must start with 254 and contain exactly 12 characters")
        @NotNull(message = "recipient mobileNumber must be provided")
        String mobileNumber,
        String status,
        String reference
) {
}
