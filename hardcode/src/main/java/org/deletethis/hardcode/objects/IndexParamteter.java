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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndexParamteter that = (IndexParamteter) o;

        return index == that.index;
    }

    @Override
    public int hashCode() {
        return index;
    }
}
