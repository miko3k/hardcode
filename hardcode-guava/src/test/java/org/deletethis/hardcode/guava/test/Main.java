package org.deletethis.hardcode.guava.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import org.deletethis.hardcode.Hardcode;

public class Main {
    private static void doIt(Hardcode hardcoder, Object o) {
        System.out.println(hardcoder.createClass("Foo", o));
    }

    public static void main(String[] args) {
        Hardcode hc = Hardcode.defaultConfig();

        Data data = new Data(null, 1, 50L);
        doIt(hc, ImmutableList.of(data, data));
        doIt(hc, ImmutableMap.of(data, data));
        doIt(hc, ImmutableMap.of(1,2,3,4,5,6,7,8,9,10));

        ImmutableMap.Builder<Integer, Integer> b = ImmutableMap.builder();
        b.put(1, 2);
        b.put(3, 4);
        b.put(5, 6);
        b.put(7, 8);
        b.put(9, 10);
        b.put(11, 12);
        b.put(13, 14);
        b.put(15, 16);
        b.put(17, 18);
        doIt(hc, b.build());

        doIt(hc, ImmutableMultimap.of(data, data));
        doIt(hc, ImmutableMultimap.of(1,2,3,4,5,6,7,8,9,10));

        ImmutableMultimap.Builder<Integer, Integer> b2 = ImmutableMultimap.builder();
        b2.put(1, 2);
        b2.put(3, 4);
        b2.put(5, 6);
        b2.put(7, 8);
        b2.put(9, 10);
        b2.put(11, 12);
        b2.put(13, 14);
        b2.put(15, 16);
        b2.put(17, 18);
        ImmutableMultimap<Integer, Integer> bb2 = b2.build();
        doIt(hc, bb2);

        doIt(hc, new SplitMap(bb2));




    }
}
