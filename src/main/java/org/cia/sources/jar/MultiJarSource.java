package org.cia.sources.jar;


import org.cia.sources.ClassData;
import org.cia.sources.ClassSource;
import org.cia.sources.ClassSourceFactory;
import org.cia.sources.ClassSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class MultiJarSource implements ClassSource {
    private final JarFileSource[] jarSources;
    private static final Logger logger = LoggerFactory.getLogger(JarFileSource.class);

    @Override
    public ClassSourceType getType() {
        return ClassSourceType.JAR;
    }

    @Override
    public String getTypeAsString() {
        return ClassSourceType.JAR.name();
    }

    @Override
    public String getMainEntry() {
        return null;
    }

    @Override
    public String getAllEntries() {
        StringBuilder sb = new StringBuilder();
        boolean addSeparator = false;
        for (JarFileSource source: jarSources) {
            if (addSeparator) {
                sb.append(",");
            }
            sb.append(source);
            if (!addSeparator) {
                addSeparator = true;
            }
        }
        return sb.toString();
    }

    private MultiJarSource(JarFileSource[] jarSources) {
        this.jarSources = jarSources;
    }

    public static MultiJarSource create(String[] jarFiles) {
        JarFileSource[] jarSources = new JarFileSource[jarFiles.length];
        for (int i = 0; i < jarSources.length; i++) {
            jarSources[i] = JarFileSource.create(jarFiles[i]);
        }
        return new MultiJarSource(jarSources);
    }

    @Override
    public Iterator<ClassData> iterator() {
        return new MultiJarIterator();
    }

    private class MultiJarIterator implements Iterator<ClassData> {
        private int current;
        private boolean nextAvailable;
        private Iterator<ClassData> jarIterator;

        MultiJarIterator() {
            current = -1;
            nextAvailable = false;
            moveToNextJarFile();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEachRemaining(Consumer<? super ClassData> action) {
            throw new UnsupportedOperationException();
        }

        private boolean moveToNextJarFile() {
            boolean foundValidJarFile = false;
            do {
                if (current == (jarSources.length - 1)) {
                    jarIterator = null;
                    break;
                }
                current += 1;
                jarIterator = jarSources[current].iterator();
                if (jarIterator.hasNext()) {
                    foundValidJarFile = true;
                    break;
                }
            } while (true);
            return foundValidJarFile;
        }

        @Override
        public boolean hasNext() {
            if (nextAvailable) {
                return true;
            }
            do {
                if (jarIterator != null && jarIterator.hasNext()) {
                    nextAvailable = true;
                    return true;
                }
            } while (moveToNextJarFile());
            return false;
        }

        @Override
        public ClassData next() {
            if (!nextAvailable) {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
            }
            nextAvailable = false;
            return jarIterator.next();
        }
    }
}
