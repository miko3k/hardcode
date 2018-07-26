package org.deletethis.hardcode.util;

import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.CodegenParameters;

public interface SplitHelper {
    static SplitHelper get(CodegenContext parentContext, String typeNameHint, Integer split, String builder, Class<?> builderType) {
        if(split == null) {
            return new SplitHelper() {
                private boolean inStatement = false;

                @Override
                public String getBuilder() {
                    return builder;
                }

                @Override
                public void prepareStatement() {
                    if(inStatement)
                        throw new IllegalStateException();

                    inStatement = true;
                }

                @Override
                public void addStatement(String format, Object... args) {
                    if(!inStatement)
                        throw new IllegalStateException();

                    parentContext.addStatement(format, args);

                    inStatement = false;
                }

                @Override
                public void finish() {
                    if(inStatement)
                        throw new IllegalStateException();
                }
                @Override
                public String getClassName() {
                    if(!inStatement)
                        throw new IllegalStateException();

                    return parentContext.getClassName();
                }
            };
        } else {
            return new SplitHelperImpl(parentContext, builder, builderType, split);
        }
    }

    String getBuilder();
    /** Signals an intention to add a statement. Must be followed be {@link #addStatement} */
    void prepareStatement();
    /** Returns current class name, valid only between {@link #prepareStatement} and {@link #addStatement} */
    String getClassName();
    /** Must be preceded by single {@link #prepareStatement} call */
    void addStatement(String format, Object... args);
    void finish();
}
