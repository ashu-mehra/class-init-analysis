package org.cia.sources;

import org.cia.sources.jar.JarFileSource;
import org.cia.sources.jar.MultiJarSource;
import org.cia.sources.maven.MavenArtifactSource;

public class ClassSourceFactory {
    public static final String JAR_FILE_SEPARATOR = ":";
    public static ClassSource getSource(ClassSourceType type, String sources) throws CiaBadClassSource {
        if (type == ClassSourceType.JAR) {
            if (sources.indexOf(JAR_FILE_SEPARATOR) != -1) {
                String entries[] = sources.split(JAR_FILE_SEPARATOR);
                for (String entry : entries) {
                    if (!entry.endsWith(".jar")) {
                        throw new UnsupportedOperationException(entry + "is not a jar file");
                    }
                }
                return MultiJarSource.create(entries);
            } else {
                if (sources.endsWith(".jar")) {
                    return JarFileSource.create(sources);
                } else {
                    throw new UnsupportedOperationException(sources + "is not a jar file");
                }
            }
        } else if (type == ClassSourceType.MAVEN_ARTIFACT) {
            return MavenArtifactSource.create(sources);
        }
        return null;
    }
}
