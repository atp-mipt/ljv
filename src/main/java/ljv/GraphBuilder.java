package ljv;

import java.lang.reflect.*;
import java.util.*;

public class GraphBuilder {
    private final IdentityHashMap<Object, String> objectsId = new IdentityHashMap<>();
    private final ObjSettings oSettings;
    private final StringBuilder out = new StringBuilder();
    private final LJV ljv;

    public GraphBuilder(LJV ljv) {
        this.ljv = ljv;
        this.oSettings = new ObjSettings(ljv);
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
            out.append("\t\t\t\t<td>")
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
            out.append("\t\t\t\t<td port=\"f").append(i).append("\"></td>\n");
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

    private int getFieldSize(LJV ljv, Object obj, Field[] fs) {
        int size = 0;
        for (Field field: fs) {
            if (!ljv.canIgnoreField(field))
                try {
                    Object ref = field.get(obj);
                    if (field.getType().isPrimitive() || oSettings.canTreatAsPrimitive(ref))
                        size++;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        }
        return size;
    }

    private void labelObjectWithSomePrimitiveFields(Object obj, Field[] fs) {
        out.append("\t")
                .append(dotName(obj))
                .append("[label=<\n")
                .append("\t\t<table border='0' cellborder='1' cellspacing='0'>\n")
                .append("\t\t\t<tr>\n\t\t\t\t<td rowspan='")
                .append(getFieldSize(ljv, obj, fs) + 1)
                .append("'>")
                .append(oSettings.className(obj, false))
                .append("</td>\n\t\t\t</tr>\n");
        Object cabs = ljv.getClassAttribute(obj.getClass());
        for (Field field : fs) {
            if (!ljv.canIgnoreField(field))
                try {
                    Object ref = field.get(obj);
                    if (field.getType().isPrimitive() || oSettings.canTreatAsPrimitive(ref)) {
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
            .append(cabs == null ? "" : "," + cabs)
            .append("];\n");
    }


    private void labelObjectWithNoPrimitiveFields(Object obj) {
        Object cabs = ljv.getClassAttribute(obj.getClass());
        out.append("\t")
                .append(dotName(obj))
                .append("[label=<\n")
                .append("\t\t<table border='0' cellborder='1' cellspacing='0'>\n")
                .append("\t\t\t<tr>\n\t\t\t\t<td>")
                .append(oSettings.className(obj, true))
                .append("</td>\n\t\t\t</tr>\n\t\t</table>\n\t>")
                .append(cabs == null ? "" : "," + cabs)
                .append("];\n");
    }

    private void processFields(Object obj, Field[] fs) {
        for (Field field : fs) {
            if (!ljv.canIgnoreField(field)) {
                try {
                    Object ref = field.get(obj);
                    if (field.getType().isPrimitive() || oSettings.canTreatAsPrimitive(ref))
                        //- The field might be declared, say, Object, but the actual
                        //- object may be, say, a String.
                        continue;
                    String name = field.getName();
                    Object fabs = ljv.getFieldAttribute(field);
                    if (fabs == null)
                        fabs = ljv.getFieldAttribute(name);
                    generateDotInternal(ref);
                    out.append("\t")
                            .append(dotName(obj))
                            .append(" -> ")
                            .append(dotName(ref))
                            .append("[label=\"")
                            .append(name)
                            .append("\",fontsize=12")
                            .append(fabs == null ? "" : "," + fabs)
                            .append("];\n");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void generateDotInternal(Object obj) {
        if (obj == null)
            out.append("\t").append(dotName(null)).append("[label=\"null\"").append(", shape=plaintext];\n");
        else if (!objectsId.containsKey(obj)) {
            Class<?> c = obj.getClass();
            if (c.isArray()) {
                if (oSettings.looksLikePrimitiveArray(obj))
                    processPrimitiveArray(obj);
                else
                    processObjectArray(obj);
            } else {
                Field[] fs = c.getDeclaredFields();
                if (!ljv.isIgnorePrivateFields())
                    AccessibleObject.setAccessible(fs, true);

                if (oSettings.hasPrimitiveFields(fs, obj))
                    labelObjectWithSomePrimitiveFields(obj, fs);
                else
                    labelObjectWithNoPrimitiveFields(obj);

                processFields(obj, fs);
            }
        }
    }

    public String generateDOT(Object obj) {
        out.append("digraph Java {\n")
                .append("\trankdir=\"")
                .append(ljv.getDirection())
                .append("\";\n")
                .append("\tnode[shape=plaintext]\n");
        generateDotInternal(obj);
        return out
            .append("}\n")
            .toString();
    }
}