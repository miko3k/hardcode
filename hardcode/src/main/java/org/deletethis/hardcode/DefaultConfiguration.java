package org.deletethis.hardcode;

import org.deletethis.hardcode.objects.NodeFactory;

import java.util.*;

public class DefaultConfiguration implements HardcodeConfiguration {
    private final Set<Class<?>> hardcodeRoots = new HashSet<>();
    private final List<NodeFactory> addtionalFactories = new ArrayList<>();
    private boolean generateSupplier = true;

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

    @Override
    public boolean generateSupplier() {
        return generateSupplier;
    }

    public void registerNodeFactory(NodeFactory nodeFactory) {
        addtionalFactories.add(nodeFactory);
    }

    @Override
    public Collection<NodeFactory> getAdditionalNodeFactories() {
        return addtionalFactories;
    }
}
