package org.atpfivt.ljv;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;

final class GraphBuilder {
    private final IdentityHashMap<Object, String> objectsId = new IdentityHashMap<>();
    private final StringBuilder out = new StringBuilder();
    private final LJV ljv;
    private final Introspection introspection;
    private boolean nullNode;

    public GraphBuilder(LJV ljv) {
        this.ljv = ljv;
        this.introspection = new IntrospectionWithReflectionAPI(ljv);
    }

    private String dotName(Object obj) {
        return obj == null ? "NULL" : objectsId.computeIfAbsent(obj, s -> "n" + (objectsId.size() + 1));
    }

    private void processPrimitiveArray(Object obj) {
        out.append("\t")
                .append(dotName(obj))
                .append("[label=<\n")
                .append("\t\t<table border='0' cellborder='1' cellspacing='0'>\n")
                .append("\t\t\t<tr>\n");
        for (int i = 0, len = Array.getLength(obj); i < len; i++) {
            out.append("\t\t\t\t<td")
                    .append(ljv.getArrayElementAttributes(obj, i))
                    .append(">")
                    .append(Quote.quote(String.valueOf(Array.get(obj, i))))
                    .append("</td>\n");
        }
        out.append("\t\t\t</tr>\n\t\t</table>\n\t>];\n");
    }

    private void processObjectArray(Object obj) {
        out.append("\t")
                .append(dotName(obj))
                .append("[label=<\n")
                .append("\t\t<table border='0' cellborder='1' cellspacing='0' cellpadding='9'>\n")
                .append("\t\t\t<tr>\n");
        int len = Array.getLength(obj);
        for (int i = 0; i < len; i++) {
            out.append("\t\t\t\t<td port=\"f").append(i).append("\"")
                    .append(ljv.getArrayElementAttributes(obj, i))
                    .append("></td>\n");
        }
        out.append("\t\t\t</tr>\n\t\t</table>\n\t>];\n");
        for (int i = 0; i < len; i++) {
            Object ref = Array.get(obj, i);
            if (ref == null)
                continue;
            generateDotInternal(ref);
            out.append("\t")
                    .append(dotName(obj))
                    .append(":f")
                    .append(i)
                    .append(" -> ")
                    .append(dotName(ref))
                    .append("[label=\"")
                    .append(i)
                    .append("\",fontsize=12];\n");
        }
    }

    private void labelObjectWithSomePrimitiveFields(Object obj) {
        Field fields[] = introspection.getObjFields(obj);

        out.append("\t")
                .append(dotName(obj))
                .append("[label=<\n")
                .append("\t\t<table border='0' cellborder='1' cellspacing='0'>\n")
                .append("\t\t\t<tr>\n\t\t\t\t<td rowspan='")
                .append(introspection.countObjectPrimitiveFields(obj) + 1)
                .append("'>")
                .append(introspection.getObjClassName(obj, false))
                .append("</td>\n\t\t\t</tr>\n");

        String cabs = ljv.getObjectAttributes(obj);
        for (Field field : fields) {
            if (!ljv.canIgnoreField(field))
                try {
                    Object ref = field.get(obj);
                    if (field.getType().isPrimitive() || introspection.canTreatObjAsPrimitive(ref)) {
                        out.append("\t\t\t<tr>\n\t\t\t\t<td>");
                        if (ljv.isShowFieldNamesInLabels())
                            out.append(field.getName()).append(": ").append(Quote.quote(String.valueOf(ref)));
                        else
                            out.append(Quote.quote(String.valueOf(ref)));
                        out.append("</td>\n\t\t\t</tr>\n");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        }
        out.append("\t\t</table>\n\t>")
                .append(cabs.isEmpty() ? "" : "," + cabs)
                .append("];\n");
    }


    private void labelObjectWithNoPrimitiveFields(Object obj) {
        String cabs = ljv.getObjectAttributes(obj);
        out.append("\t")
                .append(dotName(obj))
                .append("[label=<\n")
                .append("\t\t<table border='0' cellborder='1' cellspacing='0'>\n")
                .append("\t\t\t<tr>\n\t\t\t\t<td>")
                .append(introspection.getObjClassName(obj, false))
                .append("</td>\n\t\t\t</tr>\n\t\t</table>\n\t>")
                .append(cabs.isEmpty() ? "" : "," + cabs)
                .append("];\n");
    }

    private void processFields(Object obj) {
        Field[] fs = introspection.getObjFields(obj);

        for (Field field : fs) {
            if (!ljv.canIgnoreField(field)) {
                try {
                    Object ref = field.get(obj);
                    if (field.getType().isPrimitive() || introspection.canTreatObjAsPrimitive(ref))
                        //- The field might be declared, say, Object, but the actual
                        //- object may be, say, a String.
                        continue;
                    String name = field.getName();
                    String fabs = ljv.getFieldAttributes(field, ref);
                    generateDotInternal(ref);
                    out.append("\t")
                            .append(dotName(obj))
                            .append(" -> ")
                            .append(dotName(ref))
                            .append("[label=\"")
                            .append(name)
                            .append("\",fontsize=12")
                            .append(fabs.isEmpty() ? "" : "," + fabs)
                            .append("];\n");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void generateDotInternal(Object obj) {
        if (obj == null) {
            if (!nullNode) {
                out.append("\t").append(dotName(null)).append("[label=\"null\"").append(", shape=plaintext];\n");
                nullNode = true;
            }
        } else if (!objectsId.containsKey(obj)) {
            Class<?> c = obj.getClass();
            if (c.isArray()) {
                if (introspection.catTreatObjAsArrayOfPrimitives(obj))
                    processPrimitiveArray(obj);
                else
                    processObjectArray(obj);
            } else {

                if (introspection.hasPrimitiveFields(obj))
                    labelObjectWithSomePrimitiveFields(obj);
                else
                    labelObjectWithNoPrimitiveFields(obj);

                processFields(obj);
            }
        }
    }

    public String generateDOT() {
        out.append("digraph Java {\n")
                .append("\trankdir=\"")
                .append(ljv.getDirection())
                .append("\";\n")
                .append("\tnode[shape=plaintext]\n");
        for (Object obj : ljv.getRoots()) {
            generateDotInternal(obj);
        }
        return out
                .append("}\n")
                .toString();
    }

}
