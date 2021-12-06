package org.atpfivt.ljv.nodes;

import org.atpfivt.ljv.Visualization;

import java.util.HashMap;

public abstract class Node {

    protected Object value;
    protected String name;
    protected HashMap<String, String> fabs = new HashMap<>();

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

    public HashMap<String, String> getFabs() {
        return fabs;
    }

    public void putFab(String key, String value) {
        fabs.put(key, value);
    }

    abstract public void visit(Visualization v);
}
