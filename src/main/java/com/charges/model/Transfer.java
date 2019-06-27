package com.charges.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.MODULE)
@NoArgsConstructor
public class Transfer {
    private Long id;

    private Timestamp creationDate;

    private BigDecimal amount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accountNumberTo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accountNumberFrom;

    @JsonIgnore
    private Long clientId;
}