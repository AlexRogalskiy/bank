package com.charges.controller;

import com.charges.model.Client;
import com.charges.service.ClientService;
import com.charges.validation.ClientValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Service;

import javax.inject.Provider;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;

import static com.charges.controller.ControllerConfigure.PORT;
import static com.charges.controller.DataJsonConvertingTest.URL;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientControllerTest implements DataJsonConverting<String>{
    private Service spark;
    private HttpClient client = HttpClient.newHttpClient();
    private ClientService clientService = mock(ClientService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        spark = Service.ignite().port(PORT);
        getInjector().getInstance(ClientController.class).configure(spark);
        spark.awaitInitialization();
    }

    @After
    public void tearDown() throws Exception {
    }

    private Injector getInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ClientController.class);
                bind(ClientService.class).toProvider(new Provider<ClientService>() {
                    public ClientService get() {
                        return clientService;
                    }
                });
            }
        });
    }


    @Test
    public void getClients_allClients() throws IOException, InterruptedException {
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

        assertEquals(200, response.statusCode());
        assertEquals(convertDataToJson(expected, mapper), response.body());

    }
}