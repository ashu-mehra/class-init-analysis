package org.cia.analyzer;

import org.cia.analyzer.graph.CallGraph;
import org.cia.analyzer.graph.DFT;
import org.cia.analyzer.graph.Node;
import org.cia.analyzer.graph.NodeVisitor;
import org.cia.metadata.ClassInfo;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.cia.util.KnownMethods.CLINIT;

public class CycleDetector {
    private final CallGraph graph;
    private final Set<ClinitCycle> cycles;

    private CycleDetector(CallGraph graph) {
        this.graph = graph;
        cycles = new LinkedHashSet<>();
    }

    public static List<CGCycle> getCycles(CallGraph graph) {
        CycleDetector detector = new CycleDetector(graph);
        detector.run();
        return detector.getCycles();
    }

    private List<CGCycle> getCycles() {
        return List.copyOf(cycles);
    }

    public void run() {
        NodeVisitor visitor = new CycleDetectionVisitor();
        DFT dft = graph.initDFT(visitor);
        for (Node entryPoint: graph.getEntryPoints()) {
            dft.traverse(entryPoint);
        }
    }

    public boolean isValidCycle(ClinitCycle cycle) {
        if (cycle.clinitCount() < 2) {
            return false;
        }
        int numEntryPoints = 0;
        for (Node node: cycle.getNodes()) {
            if (node.isEntryPoint()) {
                numEntryPoints += 1;
                if (numEntryPoints > 1) {
                    break;
                }
            }
        }
        if (numEntryPoints <= 1) {
            return false;
        }
        return true;
    }

    class CycleDetectionVisitor implements NodeVisitor {
        private Set<Node> stack;

        public CycleDetectionVisitor() {
            this.stack = new LinkedHashSet<>();
        }

        private ClinitCycle getCycle(Node start) {
            ClinitCycle cycle = new ClinitCycle();
            boolean add = false;
            for (Node node: stack) {
                if (add) {
                    cycle.addNode(node);
                }
                if (node.equals(start)) {
                    cycle.addNode(node);
                    add = true;
                }
            }
            return cycle;
        }

        @Override
        public boolean onEntry(Node node) {
            if (node.getMethodInfo().getName().equals(CLINIT.methodName())) {
                ClassInfo nodeClass = node.getMethodInfo().getOwner();
                for (Node stackNode: stack) {
                    ClassInfo stackClass = stackNode.getMethodInfo().getOwner();
                    if (stackClass.isChildOf(nodeClass)) {
                        return false;
                    }
                    if (stackClass.isImplementationOf(nodeClass)) {
                        return false;
                    }
                }
            }
            if (stack.contains(node)) {
                ClinitCycle cycle = getCycle(node);
                if (isValidCycle(cycle)) {
                    cycles.add(cycle);
                }
                return false;
            } else {
                stack.add(node);
            }
            return true;
        }

        @Override
        public void onExit(Node node) {
            stack.remove(node);
        }
    }
}
