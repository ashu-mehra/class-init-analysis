package org.cia.metadata;

public class Type {
    public static boolean isBasicType(String type) {
        switch (type) {
            case "B", "C", "D", "F", "I", "J", "S", "Z": return true;
            default: return false;
        }
    }

    public static boolean isBasicArrayType(String type) {
        if (type.charAt(0) != '[') {
            return false;
        }
        String componentType = type.substring(type.lastIndexOf('[') + 1);
        switch (componentType) {
            case "B;", "C;", "D;", "F;", "I;", "J;", "S;", "Z;": return true;
            default: return false;
        }
    }

    public static boolean isJdkType(String type) {
        String typeToCheck = type;
        if (type.charAt(0) == '[') {
            typeToCheck = type.substring(type.lastIndexOf('[') + 1);
        }
        if (typeToCheck.startsWith("java/") || typeToCheck.startsWith("jdk/")) {
            return true;
        }
        return false;
    }
}
