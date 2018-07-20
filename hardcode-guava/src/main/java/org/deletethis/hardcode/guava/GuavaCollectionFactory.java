package org.deletethis.hardcode.guava;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.objects.*;
import org.deletethis.hardcode.objects.impl.NodeDefImpl;
import org.deletethis.hardcode.util.SplitHelper;

import java.util.*;

public class GuavaCollectionFactory implements NodeFactory {
    private static final TypeInfos CLASSES = TypeInfos.of(
            ImmutableList.class, ImmutableList.Builder.class, 12,
            ImmutableSet.class, ImmutableSet.Builder.class, 6
    );

    private Expression getCode(TypeInfo typeInfo, CodegenContext context, CodegenParameters obj) {
        if (obj.getArguments().size() < typeInfo.getOfMax()) {
            CodeBlock cb = GuavaUtil.printOf(typeInfo.getType(), obj);
            return Expression.complex(cb);
        } else {
            String variable = context.allocateVariable(typeInfo.getType());
            context.addStatement("$T $L = $T.builderWithExpectedSize($L)", typeInfo.getBuilder(), variable, typeInfo.getType(), obj.getArguments().size());

            SplitHelper splitHelper = SplitHelper.get(context, typeInfo.toString(), obj.getSplit(), variable, typeInfo.getBuilder());
            for (Expression arg : obj.getArguments()) {
                splitHelper.addStatement("$L.add($L)", variable, arg.getCode());
            }
            splitHelper.finish();
            return Expression.complex("$L.build()", variable);
        }
    }


    @Override
    public Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration) {
        TypeInfo typeInfo = CLASSES.find(object);

        if (typeInfo == null)
            return Optional.empty();

        NodeDefImpl nodeDef = NodeDefImpl.value(typeInfo.getType(), typeInfo.toString(), (context, obj) -> getCode(typeInfo, context, obj));

        Collection<?> coll = (Collection<?>) object;
        int idx = 0;
        for(Object o: coll) {
            nodeDef.addParameter(new IndexParamteter(idx), o);
            ++idx;
        }
        return Optional.of(nodeDef);
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
