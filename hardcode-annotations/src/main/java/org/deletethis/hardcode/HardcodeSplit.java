package org.deletethis.hardcode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a long collection or a map, that cannot be constructed in a single function, because of 64k java method size limit.
 *
 * <p>It will create multiple methods, each appending certain number of items to the collection. Items must
 * be referenced just once - by the object, that this annotation is applied to. The collection itself, can be referenced
 * more than once though.
 *
 * <p>Can be applied only on fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface HardcodeSplit {
    /**
     * Specifies the number of items, that should be added by a single method. Must be positive.
     *
     * @return The number of list/map entries
     */
    int value();
}
