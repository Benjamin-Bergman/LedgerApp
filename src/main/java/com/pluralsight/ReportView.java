// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.*;
import com.googlecode.lanterna.input.*;
import com.pluralsight.components.*;

import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

final class ReportView extends DialogWindow {
    ReportView(ReportType reportType, Iterable<Transaction> db) {
        super(reportType.getReportName());

        var result = StreamSupport
            .stream(db.spliterator(), true)
            .filter(reportType)
            .reduce(
                new Tuple(0, 0),
                (tup, tra) -> new Tuple(tup.count() + 1, tup.total() + tra.amount()),
                (t1, t2) -> new Tuple(t1.count() + t2.count(), t1.total() + t2.total()));

        var display = new Panel();
        display.addComponent(new Label("%d transactions totalling $%.2f".formatted(result.count(), result.total())));
        display.addComponent(new LabeledButton("Exit", 'x', this::close));

        setComponent(display);

        setHints(List.of(Hint.CENTERED, Hint.MODAL));
        setCloseWindowWithEscape(true);
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if ((key.getKeyType() == KeyType.Character)
            && ((key.getCharacter() == 'x') || (key.getCharacter() == 'X'))) {
            close();
            return false;
        }

        return super.handleInput(key);
    }

    /**
     * The type of some report
     */
    enum ReportType implements Predicate<Transaction> {
        MONTH_TO_DATE("Month To Date", t -> t.date().isAfter(LocalDate.now().withDayOfMonth(1).minusDays(1))),
        PRIOR_MONTH("Prior Month", t ->
            t.date().isAfter(LocalDate.now().minusMonths(1).withDayOfMonth(1).minusDays(1))
            && t.date().isBefore(LocalDate.now().withDayOfMonth(1))),
        YEAR_TO_DATE("Year To Date", t -> t.date().getYear() == LocalDate.now().getYear()),
        PRIOR_YEAR("Prior Year", t -> t.date().getYear() == (LocalDate.now().getYear() - 1));

        private final String reportName;
        private final Predicate<? super Transaction> filter;

        ReportType(String reportName, Predicate<? super Transaction> filter) {
            this.reportName = reportName;
            this.filter = filter;
        }

        @Override
        public boolean test(Transaction transaction) {
            return filter.test(transaction);
        }

        public String getReportName() {
            return reportName;
        }
    }

    private record Tuple(int count, double total) {
    }
}
