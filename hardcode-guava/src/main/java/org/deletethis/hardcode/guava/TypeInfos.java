package org.deletethis.hardcode.guava;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

class TypeInfos {
    private ImmutableCollection<TypeInfo> infos;

    public static TypeInfos of(
            Class<?> t1, Class<?> b1, int max
    ) {
        return new TypeInfos(ImmutableList.of(
                new TypeInfo(t1, b1, max)
        ));
    }
    public static TypeInfos of(
            Class<?> t1, Class<?> b1, int max1,
            Class<?> t2, Class<?> b2, int max2
    ) {
        return new TypeInfos(ImmutableList.of(
                new TypeInfo(t1, b1, max1),
                new TypeInfo(t2, b2, max2)
        ));
    }

    public TypeInfos(ImmutableCollection<TypeInfo> infos) {
        this.infos = infos;
    }

    public TypeInfo find(Object o) {
        for(TypeInfo typeInfo: infos) {
            if(typeInfo.matches(o))
                return typeInfo;
        }
        return null;
    }
}