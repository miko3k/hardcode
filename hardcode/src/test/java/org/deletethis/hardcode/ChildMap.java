package org.deletethis.hardcode;

import org.deletethis.hardcode.HardcodeRoot;
import org.junit.Test;

import java.util.Map;
import java.util.Objects;

public class ChildMap {
    @HardcodeRoot
    private final Map<String, String> map;

    public ChildMap(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChildMap)) return false;
        ChildMap childMap = (ChildMap) o;
        return Objects.equals(map, childMap.map);
    }

    @Override
    public int hashCode() {

        return Objects.hash(map);
    }
}
