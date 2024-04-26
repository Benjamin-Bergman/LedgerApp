// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import java.util.*;

/**
 * Represents the home view for an accounting ledger.
 */
final class HomeView extends LabeledMenu {
    private final TransactionDatabase database;

    HomeView(TransactionDatabase database) {
        super("Accounting Ledger");

        this.database = database;

        setHints(List.of(Hint.CENTERED));

        render(
            new LabeledButton("Enter Credits", 'C', this::showEnterCredits),
            new LabeledButton("Enter Debits", 'D', this::showEnterDebits),
            new LabeledButton("Show Ledger", 'L', this::showLedger),
            new LabeledButton("Exit", 'x', this::close)
        );
    }

    private void showEnterCredits() {
    }

    private void showEnterDebits() {
    }

    private void showLedger() {
    }
}
