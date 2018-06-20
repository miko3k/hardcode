package org.deletethis.hardcode.guava.test;

import org.deletethis.hardcode.HardcodeSplit;

import java.io.Serializable;

public class SplitMap implements Serializable {
    @HardcodeSplit(2)
    private Object theMap;

    public SplitMap(Object theMap) {
        this.theMap = theMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SplitMap)) return false;

        SplitMap splitMap = (SplitMap) o;

        return theMap.equals(splitMap.theMap);
    }

    @Override
    public int hashCode() {
        return theMap.hashCode();
    }
}
