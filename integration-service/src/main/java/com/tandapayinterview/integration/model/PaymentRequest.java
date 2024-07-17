package com.tandapayinterview.integration.model;

import com.mongodb.lang.Nullable;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document
public class PaymentRequest {
    @Id
    @Generated
    private String id;
    @Valid
    private String paymentId;
    @Valid
    private Float amount;
    @Valid
    private String mobileNumber;
    @Nullable
    private String reference;
    @Nullable
    private String status;
    @Nullable
    private String resultDesc;
    @CreatedDate
    private LocalDateTime createdAt;
}
