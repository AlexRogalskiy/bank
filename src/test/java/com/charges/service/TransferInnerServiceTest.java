package com.charges.service;

import com.charges.db.TransferRepository;
import com.charges.model.Account;
import com.charges.model.Transfer;
import com.charges.validation.TransferValidation;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import spark.HaltException;

import javax.inject.Provider;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class TransferInnerServiceTest {
    private Injector injector = getInjector();

    private TransferRepository transferRepository = mock(TransferRepository.class);
    private AccountService accountService = mock(AccountService.class);
    private TransferInnerService transferInnerService = injector.getInstance(TransferInnerService.class);

    @Test
    public void createValidTransfer_validTransfer_transferId() {
        final var transferValidation = TransferValidation.builder()
                .amount(BigDecimal.TEN)
                .accountNumberFrom("AccountFrom")
                .accountNumberTo("AccountTo")
                .build();
        final var account = Account.builder().id(2L).clientId(1L).build();
        when(accountService.getAccountByNumber(transferValidation.getAccountNumberFrom())).thenReturn(account);
        when(accountService.getAccountByNumber(transferValidation.getAccountNumberTo())).thenReturn(account);
        when(transferRepository.getTransferByIdAndClientId(eq(1L), any())).thenReturn(new Transfer());

        transferInnerService.createValidTransfer(1L, transferValidation);

        final var captor = ArgumentCaptor.forClass(Transfer.class);
        verify(transferRepository).addTransfer(captor.capture(), eq(2L), eq(2L));
        assertThat(captor.getValue().getAmount(), Is.is(BigDecimal.TEN));
    }

    @Test(expected = HaltException.class)
    public void validateAndCreateTransfer_zeroAmount_halt() {
        final var transferValidation = TransferValidation.builder()
                .amount(BigDecimal.ZERO)
                .accountNumberFrom("AccountFrom")
                .accountNumberTo("AccountTo")
                .build();
        final var account = Account.builder().id(2L).clientId(1L).build();
        when(accountService.getAccountByNumber(transferValidation.getAccountNumberFrom())).thenReturn(account);
        when(accountService.getAccountByNumber(transferValidation.getAccountNumberTo())).thenReturn(account);
        when(transferRepository.getTransferByIdAndClientId(eq(1L), any())).thenReturn(new Transfer());

        transferInnerService.createValidTransfer(1L, transferValidation);

        verifyZeroInteractions(transferRepository);
    }

    private Injector getInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(TransferInnerService.class);
                bind(TransferRepository.class).toProvider(new Provider<TransferRepository>() {
                    public TransferRepository get() {
                        return transferRepository;
                    }
                });
                bind(AccountService.class).toProvider(new Provider<AccountService>() {
                    public AccountService get() {
                        return accountService;
                    }
                });
            }
        });
    }

}