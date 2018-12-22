package com.hvo;

import com.hvo.cli.CommandLineApp;

import java.util.logging.LogManager;

public class Main {
    public static void main(String[] args) {
        // to disable logging
        LogManager.getLogManager().reset();
        CommandLineApp app = new CommandLineApp();
        app.start();
    }
}
