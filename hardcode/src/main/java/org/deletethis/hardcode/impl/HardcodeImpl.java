package org.deletethis.hardcode.impl;

import com.squareup.javapoet.TypeSpec;
import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.objects.NodeFactory;
import org.deletethis.hardcode.objects.ParameterName;

import java.util.List;

public class HardcodeImpl {
    public static Digraph<ObjectInfo, ParameterName> buildGraph(List<NodeFactory> nodeFactories, HardcodeConfiguration configuration, Object o) {
        GraphBuilder gb = new GraphBuilder(nodeFactories, configuration);

        gb.createNode(o, new BuiltinAnnotations());
        if(!gb.isInProgressEmpty()) {
            throw new IllegalStateException("something left in progress?");
        }
        return gb.getGraph();
    }

    public static void verify(Digraph<ObjectInfo, ParameterName> digraph) {
        new GraphVerifier(digraph).verify();
    }

    public static TypeSpec print(String className, HardcodeConfiguration config, Digraph<ObjectInfo, ParameterName> digraph) {
        return new Printer(className, digraph, null).run(config.generateSupplier()).get(0);
    }

    public static List<TypeSpec> printLimited(String className, HardcodeConfiguration config, Digraph<ObjectInfo, ParameterName> digraph) {
        return new Printer(className, digraph, config.getMethodLineCountHint()).run(config.generateSupplier());
    }

}
