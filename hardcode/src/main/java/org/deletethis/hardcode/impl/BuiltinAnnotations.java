package org.deletethis.hardcode.impl;

import org.deletethis.hardcode.HardcodeRoot;
import org.deletethis.hardcode.HardcodeSplit;

import java.lang.annotation.Annotation;
import java.util.List;

class BuiltinAnnotations {
    private boolean root = false;
    private Integer split = null;

    private void addAnnotation(Annotation a) {
        if(a.annotationType().equals(HardcodeRoot.class)) {
            root = true;
        } else if(a.annotationType().equals(HardcodeSplit.class)) {
            split = ((HardcodeSplit) a).value();
        }
    }

    BuiltinAnnotations() {
    }


    public void addAnnotations(List<Annotation> annotations) {
        if(annotations != null) {
            for(Annotation a: annotations) {
                addAnnotation(a);
            }
        }
    }

    boolean isRoot() {
        return root;
    }

    Integer getSplit() {
        return split;
    }

    void makeRoot() {
        root = true;
    }

    void setSplit(int split) {
        this.split = split;
    }
}
