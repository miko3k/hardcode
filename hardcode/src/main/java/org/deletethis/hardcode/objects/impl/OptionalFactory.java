package org.deletethis.hardcode.objects.impl;

import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.objects.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

public class OptionalFactory implements NodeFactory, CodeGenerator {
    private static final String VALUE = "value";

    @Override
    public Expression getCode(CodegenContext context, CodegenParameters obj) {
        CodeBlock cb;
        if(obj.getArgumentList().isEmpty()) {
            cb = CodeBlock.of("$T.empty()", Optional.class);
        } else {
            cb = CodeBlock.of("$T.of($L)", Optional.class, obj.getArgumentList().get(0).getCode(context.getClassName()));
        }
        return Expression.complex(cb);
    }

    @Override
    public Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration) {
        if(!object.getClass().equals(Optional.class)) {
            return Optional.empty();
        }

        NodeDefImpl nf = NodeDefImpl.value(Optional.class, "Optional", this);
        Optional<?> opt = (Optional<?>) object;
        opt.ifPresent(o -> nf.addParameter(new NamedParameter(VALUE), o));
        return Optional.of(nf);
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
