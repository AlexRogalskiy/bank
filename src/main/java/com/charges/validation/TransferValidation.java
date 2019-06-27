package com.charges.validation;


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
public class TransferValidation {
    private BigDecimal amount;

    private String accountNumberFrom;

    private String accountNumberTo;
}
