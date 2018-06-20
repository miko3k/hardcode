package org.deletethis.hardcode.testing;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.junit.Assert;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CompilerRunnerTest {
    @Test
    public void test() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MethodSpec main = MethodSpec.methodBuilder("run")
                .addParameter(StringBuilder.class, "out")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addStatement("out.append($S)", "Hello world!")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        CompilerRunner cr = new CompilerRunner();
        Class<?> compile = cr.compile("test.pkg", helloWorld);

        Method run = compile.getMethod("run", StringBuilder.class);
        StringBuilder bld = new StringBuilder();
        run.invoke(null, bld);

        Assert.assertEquals("Hello world!", bld.toString());
    }
}
