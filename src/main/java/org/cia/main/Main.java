package org.cia.main;

import org.cia.sources.ClassSourceType;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    private static Options options = new Options();

    private static void parseArgs(String[] args) {
        new CommandLine(options).parseArgs(args);
    }

    public static void main(String args[]) {
        parseArgs(args);

        if (options.artifactsFile != null) {
            List<String> artifacts = readArtifactsList(options.artifactsFile);
            analyzeMavenArtifactList(artifacts);
        }
        if (options.artifact != null) {
            analyzeArtifact(ClassSourceType.MAVEN_ARTIFACT, options.artifact);
        }
        if (options.jarFile != null) {
            analyzeArtifact(ClassSourceType.JAR, options.jarFile);
        }
        TaskStatistics.showStats();
    }

    private static void analyzeArtifact(ClassSourceType type, String artifact) {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        Task task = new Task(type, artifact, options.resultsDir);
        Future<Boolean> future = pool.submit(task);
        try {
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        pool.shutdown();
        while (!pool.isTerminated()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private static List<String> readArtifactsList(String artifactsFile) {
        List<String> artifacts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(artifactsFile))) {
            String artifact;
            while ((artifact = br.readLine()) != null) {
                artifacts.add(artifact);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return artifacts;
    }

    private static void analyzeMavenArtifactList(List<String> artifacts) {
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-1);
        List<Future<Boolean>> futures = new ArrayList<>();
        for (String artifact: artifacts) {
            Task task = new Task(ClassSourceType.MAVEN_ARTIFACT, artifact, options.resultsDir);
            Future<Boolean> f = pool.submit(task);
            futures.add(f);
        }
        try {
            for (Future<Boolean> future : futures) {
                future.get();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        pool.shutdown();
        while (!pool.isTerminated()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    static class Options {
        @Option(names = "--artifacts-file", description = "file containing list of maven artifacts")
        String artifactsFile;

        @Option(names = "--artifact", description = "maven artifact")
        String artifact;

        @Option(names = "--jarfiles", description = "list of jar files separated by colon")
        String jarFile;

        @Option(names = "--result-dir", defaultValue = "results", description = "Location for storing the results")
        Path resultsDir;
    }
}
