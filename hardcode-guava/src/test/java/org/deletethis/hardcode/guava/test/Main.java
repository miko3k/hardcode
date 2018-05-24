package org.deletethis.hardcode.guava.test;

import com.google.common.collect.ImmutableList;
import org.deletethis.hardcode.Hardcode;

public class Main {
    private static void doIt(Hardcode hardcoder, Object o) {
        System.out.println(hardcoder.method("foo", o));
    }

    public static void main(String[] args) {
        Hardcode bld = Hardcode.defaultConfig();

        Data data = new Data(null, 1, 50L);
        doIt(bld, ImmutableList.of(data, data));
    }
}
