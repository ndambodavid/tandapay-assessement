package com.tandapayinterview.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// payload from integration service
@Getter
@Setter
@AllArgsConstructor
public class GatewayResponse {
    private String paymentId;
    private String reference;
    private String status;
}
