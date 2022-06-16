package org.cia.metadata;

import org.cia.sources.ClassData;
import org.cia.sources.ClassSource;
import org.cia.metadata.visitor.CiaClassVisitor;
import org.objectweb.asm.ClassReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassReader.SKIP_DEBUG;
import static org.objectweb.asm.ClassReader.SKIP_FRAMES;

public class ClassRepository {
    private final ClassSource source;
    private Map<String, ClassInfo> allClasses = new LinkedHashMap<>();

    public ClassRepository(ClassSource source) {
        this.source = source;
        Iterator<ClassData> iterator = source.iterator();
        while (iterator.hasNext()) {
            ClassData classData = iterator.next();
            ClassReader reader = new ClassReader(classData.getClassBytes());
            CiaClassVisitor visitor = new CiaClassVisitor(this, classData.getContainer());
            reader.accept(visitor, SKIP_DEBUG | SKIP_FRAMES | EXPAND_FRAMES);
        }
    }

    public ClassSource getSource() {
        return source;
    }

    public ClassInfo addClass(String className) {
        return allClasses.computeIfAbsent(className, v -> new ClassInfo(normalizeClassName(className)));
    }

    public ClassInfo findClass(String className) {
        return allClasses.get(className);
    }

    public List<ClassInfo> getAllClasses() {
        return Collections.unmodifiableList(new ArrayList(allClasses.values()));
    }

    public static String normalizeClassName(String className) {
        return className.replace(".", "/");
    }
}
