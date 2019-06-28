package com.charges.integration;

import com.charges.controller.AccountController;
import com.charges.controller.ClientController;
import com.charges.controller.DataJsonConvertingForTest;
import com.charges.controller.TransferController;
import com.charges.model.Account;
import com.charges.model.Client;
import com.charges.model.Transfer;
import com.charges.service.AccountService;
import com.charges.service.ClientService;
import com.charges.service.TransferService;
import com.charges.validation.AccountValidation;
import com.charges.validation.ClientValidation;
import com.charges.validation.TransferValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mybatis.guice.XMLMyBatisModule;
import org.mybatis.guice.datasource.helper.JdbcHelper;
import spark.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.inject.Guice.createInjector;
import static org.junit.Assert.assertEquals;

public class IntegrationTest implements DataJsonConvertingForTest {
    private static final int PORT = 8888;
    private static final String URL = "http://localhost:" + PORT;
    private static Injector injector = getInjector();
    private Service spark;
    private HttpClient client = HttpClient.newHttpClient();
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() {
        spark = Service.ignite().port(PORT);

        final var clientController = injector.getInstance(ClientController.class);
        final var transferController = injector.getInstance(TransferController.class);
        final var accountController = injector.getInstance(AccountController.class);

        clientController.configure(spark);
        transferController.configure(spark);
        accountController.configure(spark);

        spark.awaitInitialization();
    }

    @After
    public void tearDown() {
        spark.stop();
        spark.awaitStop();
    }

    @Test
    public void execute100transfers_10000to10Randomaccounts_checkBalances() throws Exception {
        final var oneMillionAccountFromNumber = "Test99999";
        final var amount = BigDecimal.valueOf(1000000, 2);
        final var clientsId = List.of("Vadim", "Alina", "Ivan", "Natalia", "NataliaXXX", "Viktorai", "Aman", "Irina", "Nastja")
                .stream()
                .map(this::createClient)
                .collect(Collectors.toList());
        final var accountsId = clientsId
                .stream()
                .map(this::createAccount)
                .collect(Collectors.toList());

        IntStream.rangeClosed(1, 25)
                .parallel()
                .forEach(i -> accountsId.forEach(id -> makeTransfer("1", oneMillionAccountFromNumber, id, amount)));

        final Double actualReplenishAccountsSum = clientsId.stream().map(this::getAccountBalance).reduce(0D, Double::sum);
        assertEquals(Double.valueOf(1000000), actualReplenishAccountsSum);
        assertEquals(0D, getAccountBalance("1"), 0.1);
    }


    private static Injector getInjector() {
        return createInjector(new XMLMyBatisModule() {
            @Override
            protected void initialize() {
                install(JdbcHelper.HSQLDB_Embedded);
                bind(ClientService.class);
                bind(TransferService.class);
                bind(AccountService.class);
            }
        });
    }

    private String createClient(final String name) {
        final var input = ClientValidation.builder()
                .name(name)
                .build();
        try {
            final var request = HttpRequest.newBuilder(URI.create(URL + "/clients"))
                    .POST(HttpRequest.BodyPublishers.ofString(convertDataToJson(input, mapper)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Client client = (Client) convertJsonToData(response.body(), Client.class, mapper);
            return client.getId().toString();
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String createAccount(final String clientId) {
        final var input = AccountValidation.builder()
                .number(clientId + "Number")
                .build();
        try {
            final var request = HttpRequest.newBuilder(URI.create(URL + "/clients/" + clientId + "/accounts"))
                    .POST(HttpRequest.BodyPublishers.ofString(convertDataToJson(input, mapper)))
                    .build();
            final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            final var account = (Account) convertJsonToData(response.body(), Account.class, mapper);
            return account.getNumber();
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void makeTransfer(final String clientId,
                              final String fromAccountName,
                              final String toAccountName,
                              final BigDecimal amount) {

        final var input = TransferValidation.builder()
                .amount(amount)
                .accountNumberFrom(fromAccountName)
                .accountNumberTo(toAccountName)
                .build();
        try {
            final var request = HttpRequest.newBuilder(URI.create(URL + "/clients/" + clientId + "/transfers"))
                    .POST(HttpRequest.BodyPublishers.ofString(convertDataToJson(input, mapper)))
                    .build();
            final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            final var transfer = (Transfer) convertJsonToData(response.body(), Transfer.class, mapper);
            executeTransfer(clientId, transfer.getId().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeTransfer(String clientId, String transferId) throws Exception {
        final var request = HttpRequest.newBuilder(URI.create(URL + "/clients/" + clientId + "/transfers/" + transferId))
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private double getAccountBalance(String clientId) {
        final var request = HttpRequest.newBuilder(URI.create(URL + "/clients/" + clientId))
                .GET()
                .build();

        try {
            final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            final var client = (Client) convertJsonToData(response.body(), Client.class, mapper);
            return client.getAccounts().get(0).getBalance().doubleValue();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0D;
    }
}
