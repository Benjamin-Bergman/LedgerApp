# Personal Ledger App

Runs a personal ledger. You can enter transactions and view reports on them later.

## Installation

Build the app with your favorite Java IDE or by using Maven:
```bash
mvn clean package
```

## Running

Simly run the jar from your shell, e.g.
```bash
java -jar target/LedgerApp-1.0-SNAPSHOT.jar
```

The app will not run on `cmd` on Windows. This is [a know bug in Lanterna](https://github.com/mabe02/lanterna/issues/593).
Here are a few workarounds:

- Run the program directly from your IDE
- Use javaw.exe instead of java.exe
- Double-click the .jar file from file explorer
- Run via Git Bash
- Run via WSL

The first four options will cause Lanterna to create a terminal emulator window.

## Usage

The app takes a command line argument for the file to use. By default, it will use `Transactions.csv`.
The app runs in a TUI (Terminal User Interface) that should be intuitive to use.

## Technology Used

- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Lanterna](https://github.com/mabe02/lanterna), a TUI library for Java
- [IntelliJ IDEA](https://www.jetbrains.com/idea/)

# Demo

[![Demo](https://asciinema.org/a/rGIYM7Xyx08iWrlqX441u4thv.svg)](https://asciinema.org/a/rGIYM7Xyx08iWrlqX441u4thv?startAt=10&autoplay=1)

## Possible Future Enhancements

- Sorting mode for the transactions list
- An undo feature
- Persistent filter settings
- Running totals for reports
- Scheduled/recurring transactions
- Lazier file writing
