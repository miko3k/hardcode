package org.deletethis.hardcode.objects;

import java.util.Iterator;
import java.util.List;

public class ObjectContext implements Iterable<Expression> {
    private List<Expression> arguments;
    private Integer split;

    public ObjectContext(List<Expression> arguments, Integer split) {
        this.arguments = arguments;
        this.split = split;
    }

    public Integer getSplit() {
        return split;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public Iterator<Expression> iterator() {
        return arguments.iterator();
    }
}
