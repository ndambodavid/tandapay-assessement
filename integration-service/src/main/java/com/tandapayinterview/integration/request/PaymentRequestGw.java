package com.tandapayinterview.integration.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Currency;

public record PaymentRequestGw(

//        String id,
//        @NotNull(message = "paymentId must be provided")
        String paymentId,
//        @NotNull(message = "payment amount must be provided")
//        @DecimalMin(value = "10.00", inclusive = false, message = "Amount must be greater than 0")
//        @Digits(integer = 6, fraction = 2, message = "Amount must be a valid decimal number with up to 6 integer digits and 2 fractional digits")
        Float payableAmount,
//        @Pattern(regexp = "^254\\d{9}$", message = "Phone number must start with 254 and contain exactly 12 characters")
//        @NotNull(message = "recipient mssdn must be provided")
        String mobileNumber
//        String status,
//        String reference
) {
}
