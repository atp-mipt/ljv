package orionmipt;

//- LJV.java --- Generate a graph of an object, using Graphviz

//- Author:     John Hamer <J.Hamer@cs.auckland.ac.nz>
//- Created:    Sat May 10 15:27:48 2003
//- Time-stamp: <2004-08-23 12:47:06 jham005>

//- Copyright (C) 2004  John Hamer, University of Auckland
//-
//-   This program is free software; you can redistribute it and/or
//-   modify it under the terms of the GNU General Public License
//-   as published by the Free Software Foundation; either version 2
//-   of the License, or (at your option) any later version.
//-   
//-   This program is distributed in the hope that it will be useful,
//-   but WITHOUT ANY WARRANTY; without even the implied warranty of
//-   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//-   GNU General Public License for more details.
//-   
//-   You should have received a copy of the GNU General Public License along
//-   with this program; if not, write to the Free Software Foundation, Inc.,
//-   59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.

//- $Id: LJV.java,v 1.1 2004/07/14 02:03:45 jham005 Exp $

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

class LJV {
    private final IdGenerator idGenerator = new IdGenerator();

    private String dotName(Object obj) {
        return idGenerator.getId(obj);
    }


    private static boolean fieldExistsAndIsPrimitive(Context ctx, Field field, Object obj) {
        if (!ctx.canIgnoreField(field)) {
            try {
                //- The order of these statements matters.  If field is not
                //- accessible, we want an IllegalAccessException to be raised
                //- (and caught).  It is not correct to return true if
                //- field.getType( ).isPrimitive( )
                Object val = field.get(obj);
                if (field.getType().isPrimitive() || ctx.canTreatAsPrimitive(val))
                    //- Just calling ctx.canTreatAsPrimitive is not adequate --
                    //- val will be wrapped as a Boolean or Character, etc. if we
                    //- are dealing with a truly primitive type.
                    return true;
            } catch (IllegalAccessException e) {
            }
        }

        return false;
    }

    private static boolean hasPrimitiveFields(Context ctx, Field[] fs, Object obj) {
        for (int i = 0; i < fs.length; i++)
            if (fieldExistsAndIsPrimitive(ctx, fs[i], obj))
                return true;
        return false;
    }


    protected void processPrimitiveArray(Object obj, StringBuilder out) {
        out.append(dotName(obj) + "[shape=record, label=\"");
        for (int i = 0, len = Array.getLength(obj); i < len; i++) {
            if (i != 0)
                out.append("|");
            out.append(Quote.quote(String.valueOf(Array.get(obj, i))));
        }
        out.append("\"];\n");
    }


    protected void processObjectArray(Context ctx, Object obj, StringBuilder out, Set visited) {
        out.append(dotName(obj) + "[label=\"");
        int len = Array.getLength(obj);
        for (int i = 0; i < len; i++) {
            if (i != 0)
                out.append("|");
            out.append("<f" + i + ">");
        }
        out.append("\",shape=record];\n");
        for (int i = 0; i < len; i++) {
            Object ref = Array.get(obj, i);
            if (ref == null)
                continue;
            out.append(dotName(obj) + ":f" + i + " -> " + dotName(ref)
                    + "[label=\"" + i + "\",fontsize=12];\n");
            generateDotInternal(ctx, ref, out, visited);
        }
    }


    protected void labelObjectWithSomePrimitiveFields(Context ctx, Object obj, Field[] fs, StringBuilder out) {
        Object cabs = ctx.getClassAtribute(obj.getClass());
        out.append(dotName(obj) + "[label=\"" + ctx.className(obj, false) + "|{");
        String sep = "";
        for (int i = 0; i < fs.length; i++) {
            Field field = fs[i];
            if (!ctx.canIgnoreField(field))
                try {
                    Object ref = field.get(obj);
                    if (field.getType().isPrimitive() || ctx.canTreatAsPrimitive(ref)) {
                        if (ctx.showFieldNamesInLabels)
                            out.append(sep + field.getName() + ": " + Quote.quote(String.valueOf(ref)));
                        else
                            out.append(sep + Quote.quote(String.valueOf(ref)));
                        sep = "|";
                    }
                } catch (IllegalAccessException e) {
                }
        }

        out.append("}\"" + (cabs == null ? "" : "," + cabs) + ",shape=record];\n");
    }


    protected void labelObjectWithNoPrimitiveFields(Context ctx, Object obj, StringBuilder out) {
        Object cabs = ctx.getClassAtribute(obj.getClass());
        out.append(dotName(obj)
                + "[label=\"" + ctx.className(obj, true) + "\""
                + (cabs == null ? "" : "," + cabs)
                + "];\n");
    }

    protected void processFields(Context ctx, Object obj, Field[] fs, StringBuilder out, Set visited) {
        for (int i = 0; i < fs.length; i++) {
            Field field = fs[i];
            if (!ctx.canIgnoreField(field)) {
                try {
                    Object ref = field.get(obj);
                    if (field.getType().isPrimitive() || ctx.canTreatAsPrimitive(ref))
                        //- The field might be declared, say, Object, but the actual
                        //- object may be, say, a String.
                        continue;
                    String name = field.getName();
                    Object fabs = ctx.getFieldAttribute(field);
                    if (fabs == null)
                        fabs = ctx.getFieldAttribute(name);
                    out.append(dotName(obj) + " -> " + dotName(ref)
                            + "[label=\"" + name + "\",fontsize=12"
                            + (fabs == null ? "" : "," + fabs)
                            + "];\n");
                    generateDotInternal(ctx, ref, out, visited);
                } catch (IllegalAccessException e) {
                }
            }
        }
    }

    protected void generateDotInternal(Context ctx, Object obj, StringBuilder out, Set visited)
            throws IllegalArgumentException {
        if (visited.add(new VisitedObject(obj))) {
            if (obj == null)
                out.append(dotName(obj) + "[label=\"null\"" + ", shape=plaintext];\n");
            else {
                Class c = obj.getClass();
                if (c.isArray()) {
                    if (ctx.looksLikePrimitiveArray(obj))
                        processPrimitiveArray(obj, out);
                    else
                        processObjectArray(ctx, obj, out, visited);
                } else {
                    Field[] fs = c.getDeclaredFields();
                    if (!ctx.ignorePrivateFields)
                        AccessibleObject.setAccessible(fs, true);

                    if (hasPrimitiveFields(ctx, fs, obj))
                        labelObjectWithSomePrimitiveFields(ctx, obj, fs, out);
                    else
                        labelObjectWithNoPrimitiveFields(ctx, obj, out);

                    processFields(ctx, obj, fs, out, visited);

                    //- If we cared, we would take the trouble to check which
                    //- fields were accessible when we started, and carefully
                    //- restore them.  Leaving them accessible does no real harm.
                    // if( ! ctx.ignorePrivateFields )
                    //   AccessibleObject.setAccessible( fs, false );
                }
            }
        }
    }


    /**
     * Write a DOT digraph specification of the graph rooted at
     * <tt>obj</tt> to <tt>out</tt>.
     */
    public void generateDOT(Context ctx, Object obj, StringBuilder out) {
        out.append("digraph Java {\n");
        generateDotInternal(ctx, obj, out, new HashSet());
        out.append("}\n");
    }

    /**
     * Create a graph of the object rooted at <tt>obj</tt>.
     */
    public String drawGraph(Context ctx, Object obj) {
        StringBuilder out = new StringBuilder();
        generateDOT(ctx, obj, out);
        return out.toString();
    }

    public String drawGraph(Object obj) {
        return drawGraph(new Context(), obj);
    }

}
