package org.cia.metadata;

import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class ClassInfo {
    private final String name;
    private ClassInfo parent;
    private List<ClassInfo> interfaces;
    private Set<MethodInfo> methods;
    private Map<String, MethodInfo> methodMap;
    private List<ClassInfo> children;
    private List<ClassInfo> implementors;
    private int accessFlags;
    private String codeSource;

    public ClassInfo(String name) {
        this.name = name;
        this.interfaces = new ArrayList<>();
        this.methods = new LinkedHashSet<>();
        this.methodMap = new HashMap<>();
        this.children = new ArrayList<>();
        this.implementors = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public boolean isInterface() {
        return (accessFlags & Opcodes.ACC_INTERFACE) != 0;
    }

    public void addMethod(MethodInfo minfo) {
        methods.add(minfo);
        methodMap.putIfAbsent(minfo.getSignature(), minfo);
    }

    public MethodInfo findMethod(String signature) {
        return methodMap.get(signature);
    }

    public Set<MethodInfo> getMethods() {
        return Collections.unmodifiableSet(methods);
    }

    public void addImplementor(ClassInfo impl) {
        implementors.add(impl);
    }

    public void addInterface(ClassInfo intf) {
        interfaces.add(intf);
        intf.addImplementor(this);
    }

    public void addChild(ClassInfo child) {
        children.add(child);
    }

    public ClassInfo getParent() {
        return this.parent;
    }

    public void setParent(ClassInfo parent) {
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public void setAccessFlags(int accessFlags) {
        this.accessFlags = accessFlags;
    }

    public void forEachSubclass(Consumer<ClassInfo> consumer) {
        for (ClassInfo cinfo: children) {
            consumer.accept(cinfo);
            cinfo.forEachSubclass(consumer);
        }
    }

    public void forEachImplementor(Consumer<ClassInfo> consumer) {
        for (ClassInfo cinfo: implementors) {
            consumer.accept(cinfo);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isPublic() {
        return (accessFlags & ACC_PUBLIC) != 0;
    }

    public void setCodeSource(String codeSource) {
        this.codeSource = codeSource;
    }

    public String getCodeSource() {
        return codeSource;
    }

    public boolean isChildOf(ClassInfo cinfo) {
        if (parent == null) {
            return false;
        }
        if (parent == cinfo) {
            return true;
        } else {
            return parent.isChildOf(cinfo);
        }
    }

    public boolean isDirectImplementationOf(ClassInfo cinfo) {
        if (interfaces.contains(cinfo)) {
            return true;
        }
        return false;
    }

    public boolean isImplementationOf(ClassInfo cinfo) {
        if (isDirectImplementationOf(cinfo)) {
            return true;
        }
        for (ClassInfo intf: interfaces) {
            if (intf.isImplementationOf(cinfo)) {
                return true;
            }
        }
        if (parent != null) {
            if (parent.isImplementationOf(cinfo)) {
                return true;
            }
        }
        return false;
    }
}
