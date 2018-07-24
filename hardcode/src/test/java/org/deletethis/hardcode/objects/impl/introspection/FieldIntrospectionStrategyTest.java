package org.deletethis.hardcode.objects.impl.introspection;

import org.deletethis.hardcode.HardcodeException;
import org.deletethis.hardcode.HardcodeIgnore;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class FieldIntrospectionStrategyTest {
    private static class Parent {
        private static final int ignoredStatic = 1;
        private final transient int ignoredTransient = 5;
        private int realField = 42;
    }

    private class Target extends Parent {
        @XmlElement
        @XmlValue
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
        Assert.assertEquals(new HashSet<>(Arrays.asList(XmlElement.class, XmlValue.class)), set);

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
