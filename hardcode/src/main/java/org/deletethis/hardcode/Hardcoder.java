package org.deletethis.hardcode;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.NameAllocator;
import org.deletethis.hardcode.graph.*;
import org.deletethis.hardcode.nodes.CollectionNodeFactory;
import org.deletethis.hardcode.nodes.ObjectNodeFactory;
import org.deletethis.hardcode.nodes.PrimitiveNodeFactory;

import javax.lang.model.element.Modifier;
import java.util.*;
import org.deletethis.hardcode.nodes.MapNodeFactory;

public class Hardcoder {
    private final List<NodeFactory> nodeFactoryList;

    private Hardcoder(List<NodeFactory> nodeFactoryList) {
        this.nodeFactoryList = new ArrayList<>(nodeFactoryList);
        this.nodeFactoryList.sort(Comparator.comparing(NodeFactory::getOrdering));
    }

    private Hardcoder(List<NodeFactory> a, List<NodeFactory> b) {
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

    private static  ArrayList<NodeFactory> def() {
        ServiceLoader<NodeFactory> nodeFactoriesLoader
                = ServiceLoader.load(NodeFactory.class);

        ArrayList<NodeFactory> list = new ArrayList<>();
        for (NodeFactory nf : nodeFactoriesLoader) {
            list.add(nf);
        }
        return list;
    }

    public static Hardcoder builtinConfig() {
        return new Hardcoder(builtin());
    }

    public static Hardcoder builtinConfig(List<NodeFactory> nodeFactories) {
        return new Hardcoder(builtin(), nodeFactories);
    }

    public static Hardcoder customConfig(List<NodeFactory> nodeFactories) {
        return new Hardcoder(nodeFactories);
    }

    public static Hardcoder defaultConfig() {
        return new Hardcoder(def());
    }

    public static Hardcoder defaultConfig(List<NodeFactory> nodeFactories) {
        return new Hardcoder(def(), nodeFactories);
    }

    private class Run {
        private final Graph graph;
        private final Expression expr;

        public Run(CodeBlock.Builder body, Object o) {
            GraphBuilder graphBuilderCore = new GraphBuilder(nodeFactoryList);
            graph = graphBuilderCore.buildGraph(o);
            //graph.printNodes(System.out);

            Printer p = new Printer(body);
            expr = p.print(graph);
        }
    }


    public CodeBlock value(CodeBlock.Builder body, Object o) {
        Run r = new Run(body, o);
        return r.expr.getCode();
    }

    public MethodSpec method(String name, Object o) {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(SuppressWarnings.class);
        builder.addMember("value", "$S", "unchecked");

        MethodSpec.Builder ms = MethodSpec.methodBuilder(name);
        ms.addModifiers(Modifier.PUBLIC);
        ms.addAnnotation(builder.build());
        CodeBlock.Builder bld = CodeBlock.builder();

        Run r = new Run(bld, o);
        Class<?> type = r.graph.getRoot().getType();
        if(type == null) {
            ms.returns(Object.class);
        } else {
            ms.returns(type);
        }

        bld.addStatement("return $L", r.expr.getCode());
        ms.addCode(bld.build());
        return ms.build();
    }
    }
