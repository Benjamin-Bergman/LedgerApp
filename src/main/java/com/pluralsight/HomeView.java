// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.LinearLayout.*;

import java.util.*;

/**
 * Represents the home view for an accounting ledger.
 */
final class HomeView extends BasicWindow {
    private final TransactionDatabase database;

    HomeView(TransactionDatabase database) {
        super("Accounting Ledger");

        this.database = database;

        init();
    }

    private void init() {
        setCloseWindowWithEscape(true);
        setHints(List.of(Hint.CENTERED));

        var comp = new Panel();
        var justify = LinearLayout.createLayoutData(Alignment.Fill, GrowPolicy.CanGrow);

        var buttons = new Button[]{
            new LabeledButton("Enter Credits", 'C'),
            new LabeledButton("Enter Debits", 'D'),
            new LabeledButton("Show Ledger", 'L'),
            new LabeledButton("Exit", 'x', this::close),
        };

        for (var button : buttons)
            comp.addComponent(button, justify);

        setComponent(comp);
    }
}
