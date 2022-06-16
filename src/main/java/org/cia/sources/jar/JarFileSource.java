package org.cia.sources.jar;


import org.cia.sources.ClassData;
import org.cia.sources.ClassSource;
import org.cia.sources.ClassSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFileSource implements ClassSource {
    private final String jarFile;
    private static final Logger logger = LoggerFactory.getLogger(JarFileSource.class);

    private JarFileSource(String jarFile) {
        this.jarFile = jarFile;
    }

    public static JarFileSource create(String jarFile) {
        return new JarFileSource(jarFile);
    }

    private byte[] readContents(InputStream is) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();) {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = is.read(buffer)) != -1) {
                byteStream.write(buffer, 0, bytesRead);
            }
            byteStream.flush();
            return byteStream.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }

    public ClassData getClassData(JarFile jar, JarEntry entry) {
        try {
            InputStream is = jar.getInputStream(entry);
            byte[] contents = readContents(is);
            return new ClassData(entry.getName(), contents, jar.getName());
        } catch (IOException e) {
            logger.warn("Exception in reading entry in jar file" + jar.getName());
        }
        return null;
    }

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
        return jarFile;
    }

    @Override
    public String getAllEntries() {
        return getMainEntry();
    }

    @Override
    public Iterator<ClassData> iterator() {
        return new JarFileIterator();
    }

    private class JarFileIterator implements Iterator<ClassData> {
        private JarFile jar;
        private Enumeration<JarEntry> enumeration;
        private ClassData nextData;
        private boolean nextAvailable;

        JarFileIterator() {
            nextAvailable = false;
            try {
                jar = new JarFile(jarFile);
                enumeration = jar.entries();
            } catch (IOException e) {
                logger.warn("Error in reading jar file " + jarFile);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEachRemaining(Consumer<? super ClassData> action) {
            throw new UnsupportedOperationException();
        }

        private JarEntry getNextClassFile() {
            if (enumeration != null) {
                while (enumeration.hasMoreElements()) {
                    JarEntry entry = enumeration.nextElement();
                    if (entry.getName().endsWith(".class")) {
                        return entry;
                    }
                }
            }
            return null;
        }

        private ClassData getNextClassData() {
            ClassData next = null;
            JarEntry entry = getNextClassFile();
            if (entry != null) {
                next = getClassData(jar, entry);
            }
            return next;
        }

        @Override
        public boolean hasNext() {
            if (nextAvailable) {
                return true;
            }
            nextData = getNextClassData();
            if (nextData != null) {
                nextAvailable = true;
                return true;
            }
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
            return nextData;
        }
    }
}
