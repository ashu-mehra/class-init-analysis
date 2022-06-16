package org.cia.analyzer.graph;

import org.cia.metadata.MethodInfo;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Node {
    private final CallGraph graph;
    private final MethodInfo minfo;
    private final boolean isClinit;
    private final Set<Node> edges;
    private boolean isEntryPoint;

    public static final Node ROOT = new Node(null, null);

    public Node(CallGraph graph, MethodInfo minfo) {
        this.graph = graph;
        this.minfo = minfo;
        if (minfo != null && minfo.getName().equals("<clinit>")) {
            isClinit = true;
        } else {
            isClinit = false;
        }
        edges = new LinkedHashSet<>();
        isEntryPoint = false;
    }

    public void addEdge(MethodInfo minfo) {
        Node to = graph.addNode(minfo);
        edges.add(to);
    }

    public MethodInfo getMethodInfo() {
        return minfo;
    }

    public boolean isClinit() {
        return isClinit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(minfo, node.minfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minfo);
    }

    public Set<Node> getEdges() {
        return Collections.unmodifiableSet(edges);
    }

    public String toString() {
        return minfo.toString();
    }

    public boolean isEntryPoint() {
        return this.isEntryPoint;
    }

    public void markEntryPoint() {
        isEntryPoint = true;
    }
}