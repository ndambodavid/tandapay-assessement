package com.tandapayinterview.integration.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
@EqualsAndHashCode(callSuper=false)
public class CheckTransactionStatusResponse extends SyncGwResponse{
    @JsonProperty("Result")
    private AsyncGwResponse.Result result;

    @Data
    public static class Result {
        @JsonProperty("ConversationID")
        private String conversationId;

        @JsonProperty("OriginatorConversationID")
        private int originatorConversationID;

        @JsonProperty("ResultCode")
        private String resultCode;

        @JsonProperty("ResultDesc")
        private String resultDesc;

        @JsonProperty("ResultType")
        private int resultType;

        @JsonProperty("TransactionID")
        private String transactionID;

        @JsonProperty("ResultParameters")
        private ResultParameters resultParameters;

        @JsonProperty("ReferenceData")
        private ReferenceData referenceData;
    }

    @Data
    public static class ResultParameters {
        @JsonProperty("ResultParameter")
        private List<ResultParameter> resultParameter;
    }

    @Data
    public static class ResultParameter {
        @JsonProperty("Key")
        private String key;

        @JsonProperty("Value")
        private String value;
    }

    @Data
    public static class ReferenceData {
        @JsonProperty("ReferenceItem")
        private ReferenceItem referenceItem;
    }

    @Data
    public static class ReferenceItem {
        @JsonProperty("Key")
        private String key;
    }
}
