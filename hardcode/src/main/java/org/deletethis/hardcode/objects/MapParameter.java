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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapParameter that = (MapParameter) o;

        if (key != that.key) return false;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        int result = (key ? 1 : 0);
        result = 31 * result + index;
        return result;
    }
}
