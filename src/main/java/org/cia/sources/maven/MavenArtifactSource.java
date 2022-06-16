package org.cia.sources.maven;

import org.cia.sources.CiaBadClassSource;
import org.cia.sources.ClassData;
import org.cia.sources.ClassSource;
import org.cia.sources.ClassSourceType;
import org.cia.sources.jar.MultiJarSource;
import org.cia.sources.maven.resolver.CiaMavenResolverSystem;

import java.util.Iterator;
import java.util.List;

public class MavenArtifactSource implements ClassSource {
    private final String artifact;
    private final List<CiaMavenArtifact> allArtifacts;
    private MultiJarSource multiJarSource;

    @Override
    public ClassSourceType getType() {
        return ClassSourceType.MAVEN_ARTIFACT;
    }

    @Override
    public String getTypeAsString() {
        return ClassSourceType.MAVEN_ARTIFACT.name();
    }

    @Override
    public String getAllEntries() {
        StringBuilder sb = new StringBuilder();
        boolean appendSeparator = false;
        for (CiaMavenArtifact artifact: allArtifacts) {
            if (appendSeparator) {
                sb.append(",");
            }
            sb.append(artifact.getCoordinates());
            if (!appendSeparator) {
                appendSeparator = true;
            }
        }
        return sb.toString();
    }

    @Override
    public String getMainEntry() {
        return artifact;
    }

    public static MavenArtifactSource create(String artifactCoordinates) throws CiaBadClassSource {
        CiaMavenResolverSystem.init();
        List<CiaMavenArtifact> allArtifcats = CiaMavenResolverSystem.resolveTransitiveDependencies(artifactCoordinates);
        String[] allJarFiles = allArtifcats.stream().map(artifact -> artifact.getJarFile()).toArray(String[]::new);
        MultiJarSource multiJarSource = MultiJarSource.create(allJarFiles);
        return new MavenArtifactSource(artifactCoordinates, allArtifcats, multiJarSource);
    }

    @Override
    public Iterator<ClassData> iterator() {
        return multiJarSource.iterator();
    }

    private MavenArtifactSource(String artifact, List<CiaMavenArtifact> allArtifacts, MultiJarSource multiJarSource) {
        this.artifact = artifact;
        this.allArtifacts = allArtifacts;
        this.multiJarSource = multiJarSource;
    }
}
