package com.tandapayinterview.core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// payload from integration service
@Getter
@Setter
@AllArgsConstructor
public class GatewayResponsePayload {
    private String paymentId;
    private String reference;
    private String status;
}
