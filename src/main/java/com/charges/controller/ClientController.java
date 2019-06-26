package com.charges.controller;

import com.charges.service.ClientService;
import com.charges.validation.ClientValidation;
import com.google.inject.Inject;
import spark.Service;

public class ClientController implements DataConverting<ClientValidation> {
    @Inject
    private ClientService clientService;

    @Override
    public void configure(Service spark) {
        spark.get("/clients", (req, res) -> convertDataToJson(clientService.getClients()));

        spark.get("/clients/:clientId", (req, res) -> convertDataToJson(clientService.getClient(req.params("clientId"))));

        spark.post("/clients", (req, res) -> {
            final var client = clientService.insertClient(converJsonToData(req.body(), ClientValidation.class));
            return convertDataToJson(client);
        });

        spark.put("/clients/:clientId", (req, res) -> {
            final var client = clientService.updateClient(req.params("clientId"),
                    converJsonToData(req.body(),
                            ClientValidation.class));
            return convertDataToJson(client);
        });

        spark.delete("/clients/:clientId", (req, res) -> {
            clientService
                    .deleteClient(req.params("clientId"));
            return "";
        });
    }
}
