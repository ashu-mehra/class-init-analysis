package org.cia.analyzer.graph;

public interface NodeVisitor {
    boolean onEntry(Node node);
    void onExit(Node node);
}
