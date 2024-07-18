package com.tandapayinterview.integration.request;

// check transaction status payload
public record CheckTransactionStatusRequest(
        String OriginatorConversationID,
        String Initiator,
        String SecurityCredential,
        String CommandID,
        String TransactionId,
        String PartyA,
        String IdentifierType,
        String Remarks,
        String QueueTimeOutURL,
        String ResultURL,
        String Occassion
) {

}
