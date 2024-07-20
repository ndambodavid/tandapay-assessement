package com.tandapayinterview.integration.request;

public record PaymentRequestPayload(

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
