package org.deletethis.hardcode.test;

import com.squareup.javapoet.JavaFile;
import org.deletethis.hardcode.DefaultConfiguration;
import org.deletethis.hardcode.Hardcode;
import org.deletethis.hardcode.objects.ObjectInfo;
import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.graph.Graphviz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static void doIt(String name, Hardcode hardcoder, Object o) {
        Digraph<ObjectInfo> graph = hardcoder.buildGraph(o);
        new Graphviz<>(graph).highlight(v -> v.getPayload().isRoot()).print("target/" + name + ".gv");
        System.out.println(JavaFile.builder("foo.bar", hardcoder.createClass("Foo", "everything", graph)).build());
    }


    enum Enm {
        BAR,
        BAZ
    }

    public static void main(String[] args) throws IOException {
//        System.setOut(System.err);
        Hardcode hc = Hardcode.builtinConfig();

        doIt("hello", hc, "hello world\n");
        doIt("data", hc, new Data("hello", 1, 50L));


        Data data1 = new Data(null, 1, 50L);
        Data data2 = new Data("data2", 1, 49L);
        ArrayList<Data> arr = new ArrayList<>(2);
        arr.add(data1);
        arr.add(data1);
        doIt("simple-list", hc, arr);
        Enm enm = Enm.BAR;

        doIt("just-enum", hc, enm);
        doIt("ext-data", hc, new ExtData(true, "foo", 1, Long.MIN_VALUE));

        DefaultConfiguration dc = new DefaultConfiguration();
        dc.addHardcodeRoot(Data.class);
        Hardcode hc2 = Hardcode.builtinConfig(dc);

        Map<String, Object> map = new HashMap<>();
        ChildData cd1 = new ChildData("hello");
        ChildData cd2 = new ChildData("world");
        map.put("data1a", data1);
        map.put("data1b", data1);
        map.put("data2", data2);
        map.put("cd1a", cd1);
        map.put("cd1b", cd1);
        map.put("cd2", cd2);
        doIt("big-map", hc2, map);

        Map<String, String> aMap = new HashMap<>();
        aMap.put("a", "b");
        aMap.put("c", "d");
        doIt("child-map", hc2, new ChildMap(aMap));

/*
        ChildData d0 = new ChildData("something");
        ChildData d1 = new ChildData("middle", d0);
        ChildData d2 = new ChildData("parent", d1);
        ArrayList<ChildData> list = new ArrayList<>();
        list.add(d0);
        list.add(d1);
        list.add(d2);
        doIt("roots", hc, list);
*/


        //System.out.println(hc.method("foo", g));


        //compile error if we try to do so...
        //noinspection Convert2MethodRef
        //Splitter.getBComponents(() -> new MapDigraph<>(), g);

    }
}
