package org.deletethis.hardcode.objects;

import java.util.List;

public interface ConstructionStrategy {
    Expression getCode(CodegenContext context, List<Expression> arguments);
}
