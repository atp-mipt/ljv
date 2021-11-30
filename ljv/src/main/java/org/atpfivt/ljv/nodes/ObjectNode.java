package org.atpfivt.ljv.nodes;

import org.atpfivt.ljv.Visualization;
import java.util.HashMap;
import java.util.List;

public class ObjectNode extends Node {

    public String className;
    public int primitiveFieldsNum;
    public List<Node> children;
    public HashMap<String, String> fabs = new HashMap<>();

    public ObjectNode(Object obj, String name, int primitiveFieldsNum, List<Node> children) {
        super(obj, name);
        this.primitiveFieldsNum = primitiveFieldsNum;
        this.children = children;
    }

    @Override
    public void visit(Visualization v) {
        v.visitObjectBegin(value, className, primitiveFieldsNum);
        // First processing only primitive fields
        for (Node node: children) {
            if (node instanceof PrimitiveNode) {
                node.visit(v);
            }
        }
        v.visitObjectEnd(value);
        // Next, processing non-primitive objects and making relations with them
        for (Node node: children) {
            if (!(node instanceof PrimitiveNode)) {
                if (!v.alreadyVisualized(node.getValue())) {
                    node.visit(v);
                }
                String currentFabs = null;
                if (node instanceof ObjectNode) {
                    currentFabs = ((ObjectNode) node).fabs.get(node.getName());
                }
                if (currentFabs == null) currentFabs = "";
                v.visitObjectFieldRelationWithNonPrimitiveObject(this.value, node.getName(), currentFabs, node.getValue());
            }
        }
    }
}
