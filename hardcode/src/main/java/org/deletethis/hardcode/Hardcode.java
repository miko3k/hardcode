package org.deletethis.hardcode;

import com.squareup.javapoet.TypeSpec;
import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.impl.GraphBuilder;
import org.deletethis.hardcode.impl.Printer;
import org.deletethis.hardcode.objects.NodeFactory;
import org.deletethis.hardcode.objects.ObjectInfo;
import org.deletethis.hardcode.objects.impl.CollectionNodeFactory;
import org.deletethis.hardcode.objects.impl.ObjectNodeFactory;
import org.deletethis.hardcode.objects.impl.PrimitiveNodeFactory;

import javax.lang.model.element.Modifier;
import java.util.*;
import org.deletethis.hardcode.objects.impl.MapNodeFactory;

public class Hardcode {
    private final List<NodeFactory> nodeFactoryList;
    private final HardcodeConfiguration configuration;

    private Hardcode(HardcodeConfiguration configuration, List<NodeFactory> nodeFactoryList) {
        this.nodeFactoryList = new ArrayList<>(nodeFactoryList);
        this.nodeFactoryList.sort(Comparator.comparing(NodeFactory::getOrdering));
        this.configuration = configuration;
    }

    private Hardcode(HardcodeConfiguration configuration, List<NodeFactory> a, List<NodeFactory> b) {
        this.nodeFactoryList = new ArrayList<>(a);
        this.nodeFactoryList.addAll(b);
        this.nodeFactoryList.sort(Comparator.comparing(NodeFactory::getOrdering));
        this.configuration = configuration;
    }

    private static HardcodeConfiguration c() {
        return new DefaultConfiguration();
    }


    private static ArrayList<NodeFactory> builtin() {
        ArrayList<NodeFactory> nodeFactoryList = new ArrayList<>();
        nodeFactoryList.add(new PrimitiveNodeFactory());
        nodeFactoryList.add(new CollectionNodeFactory());
        nodeFactoryList.add(new MapNodeFactory());
        nodeFactoryList.add(new ObjectNodeFactory());
        return nodeFactoryList;
    }

    private static ArrayList<NodeFactory> def() {
        ServiceLoader<NodeFactory> nodeFactoriesLoader
                = ServiceLoader.load(NodeFactory.class);

        ArrayList<NodeFactory> list = new ArrayList<>();
        for (NodeFactory nf : nodeFactoriesLoader) {
            list.add(nf);
        }
        return list;
    }

    public static Hardcode builtinConfig() {
        return new Hardcode(c(), builtin());
    }

    public static Hardcode builtinConfig(HardcodeConfiguration c) {
        return new Hardcode(c, builtin());
    }

    public static Hardcode builtinConfig(List<NodeFactory> nodeFactories) {
        return new Hardcode(c(), builtin(), nodeFactories);
    }

    public static Hardcode builtinConfig(HardcodeConfiguration c, List<NodeFactory> nodeFactories) {
        return new Hardcode(c, builtin(), nodeFactories);
    }

    public static Hardcode customConfig(List<NodeFactory> nodeFactories) {
        return new Hardcode(c(), nodeFactories);
    }

    public static Hardcode customConfig(HardcodeConfiguration c, List<NodeFactory> nodeFactories) {
        return new Hardcode(c, nodeFactories);
    }

    public static Hardcode defaultConfig() {
        return new Hardcode(c(), def());
    }

    public static Hardcode defaultConfig(HardcodeConfiguration c) {
        return new Hardcode(c, def());
    }

    public static Hardcode defaultConfig(List<NodeFactory> nodeFactories) {
        return new Hardcode(c(), def(), nodeFactories);
    }

    public static Hardcode defaultConfig(HardcodeConfiguration c, List<NodeFactory> nodeFactories) {
        return new Hardcode(c, def(), nodeFactories);
    }

    public Digraph<ObjectInfo> buildGraph(Object root) {
        return GraphBuilder.buildGraph(nodeFactoryList, configuration, root);
    }

    public TypeSpec createClass(String className, String methodName, Object o) {
        return createClass(className, methodName, buildGraph(o));
    }

    public TypeSpec createClass(String className, String methodName, Digraph<ObjectInfo> graph) {
        TypeSpec.Builder result = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        new Printer(result, graph).run(methodName);

        return result.build();
    }
}
