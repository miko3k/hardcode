package org.deletethis.hardcode.objects;

public class IndexParamteter implements ParameterName {
    private final int index;

    public IndexParamteter(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String toString() {
        return String.valueOf(index);
    }
}
