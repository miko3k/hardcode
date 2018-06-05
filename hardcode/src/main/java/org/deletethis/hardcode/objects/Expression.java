package org.deletethis.hardcode.objects;

import com.squareup.javapoet.CodeBlock;

import java.util.Objects;

public class Expression {
    private final CodeBlock block;
    private final boolean simple;

    private Expression(CodeBlock block, boolean simple) {
        this.block = block;
        this.simple = simple;
    }

    public String toString() {
        if(simple) {
            return "simple(" + block + ")";
        } else {
            return "complex(" + block + ")";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expression)) return false;
        Expression that = (Expression) o;
        return simple == that.simple &&
                Objects.equals(block, that.block);
    }

    @Override
    public int hashCode() {

        return Objects.hash(block, simple);
    }

    public CodeBlock getCode() {
        return block;
    }

    public boolean isSimple() {
        return simple;
    }

    public static Expression simple(CodeBlock cb) {
        return new Expression(cb, true);
    }

    public static Expression complex(CodeBlock cb) {
        return new Expression(cb, false);
    }

    public static Expression simple(String cb, Object ... args) {
        return simple(CodeBlock.of(cb, args));
    }

    public static Expression complex(String cb, Object ... args) {
        return complex(CodeBlock.of(cb, args));
    }


}
