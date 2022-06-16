package org.cia.main;

import org.checkerframework.checker.units.qual.A;

import java.util.concurrent.atomic.AtomicInteger;

public class TaskStatistics {
    static AtomicInteger failed = new AtomicInteger();
    static AtomicInteger passed = new AtomicInteger();
    static AtomicInteger withCycles = new AtomicInteger();

    public static int getFailed() {
        return failed.get();
    }

    public static int getPassed() {
        return passed.get();
    }

    public static int getWithCycles() {
        return withCycles.get();
    }
    public static void addFailed() {
        failed.incrementAndGet();
    }

    public static void addPassed() {
        passed.incrementAndGet();
    }

    public static void addWithCycles() {
        withCycles.incrementAndGet();
    }

    public static void showStats() {
        System.out.println("Successfully processed: " + TaskStatistics.getPassed());
        System.out.println("Artifacts with cycle(s): " + TaskStatistics.getWithCycles());
        System.out.println("Failed to process: " + TaskStatistics.getFailed());
    }
}
