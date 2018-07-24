package org.deletethis.hardcode.objects;

import com.squareup.javapoet.CodeBlock;

import java.util.Iterator;
import java.util.List;

public interface CodegenParameters {
    interface Argument {
        Expression getExpression();
        ParameterName getName();
        default CodeBlock getCode() { return getExpression().getCode(); }
    }

    Integer getSplit();
    List<Argument> getArgumentList();
}
