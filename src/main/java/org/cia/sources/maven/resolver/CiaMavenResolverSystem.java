package org.cia.sources.maven.resolver;

import com.google.inject.Guice;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.cia.sources.CiaBadClassSource;
import org.cia.sources.maven.CiaMavenArtifact;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CiaMavenResolverSystem {
    private static RepositorySystem system;
    private static RepositorySystemSession session;
    private static RemoteRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(CiaMavenResolverSystem.class);

    public static DefaultRepositorySystemSession createSession(RepositorySystem system)
    {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository("/home/asmehra/.m2/repository");
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
        session.setRepositoryListener(new NoopRepositoryListener());
/*
        session.setTransferListener( new ConsoleTransferListener() );
        session.setRepositoryListener( new ConsoleRepositoryListener() );
*/

        // uncomment to generate dirty trees
        // session.setDependencyGraphTransformer( null );

        return session;
    }

    public static void init() {
        system = Guice.createInjector(new CiaMavenResolverGuiceModule()).getInstance(RepositorySystem.class);
        session = createSession(system);
        repository = new RemoteRepository.Builder( "central", "default", "https://repo.maven.apache.org/maven2/" ).build();
    }

    public static Set<Artifact> getDependencies(String coordinates) {
        Artifact artifact = new DefaultArtifact(coordinates);
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot( new Dependency( artifact, "" ) );
        collectRequest.setRepositories(Collections.singletonList(repository));
        try {
            CollectResult collectResult = system.collectDependencies(session, collectRequest);
            CiaMavenDependencyCollector dependencyCollector = new CiaMavenDependencyCollector();
            collectResult.getRoot().accept(dependencyCollector);
            return dependencyCollector.getDependencies();
        } catch (DependencyCollectionException e) {
            logger.error("Failed to determine dependencies of the maven artifact " + coordinates, e);
        }
        return null;
    }

    public static File resolve(Artifact artifact) {
        ArtifactRequest request = new ArtifactRequest();
        request.setArtifact(artifact);
        request.setRepositories(Collections.singletonList(repository));
        try {
            ArtifactResult result = system.resolveArtifact(session, request);
            if (result.isResolved()) {
                return result.getArtifact().getFile();
            } else {
                logger.warn("Failed to resolve the maven artifact with following exceptions:");
                for (Exception e: result.getExceptions()) {
                    logger.warn("\t", e);
                }
            }
        } catch (ArtifactResolutionException e) {
            logger.warn("Failed to resolve the maven artifact", e);
        }
        return null;
    }

    public static List<CiaMavenArtifact> resolveTransitiveDependencies(String coordinates) throws CiaBadClassSource {
        Artifact artifact = new DefaultArtifact(coordinates);
        DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(new Dependency(artifact, JavaScopes.COMPILE));
        collectRequest.setRepositories(Collections.singletonList(repository));
        DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, classpathFlter);

        try {
            List<ArtifactResult> artifactResults = system.resolveDependencies( session, dependencyRequest ).getArtifactResults();
            List<CiaMavenArtifact> artifacts = new ArrayList<>();
            for (ArtifactResult result: artifactResults) {
                Artifact temp = result.getArtifact();
                artifacts.add(new CiaMavenArtifact(temp.getGroupId(),
                        temp.getArtifactId(), temp.getVersion(), temp.getFile().toString()));
            }
            return artifacts;
        } catch (DependencyResolutionException e) {
            //logger.warn("Failed to resolve the transitive dependencies for the artifact " + coordinates, e);
            throw new CiaBadClassSource(e);
        }
    }
}
