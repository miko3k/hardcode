package org.deletethis.hardcode.guava.test;

import com.google.common.collect.ImmutableList;
import org.deletethis.hardcode.Hardcoder;

import java.util.ArrayList;

public class Main {
    private static void doIt(Hardcoder hardcoder, Object o) {
        System.out.println(hardcoder.method("foo", o));
    }

    public static void main(String[] args) {
        Hardcoder bld = Hardcoder.defaultConfig();

        Data data = new Data(null, 1, 50L);
        doIt(bld, ImmutableList.of(data, data));
    }
}
