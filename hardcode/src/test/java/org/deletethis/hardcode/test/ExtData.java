package org.deletethis.hardcode.test;

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
    
}
