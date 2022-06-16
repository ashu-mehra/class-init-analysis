package org.cia.analyzer.graph;

import org.cia.metadata.MethodInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallGraph {
    Map<MethodInfo, Node> nodes;
    List<Node> entryPoints;

    public CallGraph() {
        nodes = new HashMap<>();
        entryPoints = new ArrayList<>();
    }

    public Node addNode(MethodInfo minfo) {
        return nodes.computeIfAbsent(minfo, value -> new Node(this, minfo));
    }

    public void addEntryPoint(Node node) {
        node.markEntryPoint();
        entryPoints.add(node);
    }

    public List<Node> getEntryPoints() {
        return Collections.unmodifiableList(entryPoints);
    }

    public int entryPointsCount() {
        return entryPoints.size();
    }

    public void displayEntryPoints() {
        for (Node node: entryPoints) {
            System.out.println(node);
        }
    }

    public DFT initDFT(NodeVisitor visitor) {
        return new DFT(this, visitor);
    }

/*    Set<Node> getCycle(Set<Node> list, Node start) {
        Set<Node> cycle = new LinkedHashSet<>();
        boolean add = false;
        for (Node node: list) {
            if (add) {
                cycle.add(node);
            }
            if (node.equals(start)) {
                cycle.add(node);
                add = true;
            }
        }
        return cycle;
    }*/

/*    List<Set<Node>> detectClinitCycles() {
        List<Set<Node>> cycles = new ArrayList<>();
        LinkedHashSet<Node> traversed = new LinkedHashSet<>();
        HashMap<Node, Integer>
        Node current;
        while () {
            if (traversed.contains(current)) {
                cycles.add(getCycle(traversed, current));
            }
            current = current.nextEdge();
            if (edges.containsKey(current)) {
            }
        }
    }

    private class Edge {
        Node node;
        int index;
        Edge(Node node, int index) {
            this.node = node;
            this.index = index;
        }
    }*/
}
