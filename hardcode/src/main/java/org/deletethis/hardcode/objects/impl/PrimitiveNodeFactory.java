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

public class PrimitiveNodeFactory implements NodeFactory {
    private <T> Optional<NodeDefinition> simple(Class<?> clz, T value, CodeBlock fn) {
        return Optional.of(NodeDefImpl.value(clz, String.valueOf(value), (context, obj) -> Expression.simple(fn)));
    }

    private <T> Optional<NodeDefinition> complex(Class<?> clz, T value, CodeBlock fn, Class<? extends Throwable> exc) {
        NodeDefImpl nodeDef = NodeDefImpl.value(clz, String.valueOf(value), (context, obj) -> Expression.complex(fn));
        if(exc != null)
            nodeDef.addFatalException(exc);
        return Optional.of(nodeDef);
    }

    private <T extends Number> Optional<NodeDefinition> floatOrDouble(Class<T> clz, T value, Function<T, Boolean> isNan, Function<T, Boolean> isInf, CodeBlock fn) {
        if(isNan.apply(value)) {
            return simple(clz, value, CodeBlock.of("$T.NaN", clz));
        } else if(isInf.apply(value) && value.doubleValue() < 0) {
            return simple(clz, value, CodeBlock.of("$T.NEGATIVE_INFINITY", clz));
        } else if(isInf.apply(value)) {
            return simple(clz, value, CodeBlock.of("$T.POSITIVE_INFINITY", clz));
        } else {
            // I believe printing is always exact, if not we might require more logic and covert bits to float
            return simple(clz, value, fn);
        }
    }


    @Override
    public Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration) {
        if(object.getClass().equals(Integer.class)) {
            return simple(Integer.class, object, CodeBlock.of("$L", object));
        }
        if(object.getClass().equals(Long.class)) {
            return simple(Long.class, object, CodeBlock.of("$LL", object));
        }

        if(object.getClass().equals(Float.class)) {
            float f = (float)object;
            return floatOrDouble(Float.class,  f, (a) -> a.isNaN(), (a) -> a.isInfinite(), CodeBlock.of("$Lf", f));
        }
        if(object.getClass().equals(Double.class)) {
            double f = (double)object;
            return floatOrDouble(Double.class,  f, (a) -> a.isNaN(), (a) -> a.isInfinite(), CodeBlock.of("$L", f));
        }
        if(object.getClass().equals(Boolean.class)) {
            return simple(Boolean.class, object, CodeBlock.of("$L", object));
        }
        if(object.getClass().equals(Byte.class)) {
            return simple(Byte.class, object, CodeBlock.of("(byte)$L", object));
        }
        if(object.getClass().equals(Short.class)) {
            return simple(Short.class, object, CodeBlock.of("(short)$L", object));
        }

        if(object.getClass().equals(Character.class)) {
            // no easy way to print single quite literal -- there's Util in JavaPoet, but it's not public
            return simple(Character.class, object, CodeBlock.of("(char)$L", (int)((Character)object)));
        }

        if(object.getClass().equals(String.class)) {
            return simple(String.class, object, CodeBlock.of("$S", object));
        }
        if(object instanceof Enum) {
            Class<?> clz = object.getClass();
            String name = ((Enum)object).name();
            return simple(clz, name, CodeBlock.of("$T.$L", clz, name));
        }
        if(object.getClass().equals(URI.class)) {
            return complex(URI.class, object, CodeBlock.of("new $T($S)", URI.class, object), URISyntaxException.class);
        }
        if(object.getClass().equals(URL.class)) {
            return complex(URL.class, object, CodeBlock.of("new $T($S)", URL.class, object), MalformedURLException.class);
        }
        if(object.getClass().equals(Date.class)) {
            return complex(Date.class, object, CodeBlock.of("new $T($L)", Date.class, ((Date)object).getTime()), null);
        }

        return Optional.empty();
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
