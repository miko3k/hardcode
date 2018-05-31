package org.deletethis.hardcode;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.NodeFactory;
import org.deletethis.hardcode.graph.*;
import org.deletethis.hardcode.objects.impl.CollectionNodeFactory;
import org.deletethis.hardcode.objects.impl.ObjectNodeFactory;
import org.deletethis.hardcode.objects.impl.PrimitiveNodeFactory;

import javax.lang.model.element.Modifier;
import java.util.*;
import org.deletethis.hardcode.objects.impl.MapNodeFactory;

public class Hardcode {
    private final List<NodeFactory> nodeFactoryList;

    private Hardcode(List<NodeFactory> nodeFactoryList) {
        this.nodeFactoryList = new ArrayList<>(nodeFactoryList);
        this.nodeFactoryList.sort(Comparator.comparing(NodeFactory::getOrdering));
    }

    private Hardcode(List<NodeFactory> a, List<NodeFactory> b) {
        this.nodeFactoryList = new ArrayList<>(a);
        this.nodeFactoryList.addAll(b);
        this.nodeFactoryList.sort(Comparator.comparing(NodeFactory::getOrdering));

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
        return new Hardcode(builtin());
    }

    public static Hardcode builtinConfig(List<NodeFactory> nodeFactories) {
        return new Hardcode(builtin(), nodeFactories);
    }

    public static Hardcode customConfig(List<NodeFactory> nodeFactories) {
        return new Hardcode(nodeFactories);
    }

    public static Hardcode defaultConfig() {
        return new Hardcode(def());
    }

    public static Hardcode defaultConfig(List<NodeFactory> nodeFactories) {
        return new Hardcode(def(), nodeFactories);
    }

    public Graph buildGraph(Object root) {
        GraphBuilder graphBuilderCore = new GraphBuilder(nodeFactoryList);
        return graphBuilderCore.buildGraph(root);
    }

    public CodeBlock value(CodeBlock.Builder body, Object o) {
        Graph graph = buildGraph(o);
        Printer p = new Printer(body);
        return p.print(graph).getCode();
    }

    public MethodSpec method(String name, Graph graph) {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(SuppressWarnings.class);
        builder.addMember("value", "$S", "unchecked");

        MethodSpec.Builder ms = MethodSpec.methodBuilder(name);
        ms.addModifiers(Modifier.PUBLIC);
        ms.addAnnotation(builder.build());
        CodeBlock.Builder bld = CodeBlock.builder();

        Printer p = new Printer(bld);
        Expression expression = p.print(graph);

        Class<?> type = graph.getRoot().getObjectInfo().getType();
        if(type == null) {
            ms.returns(Object.class);
        } else {
            ms.returns(type);
        }

        bld.addStatement("return $L", expression.getCode());
        ms.addCode(bld.build());
        return ms.build();
    }

    public MethodSpec method(String name, Object o) {
        return method(name, buildGraph(o));
    }
}
