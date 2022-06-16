package org.cia.sources.maven.resolver;

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;

import java.io.PrintStream;

import static java.util.Objects.requireNonNull;

public class NoopRepositoryListener extends AbstractRepositoryListener {
    public NoopRepositoryListener() {
    }

    public void artifactDeployed(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void artifactDeploying(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void artifactDescriptorInvalid(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void artifactDescriptorMissing(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void artifactInstalled(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void artifactInstalling(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void artifactResolved(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void artifactDownloading(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void artifactDownloaded(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void artifactResolving(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void metadataDeployed(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void metadataDeploying(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void metadataInstalled(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void metadataInstalling(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void metadataInvalid(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void metadataResolved(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }

    public void metadataResolving(RepositoryEvent event) {
        requireNonNull( event, "event cannot be null" );
    }
}
