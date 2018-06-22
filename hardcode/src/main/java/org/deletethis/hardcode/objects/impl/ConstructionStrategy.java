package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.ObjectContext;

public interface ConstructionStrategy {
    Expression getCode(CodegenContext context, ObjectContext obj);
}
