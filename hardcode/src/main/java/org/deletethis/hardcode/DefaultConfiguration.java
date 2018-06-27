package org.deletethis.hardcode;

import org.deletethis.hardcode.objects.NodeFactory;

import java.util.*;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DefaultConfiguration implements HardcodeConfiguration {
    private final Set<Class<?>> hardcodeRoots = new HashSet<>();
    private final List<NodeFactory> addtionalFactories = new ArrayList<>();
    private boolean generateSupplier = true;
    private final Map<Class<?>, Object> map = new HashMap<>();

    @Override
    public Set<Class<?>> getHardcodeRoots() {
        return hardcodeRoots;
    }

    public void addHardcodeRoot(Class<?> clz) {
        hardcodeRoots.add(clz);
    }

    public void setGenerateSupplier(boolean generateSupplier) {
        this.generateSupplier = generateSupplier;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Default value is {@code true}. Use {@link #setGenerateSupplier} to change.
     *
     * @return
     */
    @Override
    public boolean generateSupplier() {
        return generateSupplier;
    }

    public void registerNodeFactory(NodeFactory nodeFactory) {
        addtionalFactories.add(nodeFactory);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Empty list by default. Use {@link #registerNodeFactory} to add more values.
     *
     */
    @Override
    public Collection<NodeFactory> getAdditionalNodeFactories() {
        return addtionalFactories;
    }

    public <T> void setAdditionalConfiguration(Class<T> clz, T value) {
        map.put(Objects.requireNonNull(clz), Objects.requireNonNull(value));
    }

    /**
     * {@inheritDoc}
     *
     * <p>By default, nothing is registered, use {@link #setAdditionalConfiguration} to register objects.
     *
     */
    @SuppressWarnings("unchecked")
    public <T> T getAdditionalConfiguration(Class<T> clz) {
        return (T) map.get(clz);
    }

}
