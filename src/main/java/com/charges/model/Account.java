package com.charges.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
public class Account {
    private Long id;
    private String number;
    private BigDecimal totalBalance;
    private BigDecimal reservedBalance;
    @JsonIgnore
    private Long clientId;
}
