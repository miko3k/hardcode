package org.deletethis.hardcode;

import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.graph.Graphviz;
import org.deletethis.hardcode.impl.ObjectInfo;
import org.deletethis.hardcode.objects.ParameterName;
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
        RootChildData childData = new RootChildData("hello");
        RootChildData data1 = new RootChildData("d1", childData);
        RootChildData data2 = new RootChildData("d2", childData);
        ArrayList<Object> list = new ArrayList<>();
        list.add(data1);
        list.add(data2);
        Digraph<ObjectInfo, ParameterName> g = Hardcode.builtinConfig().buildGraph(list);
        new Graphviz<>(g).printTemp("cr");
    }

    public static class DifferentSplit {
        @HardcodeSplit(2)
        Object a;
        @HardcodeSplit(3)
        Object b;

        public DifferentSplit(Object a, Object b) {
            this.a = a;
            this.b = b;
        }
    }

    @Test(expected = ConfigMismatchException.class)
    public void split() {
        ArrayList<Integer> list = new ArrayList<>();
        DifferentSplit ds = new DifferentSplit(list, list);
        Hardcode.builtinConfig().buildGraph(ds);
    }

    @Test(expected = CrossRootReferenceException.class)
    public void split2() {
        ArrayList<ChildData> list = new ArrayList<>();
        ChildData common = new ChildData("common");
        ChildData c1 = new ChildData("c1", common);
        ChildData c2 = new ChildData("c1", common);
        list.add(c1);
        list.add(c2);
        DifferentSplit ds = new DifferentSplit(list, null);
        Hardcode.builtinConfig().buildGraph(ds);
    }
}
