package com.tandapayinterview.integration.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
//@AllArgsConstructor
//@RequiredArgsConstructor
//@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class AsyncGwResponse extends SyncGwResponse {

    @JsonProperty("Result")
    private Result result;

    @Data
    public static class Result {
        @JsonProperty("ResultType")
        private int resultType;

        @JsonProperty("ResultCode")
        private int resultCode;

        @JsonProperty("ResultDesc")
        private String resultDesc;

        @JsonProperty("OriginatorConversationID")
        private String originatorConversationID;

        @JsonProperty("ConversationID")
        private String conversationID;

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
        private Object value;
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

        @JsonProperty("Value")
        private String value;
    }
}