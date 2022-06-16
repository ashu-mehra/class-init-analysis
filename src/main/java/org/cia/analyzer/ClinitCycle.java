package org.cia.analyzer;

import org.cia.analyzer.graph.Node;

import java.io.PrintStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ClinitCycle extends CGCycle {
    private Set<Node> clinitCycle;

    public ClinitCycle() {
        super();
        this.clinitCycle = new LinkedHashSet<>();
    }

    public int clinitCount() {
        return clinitCycle.size();
    }

    public void addNode(Node node) {
        super.addNode(node);
        if (node.isClinit()) {
            clinitCycle.add(node);
        }
    }

    public void display(PrintStream ps) {
        super.display(ps);
        ps.println("clinit cycle:");
        for (Node node: clinitCycle) {
            ps.print("    ");
            if (node.isEntryPoint()) {
                ps.print("[e] ");
            }
            ps.println(node);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClinitCycle that = (ClinitCycle) o;
        return clinitCycle.equals(that.clinitCycle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clinitCycle);
    }

    public List<Node> getClinitNodes() {
        return List.copyOf(clinitCycle);
    }
}
