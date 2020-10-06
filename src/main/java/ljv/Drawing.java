package ljv;

import java.lang.reflect.*;
import java.util.*;

public class Drawing {
    private final IdentityHashMap<Object, String> objectsId = new IdentityHashMap<>();
    private final ObjSettings oSettings = new ObjSettings();
    private final StringBuilder out = new StringBuilder("digraph Java {\n");

    private String dotName(Object obj) {
        return obj == null ? "NULL" : objectsId.computeIfAbsent(obj, s -> "n" + (objectsId.size() + 1));
    }

    private void processPrimitiveArray(Object obj) {
        out.append(dotName(obj)).append("[shape=record, label=\"");
        for (int i = 0, len = Array.getLength(obj); i < len; i++) {
            if (i != 0)
                out.append("|");
            out.append(Quote.quote(String.valueOf(Array.get(obj, i))));
        }
        out.append("\"];\n");
    }

    private void processObjectArray(LJV ljv, Object obj) {
        out.append(dotName(obj)).append("[label=\"");
        int len = Array.getLength(obj);
        for (int i = 0; i < len; i++) {
            if (i != 0)
                out.append("|");
            out.append("<f").append(i).append(">");
        }
        out.append("\",shape=record];\n");
        for (int i = 0; i < len; i++) {
            Object ref = Array.get(obj, i);
            if (ref == null)
                continue;
            generateDotInternal(ljv, ref);
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

    private void labelObjectWithSomePrimitiveFields(LJV ljv, Object obj, Field[] fs) {
        Object cabs = ljv.getClassAtribute(obj.getClass());
        out.append(dotName(obj)).append("[label=\"").append(oSettings.className(obj, ljv, false)).append("|{");
        String sep = "";
        for (Field field : fs) {
            if (!ljv.canIgnoreField(field))
                try {
                    Object ref = field.get(obj);
                    if (field.getType().isPrimitive() || oSettings.canTreatAsPrimitive(ljv, ref)) {
                        if (ljv.isShowFieldNamesInLabels())
                            out.append(sep).append(field.getName()).append(": ").append(Quote.quote(String.valueOf(ref)));
                        else
                            out.append(sep).append(Quote.quote(String.valueOf(ref)));
                        sep = "|";
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        }
        out.append("}\"").append(cabs == null ? "" : "," + cabs).append(",shape=record];\n");
    }


    private void labelObjectWithNoPrimitiveFields(LJV ljv, Object obj) {
        Object cabs = ljv.getClassAtribute(obj.getClass());
        out.append(dotName(obj)).append("[label=\"")
                .append(oSettings.className(obj, ljv, true))
                .append("\"").append(cabs == null ? "" : "," + cabs).append("];\n");
    }

    private void processFields(LJV ljv, Object obj, Field[] fs) {
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
                    generateDotInternal(ljv, ref);
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

    private void generateDotInternal(LJV ljv, Object obj) {
        if (obj == null)
            out.append(dotName(null)).append("[label=\"null\"").append(", shape=plaintext];\n");
        else if (!objectsId.containsKey(obj)) {
            Class<?> c = obj.getClass();
            if (c.isArray()) {
                if (oSettings.looksLikePrimitiveArray(obj, ljv))
                    processPrimitiveArray(obj);
                else
                    processObjectArray(ljv, obj);
            } else {
                Field[] fs = c.getDeclaredFields();
                if (!ljv.isIgnorePrivateFields())
                    AccessibleObject.setAccessible(fs, true);

                if (oSettings.hasPrimitiveFields(ljv, fs, obj))
                    labelObjectWithSomePrimitiveFields(ljv, obj, fs);
                else
                    labelObjectWithNoPrimitiveFields(ljv, obj);

                processFields(ljv, obj, fs);
            }
        }
    }

    public String generateDOT(LJV ljv, Object obj) {
        generateDotInternal(ljv, obj);
        return out
            .append("}\n")
            .toString();
    }
}