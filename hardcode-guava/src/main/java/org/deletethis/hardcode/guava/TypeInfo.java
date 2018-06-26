package org.deletethis.hardcode.guava;

class TypeInfo {
    private final Class<?> type;
    private final Class<?> builder;
    private final int ofMax;

    TypeInfo(Class<?> type, Class<?> builder, int ofMax) {
        this.type = type;
        this.builder = builder;
        this.ofMax = ofMax;
    }

    Class<?> getType() {
        return type;
    }

    Class<?> getBuilder() {
        return builder;
    }

    public String toString() { return type.getSimpleName(); }

    boolean matches(Object o) {
        return type.isInstance(o);
    }

    public int getOfMax() {
        return ofMax;
    }
}