// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import java.time.*;
import java.time.format.*;
import java.util.*;

/**
 * Represents a financial transaction.
 *
 * @param dateTime    When this transaction occurred.
 * @param description What this transaction was for.
 * @param vendor      Who this transaction was with.
 * @param amount      How much money this transaction exchanged.
 */
record Transaction(LocalDateTime dateTime, String description, String vendor, double amount) {
    /**
     * Deserializes a transaction from a row of CSV.
     *
     * @param csv The CSV row to deserialize.
     * @return A CSV representation of this transaction, or {@code Optional.none()} if {@code csv} is invalid.
     */
    static Optional<Transaction> deserialize(String csv) {
        if ((csv == null) || csv.isEmpty())
            return Optional.empty();

        String[] tokens = csv.split(",");
        if (tokens.length != 4)
            return Optional.empty();

        LocalDateTime dt;
        try {
            dt = LocalDateTime.parse(tokens[0]);
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }

        double amt;
        try {
            amt = Double.parseDouble(tokens[3]);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        return Optional.of(new Transaction(dt, tokens[1], tokens[2], amt));
    }

    @Override
    public String toString() {
        return "[%tY-%<tm-%<td %<tH:%<tM:%<TS] $%.2f %s %s for %s".formatted(
            dateTime,
            Math.abs(amount),
            (amount > 0) ? "from" : "to",
            vendor,
            description);
    }

    /**
     * @return The date when this transaction occurred.
     */
    LocalDate date() {
        return dateTime.toLocalDate();
    }

    /**
     * @return The time when this transaction occurred.
     */
    LocalTime time() {
        return dateTime.toLocalTime();
    }

    /**
     * Serializes this transaction into a row of CSV.
     *
     * @return A CSV representation of this transaction.
     */
    String serialize() {
        return "%s,%s,%s,%.2f".formatted(dateTime, description, vendor, amount);
    }
}
