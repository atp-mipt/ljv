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

    @Override
    public void visit(Visualization v) {
        int len = Array.getLength(this.value);
        v.visitArrayBegin(this.value, this.valuesArePrimitive);
        for (int i = 0; i < len; ++i) {
            Object element = Array.get(this.value, i);
            v.visitArrayElement(this.value, element, i, valuesArePrimitive);
        }
        v.visitArrayEnd(this.value);
        // Generating DOTs for array object elements and creating connection
        if (!valuesArePrimitive) {
            for (int i = 0; i < len; ++i) {
                Node node = content.get(i);
                if (!(node instanceof NullNode)) {
                    if (!v.alreadyVisualized(node.getValue())) {
                        node.visit(v);
                    }
                    v.visitArrayElementObjectConnection(this.value, i, node.getValue());
                }
            }
        }
    }
}
