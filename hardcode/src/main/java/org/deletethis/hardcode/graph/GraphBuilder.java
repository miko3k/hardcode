package org.deletethis.hardcode.graph;

import org.deletethis.hardcode.objects.*;

import java.util.*;

public class GraphBuilder {
    private final List<NodeFactory> nodeFactories;
    private final Map<Object, Node> objectMap = new IdentityHashMap<>();
    private final Set<Node> allNodes = new HashSet<>();
    private final Set<Object> objectsInProgress = Collections.newSetFromMap(new IdentityHashMap<>());

    public GraphBuilder(List<NodeFactory> nodeFactories) {
        this.nodeFactories = nodeFactories;
    }

    private static final ObjectInfo NULL = new ObjectInfo() {
        @Override
        public Class<?> getType() {
            return null;
        }

        @Override
        public Expression getCode(CodegenContext context, List<Expression> arguments) {
            return Expression.simple("null");
        }

        @Override
        public String toString() {
            return "null";
        }
    };

    public Node createNode(Object o) {
        //System.out.println("NODE: " + o);
        
        if(o == null) {
            Node n = new Node(NULL, Collections.emptyList());
            allNodes.add(n);
            return n;
        }

        if(!objectsInProgress.add(o)) {
            throw new IllegalStateException("cycle detected");
        }
        try {
            Node n = objectMap.get(o);
            if (n != null) {
                return n;
            }

            for(NodeFactory factory: nodeFactories) {
                Optional<NodeDef> nodeOptional = factory.createNode(o);
                if(nodeOptional.isPresent()) {
                    NodeDef nodeDef = nodeOptional.get();

                    List<Node> list = new ArrayList<>(nodeDef.getParameters().size());
                    for(Object param: nodeDef.getParameters()) {
                        list.add(createNode(param));
                    }

                    Node node = new Node(nodeDef.getObjectInfo(), list);

                    if(factory.enableReferenceDetection()) {
                        objectMap.put(o, node);
                    }
                    for(Node param: node.getSuccessors()) {
                        param.addPredecessor(node);
                    }
                    allNodes.add(node);
                    return node;
                }
            }
            throw new IllegalArgumentException();
        } finally {
            objectsInProgress.remove(o);
        }
    }

    public Graph buildGraph(Object o) {
        Node n = createNode(o);
        if(!objectsInProgress.isEmpty()) {
            throw new IllegalStateException("something left in progress?");
        }
        return new Graph(n, allNodes);
    }
}
