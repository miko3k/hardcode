# hardcode
*A friendly warning: This is alpha quality software. Things may break, or be already broken!*

A java library to hardcode data into classfiles.

Sometimes you just need to load your data fast. For instance, in Android application, startup time is critical. This library can help out, by generating source code, that will create data for you.

## Hello world
Following source code
```java
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
```
Produces following output - a `Provider`, which provides a single `String` greeting the world!
```java
package com.example.helloworld;

import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;

public class HelloWorldSupplier implements Supplier<String> {
  @SuppressWarnings("unchecked")
  @Override
  public String get() {
    return "Hello world!\n";
  }
}
```
## Features
- can split object data into multiple functions, to work around 64k method limit
- object graph support
-- handles multiple references to the single object
-- unfortunately, cycles are not supported
- can create [Grahviz](https://www.graphviz.org) represtantation of your object tree
- supports most common java collections
- supports some [Guava](https://github.com/google/guava) immutable types
- support for custom serializers

## Todo
- better error checking
- support for more collections
- rewrite `@HardcodeSplit`
- upload to maven repo
- add stuff to this readme
