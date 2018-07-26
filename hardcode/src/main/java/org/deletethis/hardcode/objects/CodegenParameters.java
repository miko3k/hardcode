package org.deletethis.hardcode.objects;

import com.squareup.javapoet.CodeBlock;

import java.util.Iterator;
import java.util.List;

public interface CodegenParameters {
    interface Argument extends Expression {
        ParameterName getName();
    }

    Integer getSplit();
    List<Argument> getArgumentList();
}
