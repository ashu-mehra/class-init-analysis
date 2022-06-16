package org.cia.sources;

import java.util.Iterator;

public interface ClassSource {
    Iterator<ClassData> iterator();
    ClassSourceType getType();
    String getTypeAsString();
    String getAllEntries();
    String getMainEntry();
}
