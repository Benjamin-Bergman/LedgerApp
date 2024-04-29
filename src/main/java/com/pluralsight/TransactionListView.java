// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.gui2.*;

import java.util.*;

final class TransactionListView extends BasicWindow {
    private final TransactionDatabase database;

    TransactionListView(TransactionDatabase database) {
        super("Transactions");

        this.database = database;

        var list = new TransactionList();
        for (var t : database)
            list.addItem(t);

        setComponent(list);

        setHints(List.of(Hint.CENTERED));
    }

    private static final class TransactionList extends AbstractListBox<Transaction, TransactionList> {
        @Override
        protected ListItemRenderer<Transaction, TransactionList> createDefaultListItemRenderer() {
            return new TransactionRenderer();
        }

        @SuppressWarnings("InnerClassTooDeeplyNested")
        private static final class TransactionRenderer extends ListItemRenderer<Transaction, TransactionList> {
            @Override
            public String getLabel(TransactionList listBox, int index, Transaction item) {
                return super.getLabel(listBox, index, item);
            }
        }
    }
}
