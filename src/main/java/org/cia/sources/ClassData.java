package org.cia.sources;

public class ClassData {
    private final String className;
    private final byte[] classBytes;
    private final String container;

    public ClassData(String className, byte[] classBytes, String container) {
        this.className = className;
        this.classBytes = classBytes;
        this.container = container;
    }

    public String getClassName() {
        return className;
    }

    public byte[] getClassBytes() {
        return classBytes;
    }

    public String getContainer() {
        return container;
    }
}
