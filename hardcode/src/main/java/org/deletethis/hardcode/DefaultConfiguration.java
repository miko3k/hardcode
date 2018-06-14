package org.deletethis.hardcode;

import org.deletethis.hardcode.objects.IntrospectionConfiguration;

import java.util.HashSet;
import java.util.Set;

public class DefaultConfiguration implements HardcodeConfiguration, IntrospectionConfiguration {
    private final Set<Class<?>> hardcodeRoots = new HashSet<>();
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
}
