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
    @Inject
    private ClientRepository clientRepository;

    public List<Client> getClients() {
        return clientRepository.getClients();
    }

    public Client getClientById(final String clientId) {
        return clientRepository.getClient(Long.valueOf(clientId));
    }

    @Transactional
    public Client insertClient(final ClientValidation clientValidation) {
        final var client = Client.builder()
                .name(clientValidation.getName())
                .build();
        clientRepository.insert(client);
        return clientRepository.getClient(client.getId());
    }

    private void validateClientId(final Long clientId) {
        final var selected = clientRepository.getClient(clientId);
        if (selected == null) {
            halt(400, "Client not found");
        }
    }
}