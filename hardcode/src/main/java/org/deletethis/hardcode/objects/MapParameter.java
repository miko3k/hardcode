package org.deletethis.hardcode.objects;

public class MapParameter implements ParameterName {
    private final boolean key;
    private final int index;

    public MapParameter(boolean key, int index) {
        this.key = key;
        this.index = index;
    }

    public boolean isKey() {
        return key;
    }

    public int getIndex() {
        return index;
    }

    public String toString() {
        if(key) {
            return index + ".key";
        } else {
            return index + ".value";
        }
    }
}
