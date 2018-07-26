package org.deletethis.hardcode.objects;

import com.squareup.javapoet.CodeBlock;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ExpressionTests {
    @Test
    public void toStringTest() {
        Expression s1 = Expression.simple(CodeBlock.of("foo"));
        Expression c1 = Expression.complex(CodeBlock.of("foo"));

        Assert.assertEquals("simple(foo)", s1.toString());
        Assert.assertEquals("complex(foo)", c1.toString());
    }

}
