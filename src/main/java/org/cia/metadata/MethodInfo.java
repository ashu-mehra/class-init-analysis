package org.cia.metadata;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class MethodInfo {
    private final ClassInfo owner;
    private final String name;
    private final String parameters;
    private final String returnType;
    private final Set<Callsite> callsites;
    private final Set<ClassInfo> referencedClasses;
    private boolean resolved;
    private int accessFlags;

    public MethodInfo(ClassInfo owner, String name, String desc) {
        this.owner = owner;
        this.name = name;
        this.parameters = getParameters(desc);
        this.returnType = getReturnType(desc);
        this.callsites = new LinkedHashSet<>();
        this.referencedClasses = new LinkedHashSet();
        this.resolved = false;
    }

    public static String getParameters(String desc) {
        int start = 1; // skip '('
        int end = desc.indexOf(')');
        return desc.substring(start, end);
    }

    public static String getReturnType(String desc) {
        int start = desc.indexOf(')');
        return desc.substring(start+1);
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public ClassInfo getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getParameters() {
        return parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getSignature() {
        return name + parameters;
    }

    public Set<MethodInfo> getCallees() {
        return callsites.stream().map(callsite -> callsite.callee).collect(Collectors.toSet());
    }

    public Set<Callsite> getCallsites() {
        return Set.copyOf(callsites);
    }

    public void addCallsite(MethodInfo callee, Callsite.CallType type) {
        callsites.add(new Callsite(callee, type));
    }

    public void addReferencedClasss(ClassInfo cinfo) {
        referencedClasses.add(cinfo);
    }

    @Override
    public String toString() {
        return owner.toString() + "." + name + "(" + parameters + ")" + returnType;
    }

    public Set<ClassInfo> getReferencedClasses() {
        return Collections.unmodifiableSet(referencedClasses);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodInfo that = (MethodInfo) o;
        return Objects.equals(owner, that.owner)
                && Objects.equals(name, that.name)
                && Objects.equals(parameters, that.parameters)
                && Objects.equals(returnType, that.returnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, name, parameters, returnType);
    }

    public boolean isPublic() {
        return (isResolved() && ((accessFlags & ACC_PUBLIC) != 0));
    }

    public void setAccessFlags(int access) {
        this.accessFlags = access;
    }
}
