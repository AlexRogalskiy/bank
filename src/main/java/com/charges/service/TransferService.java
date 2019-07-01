package com.charges.service;

import com.charges.db.TransferRepository;
import com.charges.model.Transfer;
import com.charges.validation.TransferValidation;
import com.google.inject.Inject;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static spark.Spark.halt;

@Singleton
public class TransferService {
    private final ReentrantLock reentrantLock = new ReentrantLock(true);

    @Inject
    private TransferRepository transferRepository;

    @Inject
    private TransferInnerService transferInnerService;

    public List<Transfer> getTransfersByClientId(final String clientId) {
        return transferRepository.getTransfersByClientId(Long.valueOf(clientId));
    }

    public Transfer getTransferByIdAndClientId(final String clientId, final String transferId) {
        final var transfer = transferRepository
                .getTransferByIdAndClientId(Long.valueOf(clientId), Long.valueOf(transferId));

        if (transfer == null) halt(400, "Cant find transfer or client");

        return transfer;
    }

    public Transfer createTransfer(final String clientId, final TransferValidation transferValidation) {
        try {
            reentrantLock.lock();

            final var transferId = transferInnerService
                    .createValidTransfer(Long.valueOf(clientId), transferValidation);

            return transferRepository.getTransferByIdAndClientId(Long.valueOf(clientId), transferId);

        } catch (final Exception e) {
            e.printStackTrace();
            halt(400, e.getMessage());

        } finally {
            if (reentrantLock.isHeldByCurrentThread()) reentrantLock.unlock();
        }

        return null;
    }

    @Transactional
    public Transfer executeTransfer(final String pClientId, final String pTransferId) {
        try {
            reentrantLock.lock();

            final var clientId = Long.valueOf(pClientId);
            final var transferId = Long.valueOf(pTransferId);

            transferInnerService.checkAndExecuteTransfer(clientId, transferId);

            return transferRepository.getTransferByIdAndClientId(clientId, transferId);
        } catch (final Exception e) {
            e.printStackTrace();

            halt(400, e.getMessage());
        } finally {
            if (reentrantLock.isHeldByCurrentThread()) {
                reentrantLock.unlock();
            }
        }

        return null;
    }
}