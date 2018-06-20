package org.deletethis.hardcode;

import java.io.Serializable;

@HardcodeRoot
public class ChildData implements Serializable {
    private String value;
    private ChildData more;

    public ChildData(String value) {
        this.value = value;
    }

    public ChildData(String value, ChildData more) {
        this.value = value;
        this.more = more;
    }
}
