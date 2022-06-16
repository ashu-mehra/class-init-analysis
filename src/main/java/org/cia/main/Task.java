package org.cia.main;

import org.cia.analyzer.CiaAnalysisResult;
import org.cia.analyzer.CiaAnalyzer;
import org.cia.analyzer.CiaReportGenerator;
import org.cia.metadata.ClassRepository;
import org.cia.sources.CiaBadClassSource;
import org.cia.sources.ClassSource;
import org.cia.sources.ClassSourceFactory;
import org.cia.sources.ClassSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.Callable;

public class Task implements Callable<Boolean> {
    private final ClassSourceType type;
    private final String artifact;
    private final Path resultsDir;
    private static final Logger logger = LoggerFactory.getLogger(Task.class);

    public Task(ClassSourceType type, String artifact, Path resultsDir) {
        this.type = type;
        this.artifact = artifact;
        this.resultsDir = resultsDir;
    }

    private CiaAnalysisResult analyzeSingleMavenArtifact() throws CiaBadClassSource {
        ClassSource sources = ClassSourceFactory.getSource(ClassSourceType.MAVEN_ARTIFACT, artifact);
        ClassRepository repo = new ClassRepository(sources);
        CiaAnalyzer analyzer = new CiaAnalyzer(repo);
        analyzer.runAnalysis();
        return analyzer.getResult();
    }

    private CiaAnalysisResult analyzeJarFile() throws CiaBadClassSource {
        ClassSource sources = ClassSourceFactory.getSource(ClassSourceType.JAR, artifact);
        ClassRepository repo = new ClassRepository(sources);
        CiaAnalyzer analyzer = new CiaAnalyzer(repo);
        analyzer.runAnalysis();
        return analyzer.getResult();
    }

    @Override
    public Boolean call() {
        logger.info("Processing artifact: " + artifact);
        try {
            CiaAnalysisResult result = null;
            if (type == ClassSourceType.JAR) {
                result = analyzeJarFile();
            } else if (type == ClassSourceType.MAVEN_ARTIFACT) {
                result = analyzeSingleMavenArtifact();
            }
            if (result.cycleCount() > 0) {
                CiaReportGenerator.generateReport(result, resultsDir);
                TaskStatistics.addWithCycles();
            }
            TaskStatistics.addPassed();
        } catch (CiaBadClassSource e) {
            logger.warn("Error in processing artifact " + artifact, e);
            TaskStatistics.addFailed();
        }
        return true;
    }
}
