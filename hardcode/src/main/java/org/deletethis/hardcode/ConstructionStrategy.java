package org.deletethis.hardcode;

import java.util.List;

public interface ConstructionStrategy {
    Expression getCode(CodegenContext context, List<Expression> arguments);
}
