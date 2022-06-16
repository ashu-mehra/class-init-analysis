package org.cia.analyzer;

import org.cia.analyzer.graph.Node;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CGCycle {
    protected List<Node> cycle;

    public CGCycle() {
        cycle = new ArrayList<>();
    }

    public void addNode(Node node) {
        cycle.add(node);
    }

    public void display(PrintStream ps) {
        ps.println("method call cycle:");
        for (Node node: cycle) {
            ps.print("  ");
            if (node.isEntryPoint()) {
                ps.print("[e] ");
            }
            ps.println(node);
        }
    }

    public List<Node> getNodes() {
        return List.copyOf(cycle);
    }
}
