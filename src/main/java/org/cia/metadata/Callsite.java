package org.cia.metadata;

public class Callsite {

    public enum CallType {
        STATIC,
        VIRTUAL,
        SPECIAL,
    };

    final MethodInfo callee;
    final CallType type;

    public Callsite(MethodInfo callee, CallType type) {
        this.callee = callee;
        this.type = type;
    }

    public MethodInfo getCallee() {
        return callee;
    }

    public CallType getType() {
        return type;
    }
}
