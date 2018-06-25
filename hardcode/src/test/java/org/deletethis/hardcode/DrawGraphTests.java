package org.deletethis.hardcode;

import com.squareup.javapoet.TypeSpec;
import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.graph.Graphviz;
import org.deletethis.hardcode.impl.ObjectInfo;
import org.deletethis.hardcode.objects.ParameterName;
import org.deletethis.hardcode.testing.HardcodeTesting;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DrawGraphTests {
    @Rule
    public TestName name = new TestName();

    public enum Enm {
        BAR,
        BAZ
    }


    private <T> T run(T o) {
        return run(Hardcode.builtinConfig(), o);
    }

    private <T> T run(Hardcode hc, T o) {
        Digraph<ObjectInfo, ParameterName> g = hc.buildGraph(o);
        new Graphviz<>(g).print(HardcodeTesting.outputFile(name.getMethodName() + ".gv"));

        TypeSpec theClass = hc.createClassFromGraph("TheClass", g);
        Supplier<T> objectSupplier = HardcodeTesting.SUPPLIER_COMPILER.get(theClass);
        return objectSupplier.get();
    }


    @Test
    public void testNull() {
        Assert.assertNull(run(null));
    }


    @Test
    public void testString() {

        Assert.assertEquals("Hello", run("Hello"));
    }

    @Test
    public void testStruct() {
        Data d = run(new Data("hello", 1, 50L));
        Assert.assertEquals("hello", d.getFoo());
        Assert.assertEquals(1, d.getBar());
        Assert.assertEquals(Long.valueOf(50L), d.getLng());
    }

    @Test
    public void simpleGraph() {
        Data data1 = new Data(null, 1, 50L);
        Data data2 = new Data("data2", 1, 49L);
        ArrayList<Data> arr = new ArrayList<>(15);
        arr.add(data1);
        arr.add(data1);
        arr.add(data2);

        ArrayList<Data> arr2 = run(arr);

        Assert.assertSame(arr2.get(0), arr2.get(1));
    }

    @Test
    public void testEnum() {
        Assert.assertEquals(Enm.BAR, run(Enm.BAR));
        Assert.assertEquals(Enm.BAZ, run(Enm.BAZ));
    }

    @Test
    public void inheritance() {
        ExtData data1 = new ExtData(true, "foo", 1, Long.MIN_VALUE);
        ExtData data2 = run(data1);

        Assert.assertEquals(data1, data2);
    }

    @Test
    public void something() {
        Data data1 = new Data(null, 1, 50L);
        Data data2 = new Data("data2", 1, 49L);

        DefaultConfiguration dc = new DefaultConfiguration();
        dc.addHardcodeRoot(Data.class);
        Hardcode hc2 = Hardcode.builtinConfig(dc);

        Map<String, Object> map = new HashMap<>();
        RootChildData cd1 = new RootChildData("hello");
        RootChildData cd2 = new RootChildData("world");
        map.put("data1a", data1);
        map.put("data1b", data1);
        map.put("data2", data2);
        map.put("cd1a", cd1);
        map.put("cd1b", cd1);
        map.put("cd2", cd2);

        Map<String, Object> run = run(hc2, map);
        Assert.assertEquals(map, run);
    }
}
