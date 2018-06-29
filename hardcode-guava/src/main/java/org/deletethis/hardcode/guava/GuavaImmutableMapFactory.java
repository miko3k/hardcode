package org.deletethis.hardcode.guava;

import com.google.common.collect.*;
import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.objects.*;
import org.deletethis.hardcode.objects.impl.NodeDefImpl;
import org.deletethis.hardcode.util.SplitHelper;

import java.util.*;

public class GuavaImmutableMapFactory implements NodeFactory {
    private static final TypeInfos MAP_CLASSES = TypeInfos.of(
            ImmutableMap.class, ImmutableMap.Builder.class, 10
    );

    private static final TypeInfos MULTIMAP_CLASSES = TypeInfos.of(
            ImmutableListMultimap.class, ImmutableListMultimap.Builder.class, 10,
            ImmutableSetMultimap.class, ImmutableSetMultimap.Builder.class, 10
    );


    @Override
    public boolean enableReferenceDetection() {
        return false;
    }

    private Expression getCode(TypeInfo typeInfo, CodegenContext context, ObjectContext obj) {
        List<Expression> arguments = obj.getArguments();
        Class<?> clz = typeInfo.getType();
        Class<?> builder = typeInfo.getBuilder();

        // there are also longer variants, but's use builder for larger ones
        if (arguments.size() <= typeInfo.getOfMax()) {
            CodeBlock cb = GuavaUtil.printOf(clz, obj);
            return Expression.complex(cb);
        } else {
            String variable = context.allocateVariable(clz);

            context.addStatement("$T $L = $T.builder()", builder, variable, clz);
            SplitHelper splitHelper = SplitHelper.get(context, typeInfo.toString(), obj.getSplit(), variable, builder);

            for (int i = 0; i < arguments.size(); i += 2) {
                splitHelper.addStatement("$L.put($L, $L)", splitHelper.getBuilder(), arguments.get(i).getCode(), arguments.get(i + 1).getCode());
            }
            splitHelper.finish();

            return Expression.complex("$L.build()", variable);
        }
    }

    private Optional<NodeDefinition> createIt(TypeInfo typeInfo, Iterable<? extends Map.Entry<?,?>> entryIterable) {

        NodeDefImpl nodeDef = NodeDefImpl.value(
                typeInfo.getType(),
                typeInfo.toString(),
                (context, obj) -> getCode(typeInfo, context, obj));

        int idx = 0;
        for (Map.Entry<?, ?> e : entryIterable) {
            nodeDef.addParameter(new MapParameter(true, idx), e.getKey());
            nodeDef.addParameter(new MapParameter(false, idx), e.getValue());
        }
        ++idx;
        return Optional.of(nodeDef);
    }

    @Override
    public Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration) {
        TypeInfo aClass = MAP_CLASSES.find(object);
        if(aClass != null) {
            return createIt(aClass, ((Map<?, ?>) object).entrySet());
        }

        aClass = MULTIMAP_CLASSES.find(object);
        if(aClass != null) {
            return createIt(aClass, ((Multimap<?, ?>) object).entries());
        }

        return Optional.empty();
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
