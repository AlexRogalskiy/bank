package com.charges.db;

import com.charges.model.Client;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class ClientRepositoryTest implements SqlSessionFactoryGenerate {

    protected SqlSessionFactory sqlSessionFactory;

    @Before
    public void setUp() throws Exception {
        sqlSessionFactory = getSqlSessionFactory();
    }

    @Test
    public void getAllClients_clientsList() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final var clientRepository = session.getMapper(ClientRepository.class);

            final var clients = clientRepository.getAllClients();

            assertEquals(4, clients.size());
        }
    }

    @Test
    public void getClientById_id_client() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final var clientRepository = session.getMapper(ClientRepository.class);

            final var client = clientRepository.getClientById(1L);

            assertNotNull(client);
            assertEquals(1, client.getAccounts().size());
        }
    }

    @Test
    public void getClientById_incorrectId_null() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final var clientRepository = session.getMapper(ClientRepository.class);

            final var client = clientRepository.getClientById(99L);

            assertNull(client);
        }
    }

    @Test
    public void addClient_validClient() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final var clientRepository = session.getMapper(ClientRepository.class);
            final var newName = "Ivan Petrovich";
            final var client = Client.builder()
                    .name(newName)
                    .build();

            clientRepository.addClient(client);
            final var inserted = clientRepository.getClientById(client.getId());

            assertNotNull(inserted);
            assertEquals(newName, inserted.getName());
        }
    }
}