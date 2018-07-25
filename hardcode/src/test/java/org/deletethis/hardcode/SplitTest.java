package org.deletethis.hardcode;

import com.google.common.collect.*;
import org.deletethis.hardcode.testing.HardcodeTesting;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.ArrayList;
import java.util.HashMap;

public class SplitTest {
    @Rule
    public TestName name = new TestName();

    private void testSplit(Object o) {
        SplitWrapper1000 mm = new SplitWrapper1000(o);
        SplitWrapper1000 mm2 = HardcodeTesting.supply(Hardcode.defaultConfig().createClasses("Split" + name.getMethodName(), mm));
        Assert.assertEquals(mm, mm2);

    }

    @Test
    public void arrayList() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for(int i=0;i<20000;++i) {
            arrayList.add(i);
        }
        testSplit(arrayList);
    }
    @Test
    public void hashmap() {
        HashMap<Integer, Integer> hashmap = new HashMap<>();
        for(int i=0;i<20000;++i) {
            hashmap.put(i, -i);
        }
        testSplit(hashmap);
    }


}
