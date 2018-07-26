package org.deletethis.hardcode.guava;

import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.CodegenParameters;

class GuavaUtil {
    static CodeBlock printOf(Class<?> what, CodegenParameters obj, String className) {
        CodeBlock.Builder cb = CodeBlock.builder();
        cb.add("$T.of(", what);
        boolean first = true;
        for (CodegenParameters.Argument e : obj.getArgumentList()) {
            if (first) {
                first = false;
            } else {
                cb.add(",");
            }
            cb.add(e.getCode(className));
        }
        cb.add(")");
        return cb.build();
    }
}
