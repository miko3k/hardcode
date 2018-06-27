package org.deletethis.hardcode.examples;

import com.squareup.javapoet.JavaFile;
import org.deletethis.hardcode.Hardcode;

public class HelloWorld {
    public static void main(String[] args) {
        // create the hardcode object
        Hardcode hardcode = Hardcode.defaultConfig();

        // create a class
        JavaFile helloFile = hardcode.createJavaFile("com.example.helloworld", "HelloWorldSupplier", "Hello world!\n");

        // write class somewhere
        System.out.println(helloFile);
    }
}
