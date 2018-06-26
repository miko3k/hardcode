package org.deletethis.hardcode;

import org.deletethis.hardcode.objects.NodeFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface HardcodeConfiguration {
    boolean generateSupplier();
    Collection<NodeFactory> getAdditionalNodeFactories();
    Set<Class<?>> getHardcodeRoots();

}
