package org.deletethis.hardcode.objects;

import org.deletethis.hardcode.objects.IndexParamteter;
import org.deletethis.hardcode.objects.MapParameter;
import org.deletethis.hardcode.objects.NamedParameter;
import org.junit.Assert;
import org.junit.Test;

public class ParameterNameTests {
    @Test
    public void a() {
        NamedParameter n1 = new NamedParameter("a");
        NamedParameter n2 = new NamedParameter("a");
        NamedParameter nx = new NamedParameter("b");

        IndexParamteter i1 = new IndexParamteter(1);
        IndexParamteter i2 = new IndexParamteter(1);
        IndexParamteter ix = new IndexParamteter(2);

        MapParameter m1 = new MapParameter(true, 1);
        MapParameter m2 = new MapParameter(true, 1);
        MapParameter mx1 = new MapParameter(false, 1);
        MapParameter mx2 = new MapParameter(true, 2);

        Assert.assertEquals(n1.getName(), "a");
        Assert.assertEquals(i1.getIndex(), 1);
        Assert.assertEquals(m1.getIndex(), 1);
        Assert.assertTrue(m1.isKey());


        Assert.assertNotEquals(n1, null);
        Assert.assertNotEquals(i1, null);
        Assert.assertNotEquals(m1, null);

        Assert.assertEquals(n1, n2);
        Assert.assertEquals(n1.hashCode(), n2.hashCode());
        Assert.assertNotEquals(n1, nx);

        Assert.assertEquals(i1, i2);
        Assert.assertEquals(i1.hashCode(), i2.hashCode());
        Assert.assertNotEquals(i1, ix);

        Assert.assertEquals(m1, m2);
        Assert.assertEquals(m1.hashCode(), m2.hashCode());
        Assert.assertNotEquals(m1, mx1);
        Assert.assertNotEquals(m1, mx2);

        Assert.assertNotEquals(n1, i1);
        Assert.assertNotEquals(n1, m1);
        Assert.assertNotEquals(i1, n1);
        Assert.assertNotEquals(i1, m1);
        Assert.assertNotEquals(m1, i1);
        Assert.assertNotEquals(m1, n1);
    }
}
