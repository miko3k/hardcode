package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;

import java.util.List;

public interface ConstructionStrategy {
    Expression getCode(Class<?> clz, CodegenContext context, List<Expression> arguments);
}
