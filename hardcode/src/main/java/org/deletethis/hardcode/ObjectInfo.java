package org.deletethis.hardcode;

import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;

import java.util.List;

public interface ObjectInfo {
    Class<?> getType();
    Expression getCode(CodegenContext context, List<Expression> arguments);
    boolean isRoot();
    @Override
    String toString();
}
