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
        return clientRepository.getAllClients();
    }

    public Client getClientById(final String clientId) {
        return clientRepository.getClientById(Long.valueOf(clientId));
    }

    @Transactional
    public Client addClient(final ClientValidation clientValidation) {
        final var client = Client.builder()
                .name(clientValidation.getName())
                .build();
        clientRepository.addClient(client);
        return clientRepository.getClientById(client.getId());
    }

    private void validateClientId(final Long clientId) {
        final var selected = clientRepository.getClientById(clientId);
        if (selected == null) {
            halt(400, "Client not found");
        }
    }
}