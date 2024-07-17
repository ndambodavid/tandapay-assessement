package com.tandapayinterview.core.model;

import com.mongodb.lang.Nullable;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Currency;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document
public class Payment {
    @Id
    @Generated
    private String id;
    private Double amount;
    @Valid
    private Currency currency;
    private String mobileNumber;
    @Nullable
    private String reference;
    private String status;
    @CreatedDate
    private LocalDateTime createdAt;
}
