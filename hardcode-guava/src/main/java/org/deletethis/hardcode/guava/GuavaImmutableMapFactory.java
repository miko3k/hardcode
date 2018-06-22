package org.deletethis.hardcode.guava;

import com.google.common.collect.*;
import com.squareup.javapoet.CodeBlock;
import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.HardcodeException;
import org.deletethis.hardcode.HardcodeSplit;
import org.deletethis.hardcode.objects.*;
import org.deletethis.hardcode.objects.impl.NodeDefImpl;

import java.lang.annotation.Annotation;
import java.util.*;

public class GuavaImmutableMapFactory implements NodeFactory {
    private static class TypeInfo {
        private final Class<?> type;
        private final Class<?> builder;

        TypeInfo(Class<?> type, Class<?> builder) {
            this.type = type;
            this.builder = builder;
        }

        Class<?> getType() {
            return type;
        }

        Class<?> getBuilder() {
            return builder;
        }

        boolean matches(Object o) {
            return type.isInstance(o);
        }
    }

    private static final Set<TypeInfo> MAP_CLASSES = ImmutableSet.of(
            new TypeInfo(ImmutableMap.class, ImmutableMap.Builder.class)
    );

    private static final Set<TypeInfo> MULTIMAP_CLASSES = ImmutableSet.of(
            new TypeInfo(ImmutableListMultimap.class, ImmutableListMultimap.Builder.class),
            new TypeInfo(ImmutableSetMultimap.class, ImmutableSetMultimap.Builder.class)
    );


    private TypeInfo findMapClass(Object o) {
        for (TypeInfo c : MAP_CLASSES) {
            if (c.matches(o))
                return c;
        }
        return null;
    }

    private TypeInfo findMultimapClass(Object o) {
        for (TypeInfo c : MULTIMAP_CLASSES) {
            if (c.matches(o))
                return c;
        }
        return null;
    }


    @Override
    public boolean enableReferenceDetection() {
        return false;
    }

    private static final String PARAM = "out";


    private Expression getCode(TypeInfo typeInfo, CodegenContext context, ObjectContext obj, Integer split) {
        List<Expression> arguments = obj.getArguments();
        Class<?> clz = typeInfo.getType();
        Class<?> builder = typeInfo.getBuilder();

        // there are also longer variants, but's use builder for larger ones
        if (arguments.size() <= 10) {
            CodeBlock cb = GuavaUtil.printOf(clz, obj);
            return Expression.complex(cb);
        } else {
            String variable = context.allocateVariable(clz);

            context.addStatement("$T $L = $T.builder()", builder, variable, clz);

            if(split == null) {
                for(int i=0;i<arguments.size();i+=2) {
                    context.addStatement("$L.put($L, $L)", variable, arguments.get(i).getCode(), arguments.get(i + 1).getCode());
                }
            } else {
                // FIXME: verify if argument (+ ALL CHILDREN) is only referenced here
                int n = 0;
                while(n < arguments.size()) {
                    CodegenContext ctx = context.createVoidMethod("createSub" + clz.getSimpleName(), builder, PARAM);

                    for (int i = 0; i < split && n < arguments.size(); ++i) {
                        ctx.addStatement("$L.put($L, $L)", PARAM, arguments.get(n).getCode(), arguments.get(n + 1).getCode());
                        n += 2;
                    }
                    context.addStatement("$L($L)", ctx.getMethodName(), variable);
                    ctx.finish();
                }
            }
            return Expression.complex("$L.build()", variable);
        }
    }

    private Optional<NodeDefinition> createIt(TypeInfo typeInfo, Iterable<? extends Map.Entry<?,?>> entryIterable, Integer split) {

        List<NodeParameter> members = new ArrayList<>();
        int idx = 0;
        for (Map.Entry<?, ?> e : entryIterable) {
            members.add(new NodeParameter(new MapParameter(true, idx), e.getKey()));
            members.add(new NodeParameter(new MapParameter(false, idx), e.getValue()));
        }
        ++idx;
        return Optional.of(new NodeDefImpl(
                typeInfo.getType(),
                typeInfo.getType().getSimpleName(),
                (context, obj) -> getCode(typeInfo, context, obj, split),
                members)
        );
    }

    @Override
    public Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration, List<Annotation> annotations) {
        Integer split = null;
        for(Annotation a: annotations) {
            if(a instanceof HardcodeSplit) {
                split = ((HardcodeSplit)a).value();
                if(split <= 0)
                    throw new HardcodeException("illegal split: " + split);
            }
        }

        TypeInfo aClass = findMapClass(object);
        if(aClass != null) {
            return createIt(aClass, ((Map<?, ?>) object).entrySet(), split);
        }

        aClass = findMultimapClass(object);
        if(aClass != null) {
            return createIt(aClass, ((Multimap<?, ?>) object).entries(), split);
        }

        return Optional.empty();
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
