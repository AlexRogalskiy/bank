package com.charges.controller;

import com.charges.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import spark.Service;

import com.charges.validation.AccountValidation;

public class AccountController implements DataJsonConverting<AccountValidation>, ControllerConfigure {
    @Inject
    private AccountService accountService;

    @Inject
    private ObjectMapper mapper;

    @Override
    public void configure(final Service spark) {
        spark.get("/accounts/:accountNumber", (req, res) -> convertDataToJson(accountService
                .getAccountByNumber(req.params("accountNumber")), mapper));

        spark.post("/clients/:clientId/accounts", (req, res) -> {
            final var account = accountService.insertAccount(req.params("clientId"),
                    convertJsonToData(req.body(), AccountValidation.class, mapper));
            return convertDataToJson(account, mapper);
        });

        spark.put("/clients/:clientId/accounts/:accountId", (req, res) -> {
            final var account = accountService.updateAccount(req.params("clientId"),
                    req.params("accountId"),
                    convertJsonToData(req.body(), AccountValidation.class, mapper));
            return convertDataToJson(account, mapper);
        });
    }
}