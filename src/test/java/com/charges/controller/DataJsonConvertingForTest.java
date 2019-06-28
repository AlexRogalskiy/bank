package com.charges.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

public interface DataJsonConvertingForTest {
    int PORT = 8888;
    String URL = "http://localhost:" + PORT;

    default String convertDataToJson(final Object data, final ObjectMapper mapper) throws IOException {
        StringWriter sw = new StringWriter();
        mapper.writeValue(sw, data);
        return sw.toString();
    }

    default Object convertJsonToData(final String json, final Class clazz, final ObjectMapper mapper) throws IOException{
        return mapper.readValue(json, clazz);
    }
}
