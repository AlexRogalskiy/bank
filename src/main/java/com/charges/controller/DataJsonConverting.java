package com.charges.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Service;

import java.io.IOException;
import java.io.StringWriter;


public interface DataJsonConverting<T> {
    default <V> String convertDataToJson(V data, final ObjectMapper mapper) {
        try {
            final var stringWriter = new StringWriter();
            mapper.writeValue(stringWriter, data);
            return stringWriter.toString();
        } catch (final IOException e) {
            throw new RuntimeException("IOException in convertDataToJson method" + data, e);
        }
    }

    default T convertJsonToData(final String json, final Class<T> clazz, final ObjectMapper mapper) {
        try {
            return mapper.readValue(json, clazz);
        } catch (final IOException e) {
            throw new RuntimeException("IOException in convertJsonToData method" + json, e);
        }
    }
}