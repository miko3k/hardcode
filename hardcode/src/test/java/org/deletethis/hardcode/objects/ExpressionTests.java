package org.deletethis.hardcode.objects;

import com.squareup.javapoet.CodeBlock;
import org.junit.Assert;
import org.junit.Test;

public class ExpressionTests {
    @Test
    public void tests() {
        Expression s1 = Expression.simple(CodeBlock.of("foo"));
        Expression s2 = Expression.simple(CodeBlock.of("foo"));
        Expression sx = Expression.simple(CodeBlock.of("fo"));
        Expression c1 = Expression.complex(CodeBlock.of("foo"));
        Expression c2 = Expression.complex(CodeBlock.of("foo"));
        Expression cx = Expression.complex(CodeBlock.of("fo"));

        Assert.assertEquals(s1, s2);
        Assert.assertEquals(c1, c2);
        Assert.assertEquals(s1.hashCode(), s2.hashCode());
        Assert.assertEquals(c1.hashCode(), c2.hashCode());
        Assert.assertNotEquals(s1, sx);
        Assert.assertNotEquals(s1, c1);
        Assert.assertNotEquals(cx, sx);

        Assert.assertEquals(s1.toString(), "simple(foo)");
        Assert.assertEquals(c1.toString(), "complex(foo)");
    }
}
