package org.deletethis.hardcode;

import java.io.Serializable;
import java.util.Objects;

@HardcodeRoot
public class RootChildData implements Serializable {
    private String value;
    private RootChildData more;

    public RootChildData(String value) {
        this.value = value;
    }

    public RootChildData(String value, RootChildData more) {
        this.value = value;
        this.more = more;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RootChildData)) return false;
        RootChildData childData = (RootChildData) o;
        return Objects.equals(value, childData.value) &&
                Objects.equals(more, childData.more);
    }

    @Override
    public int hashCode() {

        return Objects.hash(value, more);
    }
}
