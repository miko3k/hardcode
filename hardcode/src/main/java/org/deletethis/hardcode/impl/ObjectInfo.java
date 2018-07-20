package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;
import org.deletethis.hardcode.objects.NodeDefinition;
import org.deletethis.hardcode.objects.CodegenParameters;
import org.deletethis.hardcode.objects.CodeGenerator;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Information about object
 */
public interface ObjectInfo {
    CodeGenerator getCodeGenerator();
    Collection<Class<? extends Throwable>> getFatalExceptions();
    Integer getSplit();
    Class<?> getType();
    boolean isRoot();
}
