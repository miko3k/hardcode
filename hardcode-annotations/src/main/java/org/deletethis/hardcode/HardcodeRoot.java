package org.deletethis.hardcode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks something, that should be created by a separate method.
 *
 * <p>It is used to split object graph to smaller parts in order to keep methods under 64kb limit. May be applied
 * to classes or fields. Any object, that is marked as {@code @HardcodeRoot}, must be referenced exactly once.
 *
 * <p>Can be applied on fields or classes.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface HardcodeRoot {
}
