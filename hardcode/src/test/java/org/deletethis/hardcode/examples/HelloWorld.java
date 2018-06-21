package org.deletethis.hardcode.examples;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.deletethis.hardcode.Hardcode;

public class HelloWorld {
    public static void main(String[] args) {
        // create hardcode object
        Hardcode hardcode = Hardcode.defaultConfig();

        // create class
        TypeSpec helloType = hardcode.createClass("HelloWorldSupplier", "Hello world!\n");

        // write class somewhere
        System.out.println(JavaFile.builder("com.example.helloworld", helloType).build());
    }
}
