package org.deletethis.hardcode;

import com.sun.xml.internal.rngom.digested.DDataPattern;
import org.deletethis.hardcode.objects.NodeFactory;
import org.deletethis.hardcode.objects.ParameterName;

import java.util.*;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DefaultConfiguration implements HardcodeConfiguration {
    class ClassInfo {
        Set<ParameterName> roots = new HashSet<>();
        Map<ParameterName, Integer> split = new HashMap<>();
    }

    private final Set<Class<?>> hardcodeRoots = new HashSet<>();
    private final List<NodeFactory> addtionalFactories = new ArrayList<>();
    private boolean generateSupplier = true;
    private boolean generateMultipleCatch = true;
    private final Map<Class<?>, Object> map = new HashMap<>();
    private final Map<Class<?>, ClassInfo> membersInfo = new HashMap<>();

    private ClassInfo classInfo(Class<?> clz) {
        ClassInfo ci = membersInfo.get(clz);
        if(ci == null) {
            ci = new ClassInfo();
            membersInfo.put(clz, ci);
        }
        return ci;
    }

    @Override
    public boolean isRootClass(Class<?> clz) {
        return hardcodeRoots.contains(clz);
    }

    @Override
    public boolean isRootMembers(Class<?> parent, ParameterName name) {
        ClassInfo classInfo = membersInfo.get(parent);
        if(classInfo != null)
            return classInfo.roots.contains(name);
        else
            return false;
    }

    @Override
    public Integer getSplitMember(Class<?> parent, ParameterName name) {
        ClassInfo classInfo = membersInfo.get(parent);
        if(classInfo != null)
            return classInfo.split.get(name);
        else
            return null;
    }

    public void addRootMember(Class<?> clz, ParameterName name) {
        classInfo(clz).roots.add(name);
    }

    public void setSplit(Class<?> clz, ParameterName name, int split) {
        classInfo(clz).split.put(name, split);
    }

    public void addRootClass(Class<?> clz) {
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
