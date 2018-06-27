# hardcode
*A friendly warning: This is an alpha quality software. Things may break, or
be already broken!*

A java library to hardcode data into the classfiles.

Sometimes, one need to load data *fast*. For instance, startup time is
critical for Android applications. This library can help out.
It prints out the source code that creates the data.

It's about an order of magnitude faster than java serialization,
about 2-3 times as fast as [FST](https://github.com/RuedigerMoeller/fast-serialization)
(TODO: an actual benchmark). Obviously, this is suitable
only for the static data.

Source code is handled by [JavaPoet](https://github.com/square/javapoet),
output takes form of a `com.squareup.javapoet.TypeSpec` that can be
simply printed out or processed further.

## Hello world
Following source code
```java
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
```
Produces an implementation of the `Provider` which provides a single `String`.

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
- can split an object data into multiple functions, to work around the 64k method size limit
- object graph support
  - handles multiple references to the single object
  - unfortunately, cycles are not supported
- can create [Grahviz](https://www.graphviz.org) representation of your object tree
- supports most common of the collections
- supports some of [Guava](https://github.com/google/guava) immutable types
- support for custom serializers, either via API or `java.util.ServiceLoader`
- `Provider` is optional, in order to support Java 7 - or  Android up to API level 23


## Todo
- support for more more types
  - guava types
  - frequently used JDK types, such as `Date` or `URL`
  - support more Guava types
- rewrite `@HardcodeSplit` - especially error checking
- better error checking in general - should be done during graph building,
  not afterwards
- upload to maven repo
- improve this readme
- document `@HardcodeSplit` and `@HardcodeRoot`
- better test coverage
- javadoc for public APIs

