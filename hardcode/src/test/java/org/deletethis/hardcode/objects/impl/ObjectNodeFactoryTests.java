package org.deletethis.hardcode.objects.impl;

import org.deletethis.hardcode.DefaultConfiguration;
import org.deletethis.hardcode.HardcodeException;
import org.deletethis.hardcode.objects.NodeDefinition;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.Callable;

public class ObjectNodeFactoryTests {
    @Test
    public void lambda() {
        ObjectNodeFactory onf = new ObjectNodeFactory();
        Callable<Integer> a = () -> 1;
        Optional<NodeDefinition> node = onf.createNode(a, new DefaultConfiguration());
        Assert.assertFalse(node.isPresent());
    }

    @SuppressWarnings("ALL")
    public static class FewConstructors {
        private Object a;

        private FewConstructors(String a) { }

        public FewConstructors() { }
        public FewConstructors(String a, String b) { }
        public FewConstructors(int a) { }
        public FewConstructors(InputStream a) { }
        public FewConstructors(ByteArrayInputStream a) { }
    }

    private Optional<NodeDefinition> test(Object o) {
        return new ObjectNodeFactory().createNode(o, new DefaultConfiguration());
    }

    private void fewConstructors(Object a) {
        FewConstructors fewConstructors = new FewConstructors();
        fewConstructors.a = a;
        test(fewConstructors);
    }

    @Test(expected = HardcodeException.class)
    public void moreThanOne() {
        fewConstructors(new ByteArrayInputStream(new byte[]{}));
    }

    @Test(expected = HardcodeException.class)
    public void none() {
        fewConstructors("String");
    }

    @Test
    public void integer() {
        fewConstructors(42);
    }

    @Test(expected = HardcodeException.class)
    public void nothing() {
        fewConstructors(null);
    }

    @Test
    public void justOne() {
        fewConstructors(new InputStream() {
            @Override
            public int read()  {
                return 0;
            }
        });
    }

    public static class BadName {
        private String x = "x";

        public BadName(String y) {
            this.x = y;
        }
    }

    @Test(expected = HardcodeException.class)
    public void badName() {
        test(new BadName("a"));
    }

    public static class PrimitiveNull {
        private Integer x = null;

        public PrimitiveNull(int x) { }
    }

    @Test(expected = HardcodeException.class)
    public void primitiveNull() {
        test(new PrimitiveNull(1));
    }

    public class Inner { }
    private static class PrivateStatic { }

    public static class DefaultConstructor { }
    public static class NoDefaultConstructor { private NoDefaultConstructor() { } }
    public static class ExplicitDefaultConstructor { public ExplicitDefaultConstructor() { } }

    @Test(expected = HardcodeException.class)
    public void noDefaultConstructor() {
        test(new NoDefaultConstructor());
    }

    @Test
    public void defaultConstructor() {
        Assert.assertTrue(test(new DefaultConstructor()).isPresent());
        Assert.assertTrue(test(new ExplicitDefaultConstructor()).isPresent());
    }

    @Test
    public void ignoredClasses() {
        Callable<Integer> a = () -> 1;
        Assert.assertFalse(test(a).isPresent());
        Assert.assertFalse(test(new Inner()).isPresent());
        Assert.assertFalse(test(new PrivateStatic()).isPresent());

    }

}
