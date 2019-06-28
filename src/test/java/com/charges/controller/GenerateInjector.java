package com.charges.controller;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.inject.Provider;

public interface GenerateInjector {

    default <S, C> Injector getInjector(Class<S> serviceClass, Class<C> controllerClass, S service) {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(controllerClass);
                bind(serviceClass).toProvider(new Provider<S>() {
                    public S get() {
                        return service;
                    }
                });
            }
        });
    }
}
