package com.tandapayinterview.integration.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Data
//@AllArgsConstructor
//@RequiredArgsConstructor
//@NoArgsConstructor
public class SyncGwResponse {
    @JsonProperty("ConversationID")
    private String conversationID;

    @JsonProperty("OriginatorConversationID")
    private String originatorConversationID;

    @JsonProperty("ResponseCode")
    private String responseCode;

    @JsonProperty("ResponseDescription")
    private String responseDescription;

}
