package com.charges.controller;

import com.charges.service.AccountService;
import com.google.inject.Inject;
import spark.Service;

import com.charges.validation.AccountValidation;

public class AccountController implements DataConverting<AccountValidation> {
    @Inject
    private AccountService accountService;

    @Override
    public void configure(final Service spark) {
        spark.get("/accounts/:accountNumber", (req, res) -> convertDataToJson(accountService
                .getAccountByName(req.params("accountNumber"))));

        spark.post("/clients/:clientId/accounts", (req, res) -> {
            final var account = accountService.insertAccount(req.params("clientId"),
                    converJsonToData(req.body(), AccountValidation.class));
            return convertDataToJson(account);
        });

        spark.put("/clients/:clientId/accounts/:accountId", (req, res) -> {
            final var account = accountService.updateAccount(req.params("clientId"),
                    req.params("accountId"),
                    converJsonToData(req.body(), AccountValidation.class));
            return convertDataToJson(account);
        });

        spark.delete("/clients/:clientId/accounts/:accountId", (req, res) -> {
            accountService.deleteAccount(req.params("clientId"), req.params("accountId"));
            return "";
        });
    }
}