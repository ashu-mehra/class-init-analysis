package org.cia.sources.maven;

public class CiaMavenArtifact {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String jarFile;

    public CiaMavenArtifact(String groupId, String artifactId, String version, String jarFile) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.jarFile = jarFile;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getJarFile() {
        return jarFile;
    }

    public String getCoordinates() {
        return getGroupId() + ":" + getArtifactId() + ":" + getVersion();
    }
}
