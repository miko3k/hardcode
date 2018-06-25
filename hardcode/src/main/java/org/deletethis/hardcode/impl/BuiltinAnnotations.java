package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.ConfigMismatchException;
import org.deletethis.hardcode.HardcodeRoot;
import org.deletethis.hardcode.HardcodeSplit;
import org.deletethis.hardcode.objects.ObjectContext;
import org.deletethis.hardcode.objects.CodegenContext;
import org.deletethis.hardcode.objects.Expression;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuiltinAnnotations {
    private boolean root = false;
    private Integer split = null;

    private void process(Annotation a) {
        if(a.annotationType().equals(HardcodeRoot.class)) {
            root = true;
        } else if(a.annotationType().equals(HardcodeSplit.class)) {
            split = ((HardcodeSplit) a).value();
        }
    }

    BuiltinAnnotations(List<Annotation> annotations) {
        if(annotations != null) {
            for(Annotation a: annotations) {
                process(a);
            }
        }
    }

    public void apply(ObjectInfo objectInfo) {
        if(root) {
            objectInfo.setRoot(true);
        }
        if(split != null) {
            if(objectInfo.getSplit() != null) {
                if(!split.equals(objectInfo.getSplit()))
                    throw new ConfigMismatchException("incompatible splits: " + objectInfo.getSplit() + " and " + split);
            }
            objectInfo.setSplit(split);
        }
    }
}
