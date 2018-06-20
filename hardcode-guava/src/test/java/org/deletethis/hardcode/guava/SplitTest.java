package org.deletethis.hardcode.guava;

import com.google.common.collect.ImmutableMultimap;
import org.deletethis.hardcode.Hardcode;
import org.deletethis.hardcode.testing.HardcodeTesting;
import org.junit.Assert;
import org.junit.Test;

public class SplitTest {

    @Test
    public void multimap() {
        ImmutableMultimap.Builder<Integer,Integer> mmb = ImmutableMultimap.builder();
        for(int i=0;i<100;++i) {
            for(int j=0;j<100;j++) {
                mmb.put(i, j);
            }
        }
        SplitWrapper1000 mm = new SplitWrapper1000(mmb.build());
        SplitWrapper1000 mm2 = HardcodeTesting.supply(Hardcode.defaultConfig().createClass("Foo", mm));
        Assert.assertEquals(mm, mm2);

    }
}
