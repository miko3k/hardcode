package org.deletethis.hardcode.objects.impl.introspection;

import org.deletethis.hardcode.HardcodeException;
import org.deletethis.hardcode.HardcodeIgnore;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.*;
import java.util.stream.Collectors;

public class FieldIntrospectionStrategyTest {
    private static class Parent {
        private static final int ignoredStatic = 1;
        private final transient int ignoredTransient = 5;
        private int realField = 42;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface A {}
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface B {}

    private class Target extends Parent {
        @A @B
        public String otherRealField = "hello";
        @HardcodeIgnore
        public String ingoredByAnnotation = "world";
    }


    @Test
    public void testIntrospection() {
        FieldIntrospectionStrategy introspectionStrategy = new FieldIntrospectionStrategy();
        IntrospectionResult introspect = introspectionStrategy.introspect(Target.class);

        Assert.assertEquals(new HashSet<>(Arrays.asList("realField", "otherRealField")), new HashSet<>(introspect.getMembers()));

        List<Annotation> annotations = introspect.getMemberAnnotations("otherRealField");
        Assert.assertEquals(2, annotations.size());
        Set<? extends Class<? extends Annotation>> set = annotations.stream().map(Annotation::annotationType).collect(Collectors.toSet());
        Assert.assertEquals(new HashSet<>(Arrays.asList(A.class, B.class)), set);

        Map<String, Object> memberValues = introspect.getMemberValues(new Target());
        Assert.assertEquals(2, memberValues.size());
        Assert.assertEquals("hello", memberValues.get("otherRealField"));
        Assert.assertEquals(42, memberValues.get("realField"));


    }

    private class BadTarget extends Parent {
        private  int realField = 43;
    }

    @Test(expected = HardcodeException.class)
    public void testDuplicateField() {
        FieldIntrospectionStrategy introspectionStartegy = new FieldIntrospectionStrategy();
        introspectionStartegy.introspect(BadTarget.class);

    }

}
