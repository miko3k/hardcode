package org.deletethis.hardcode.graph;

import java.util.*;
import java.util.function.Function;

public class AdapterCollection<FROM,TO> extends AbstractCollection<TO> {
    private final Collection<FROM> base;
    private final Function<FROM, TO> function;

    public AdapterCollection(Collection<FROM> base, Function<FROM, TO> function) {
        this.base = base;
        this.function = function;
    }


    @Override
    public Iterator<TO> iterator() {
        Iterator<FROM> iterator = base.iterator();

        return new Iterator<TO>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public TO next() {
                return function.apply(iterator.next());
            }
        };
    }

    @Override
    public int size() {
        return base.size();
    }
}
