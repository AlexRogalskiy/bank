package com.charges.service;

import com.charges.db.TransferRepository;
import com.charges.model.Transfer;
import com.charges.validation.TransferValidation;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.hamcrest.core.Is;
import org.junit.Test;
import spark.HaltException;

import javax.inject.Provider;
import java.math.BigDecimal;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class TransferServiceTest {
    private Injector injector = getInjector();
    private TransferRepository transferRepository = mock(TransferRepository.class);
    private TransferInnerService transferInnerService = mock(TransferInnerService.class);
    private TransferService transferService = injector.getInstance(TransferService.class);

    @Test
    public void getTransfersByClientId_clientId_transferList() {
        transferService.getTransfersByClientId("1");

        verify(transferRepository).getTransfersByClientId(1L);
    }

    @Test
    public void getTransferByIdAndClientId_validIds_transfer() {
        when(transferRepository.getTransferByIdAndClientId(1L, 2L)).thenReturn(new Transfer());
        transferService.getTransferByIdAndClientId("1", "2");

        verify(transferRepository).getTransferByIdAndClientId(1L, 2L);
    }

    @Test(expected = HaltException.class)
    public void getTransfer_notExistingIds_halted() {
        transferService.getTransferByIdAndClientId("11", "22");

        verify(transferRepository).getTransferByIdAndClientId(11L, 22L);
    }

    @Test
    public void createTransfer_validTransfer_transfer() {
        final var validTransfer = TransferValidation.builder()
                .amount(BigDecimal.TEN)
                .accountNumberFrom("FROM")
                .accountNumberTo("TO")
                .build();
        final var expected = Transfer.builder()
                .id(1L)
                .build();
        when(transferInnerService.createValidTransfer(1L, validTransfer)).thenReturn(1L);
        when(transferRepository.getTransferByIdAndClientId(eq(1L), any())).thenReturn(expected);

        final var actualTransfer = transferService.createTransfer("1", validTransfer);

        verify(transferInnerService).createValidTransfer(1L, validTransfer);
        verify(transferRepository).getTransferByIdAndClientId(1L, 1L);
        assertThat(actualTransfer, Is.is(expected));
    }

    @Test
    public void executeTransfer_transferIdclientId_transfer() {
        final var expected = Transfer.builder()
                .id(2L)
                .build();
        when(transferRepository.getTransferByIdAndClientId(3L, 4L)).thenReturn(expected);

        final var transfer = transferService.executeTransfer("3", "4");

        verify(transferInnerService).checkAndExecuteTransfer(3L, 4L);
        verify(transferRepository).getTransferByIdAndClientId(3L, 4L);
        assertThat(transfer, Is.is(expected));
    }

    private Injector getInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(TransferService.class);
                bind(TransferRepository.class).toProvider(new Provider<TransferRepository>() {
                    public TransferRepository get() {
                        return transferRepository;
                    }
                });
                bind(TransferInnerService.class).toProvider(new Provider<TransferInnerService>() {
                    public TransferInnerService get() {
                        return transferInnerService;
                    }
                });
            }
        });
    }
}