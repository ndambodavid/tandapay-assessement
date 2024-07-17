package com.tandapayinterview.integration.request;

public record GatewayRequest(
        String OriginatorConversationID,
         String InitiatorName,
         String SecurityCredential,
         String CommandID,
         String Amount,
         String PartyA,
         String PartyB,
         String Remarks,
         String QueueTimeOutURL,
         String ResultURL,
         String Occassion
) {
}
