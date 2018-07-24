package org.deletethis.hardcode.guava;

import com.google.common.collect.*;
import org.deletethis.hardcode.Hardcode;
import org.deletethis.hardcode.testing.HardcodeTesting;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class SplitTest {
    @Rule
    public TestName name = new TestName();

    void testSplit(Object o) {
        SplitWrapper1000 mm = new SplitWrapper1000(o);
        SplitWrapper1000 mm2 = HardcodeTesting.supply(Hardcode.defaultConfig().createClass("Split" + name.getMethodName(), mm));
        Assert.assertEquals(mm, mm2);

    }

    @Test
    public void multimap() {
        ImmutableListMultimap.Builder<Integer,Integer> bld = ImmutableListMultimap.builder();
        for(int i=0;i<100;++i) {
            for(int j=0;j<100;j++) {
                bld.put(i, j);
            }
        }
        testSplit(bld.build());
    }

    @Test
    public void multimap2() {
        ImmutableSetMultimap.Builder<Integer,Integer> bld = ImmutableSetMultimap.builder();
        for(int i=0;i<100;++i) {
            for(int j=0;j<100;j++) {
                bld.put(i, j);
            }
        }
        testSplit(bld.build());
    }

    @Test
    public void map() {
        ImmutableMap.Builder<Integer,Integer> bld = ImmutableMap.builder();
        for (int i = 0; i < 20000; ++i) {
            bld.put(i, i);
        }
        testSplit(bld.build());
    }

    @Test
    public void list() {
        ImmutableList.Builder<Integer> bld = ImmutableList.builder();
        for(int i=0;i<20000;++i) {
            bld.add(i);
        }
        testSplit(bld.build());

    }

    @Test
    public void set() {
        ImmutableSet.Builder<Integer> bld = ImmutableSet.builder();
        for(int i=0;i<20000;++i) {
            bld.add(i);
        }
        testSplit(bld.build());
    }


}
