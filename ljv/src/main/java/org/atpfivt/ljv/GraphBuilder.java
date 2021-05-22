package org.atpfivt.ljv;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

final class GraphBuilder {
    private final LJV ljv;
    private final Introspection introspection;
    private Visualization visualization;

    public GraphBuilder(LJV ljv) {
        this.ljv = ljv;
        this.introspection = new IntrospectionWithReflectionAPI(ljv);
        this.visualization = new VisualizationCommon(ljv);
    }

    public String generateDOT() {
        visualization.beginDOT();
        for (Object obj : ljv.getRoots()) {
            processObject(obj);
        }
        visualization.finishDOT();
        return visualization.generateResult();
    }

    private void processArray(Object obj) {
        boolean valuesArePrimitive = introspection.catTreatObjAsArrayOfPrimitives(obj);
        int len = Array.getLength(obj);

        visualization.visitArrayBegin(obj, valuesArePrimitive);
        for (int i = 0; i < len; i++) {
            Object element = Array.get(obj, i);
            visualization.visitArrayElement(obj, element, i, valuesArePrimitive);
        }
        visualization.visitArrayEnd(obj);

        // Generating DOTs for array object elements and creating connection
        if (!valuesArePrimitive) {
            for (int i = 0; i < len; i++) {
                Object ref = Array.get(obj, i);
                if (ref == null)
                    continue;

                processObject(ref);
                visualization.visitArrayElementObjectConnection(obj, i, ref);
            }
        }
    }

    private void processObject(Object obj) {
        if (visualization.alreadyVisualized(obj)) {
            return;
        }

        if (obj == null) {
            visualization.visitNull();
            return;
        }

        Class<?> c = obj.getClass();
        if (c.isArray()) {
            processArray(obj);
            return;
        }


        String className = introspection.getObjClassName(obj, false);
        Field fields[] = introspection.getObjFields(obj);
        int primitiveFieldsNum = introspection.countObjectPrimitiveFields(obj);

        visualization.visitObjectBegin(obj, className, primitiveFieldsNum);

        // First processing only primitive fields
        for (Field field: fields) {
            if (!introspection.objectFieldIsPrimitive(field, obj)) {
                continue;
            }

            try {
                Object ref = field.get(obj);
                if (introspection.objectFieldIsPrimitive(field, obj)) {
                    String name = field.getName();
                    String value = String.valueOf(ref);
                    visualization.visitObjectPrimitiveField(obj, name, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        visualization.visitObjectEnd(obj);

        // Next, processing non-primitive objects and making relations with them
        for (Field field : fields) {
            if (introspection.objectFieldIsPrimitive(field, obj) || ljv.canIgnoreField(field)) {
                continue;
            }

            try {
                Object ref = field.get(obj);
                String name = field.getName();
                String fabs = ljv.getFieldAttributes(field, ref);

                processObject(ref);
                visualization.visitObjectFieldRelationWithNonPrimitiveObject(obj, name, fabs, ref);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
