package org.cia.analyzer.graph;

import java.util.LinkedHashSet;
import java.util.Set;

public class DFT {
    private final CallGraph graph;
    private final NodeVisitor visitor;
    private final Set<Node> visited;

    public DFT(CallGraph graph, NodeVisitor visitor) {
        this.graph = graph;
        this.visitor = visitor;
        this.visited = new LinkedHashSet<>();
    }

    private void process(Node node) {
        if (!visitor.onEntry(node)) {
            return;
        }
        if (visited.contains(node)) {
            visitor.onExit(node);
            return;
        }
        visited.add(node);
        for (Node to: node.getEdges()) {
            process(to);
        }
        visitor.onExit(node);
    }

    public void traverse(Node entryPoint) {
        process(entryPoint);
    }
}
