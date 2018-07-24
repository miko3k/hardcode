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

    // let's construct this sneakily via factory method
    private FewConstructors getInvalidConstructors(Object a) {
        FewConstructors result = new FewConstructors();
        result.a = a;
        return result;
    }

    private void run(Object o) {
        new ObjectNodeFactory().createNode(getInvalidConstructors(o), new DefaultConfiguration());
    }

    @Test(expected = HardcodeException.class)
    public void moreThanOne() {
        run(new ByteArrayInputStream(new byte[]{}));
    }

    @Test(expected = HardcodeException.class)
    public void none() {
        run("String");
    }

    @Test
    public void integer() {
        run(42);
    }


    @Test
    public void justOne() {
        run(new InputStream() {
            @Override
            public int read()  {
                return 0;
            }
        });
    }

}
