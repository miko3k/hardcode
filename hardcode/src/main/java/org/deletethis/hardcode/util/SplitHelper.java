package org.deletethis.hardcode.util;

import org.deletethis.hardcode.objects.CodegenContext;

public interface SplitHelper {
    static SplitHelper get(CodegenContext parentContext, Integer split, String builder, Class<?> builderType) {
        if(split == null) {
            return new SplitHelper() {
                @Override
                public String getBuilder() {
                    return builder;
                }

                @Override
                public void addStatement(String format, Object... args) {
                    parentContext.addStatement(format, args);
                }

                @Override
                public void finish() {

                }
            };
        } else {
            return new SplitHelperImpl(parentContext, split, builder, builderType);
        }
    }

    String getBuilder();
    void addStatement(String format, Object... args);
    void finish();
}
