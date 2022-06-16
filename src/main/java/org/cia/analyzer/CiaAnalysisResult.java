package org.cia.analyzer;

import org.cia.sources.ClassSource;

import java.util.List;

public class CiaAnalysisResult {
    private final ClassSource codeSource;
    private final List<CGCycle> cycles;

    public CiaAnalysisResult(ClassSource codeSource, List<CGCycle> cycles) {
        this.codeSource = codeSource;
        this.cycles = cycles;
    }

    public int cycleCount() {
        return cycles.size();
    }

    public ClassSource getCodeSource() {
        return codeSource;
    }

    public List<CGCycle> getCycles() {
        return cycles;
    }
}
