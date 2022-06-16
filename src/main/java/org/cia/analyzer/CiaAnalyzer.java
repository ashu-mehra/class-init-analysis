package org.cia.analyzer;

import org.cia.analyzer.graph.CGBuilder;
import org.cia.analyzer.graph.CallGraph;
import org.cia.metadata.ClassRepository;
import org.cia.sources.ClassSource;

import java.util.List;

public class CiaAnalyzer {
    private final ClassRepository repository;
    private List<CGCycle> cycles;

    public CiaAnalyzer(ClassRepository repository) {
        this.repository = repository;
    }

    private ClassSource getCodeSource() {
        return repository.getSource();
    }

    public void runAnalysis() {
        CGBuilder cgBuilder = new CGBuilder(repository);
        CallGraph graph = cgBuilder.build();
        cycles = CycleDetector.getCycles(graph);
    }

    public CiaAnalysisResult getResult() {
        return new CiaAnalysisResult(getCodeSource(), cycles);
    }
}
