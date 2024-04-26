// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import org.junit.jupiter.api.*;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class TransactionTest {
    @Test
    void serializationCoherence() {
        var dateTime = LocalDateTime.of(2_024, 3, 7, 12, 18);
        var transaction = new Transaction(dateTime, "Description", "Vendor", 100.0);
        var deserialized = Transaction.deserialize(transaction.serialize());
        assertTrue(deserialized.isPresent(), "Serialized value does not deserialize");
        assertEquals(transaction, deserialized.get(), "Serialized value does not deserialize correctly");

        var serial = "2024-04-26T12:30,Desc,Vend,100.00";
        var created = Transaction.deserialize(serial);
        assumeTrue(created.isPresent(), "String deserializes");
        assertEquals(serial, created.get().serialize(), "Deserialized value does not serialize correctly");
    }

    @Test
    void deserialize() {
        assertEquals(Optional.empty(), Transaction.deserialize(null), "null string");
        assertEquals(Optional.empty(), Transaction.deserialize(""), "Empty string");
        assertEquals(Optional.empty(), Transaction.deserialize("2024-04-26T12:30"), "One column");
        assertEquals(Optional.empty(), Transaction.deserialize("2024-04-26T12:30,Desc"), "Two columns");
        assertEquals(Optional.empty(), Transaction.deserialize("2024-04-26T12:30,Desc,Vend"), "Three columns");
        assertEquals(Optional.empty(), Transaction.deserialize("2024-04-26T12:30,Desc,Vend,100.00,Data"), "Five columns");
        assertEquals(Optional.empty(), Transaction.deserialize("Date,Desc,Vend,100.00"), "Bad date");
        assertEquals(Optional.empty(), Transaction.deserialize("2024-04-26T12:30,Desc,Vend,Amount"), "Bad amount");

        var deserialized = Transaction.deserialize("2024-04-26T12:30,Desc,Vend,100.00");
        assertTrue(deserialized.isPresent(), "Value deserializes");

        var dt = LocalDateTime.of(2_024, 4, 26, 12, 30);
        assertEquals(new Transaction(dt, "Desc", "Vend", 100.00), deserialized.get(), "Value deserializes correctly");
    }

    @Test
    void testToString() {
        var dateTime = LocalDateTime.of(2_024, 3, 7, 12, 18);
        var transaction1 = new Transaction(dateTime, "Description", "Vendor", 100.0);
        assertEquals("[2024-03-07 12:18:00] $100.00 from Vendor for Description", transaction1.toString(), "Credit");
        Transaction transaction2 = new Transaction(dateTime, "Description", "Vendor", -100.0);
        assertEquals("[2024-03-07 12:18:00] $100.00 to Vendor for Description", transaction2.toString(), "Debit");
    }

    @Test
    void date() {
        var dateTime = LocalDateTime.of(2_024, 3, 7, 12, 18);
        var transaction = new Transaction(dateTime, "Description", "Vendor", 100.0);
        assertEquals(dateTime.toLocalDate(), transaction.date(), "Date is wrong");
    }

    @Test
    void time() {
        var dateTime = LocalDateTime.of(2_024, 3, 7, 12, 18);
        var transaction = new Transaction(dateTime, "Description", "Vendor", 100.0);
        assertEquals(dateTime.toLocalTime(), transaction.time(), "Time is wrong");
    }

    @Test
    void serialize() {
        var dateTime = LocalDateTime.of(2_024, 3, 7, 12, 18);
        var transaction = new Transaction(dateTime, "Description", "Vendor", 100.0);
        assertEquals("2024-03-07T12:18,Description,Vendor,100.00", transaction.serialize(), "Serialized string is wrong");
    }
}