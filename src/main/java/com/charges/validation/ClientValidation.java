package com.charges.validation;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
public class ClientValidation {
    private String name;
}
