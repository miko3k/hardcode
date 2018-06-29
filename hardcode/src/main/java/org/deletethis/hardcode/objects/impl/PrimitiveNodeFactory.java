package org.deletethis.hardcode.objects.impl;

import com.squareup.javapoet.CodeBlock;

import org.deletethis.hardcode.HardcodeConfiguration;
import org.deletethis.hardcode.objects.*;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

public class PrimitiveNodeFactory implements NodeFactory {
    @Override
    public boolean enableReferenceDetection() {
        return false;
    }

    private <T> Optional<NodeDefinition> simple(Class<?> clz, T value, Function<T, CodeBlock> fn) {
        return Optional.of(new NodeDefImpl(
                clz,
                String.valueOf(value),
                (context, obj) -> Expression.simple(fn.apply(value))));
    }

    private <T> Optional<NodeDefinition> complex(Class<?> clz, T value, Function<T, CodeBlock> fn) {
        return complex(clz, value, fn, null);
    }

    private <T> Optional<NodeDefinition> complex(Class<?> clz, T value, Function<T, CodeBlock> fn, Class<?> exc) {
        NodeDefImpl nodeDef = new NodeDefImpl(
                clz,
                String.valueOf(value),
                (context, obj) -> Expression.complex(fn.apply(value)));
        if(exc != null)
            nodeDef.addFatalException(exc);
        return Optional.of(nodeDef);
    }

    
    @Override
    public Optional<NodeDefinition> createNode(Object object, HardcodeConfiguration configuration) {
        if(object.getClass().equals(Integer.class)) {
            return simple(Integer.class, object, (val)->CodeBlock.of("$L", val));
        }
        if(object.getClass().equals(Long.class)) {
            return simple(Long.class, object, (val)->CodeBlock.of("$LL", val));
        }
        if(object.getClass().equals(Float.class)) {
            float f = (float)object;
            String str = String.valueOf(f);
            if(f == Float.parseFloat(str)) {
                return simple(Float.class, object, (val)->CodeBlock.of("$Lf", val));
            } else {
                int integer = Float.floatToIntBits(f);
                return complex(Float.class, object, (val)->CodeBlock.of("$T.intBitsToFloat($L)", Float.class, integer));
            }
        }
        if(object.getClass().equals(Double.class)) {
            double f = (double)object;
            String str = String.valueOf(f);
            if(f == Double.parseDouble(str)) {
                return simple(Double.class, object, (val)->CodeBlock.of("$L", val));
            } else {
                long integer = Double.doubleToLongBits(f);
                return complex(Double.class, object, (val)->CodeBlock.of("$T.longBitsToDouble($L)", Float.class, integer));
            }
        }
        if(object.getClass().equals(Boolean.class)) {
            return simple(Boolean.class, object, (val)->CodeBlock.of("$L", val));
        }
        if(object.getClass().equals(Byte.class)) {
            return simple(Byte.class, object, (val)->CodeBlock.of("(byte)$L", val));
        }
        if(object.getClass().equals(Short.class)) {
            return simple(Short.class, object, (val)->CodeBlock.of("(short)$L", val));
        }

        if(object.getClass().equals(Character.class)) {
            // no easy way to print single quite literal -- there's Util in JavaPoet, but it's not public
            return simple(Character.class, object, (val)->CodeBlock.of("(char)$L", (int)((Character)val)));
        }

        if(object.getClass().equals(String.class)) {
            return simple(String.class, object, (val)->CodeBlock.of("$S", val));
        }
        if(object instanceof Enum) {
            Class<?> clz = object.getClass();
            return simple(clz, object, (val)->CodeBlock.of("$T.$L", clz, val));
        }
        if(object.getClass().equals(URI.class)) {
            return complex(URI.class, object, (val) -> CodeBlock.of("new $T($S)", URI.class, object), URISyntaxException.class);
        }
        if(object.getClass().equals(URL.class)) {
            return complex(URL.class, object, (val)->CodeBlock.of("new $T($S)", URL.class, object), MalformedURLException.class);
        }
        if(object.getClass().equals(Date.class)) {
            return complex(Date.class, object, (val)->CodeBlock.of("new $T($L)", Date.class, ((Date)object).getTime()));
        }

        return Optional.empty();
    }

    @Override
    public int getOrdering() {
        return 0;
    }
}
