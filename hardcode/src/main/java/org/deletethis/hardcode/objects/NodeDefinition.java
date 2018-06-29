package org.deletethis.hardcode.objects;

import java.util.Collection;

public interface NodeDefinition {
    Class<?> getType();
    CodeGenerator getConstructionStrategy();
    Collection<NodeParameter> getParameters();
    Collection<Class<? extends Throwable>> getFatalExceptions();

    /**
     * Returns true if current class is a
     * <a href='https://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html'>value-based</a>
     * class.
     *
     * Such values are never searched for additional references, and will be simply duplicated if multiple references
     * point to the same object.
     *
     * @return {@code true} if current node is value-based otherwise {@code false}
     */
    boolean isValueBased();
    String toString();
}
