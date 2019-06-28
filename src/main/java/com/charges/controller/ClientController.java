package com.charges.controller;

import com.charges.service.ClientService;
import com.charges.validation.ClientValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import spark.Service;

public class ClientController implements DataJsonConverting<ClientValidation>, ControllerConfigure {
    @Inject
    private ClientService clientService;

    @Inject
    private ObjectMapper mapper;

    @Override
    public void configure(Service spark) {
        spark.get("/clients", (req, res) -> convertDataToJson(clientService.getClients(), mapper));

        spark.get("/clients/:clientId", (req, res) -> convertDataToJson(clientService.getClientById(req.params("clientId")), mapper));

        spark.post("/clients", (req, res) -> {
            final var client = clientService.insertClient(convertJsonToData(req.body(), ClientValidation.class, mapper));
            return convertDataToJson(client, mapper);
        });

    }
}
