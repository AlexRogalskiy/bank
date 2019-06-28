package com.charges.db;

import com.charges.model.Client;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

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

            assertThat(clients.size(), Is.is(4));
        }
    }

    @Test
    public void getClientById_id_client() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final var clientRepository = session.getMapper(ClientRepository.class);

            final var client = clientRepository.getClientById(1L);

            assertNotNull(client);
            assertThat(client.getAccounts().size(), Is.is(1));
        }
    }

    @Test
    public void getClientById_incorrectId_null() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final var clientRepository = session.getMapper(ClientRepository.class);

            final var client = clientRepository.getClientById(99L);

            assertThat(client, Is.is(nullValue()));
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

            assertThat(inserted, Is.is(notNullValue()));
            assertThat(inserted.getName(), Is.is(newName));
        }
    }
}