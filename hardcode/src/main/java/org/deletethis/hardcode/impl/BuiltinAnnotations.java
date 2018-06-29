package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.ConfigMismatchException;
import org.deletethis.hardcode.HardcodeRoot;
import org.deletethis.hardcode.HardcodeSplit;

import java.lang.annotation.Annotation;
import java.util.List;

class BuiltinAnnotations {
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

    void apply(ObjectInfo objectInfo) {
        if(root) {
            objectInfo.makeRoot();
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
