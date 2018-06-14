package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.HardcodeRoot;
import org.deletethis.hardcode.objects.ObjectContext;
import org.deletethis.hardcode.objects.ObjectInfo;
import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuiltinAnnotations {
    private boolean root = false;

    private boolean process(Annotation a) {
        if(a.annotationType().equals(HardcodeRoot.class)) {
            root = true;
            return true;
        } else {
            return false;
        }
    }

    public List<Annotation> process(List<Annotation> list) {
        if(list == null)
            return Collections.emptyList();

        ArrayList<Annotation> out = new ArrayList<>(list.size());
        for(Annotation a: list) {
            if(!process(a)) {
                out.add(a);
            }
        }
        return Collections.unmodifiableList(out);
    }

    public ObjectInfo wrap(ObjectInfo input) {
        if(root == false) {
            return input;
        }

        return new ObjectInfo() {
            @Override
            public Class<?> getType() {
                return input.getType();
            }

            @Override
            public Expression getCode(CodegenContext context, ObjectContext obj) {
                return input.getCode(context, obj);
            }

            @Override
            public boolean isRoot() {
                return true;
            }
        };
    }
}
