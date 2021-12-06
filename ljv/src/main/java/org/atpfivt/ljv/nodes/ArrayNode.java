package org.atpfivt.ljv.nodes;

import org.atpfivt.ljv.Visualization;

import java.lang.reflect.Array;
import java.util.List;

public class ArrayNode extends Node {

    boolean valuesArePrimitive;
    public List<Node> content;

    public ArrayNode(Object obj, String name, boolean valuesArePrimitive, List<Node> content) {
        super(obj, name);
        this.valuesArePrimitive = valuesArePrimitive;
        this.content = content;
    }

    public boolean areValuesPrimitive() {
        return valuesArePrimitive;
    }

    @Override
    public void visit(Visualization v) {
        int len = Array.getLength(value);
        v.visitArrayBegin(this);
        for (int i = 0; i < len; ++i) {
            Object element = Array.get(value, i);
            v.visitArrayElement(this, String.valueOf(element), i);
        }
        v.visitArrayEnd(value);
        // Generating DOTs for array object elements and creating connection
        if (!valuesArePrimitive) {
            for (int i = 0; i < len; ++i) {
                Node node = content.get(i);
                if (!(node instanceof NullNode)) {
                    if (!v.alreadyVisualized(node.getValue())) {
                        node.visit(v);
                    }
                    v.visitArrayElementObjectConnection(value, i, node.getValue());
                }
            }
        }
    }
}
