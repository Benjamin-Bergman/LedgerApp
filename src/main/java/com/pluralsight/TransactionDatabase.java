// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import java.io.*;
import java.util.*;
import java.util.stream.*;

/**
 * Represents a database of {@link Transaction}s backed by a file.
 */
final class TransactionDatabase implements Closeable {
    private final File filePath;
    private List<Transaction> transactions;

    /**
     * Creates a new database backed by the specified file.
     *
     * @param filePath The file to use as the database.
     * @throws IOException           When reading from the file fails.
     * @throws FileNotFoundException When {@code filePath} does not point to a valid file.
     */
    TransactionDatabase(File filePath) throws IOException {
        //noinspection IfCanBeAssertion
        if (!filePath.exists() || filePath.isDirectory())
            throw new FileNotFoundException(filePath.getAbsolutePath());
        this.filePath = filePath;
        readFromDisk();
    }

    /**
     * Calls {@code writeToDisk()}. Useful in a {@code try-with-resources} block
     * to ensure data integrity before this object goes out of scope.
     *
     * @throws IOException When writing to the file fails.
     */
    @Override
    public void close() throws IOException {
        writeToDisk();
    }

    /**
     * Updates the in-memory representation of this database to match the file representation.
     *
     * @throws IOException When reading from the file fails.
     */
    void readFromDisk() throws IOException {
        assertGoodFile();
        try (var fr = new FileReader(filePath);
             var br = new BufferedReader(fr)) {
            transactions = br.lines()
                .map(Transaction::deserialize)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        }
    }

    /**
     * Updates the file representation of this database to match the in-memory representation.
     *
     * @throws IOException When writing to the file fails.
     */
    void writeToDisk() throws IOException {
        assertGoodFile();
        try (var fw = new FileWriter(filePath);
             var bw = new BufferedWriter(fw)) {
            for (Transaction transaction : transactions)
                bw.write(transaction.serialize());
        }
    }

    private void assertGoodFile() {
        assert filePath.exists() && !filePath.isDirectory() : filePath.getAbsolutePath();
    }
}
