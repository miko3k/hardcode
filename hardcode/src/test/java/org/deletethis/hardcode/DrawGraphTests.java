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
import sun.tools.tree.DoubleExpression;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
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

        TypeSpec theClass = hc.createClassFromGraph(name.getMethodName(), g);
        return HardcodeTesting.supply(theClass);
    }

    private <T> T runUncached(T o) {
        Hardcode hc = Hardcode.builtinConfig();
        Digraph<ObjectInfo, ParameterName> g = hc.buildGraph(o);
        new Graphviz<>(g).print(HardcodeTesting.outputFile(name.getMethodName() + ".gv"));

        TypeSpec theClass = hc.createClassFromGraph(name.getMethodName(), g);
        return HardcodeTesting.supplyUncached(theClass);
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

    @SuppressWarnings("WeakerAccess")
    public static class Empty implements Serializable {}

    @Test
    public void empty() {
        Empty run = run(new Empty());
        Assert.assertNotNull(run);
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
        dc.addRootClass(Data.class);
        Hardcode hc = Hardcode.builtinConfig(dc);

        Map<String, Object> map = new HashMap<>();
        RootChildData cd1 = new RootChildData("hello");
        RootChildData cd2 = new RootChildData("world");
        map.put("data1a", data1);
        map.put("data1b", data1);
        map.put("data2", data2);
        map.put("cd1a", cd1);
        map.put("cd1b", cd1);
        map.put("cd2", cd2);

        Map<String, Object> run = run(hc, map);
        Assert.assertEquals(map, run);
        Assert.assertSame(run.get("data1a"), run.get("data1b"));

        Map<String, String> treeMap = new TreeMap<>();
        treeMap.put("a", "b");
        treeMap.put("c", "d");
        Assert.assertEquals(treeMap, run(hc, treeMap));


        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("a");
        linkedList.add("b");
        Assert.assertEquals(linkedList, run(hc, linkedList));
    }

    @Test
    public void optinal() {
        Optional<Integer> a = Optional.empty();
        Optional<Integer> b = Optional.empty();

        Assert.assertEquals(a, runUncached(a));
        Assert.assertEquals(b, runUncached(b));

    }

    @Test
    public void primitives() throws URISyntaxException, MalformedURLException {
        URI uri = new URI("http://example.com");
        URL url = new URL("http://example.com");
        Date date = new Date(123456789);

        Assert.assertEquals(Integer.valueOf(5), run(5));
        Assert.assertEquals(Long.valueOf(5L), run(5L));
        Assert.assertEquals(Float.valueOf(5f), run(5f));
        Assert.assertEquals(Byte.valueOf((byte)5), run((byte)5));
        Assert.assertEquals(Boolean.TRUE, run(Boolean.TRUE));
        Assert.assertEquals(Double.valueOf(5.0), run(5.0));
        Assert.assertEquals(Character.valueOf('c'), run('c'));
        Assert.assertEquals(Short.valueOf((short)5), run((short)5));
        Assert.assertEquals(uri, run(uri));
        Assert.assertEquals(url, run(url));
        Assert.assertEquals(date, run(date));

        List<Object> array = new ArrayList<>();
        array.add(0.5599999999999999 + 0.04);
        array.add(0.68 + 0.04);
        array.add(2.05+1.01);
        array.add(0.5599999999999999f + 0.04f);
        array.add(0.68f + 0.04f);
        array.add(2.05f+1.01f);
        array.add(0.6812345678901234534363457890f);
        array.add(0.68123456789012345623427890e5f);
        array.add(0.6812345678901233344567890e10f);
        array.add(0.6812345678901244334567890e30f);
        array.add(0.6812345678901238734567890e34f);
        array.add(0.6812345678901234591267890e37f);
        array.add(0.9234342345732433124523428e38f);
        array.add(2.2999999999999996);
        array.add(2.2999999999999997);
        array.add(Float.NaN);
        array.add(Float.POSITIVE_INFINITY);
        array.add(Float.NEGATIVE_INFINITY);
        array.add(Double.NaN);
        array.add(Double.POSITIVE_INFINITY);
        array.add(Double.NEGATIVE_INFINITY);
        Assert.assertEquals(array, run(array));
    }
}
