package org.deletethis.hardcode;

import org.deletethis.hardcode.testing.HardcodeTesting;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class BigRootTest {
    @HardcodeRoot
    public static class List implements Serializable {
        private ArrayList<Integer> list;

        public List(ArrayList<Integer> list) {
            this.list = list;
        }

        public List() {
            list = new ArrayList<>(500);
            for(int i=0;i<500;++i)
                list.add(i);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof List)) return false;

            List list1 = (List) o;

            return list.equals(list1.list);
        }

        @Override
        public int hashCode() {
            return list.hashCode();
        }
    }

    @Test
    public void testIt() {
        ArrayList<List> data = new ArrayList<>();
        for(int i=0;i<50;++i) {
            data.add(new List());
        }

        ArrayList<List> data2 = HardcodeTesting.supply(Hardcode.defaultConfig().createClasses("BigRoot", data));
        Assert.assertEquals(data, data2);
    }
}
