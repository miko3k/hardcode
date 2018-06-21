package org.deletethis.hardcode;

import org.junit.Test;

import java.util.ArrayList;

public class SimpleExceptionTests {
    @Test(expected = CycleException.class)
    public void simpleCycle() {
        Container<Object> c1 = new Container<>();
        Container<Object> c2 = new Container<>();
        c1.setValue(c2);
        c2.setValue(c1);
        Hardcode.builtinConfig().buildGraph(c1);
    }

    @Test(expected = CrossRootReferenceException.class)
    public void crossRoot() {
        ChildData childData = new ChildData("hello");
        ChildData data1 = new ChildData("d1", childData);
        ChildData data2 = new ChildData("d2", childData);
        ArrayList<Object> list = new ArrayList<>();
        list.add(data1);
        list.add(data2);
        Hardcode.builtinConfig().createClass("foo", list);
    }
}
