package org.deletethis.hardcode.impl;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.NameAllocator;
import org.deletethis.graph.Digraph;
import org.deletethis.graph.Divertex;
import org.deletethis.hardcode.ObjectInfo;

import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Printer implements CodegenContext {
    private final CodeBlock.Builder body;
    private final NameAllocator nameAllocator = new NameAllocator();
    private final Map<Divertex, Expression> exprMap = new HashMap<>();
    private Map<String, Integer> variableNumbers = new HashMap<>();

    private Printer(CodeBlock.Builder body) {
        this.body = body;
    }

    
    @Override
    public String allocateVariable(String hint) {
        // we allocate plenty of variables with the same name,
        // let's add a number before NameAllocator starts adding underscores
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

    private Expression print(CodegenContext context, Divertex<ObjectInfo> n) {
        Expression expression = exprMap.get(n);
        if(expression != null) {
            return expression;
        }

        List<Expression> args = new ArrayList<>();

        for(Divertex<ObjectInfo> a: n.getSuccessors()) {
            args.add(print(context, a));
        }
        ObjectInfo objectInfo = n.getPayload();
        expression = objectInfo.getCode(context, args);

        if(n.getOutDegree() > 1 && !expression.isSimple()) {
            String var = allocateVariable(objectInfo.getType().getSimpleName());
            body.addStatement("$T $L = $L", objectInfo.getType(), var, expression.getCode());
            expression = Expression.simple(var);
            exprMap.put(n, expression);
        }
        return expression;
    }

    private Expression print(Digraph<ObjectInfo> graph) {

        return print(this, graph.getRoot());
    }

    public static Expression print(CodeBlock.Builder body, Digraph<ObjectInfo> graph) {
        Printer p = new Printer(body);
        return p.print(graph);
    }
}