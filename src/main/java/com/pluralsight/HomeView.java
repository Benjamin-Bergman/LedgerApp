// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Interactable.*;
import com.googlecode.lanterna.gui2.LinearLayout.*;
import com.googlecode.lanterna.input.*;

import java.util.*;

/**
 * Represents the home view for an accounting ledger.
 */
final class HomeView extends BasicWindow {
    private final TransactionDatabase database;
    private final LabeledButton[] buttons;

    HomeView(TransactionDatabase database) {
        super("Accounting Ledger");

        this.database = database;

        buttons = new LabeledButton[]{
            new LabeledButton("Enter Credits", 'C', this::showEnterCredits),
            new LabeledButton("Enter Debits", 'D', this::showEnterDebits),
            new LabeledButton("Show Ledger", 'L', this::showLedger),
            new LabeledButton("Exit", 'x', this::close),
        };

        init();
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        for (LabeledButton button : buttons)
            if (button.handleKeyStroke(key) == Result.HANDLED)
                return false;

        return super.handleInput(key);
    }

    private void init() {
        setCloseWindowWithEscape(true);
        setHints(List.of(Hint.CENTERED));

        var comp = new Panel();
        var justify = LinearLayout.createLayoutData(Alignment.Fill, GrowPolicy.CanGrow);

        for (var button : buttons)
            comp.addComponent(button, justify);

        setComponent(comp);
    }

    private void showEnterCredits() {
    }

    private void showEnterDebits() {
    }

    private void showLedger() {
    }
}
