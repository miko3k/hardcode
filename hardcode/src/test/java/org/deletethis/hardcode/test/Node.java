package org.deletethis.hardcode.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node {
    private final String name;
    private List<Node> others = null;

    public Node(String name, List<Node> others) {
        this.name = name;
        this.others = others;
    }

    public Node(String name) {
        this.name = name;
    }

    public void add(Node ... n) {
        if(others == null) {
            others = new ArrayList<>();
        }
        others.addAll(Arrays.asList(n));
    }
}
