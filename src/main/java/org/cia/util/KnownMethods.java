package org.cia.util;

public enum KnownMethods {
    CLINIT("<clinit>", "", "V"),
    MAIN("main", "[Ljava/lang/String;", "V");

    private final String name;
    private final String parameters;
    private final String returnType;

    KnownMethods(String name, String parameters, String returnType) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
    }

    public String methodName() {
        return this.name;
    }
    public String parameters() {
        return this.parameters;
    }
    public String returnType() {
        return this.returnType;
    }
    public String signature() {
        return this.name + this.parameters;
    }
}
