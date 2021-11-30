package org.atpfivt.ljv.nodes;

import org.atpfivt.ljv.Visualization;

public abstract class Node {
    Object value;

    String name;

    public Node(Object obj, String name) {
        this.value = obj;
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    abstract public void visit(Visualization v);
}
