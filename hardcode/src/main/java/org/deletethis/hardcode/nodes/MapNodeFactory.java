package org.deletethis.hardcode.nodes;

import org.deletethis.hardcode.codegen.CodegenContext;
import org.deletethis.hardcode.codegen.ConstructionStrategy;
import org.deletethis.hardcode.codegen.Expression;
import org.deletethis.hardcode.graph.Node;
import org.deletethis.hardcode.graph.NodeFactory;
import org.deletethis.hardcode.graph.NodeFactoryContext;

import java.util.*;

public class MapNodeFactory implements NodeFactory {
    final private static Set<Class> CLASSES_WITH_CAPACITY = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            HashMap.class, Hashtable.class, LinkedHashMap.class
    )));

    final private static Set<Class> CLASSES_WITHOUT_CAPACITY = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            TreeMap.class
    )));


    @Override
    public boolean enableReferenceDetection() {
        return true;
    }

    private static class EmitCode implements ConstructionStrategy {
        private final Class<?> clz;

        EmitCode(Class<?> clz) {
            this.clz = clz;
        }

        @Override
        public Expression getCode(CodegenContext context, List<Expression> arguments) {
            String variable = context.allocateVariable(clz.getSimpleName());
            if(CLASSES_WITH_CAPACITY.contains(clz)) {
                // arguments contain both keys and values, we create map with double initial capacitity
                // 1.33 should be enough at default load factor of of 0.75 but what the heck
                context.getBody().addStatement("$T $L = new $T($L)", clz, variable, clz, arguments.size());
            } else {
                context.getBody().addStatement("$T $L = new $T()", clz, variable, clz);
            }
            for(int i=0;i<arguments.size();i+=2) {
                context.getBody().addStatement("$L.put($L, $L)", variable, arguments.get(i).getCode(), arguments.get(i+1).getCode());
            }
            return Expression.simple(variable);
        }

        @Override
        public String toString() {
            return clz.getName();
        }
    }


    @Override
    public Optional<Node> createNode(NodeFactoryContext context, Object object) {
        Class<?> aClass = object.getClass();

        if(!CLASSES_WITH_CAPACITY.contains(aClass) && !CLASSES_WITHOUT_CAPACITY.contains(aClass))
            return Optional.empty();

        Map<?,?> coll = (Map<?,?>)object;
        List<Node> members = new ArrayList<>(coll.size());
        for(Map.Entry<?,?> o: coll.entrySet()) {
            members.add(context.getNode(o.getKey()));
            members.add(context.getNode(o.getValue()));
        }
        return Optional.of(new Node(aClass, members, new EmitCode(aClass)));
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
