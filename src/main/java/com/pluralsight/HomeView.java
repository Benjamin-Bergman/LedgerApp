// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.pluralsight.components.*;

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
        getTextGUI().addWindowAndWait(new EnterTransactionView(true, database));
    }

    private void showEnterDebits() {
        getTextGUI().addWindowAndWait(new EnterTransactionView(false, database));
    }

    private void showLedger() {
        getTextGUI().addWindowAndWait(new TransactionListView(database));
    }
}
