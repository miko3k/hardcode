package org.deletethis.hardcode.util;

import org.deletethis.hardcode.impl.ObjectInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class TypeUtilTests {
    class A { }
    interface I1 { }
    interface I2 extends I1 { }
    interface I3 extends I1 { }
    class B extends A implements I2 { }
    class T extends B implements I1, I2, I3 {}

    @Test
    public void ancestors() {
        Set<Class<?>> actual = new HashSet<>();
        Set<Class<?>> expected = new HashSet<>();

        expected.add(A.class);
        expected.add(B.class);
        expected.add(T.class);
        expected.add(I1.class);
        expected.add(I2.class);
        expected.add(I3.class);
        expected.add(Object.class);

        for(Class<?> clz: TypeUtil.ancestors(T.class)) {
            Assert.assertFalse(actual.contains(clz));
            actual.add(clz);
        }
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void wrapType() {
        Assert.assertEquals(Boolean.class, TypeUtil.wrapType(boolean.class));
        Assert.assertEquals(Byte.class, TypeUtil.wrapType(byte.class));
        Assert.assertEquals(Character.class, TypeUtil.wrapType(char.class));
        Assert.assertEquals(Double.class, TypeUtil.wrapType(double.class));
        Assert.assertEquals(Float.class, TypeUtil.wrapType(float.class));
        Assert.assertEquals(Integer.class, TypeUtil.wrapType(int.class));
        Assert.assertEquals(Long.class, TypeUtil.wrapType(long.class));
        Assert.assertEquals(Short.class, TypeUtil.wrapType(short.class));
        Assert.assertEquals(Void.class, TypeUtil.wrapType(void.class));

        Assert.assertEquals(Long.class, TypeUtil.wrapType(Long.class));
        Assert.assertEquals(Long.class, TypeUtil.wrapType(long.class));
        Assert.assertEquals(Long.class, TypeUtil.wrapType(Long.TYPE));

    }
}
