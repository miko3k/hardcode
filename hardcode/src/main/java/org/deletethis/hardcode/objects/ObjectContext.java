package org.deletethis.hardcode.objects;

import java.util.Iterator;
import java.util.List;

public class ObjectContext implements Iterable<Expression> {
    private List<Expression> arguments;

    public ObjectContext(List<Expression> arguments) {
        this.arguments = arguments;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public Iterator<Expression> iterator() {
        return arguments.iterator();
    }
}
