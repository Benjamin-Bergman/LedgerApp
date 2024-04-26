// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.terminal.*;

import java.io.*;

final class Program {
    public static void main(String[] args) {
        try (var screen = new DefaultTerminalFactory().createScreen()) {
            screen.startScreen();

            var gui = new MultiWindowTextGUI(screen);
            var window = new BasicWindow();
            gui.addWindow(window);
            window.waitUntilClosed();

            screen.stopScreen();
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}
