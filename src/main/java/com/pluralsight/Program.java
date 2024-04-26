// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.terminal.*;

import java.io.*;

final class Program {
    public static void main(String[] args) {
        if (args.length < 1) System.out.println("No database file provided, using the default...");
        String location = (args.length < 1) ? "Transactions.csv" : args[0];

        try (var screen = new DefaultTerminalFactory().createScreen();
             var db = new TransactionDatabase(new File(location))
        ) {
            screen.startScreen();

            var gui = new MultiWindowTextGUI(screen);
            var window = new HomeView(db);
            gui.addWindow(window);
            window.waitUntilClosed();

            screen.stopScreen();
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}
