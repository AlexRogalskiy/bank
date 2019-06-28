package com.charges.controller;

import com.charges.model.Client;
import com.charges.service.ClientService;
import com.charges.validation.ClientValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientControllerForTest implements DataJsonConvertingForTest, GenerateInjector {
    private Service spark;
    private HttpClient client = HttpClient.newHttpClient();
    private ClientService clientService = mock(ClientService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        spark = Service.ignite().port(PORT);
        getInjector(ClientService.class, ClientController.class, clientService).getInstance(ClientController.class).configure(spark);
        spark.awaitInitialization();
    }

    @After
    public void tearDown() throws Exception {
        spark.stop();
        spark.awaitStop();
    }

    @Test
    public void getClients_clientsList() throws IOException, InterruptedException {
        final var expectedClient = Client.builder()
                .name("Petr Petrov")
                .id(1L)
                .build();
        final var expected = Collections.singletonList(expectedClient);
        when(clientService.getClients()).thenReturn(expected);

        final var request = HttpRequest.newBuilder(URI.create(URL + "/clients"))
                .GET()
                .build();
        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode(), Is.is(200));
        assertThat(response.body(), Is.is(convertDataToJson(expected, mapper)));
    }

    @Test
    public void getClientById_clientId_client() throws Exception {
        final var expected = Client.builder()
                .name("Ivan")
                .id(1L)
                .build();
        when(clientService.getClientById("1")).thenReturn(expected);

        final var request = HttpRequest.newBuilder(URI.create(URL + "/clients/1"))
                .GET()
                .build();
        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode(), Is.is(200));
        assertThat(response.body(), Is.is(convertDataToJson(expected, mapper)));
    }

    @Test
    public void insertClientPost_clientValidation_client() throws Exception {
        final var expected = Client.builder()
                .name("Ivan Ivanov")
                .id(1L)
                .build();
        final var input = ClientValidation.builder()
                .name("Ivan Ivanov")
                .build();
        when(clientService.insertClient(input)).thenReturn(expected);

        final var request = HttpRequest.newBuilder(URI.create(URL + "/clients"))
                .POST(HttpRequest.BodyPublishers.ofString(convertDataToJson(input, mapper)))
                .build();
        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode(), Is.is(200));
        assertThat(response.body(), Is.is(convertDataToJson(expected, mapper)));
    }
}