package org.deletethis.hardcode.objects;

import com.squareup.javapoet.CodeBlock;

import java.util.Objects;

public interface Expression {
    class Simple implements Expression {

        private Simple(CodeBlock block) {
            this.block = block;
        }

        private final CodeBlock block;

        @Override
        public String toString() {
            return "simple(" + block + ")";
        }

        @Override
        public CodeBlock getCode(String className) {
            return block;
        }

        @Override
        public boolean isSimple() {
            return true;
        }
    }

    class Complex implements Expression {
        private final CodeBlock block;

        private Complex(CodeBlock block) {
            this.block = block;
        }

        @Override
        public String toString() {
            return "complex(" + block + ")";
        }

        @Override
        public CodeBlock getCode(String className) {
            return block;
        }

        @Override
        public boolean isSimple() {
            return false;
        }
    }

    String toString();
    CodeBlock getCode(String className);
    boolean isSimple();

    static Expression simple(CodeBlock cb) {
        return new Simple(cb);
    }

    static Expression complex(CodeBlock cb) {
        return new Complex(cb);
    }

    static Expression simple(String cb, Object ... args) {
        return simple(CodeBlock.of(cb, args));
    }

    static Expression complex(String cb, Object ... args) {
        return complex(CodeBlock.of(cb, args));
    }
}
