package org.deletethis.hardcode.test;

import org.deletethis.hardcode.HardcodeRoot;
import org.junit.Test;

@HardcodeRoot
public class ChildData {
    private String value;

    public ChildData(String value) {
        this.value = value;
    }
}
