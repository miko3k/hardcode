package org.deletethis.hardcode;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.deletethis.hardcode.graph.Digraph;
import org.deletethis.hardcode.impl.GraphBuilder;
import org.deletethis.hardcode.impl.GraphVerifier;
import org.deletethis.hardcode.impl.Printer;
import org.deletethis.hardcode.objects.NodeFactory;
import org.deletethis.hardcode.impl.ObjectInfo;
import org.deletethis.hardcode.objects.ParameterName;
import org.deletethis.hardcode.objects.impl.CollectionNodeFactory;
import org.deletethis.hardcode.objects.impl.ObjectNodeFactory;
import org.deletethis.hardcode.objects.impl.PrimitiveNodeFactory;

import java.util.*;
import org.deletethis.hardcode.objects.impl.MapNodeFactory;

/**
 * A class to hardcode structures into a code. For input {@link Object}, it is able to generate source code do
 * build such an object as a {@link TypeSpec}.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Hardcode {
    private final List<NodeFactory> nodeFactoryList;
    private final HardcodeConfiguration configuration;

    private Hardcode(HardcodeConfiguration configuration, List<NodeFactory> nodeFactoryList) {
        this.nodeFactoryList = new ArrayList<>(nodeFactoryList);
        this.nodeFactoryList.addAll(configuration.getAdditionalNodeFactories());
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

    public static Hardcode customConfig(HardcodeConfiguration c) {
        return new Hardcode(c, Collections.emptyList());
    }

    public static Hardcode defaultConfig() {
        return new Hardcode(c(), def());
    }

    public static Hardcode defaultConfig(HardcodeConfiguration c) {
        return new Hardcode(c, def());
    }

    public Digraph<ObjectInfo, ParameterName> buildGraph(Object root) {
        return GraphBuilder.buildGraph(nodeFactoryList, configuration, root);
    }

    public void verifyGraph(Digraph<ObjectInfo, ParameterName> graph) {
        new GraphVerifier(graph).verify();
    }

    public TypeSpec createClass(String className, Object o) {
        return createClassFromGraph(className, buildGraph(o));
    }

    public JavaFile createJavaFile(String packageName, String className, Object o) {
        return JavaFile.builder(packageName, createClass(className, o)).build();
    }

    public TypeSpec createClassFromGraph(String className, Digraph<ObjectInfo, ParameterName> graph) {
        verifyGraph(graph);
        return new Printer(className, graph).run(configuration.generateSupplier());
    }

    public JavaFile createJavaFileFromGraph(String packageName, String className, Digraph<ObjectInfo, ParameterName> graph) {
        return JavaFile.builder(packageName, createClassFromGraph(className, graph)).build();
    }
}
