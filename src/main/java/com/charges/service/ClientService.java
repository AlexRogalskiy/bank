package com.charges.service;

import com.charges.db.ClientRepository;
import com.charges.model.Client;
import com.charges.validation.ClientValidation;
import org.mybatis.guice.transactional.Transactional;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static spark.Spark.halt;

@Singleton
public class ClientService {
    public static final String RESPONSE_EXCEPTION = "{\"errorMessage\":\"%s\"}";

    @Inject
    private ClientRepository clientRepository;

    public List<Client> getClients() {
        return clientRepository.getClients();
    }

    public Client getClient(final String clientId) {
        return clientRepository.getClient(Long.valueOf(clientId));
    }

    @Transactional
    public Client insertClient(final ClientValidation clientValidation) {
        Client client = Client.builder()
                .name(clientValidation.getName())
                .build();
        clientRepository.insert(client);
        return clientRepository.getClient(client.getId());
    }

    @Transactional
    public Client updateClient(final String pClientId,
                               final ClientValidation clientValidation) {
        final var clientId = Long.valueOf(pClientId);
        validateClientId(clientId);
        final var client = Client.builder()
                .id(clientId)
                .name(clientValidation.getName())
                .build();
        clientRepository.update(client);
        return clientRepository.getClient(clientId);
    }

    @Transactional
    public void deleteClient(final String pClientId) {
        final var clientId = Long.valueOf(pClientId);
        validateClientId(clientId);
        clientRepository.delete(clientId);
    }

    private void validateClientId(final Long clientId) {
        final var selected = clientRepository.getClient(clientId);
        if (selected == null) {
            halt(422, String.format(RESPONSE_EXCEPTION, "Client not found"));
        }
    }
}