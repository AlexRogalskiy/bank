package com.charges.db;

import com.charges.model.Transfer;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class TransferRepositoryTest implements SqlSessionFactoryGenerate {
    protected static SqlSessionFactory sqlSessionFactory;

    @Test
    public void getTransfersByClientId_clientId_validClient() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final var transferRepository = session.getMapper(TransferRepository.class);

            final var transfers = transferRepository.getTransfersByClientId(1L);

            assertNotNull(transfers);
            assertEquals(3, transfers.size());
            assertEquals("Test99999", transfers.get(0).getAccountNumberTo());
        }
    }

    @Test
    public void getTransfersByClientId_incorrectId_empty() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final var transferRepository = session.getMapper(TransferRepository.class);

            final var transfers = transferRepository.getTransfersByClientId(99L);

            assertNotNull(transfers);
            assertTrue(transfers.isEmpty());
        }
    }

    @Test
    public void getTransferByIdAndClientId_transferId_returns() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final var transferRepository = session.getMapper(TransferRepository.class);

            final var transfer = transferRepository.getTransferByIdAndClientId(1L, 1L);

            assertNotNull(transfer);
            assertEquals("Test7777", transfer.getAccountNumberTo());
        }
    }

    @Test
    public void getTransferByIdAndClientId_incorrectId_empty() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final var transferRepository = session.getMapper(TransferRepository.class);

            final var transfer = transferRepository.getTransferByIdAndClientId(99L, 99L);

            assertNull(transfer);
        }
    }

    @Test
    public void addTransfer_transfer_added() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final var transferRepository = session.getMapper(TransferRepository.class);
            final var transfer = Transfer.builder()
                    .amount(BigDecimal.TEN)
                    .clientId(1L)
                    .build();

            transferRepository.addTransfer(transfer, 1L, 2L);

            assertNotNull(transfer.getId());
            assertNotNull(transferRepository.getTransferByIdAndClientId(1L, transfer.getId()));
        }
    }
}