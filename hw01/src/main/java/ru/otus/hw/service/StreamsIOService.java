package ru.otus.hw.service;

import java.io.PrintStream;
import java.util.Scanner;

public class StreamsIOService implements IOService {
    private final PrintStream printStream;

    private final Scanner scanner;

    public StreamsIOService(PrintStream printStream) {
        this.printStream = printStream;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void printLine(String s) {
        printStream.println(s);
    }

    @Override
    public void printFormattedLine(String s, Object... args) {
        printStream.printf(s + "%n", args);
    }

    @Override
    public int readIntForRange(int min, int max, String prompt) {
        while (true) {
            try {
                printStream.print(prompt);
                int value = Integer.parseInt(scanner.nextLine());
                if (value < min || value > max) {
                    printStream.printf("Please enter a number between %d and %d%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                printStream.println("Invalid input. Please enter a number.");
            }
        }
    }
}
