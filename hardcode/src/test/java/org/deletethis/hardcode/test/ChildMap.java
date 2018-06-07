package org.deletethis.hardcode.test;

import org.deletethis.hardcode.HardcodeRoot;
import org.junit.Test;

import java.util.Map;

public class ChildMap {
    @HardcodeRoot
    private final Map<String, String> map;

    public ChildMap(Map<String, String> map) {
        this.map = map;
    }
}
