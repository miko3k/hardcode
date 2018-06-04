package org.deletethis.hardcode;

import org.deletethis.hardcode.objects.IntrospectionConfiguration;

import java.util.HashSet;
import java.util.Set;

public class DefaultConfiguration implements HardcodeConfiguration, IntrospectionConfiguration {
    private final Set<Class<?>> hardcodeRoots = new HashSet<>();

    @Override
    public Set<Class<?>> getHardcodeRoots() {
        return hardcodeRoots;
    }

    public void addHardcodeRoot(Class<?> clz) {
        hardcodeRoots.add(clz);
    }
}
