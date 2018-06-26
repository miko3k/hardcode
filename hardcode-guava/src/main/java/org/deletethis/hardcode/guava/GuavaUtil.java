package org.deletethis.hardcode.guava;

import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.ObjectContext;

class GuavaUtil {
    static CodeBlock printOf(Class<?> what, ObjectContext obj) {
        CodeBlock.Builder cb = CodeBlock.builder();
        cb.add("$T.of(", what);
        boolean first = true;
        for (Expression e : obj.getArguments()) {
            if (first) {
                first = false;
            } else {
                cb.add(",");
            }
            cb.add(e.getCode());
        }
        cb.add(")");
        return cb.build();
    }
}
