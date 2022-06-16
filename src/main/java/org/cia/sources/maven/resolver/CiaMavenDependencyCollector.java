package org.cia.sources.maven.resolver;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

import java.util.LinkedHashSet;
import java.util.Set;

public class CiaMavenDependencyCollector implements DependencyVisitor {
    private final Set<Artifact> dependencies;

    public CiaMavenDependencyCollector() {
        dependencies = new LinkedHashSet<>();
    }

    @Override
    public boolean visitEnter(DependencyNode dependencyNode) {
        dependencies.add(dependencyNode.getArtifact());
        return true;
    }

    @Override
    public boolean visitLeave(DependencyNode dependencyNode) {
        return true;
    }

    public Set<Artifact> getDependencies() {
        return Set.copyOf(dependencies);
    }
}
