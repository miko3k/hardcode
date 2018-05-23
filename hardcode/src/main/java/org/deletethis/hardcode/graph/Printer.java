package org.deletethis.hardcode.graph;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.NameAllocator;
import org.deletethis.hardcode.CodegenContext;
import org.deletethis.hardcode.Expression;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Printer implements CodegenContext {
    private final CodeBlock.Builder body;
    private final NameAllocator nameAllocator = new NameAllocator();
    private final Map<Node, Expression> exprMap = new HashMap<>();
    Map<String, Integer> variableNumbers = new HashMap<>();

    public Printer(CodeBlock.Builder body) {
        this.body = body;
    }

    
    @Override
    public String allocateVariable(String hint) {
        // we allocate plenty of same variables, let's add a number before
        // adding underscores
        String s = Introspector.decapitalize(hint);
        String base;
        Integer n = variableNumbers.get(s);
        if(n == null) {
            base = s;
            n = 1;
        } else {
            base = s + "_" + n;
            ++n;
        }
        variableNumbers.put(s, n);
        
        return nameAllocator.newName(base);
    }

    @Override
    public CodeBlock.Builder getBody() {
        return body;
    }

    private Expression print(CodegenContext context, Node n) {
        Expression expression = exprMap.get(n);
        if(expression != null) {
            return expression;
        }

        List<Expression> args = new ArrayList<>();

        for(Node a: n.getParameters()) {
            args.add(print(context, a));
        }
        expression = n.getConstructor().getCode(context, args);

        if(n.getRefCount() > 1 && !expression.isSimple()) {
            String var = allocateVariable(n.getType().getSimpleName());
            body.addStatement("$T $L = $L", n.getType(), var, expression.getCode());
            expression = Expression.simple(var);
            exprMap.put(n, expression);
        }
        return expression;
    }

    public Expression print(Graph graph) {
        return print(this, graph.getRoot());
    }
}
