package org.cia.analyzer;

import org.cia.analyzer.CiaAnalysisResult;
import org.cia.sources.ClassSource;
import org.cia.sources.ClassSourceType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class CiaReportGenerator {
    private final CiaAnalysisResult result;
    private final PrintStream ps;

    public CiaReportGenerator(CiaAnalysisResult result, PrintStream ps) {
        this.result = result;
        this.ps = ps;
    }

    public static void generateReport(CiaAnalysisResult result, Path reportsDir) {
        ClassSource source = result.getCodeSource();
        String reportFile;
        if (source.getType() == ClassSourceType.JAR) {
            reportFile = "report.txt";
        } else {
            reportFile = "report-" + source.getMainEntry() + ".txt";
            // convert all ":" to "_"
            reportFile = reportFile.replace(':', '_');
        }
        PrintStream ps;
        try {
            Files.createDirectories(reportsDir);
            Path report = reportsDir.resolve(reportFile);
            Files.createFile(report);
            FileOutputStream fos = new FileOutputStream(report.toFile());
            ps = new PrintStream(fos);
        } catch (IOException e) {
            ps = System.out;
        }
        CiaReportGenerator reporter = new CiaReportGenerator(result, ps);
        reporter.generateHeader();
        reporter.generateReport();
        reporter.generateFooter();
        ps.close();
    }

    private void generateFooter() {
        ps.println("--------");
    }

    private void generateReport() {
        ps.println("Number of cycles: " + result.cycleCount());
        int index = 0;
        for (CGCycle cycle: result.getCycles()) {
            ps.println("----");
            ps.println("Cycle " + index + ":");
            cycle.display(ps);
            index += 1;
        }
        ps.println("----");
    }

    private void generateHeader() {
        ps.println("Source type: " + result.getCodeSource().getTypeAsString());
        ps.println("Source code entries: " + result.getCodeSource().getAllEntries());
        ps.println("----");
    }
}
