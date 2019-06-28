package com.charges.db;

import com.charges.model.Transfer;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class TransferRepositoryTest implements SqlSessionFactoryGenerate {
    protected static SqlSessionFactory sqlSessionFactory;

    @Before
    public void setUp() throws Exception {
        sqlSessionFactory = getSqlSessionFactory();
    }

    @Test
    public void getTransfersByClientId_clientId_validClient() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final var transferRepository = session.getMapper(TransferRepository.class);

            final var transfers = transferRepository.getTransfersByClientId(1L);

            assertThat(transfers, Is.is(notNullValue()));
            assertThat(transfers.size(), Is.is(3));
            assertThat(transfers.get(0).getAccountNumberTo(), Is.is("Test99999"));
        }
    }

    @Test
    public void getTransfersByClientId_incorrectId_empty() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final var transferRepository = session.getMapper(TransferRepository.class);

            final var transfers = transferRepository.getTransfersByClientId(99L);

            assertThat(transfers, Is.is(notNullValue()));
            assertTrue(transfers.isEmpty());
        }
    }

    @Test
    public void getTransferByIdAndClientId_transferId_returns() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final var transferRepository = session.getMapper(TransferRepository.class);

            final var transfer = transferRepository.getTransferByIdAndClientId(1L, 1L);

            assertThat(transfer, Is.is(notNullValue()));
            assertThat(transfer.getAccountNumberTo(), Is.is("Test7777"));
        }
    }

    @Test
    public void getTransferByIdAndClientId_incorrectId_empty() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final var transferRepository = session.getMapper(TransferRepository.class);

            final var transfer = transferRepository.getTransferByIdAndClientId(99L, 99L);

            assertThat(transfer, Is.is(nullValue()));
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

            assertThat(transfer.getId(), Is.is(notNullValue()));
            assertThat(transferRepository.getTransferByIdAndClientId(1L, transfer.getId()), Is.is(notNullValue()));
        }
    }
}