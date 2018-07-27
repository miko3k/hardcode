package org.deletethis.hardcode;

import com.google.common.io.Files;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.deletethis.hardcode.testing.HardcodeTesting;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HardcodeUtilTest {
    private File directory;

    @Before
    public void setup() {
        directory = Files.createTempDir();
    }

    private List<JavaFile> createFiles(String pkg, String name, int first, int last) {
        List<JavaFile> result = new ArrayList<>();

        for(int i=first;i<last;i++) {
            String str = (i==0) ? name : name + i;
            TypeSpec typeSpec = TypeSpec.classBuilder(str).build();
            JavaFile javaFile = JavaFile.builder(pkg, typeSpec).build();
            result.add(javaFile);
        }

        return result;
    }

    private File packageDirectory(String pkg) {
        return  new File(directory, pkg.replace('.', File.separatorChar));
    }

    private Set<String> listPackageFiles(String pkg) {
        File[] files = packageDirectory(pkg).listFiles();
        if(files == null)
            throw new IllegalStateException("fail!");

        HashSet<String> result = new HashSet<>();
        for(File f: files) {
            result.add(f.getName());
        }
        return result;
    }

    @Test
    public void testSimple() throws IOException {
        HardcodeUtil.writeJavaFiles(directory, createFiles("hello.world","Hello", 0, 5));
        HardcodeUtil.writeJavaFiles(directory, createFiles("hello.world","Hello", 1, 3));

        HashSet<String> wanted = new HashSet<>(Arrays.asList(
            "Hello1.java", "Hello2.java"
        ));

        Assert.assertEquals(wanted, listPackageFiles("hello.world"));
    }

    @Test
    public void testKeepOther() throws IOException {
        HardcodeUtil.writeJavaFiles(directory, createFiles("pkg","Foo", 0, 2));
        HardcodeUtil.writeJavaFiles(directory, createFiles("pkg","Hello", 1, 3));

        HashSet<String> wanted = new HashSet<>(Arrays.asList(
                "Foo.java", "Foo1.java", "Hello1.java", "Hello2.java"
        ));

        Assert.assertEquals(wanted, listPackageFiles("pkg"));
    }


    @Test
    public void noPackage() throws IOException {
        HardcodeUtil.writeJavaFiles(directory, createFiles("","Hello", 0, 5));
        HardcodeUtil.writeJavaFiles(directory, createFiles("","Hello", 1, 3));

        HashSet<String> wanted = new HashSet<>(Arrays.asList(
                "Hello1.java", "Hello2.java"
        ));

        Assert.assertEquals(wanted, listPackageFiles(""));
    }


    @After
    public void remove() {
        HardcodeTesting.deleteRecursively(directory);
        directory = null;
    }
}
