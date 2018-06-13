package org.deletethis.hardcode.guava.test;

import org.deletethis.hardcode.HardcodeSplit;

public class SplitMap {
    @HardcodeSplit(2)
    private Object theMap;

    public SplitMap(Object theMap) {
        this.theMap = theMap;
    }
}
