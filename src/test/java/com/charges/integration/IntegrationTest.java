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
import org.hamcrest.core.Is;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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
    public void generateAndExecute_100transfers_per10000_to_5randomAccounts_in20threads_checkBalances() throws Exception {
        final var oneMillionAccountFromNumber = "Test99999";
        assertEquals(1_000_000d, getAccountBalance("1"), 0.1);
        final var amountToTransfer = BigDecimal.valueOf(1_000_000, 2);
        final var clientsId = List.of("Vadim", "Alina", "Ivan", "Natalia", "John")
                .stream()
                .map(this::createClient)
                .collect(Collectors.toList());

        final var replenishAccountsId = clientsId
                .stream()
                .map((String clientId) -> createAccount2(clientId, "Account" + clientId))
                .collect(Collectors.toList());

        IntStream.rangeClosed(1, 20)
                .parallel()
                .forEach(i -> replenishAccountsId
                        .forEach(id -> createAndExecuteTransfer("1", oneMillionAccountFromNumber, id, amountToTransfer)));

        final double actualReplenishAccountsSum = clientsId
                .stream()
                .map(this::getAccountBalance)
                .reduce(0D, Double::sum);

        assertEquals(1_000_000d, actualReplenishAccountsSum, 0.1);
        assertThat(getAccountBalance("1"), Is.is(0D));
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

    private String createAccount2(final String clientId, String accountNumber) {
        final var input = AccountValidation.builder()
                .number(accountNumber)
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

    private void createAndExecuteTransfer(final String clientId,
                                          final String fromAccountNumber,
                                          final String toAccountNumber,
                                          final BigDecimal amount) {
        final var input = TransferValidation.builder()
                .amount(amount)
                .accountNumberFrom(fromAccountNumber)
                .accountNumberTo(toAccountNumber)
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
