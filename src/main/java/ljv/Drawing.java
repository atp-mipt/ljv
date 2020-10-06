package ljv;

import java.lang.reflect.*;
import java.util.*;

public class Drawing {
    private final IdentityHashMap<Object, String> objectsId = new IdentityHashMap<>();
    private final ObjSettings oSettings;
    private final StringBuilder out = new StringBuilder();
    private final LJV ljv;

    public Drawing(LJV ljv) {
        this.ljv = ljv;
        this.oSettings = new ObjSettings(ljv);
    }
    //TODO add сюда лджв финал

    private String dotName(Object obj) {
        return obj == null ? "NULL" : objectsId.computeIfAbsent(obj, s -> "n" + (objectsId.size() + 1));
    }

    private void processPrimitiveArray(Object obj) {
        out.append(dotName(obj)).append("[label=<\n")
            .append("<table border='0' cellborder='1' cellspacing='0'>\n")
            .append("<tr>\n");
        for (int i = 0, len = Array.getLength(obj); i < len; i++) {
            out.append("<td>")
                .append(Quote.quote(String.valueOf(Array.get(obj, i))))
                .append("</td>");
        }
        out.append("</tr>\n</table>\n>];\n");
    }

    private void processObjectArray(Object obj) {
        out.append(dotName(obj)).append("[label=<\n")
            .append("<table border='0' cellborder='1' cellspacing='0' cellpadding='9'>\n")
            .append("<tr>\n");
        int len = Array.getLength(obj);
        for (int i = 0; i < len; i++) {
            out.append("<td port=\"f").append(i).append("\"></td>");
        }
        out.append("</tr>\n</table>\n>];\n");
        for (int i = 0; i < len; i++) {
            Object ref = Array.get(obj, i);
            if (ref == null)
                continue;
            generateDotInternal(ref);
            out.append(dotName(obj))
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
                    if (field.getType().isPrimitive() || oSettings.canTreatAsPrimitive(ljv, ref))
                        size++;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        }
        return size;
    }

    private void labelObjectWithSomePrimitiveFields(Object obj, Field[] fs) {
        out.append(dotName(obj)).append("[label=<\n")
            .append("<table border='0' cellborder='1' cellspacing='0'>\n")
            .append("<tr><td colspan='" + getFieldSize(ljv, obj, fs) + "'>")
            .append(oSettings.className(obj, false)).append("</td></tr>\n")
            .append("<tr>");
        Object cabs = ljv.getClassAtribute(obj.getClass());
        for (Field field : fs) {
            if (!ljv.canIgnoreField(field))
                try {
                    Object ref = field.get(obj);
                    if (field.getType().isPrimitive() || oSettings.canTreatAsPrimitive(ljv, ref)) {
                        out.append("<td>");
                        if (ljv.isShowFieldNamesInLabels())
                            out.append(field.getName()).append(": ").append(Quote.quote(String.valueOf(ref)));
                        else
                            out.append(Quote.quote(String.valueOf(ref)));
                        out.append("</td>\n");                            
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        }
        out.append("</tr>\n</table>\n>")
            .append(cabs == null ? "" : "," + cabs)
            .append("];\n");
    }


    private void labelObjectWithNoPrimitiveFields(Object obj) {
        Object cabs = ljv.getClassAtribute(obj.getClass());
        out.append(dotName(obj)).append("[label=<\n")
            .append("<table border='0' cellborder='1' cellspacing='0'>\n")
            .append("<tr><td>")
            .append(oSettings.className(obj, true))
            .append("</td></tr>\n</table>\n>")
            .append(cabs == null ? "" : "," + cabs)
            .append("];\n");
    }

    private void processFields(Object obj, Field[] fs) {
        for (Field field : fs) {
            if (!ljv.canIgnoreField(field)) {
                try {
                    Object ref = field.get(obj);
                    if (field.getType().isPrimitive() || oSettings.canTreatAsPrimitive(ljv, ref))
                        //- The field might be declared, say, Object, but the actual
                        //- object may be, say, a String.
                        continue;
                    String name = field.getName();
                    Object fabs = ljv.getFieldAttribute(field);
                    if (fabs == null)
                        fabs = ljv.getFieldAttribute(name);
                    generateDotInternal(ref);
                    out.append(dotName(obj)).append(" -> ")
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
            out.append(dotName(null)).append("[label=\"null\"").append(", shape=plaintext];\n");
        else if (!objectsId.containsKey(obj)) {
            Class<?> c = obj.getClass();
            if (c.isArray()) {
                if (oSettings.looksLikePrimitiveArray(obj, ljv))
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
        out.append("digraph Java {\n");
        //TODO out.append("rankdir=\"LR\";");
        out.append("node[shape=plaintext]\n");
        generateDotInternal(obj);
        return out
            .append("}\n")
            .toString();
    }
}