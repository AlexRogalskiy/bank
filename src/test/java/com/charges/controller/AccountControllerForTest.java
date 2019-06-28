package com.charges.controller;

import com.charges.model.Account;
import com.charges.service.AccountService;
import com.charges.validation.AccountValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountControllerForTest implements DataJsonConvertingForTest, GenerateInjector {
    private Service spark;
    private final HttpClient client = HttpClient.newHttpClient();
    private AccountService accountService = mock(AccountService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        spark = Service.ignite().port(PORT);
        getInjector(AccountService.class, AccountController.class, accountService)
                .getInstance(AccountController.class)
                .configure(spark);
        spark.awaitInitialization();
    }

    @After
    public void tearDown() throws Exception {
        spark.stop();
        spark.awaitStop();
    }

    @Test
    public void insertAccount_validAcount_insertedAccount() throws Exception {
        final var input = AccountValidation.builder()
                .number("Test99999")
                .build();
        final var expected = Account.builder()
                .number("Test99999")
                .build();
        when(accountService.insertAccount("1", input)).thenReturn(expected);

        final var request = HttpRequest.newBuilder(URI.create(URL + "/clients/1/accounts"))
                .POST(HttpRequest.BodyPublishers.ofString(convertDataToJson(input, mapper)))
                .build();
        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode(), Is.is(200));
        assertThat(convertJsonToData(response.body(), Account.class, mapper), Is.is(expected));
    }

    @Test
    public void updateAccountPut_validAccount_updatedAccount() throws Exception {
        final var input = AccountValidation.builder()
                .number("Test99999")
                .build();
        final var expected = Account.builder()
                .number("Test99999")
                .build();
        when(accountService.updateAccount("1", "2", input)).thenReturn(expected);

        final var request = HttpRequest.newBuilder(URI.create(URL + "/clients/1/accounts/2"))
                .PUT(HttpRequest.BodyPublishers.ofString(convertDataToJson(input, mapper)))
                .build();
        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode(), Is.is(200));
        assertThat(convertJsonToData(response.body(), Account.class, mapper), Is.is(expected));
    }
}