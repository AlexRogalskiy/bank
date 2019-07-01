package com.charges.service;

import com.charges.controller.GenerateInjector;
import com.charges.db.ClientRepository;
import com.charges.model.Client;
import com.charges.validation.ClientValidation;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Provider;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientServiceTest implements GenerateInjector {
    private ClientRepository clientRepository = mock(ClientRepository.class);
    private Injector injector = getInjector();
    private ClientService clientService = injector.getInstance(ClientService.class);

    @Test
    public void getClients_clientsList() {
        final var client = Client.builder().id(1L).name("Petr").build();
        final var expectedClientList = Collections.singletonList(client);
        when(clientRepository.getAllClients()).thenReturn(expectedClientList);

        final var clientList = clientService.getClients();

        assertEquals(expectedClientList, clientList);
    }

    @Test
    public void getClientById_validId_client() {
        final var expectedResult = Client.builder().id(1L).name("Petr").build();
        when(clientRepository.getClientById(1L)).thenReturn(expectedResult);

        final var client = clientService.getClientById("1");

        assertEquals(expectedResult, client);
    }

    @Test
    public void addClient_validClient_addedClient() {
        final var validClient = ClientValidation.builder().name("Ivan").build();

        clientService.addClient(validClient);

        final var captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).addClient(captor.capture());
        assertEquals("Ivan", captor.getValue().getName());
    }

    private Injector getInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ClientService.class);
                bind(ClientRepository.class).toProvider(new Provider<ClientRepository>() {
                    public ClientRepository get() {
                        return clientRepository;
                    }
                });
            }
        });
    }

}