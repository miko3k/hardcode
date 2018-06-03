package org.deletethis.hardcode.test;

import org.deletethis.graph.Dag;
import org.deletethis.graph.graphviz.Graphviz;
import org.deletethis.graph.MapDag;
import org.deletethis.hardcode.Hardcode;
import org.deletethis.hardcode.ObjectInfo;
import org.deletethis.graph.algo.BComponent;
import org.deletethis.graph.algo.DagAlgorithms;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static void doIt(Hardcode hardcoder, Object o) {
        Dag<ObjectInfo> graph = hardcoder.buildGraph(o);
        DagAlgorithms.printNodes(graph, System.out);
        System.out.println(hardcoder.method("foo", graph));
    }


    enum Enm {
        BAR,
        BAZ
    }

    public static void main(String[] args) throws IOException {
        Hardcode hc = Hardcode.builtinConfig();

        doIt(hc, "hello world\n");
        doIt(hc, new Data("hello", 1, 50L));


        Data data = new Data(null, 1, 50L);
        ArrayList<Data> arr = new ArrayList<>(2);
        arr.add(data);
        arr.add(data);
        doIt(hc, arr);
        Enm enm = Enm.BAR;

        doIt(hc, enm);
        doIt(hc, new ExtData(true, "foo", 1, Long.MIN_VALUE));

        Map<String, Data> map = new HashMap<>();
        map.put("data1", data);
        map.put("data2", data);
        doIt(hc, map);

        Node n1 = new Node("1");
        Node n2 = new Node("2");
        Node n3 = new Node("3");
        Node n4 = new Node("4");
        Node n5 = new Node("5");
        Node n6 = new Node("6");
        Node n7 = new Node("7");
        Node n8 = new Node("8");

        n2.add(n1,n3,n4,n5);
        n1.add(n3);
        n5.add(n4, n6);
        n4.add(n6);
        n6.add(n7, n8);
        n7.add(n8);

        Dag<ObjectInfo> g = hc.buildGraph(n2);
        new Graphviz<>(g).highlight(DagAlgorithms.findArticulationPoints(g.getRoot(), false)).print("target/aa.gv");
        System.out.println(hc.method("foo", g));

        Dag<BComponent<ObjectInfo>> bComponents = DagAlgorithms.getBComponents(new MapDag<>(), g);
        new Graphviz<>(bComponents).print("target/aaa.gv");
    }
}
