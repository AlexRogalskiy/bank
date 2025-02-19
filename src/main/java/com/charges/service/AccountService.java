package com.charges.service;

import com.charges.db.AccountRepository;
import com.charges.model.Account;

import com.charges.validation.AccountValidation;
import com.google.inject.Inject;
import org.mybatis.guice.transactional.Transactional;

import javax.inject.Singleton;
import java.math.BigDecimal;

import static spark.Spark.halt;

@Singleton
public class AccountService {
    @Inject
    private AccountRepository accountRepository;

    @Inject
    private ClientService clientService;

    public Account getAccountByNumber(final String accountNumber) {
        return accountRepository.getAccountByNumber(accountNumber);
    }

    public void updateBalance(final Long accountId, final BigDecimal balance) {
        accountRepository.updateBalance(accountId, balance);
    }

    @Transactional
    public Account insertAccount(final String clientId, final AccountValidation accountValidation) {
        final var selectedClient = clientService.getClientById(clientId);
        if (selectedClient == null) {
            halt(400, "Client not found");
        }
        final var account = Account.builder()
                .number(accountValidation.getNumber())
                .clientId(Long.valueOf(clientId))
                .build();
        accountRepository.insertAccount(account);
        return accountRepository.selectAccount(account.getId());
    }

    @Transactional
    public Account updateAccount(String pClientId, String pAccountId, AccountValidation accountValidation) {
        final var clientId = Long.valueOf(pClientId);
        final var accountId = Long.valueOf(pAccountId);
        validateAccountId(clientId, accountId);
        final var account = Account.builder()
                .id(accountId)
                .number(accountValidation.getNumber())
                .clientId(clientId)
                .build();
        accountRepository.updateAccount(account);
        return accountRepository.selectAccount(accountId);
    }

    private void validateAccountId(Long clientId, Long accountId) {
        final var selected = accountRepository.selectAccount(accountId);
        if (selected == null) {
            halt(400, "Account can`t found");
        }
        if (!selected.getClientId().equals(clientId)) {
            halt(400, "Account don't belong to the user");
        }
    }

}
