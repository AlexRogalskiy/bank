package com.charges.controller;

import com.charges.model.Transfer;
import com.charges.service.TransferService;
import com.charges.validation.TransferValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransferControllerForTest implements DataJsonConvertingForTest, GenerateInjector {
    private Service spark;
    private HttpClient client = HttpClient.newHttpClient();
    private TransferService transferService = mock(TransferService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() {
        spark = Service.ignite().port(PORT);
        getInjector(TransferService.class, TransferController.class, transferService).getInstance(TransferController.class).configure(spark);
        spark.awaitInitialization();
    }

    @After
    public void tearDown() {
        spark.stop();
        spark.awaitStop();
    }

    @Test
    public void getTransferByClientId_transfersList() throws Exception {
        final var transfer = Transfer.builder()
                .clientId(1L)
                .amount(BigDecimal.TEN)
                .build();
        final var expectedList = Collections.singletonList(transfer);
        when(transferService.getTransfersByClientId("1")).thenReturn(expectedList);

        final var request = HttpRequest.newBuilder(URI.create(URL + "/clients/1/transfers"))
                .GET()
                .build();
        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(convertDataToJson(expectedList, mapper), response.body());
    }

    @Test
    public void getTransferByIdAndClientId_clientIdAndTransferId_transfer() throws Exception {
        final var expected = Transfer.builder()
                .id(1L)
                .amount(BigDecimal.TEN)
                .build();
        when(transferService.getTransferByIdAndClientId("1", "2")).thenReturn(expected);

        final var request = HttpRequest.newBuilder(URI.create(URL + "/clients/1/transfers/2"))
                .GET()
                .build();
        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(convertDataToJson(expected, mapper), response.body());
    }

    @Test
    public void createTransferPost_transferValidation_createTransfer() throws Exception {
        final var input = TransferValidation.builder()
                .amount(BigDecimal.TEN)
                .accountNumberFrom("TVISA000001")
                .accountNumberTo("TVISA555555")
                .build();
        final var expected = Transfer.builder()
                .id(5L)
                .amount(BigDecimal.TEN)
                .build();
        when(transferService.createTransfer("1", input)).thenReturn(expected);

        final var request = HttpRequest.newBuilder(URI.create(URL + "/clients/1/transfers"))
                .POST(HttpRequest.BodyPublishers.ofString(convertDataToJson(input, mapper)))
                .build();
        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(convertDataToJson(expected, mapper), response.body());
    }

    @Test
    public void executeTransferPost_transferId_executeTransfer() throws Exception {
        final var expected = Transfer.builder()
                .id(5L)
                .amount(BigDecimal.TEN)
                .build();
        when(transferService.executeTransfer("1", "2")).thenReturn(expected);

        final var request = HttpRequest.newBuilder(URI.create(URL + "/clients/1/transfers/2"))
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();
        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(convertDataToJson(expected, mapper), response.body());
    }
}