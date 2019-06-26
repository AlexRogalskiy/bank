package com.charges;

import com.charges.controller.AccountController;
import com.charges.controller.ClientController;
import com.charges.service.AccountService;
import com.charges.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Injector;
import org.mybatis.guice.datasource.helper.JdbcHelper;
import spark.Service;
import org.mybatis.guice.XMLMyBatisModule;

import javax.inject.Provider;
import javax.inject.Singleton;

import java.text.SimpleDateFormat;

import static com.google.inject.Guice.createInjector;

public class ChargesApp {
    private static Injector serviceInit = serviceInit();

    private static Injector serviceInit() {
        return createInjector(new XMLMyBatisModule() {
            @Override
            protected void initialize() {
                install(JdbcHelper.HSQLDB_Embedded);
                bind(ClientService.class);
                bind(AccountService.class);
                bind(ObjectMapper.class)
                        .toProvider(ObjectMapperProvider.class)
                        .in(Singleton.class);
            }
        });
    }

    public static void main(String[] args) {
        final var spark = Service.ignite().port(8080);
        serviceInit.getInstance(ClientController.class).configure(spark);
        serviceInit.getInstance(AccountController.class).configure(spark);
        spark.awaitInitialization();

    }

    static class ObjectMapperProvider implements Provider<ObjectMapper> {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

        public ObjectMapper get() {
            final var mapper = new ObjectMapper();
            mapper.setDateFormat(dateFormat);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper;
        }
    }

}