package org.deletethis.hardcode;

import org.deletethis.hardcode.objects.NodeFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * The configuration.
 *
 * Any implementation of this interface will do. Most people should be happy with {@link DefaultConfiguration}.
 */
public interface HardcodeConfiguration {
    /**
     * <p>Implements {@link java.util.function.Supplier}.
     *
     * <p>Setting this to true makes code require Java 1.8 to run.
     *
     * @return {@code true} if {@link java.util.function.Supplier} should be implemented, {@code false} otherwise
     */
    boolean generateSupplier();

    /**
     * <p>Additional {@link NodeFactory} objects. This is commonly used to provide serializers for custom types.
     *
     * @return A collection of {@link NodeFactory} objects. Order is not important as they will be sorted anyways.
     */
    Collection<NodeFactory> getAdditionalNodeFactories();

    /**
     * <p>An alternative to {@link HardcodeRoot} annotation on class.
     *
     * @return List of classes that should behave the same as {@link HardcodeRoot} annotation was present
     */
    Set<Class<?>> getHardcodeRoots();

    /**
     * <p>Additional configuration for extensions.
     *
     * <p>They could require user to extend {@link HardcodeConfiguration}, however this approach doesn't really scale
     * well - two extensions whould have to provide to incompatible sublcasses of {@link DefaultConfiguration}.
     *
     * <p>Therefore we have a mechanism to query objects by their type - somewhat like dependency injection - in order
     * to provide additonal configuration.
     *
     * @param clz type of additional configuration object
     * @return additional config, or null if no such object exists
     */
    <T> T getAdditionalConfiguration(Class<T> clz);

}
