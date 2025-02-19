package com.charges.controller;

import com.charges.service.TransferService;
import com.charges.validation.TransferValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import spark.Service;

public class TransferController implements DataJsonConverting<TransferValidation>, ControllerConfigure {
    @Inject
    private TransferService transferService;

    @Inject
    private ObjectMapper mapper;

    @Override
    public void configure(Service spark) {
        spark.get("/clients/:clientId/transfers", (req, res) ->
                convertDataToJson(transferService.getTransfersByClientId(req.params("clientId")), mapper));

        spark.get("/clients/:clientId/transfers/:transferId", (req, res) ->
                convertDataToJson(transferService.getTransferByIdAndClientId(req.params("clientId"), req.params("transferId")), mapper));

        spark.post("/clients/:clientId/transfers", (req, res) -> {
            final var transfer = transferService.createTransfer(req.params("clientId"),
                    convertJsonToData(req.body(), TransferValidation.class, mapper));
            return convertDataToJson(transfer, mapper);
        });

        spark.put("/clients/:clientId/transfers/:transferId", (req, res) ->
                convertDataToJson(transferService.executeTransfer(req.params("clientId"), req.params("transferId")), mapper));
    }
}
