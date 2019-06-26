package com.charges.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Service;

import java.io.IOException;
import java.io.StringWriter;


public interface DataConverting<T> {
    ObjectMapper mapper = new ObjectMapper();

    default <V>String convertDataToJson(V data) {
        try {
            final var stringWriter = new StringWriter();
            mapper.writeValue(stringWriter, data);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException("IOException in convertDataToJson method" + data, e);
        }
    }

    default T converJsonToData(final String json, final Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("IOException in converJsonToData method" + json, e);
        }
    }

    void configure(final Service spark);
}