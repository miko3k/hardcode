package org.deletethis.hardcode;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChildData)) return false;
        ChildData childData = (ChildData) o;
        return Objects.equals(value, childData.value) &&
                Objects.equals(more, childData.more);
    }

    @Override
    public int hashCode() {

        return Objects.hash(value, more);
    }
}
