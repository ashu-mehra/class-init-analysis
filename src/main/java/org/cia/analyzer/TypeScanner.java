package org.cia.analyzer;

import java.util.NoSuchElementException;

public class TypeScanner {
    private final String string;
    private int next;
    private boolean consumed;
    private boolean empty;

    public TypeScanner(String string) {
        this.string = string;
        this.next = 0;
        this.consumed = false;
        if (string.equals("")) {
            empty = true;
        } else {
            empty = false;
        }
    }

    public static TypeScanner create(String string) {
        return new TypeScanner(string);
    }

    public boolean hasToken() {
        if (empty) {
            return false;
        }
        if (consumed) {
            if (next < string.length()) {
                consumed = false;
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private int findEndOfToken() {
        do {
            switch(string.charAt(next)) {
                case 'B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z':
                    return next + 1;
                case 'L': {
                    int index = next + 1;
                    while (string.charAt(index) != ';') {
                        index += 1;
                    }
                    return index;
                }
                case '[': {
                    int index = next + 1;
                    while (string.charAt(index) == '[') {
                        index += 1;
                    }
                    next = index;
                }
                break;
                default:
                    throw new IllegalStateException("Invalid type in string: " + string);
            }
        } while (true);
    }
    
    public String nextToken() {
        if (consumed) {
            if (!hasToken()) {
                throw new NoSuchElementException();
            }
        }
        int end = findEndOfToken();
        consumed = true;
        String token = string.substring(next, end);
        next = end;
        return token;
    }
}
