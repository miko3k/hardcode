package org.deletethis.hardcode;

import java.util.Objects;

/**
 *
 * @author miko
 */
public class ExtData  extends Data {
    private final boolean ext;

    public ExtData(boolean ext, String foo, int bar, Long lng) {
        super(foo, bar, lng);
        this.ext = ext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExtData)) return false;
        if (!super.equals(o)) return false;
        ExtData extData = (ExtData) o;
        return ext == extData.ext;
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), ext);
    }
}
