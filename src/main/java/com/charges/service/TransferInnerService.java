package com.charges.service;

import com.charges.db.TransferRepository;
import com.charges.model.Transfer;
import com.charges.validation.TransferValidation;
import com.google.inject.Inject;
import org.mybatis.guice.transactional.Transactional;

import java.math.BigDecimal;

import static spark.Spark.halt;

public class TransferInnerService {
    @Inject
    private AccountService accountService;

    @Inject
    private TransferRepository transferRepository;

    @Transactional
    public Long createTransfer(final Long clientId,
                               final TransferValidation transferValidation) {
        final var fromAccount = accountService.getAccountByNumber(transferValidation.getAccountNumberFrom());
        if (transferValidation.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            halt(400, "Amount cant be zero");

        if (fromAccount == null)
            halt(400, "Cant find account from");

        if (!fromAccount.getClientId().equals(clientId))
            halt(400, "From account do not belong to user");

        final var accountNumberTo = accountService
                .getAccountByNumber(transferValidation.getAccountNumberTo());

        if (accountNumberTo == null) {
            halt(400, "AccountNumberTo not found");
        }

        final var transfer = Transfer.builder()
                .amount(transferValidation.getAmount())
                .clientId(clientId)
                .build();

        transferRepository.addTransfer(transfer, fromAccount.getId(), accountNumberTo.getId());

        return transfer.getId();
    }

    @Transactional
    public void checkAndExecuteTransfer(final Long clientId,
                                        final Long transferId) {
        final var transfer = transferRepository.getTransferByIdAndClientId(clientId, transferId);

        if (transfer == null) halt(400, "Cant find transfer");

        final var accountNumberFrom = accountService.getAccountByNumber(transfer.getAccountNumberFrom());
        final var accountNumberTo = accountService.getAccountByNumber(transfer.getAccountNumberTo());

        if (accountNumberFrom.getBalance().compareTo(transfer.getAmount()) < 0)
            halt(400, "Account cant be less zero");

        accountService.updateBalance(accountNumberFrom.getId(),
                accountNumberFrom.getBalance().subtract(transfer.getAmount()));
        accountService.updateBalance(accountNumberTo.getId(),
                accountNumberTo.getBalance().add(transfer.getAmount()));
    }
}