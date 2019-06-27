package com.charges.controller;

import spark.Service;

public interface ControllerConfigure {
    public static final int PORT = 8888;

    void configure(final Service spark);
}
