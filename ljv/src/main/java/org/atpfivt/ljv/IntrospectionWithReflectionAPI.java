package org.atpfivt.ljv;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.FieldLayout;
import org.openjdk.jol.util.ObjectUtils;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Array;

import java.util.SortedSet;
import java.util.function.Predicate;

public class IntrospectionWithReflectionAPI extends IntrospectionBase {
    public IntrospectionWithReflectionAPI(LJV ljv) {
        super(ljv);
    }

    @Override
    public Field[] getObjFields(Object obj) {
        Class<?> cls = obj.getClass();

        SortedSet<FieldLayout> fields = ClassLayout.parseClass(cls).fields();

        Field[] fs = ReflectionUtils
                .getAllFields(cls, getObjFieldsIgnoreNullValuedPredicate(obj))
                .toArray(new Field[0]);
        normalizeFieldsOrder(fs);

        return fs;
    }

    @Override
    public int countObjectPrimitiveFields(Object obj) {
        int size = 0;
        Field[] fields = getObjFields(obj);
        for (Field field : fields) {
            if (objectFieldIsPrimitive(field, obj)) {
                size++;
            }
        }
        return size;
    }

    @Override
    public boolean hasPrimitiveFields(Object obj) {
        return countObjectPrimitiveFields(obj) > 0;
    }


    @Override
    public boolean objectFieldIsPrimitive(Field field, Object obj) {
        if (!ljv.canIgnoreField(field)) {
            //- The order of these statements matters. It is not correct
            //- to return true if field.getType( ).isPrimitive( )
            Object val = ObjectUtils.value(obj, field);
            //- Just calling ljv.canTreatAsPrimitive is not adequate --
            //- val will be wrapped as a Boolean or Character, etc. if we
            //- are dealing with a truly primitive type.
            return field.getType().isPrimitive() || canTreatObjAsPrimitive(val);
        }

        return false;
    }

    @Override
    public boolean canBeConvertedToString(Object obj) {
        Method[] ms = obj.getClass().getMethods();
        for (Method m : ms) {
            if (m.getName().equals("toString") && m.getDeclaringClass() != Object.class) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean catTreatObjAsArrayOfPrimitives(Object obj) {
        Class<?> c = obj.getClass();
        if (c.getComponentType().isPrimitive()) {
            return true;
        }

        for (int i = 0, len = Array.getLength(obj); i < len; i++) {
            if (!canTreatObjAsPrimitive(Array.get(obj, i))) {
                return false;
            }
        }

        return true;
    }

    private Predicate<Field> getObjFieldsIgnoreNullValuedPredicate(Object obj) {
        return (Field f) -> {
            if (ljv.isIgnoreNullValuedFields()) {
                return ObjectUtils.value(obj, f) != null;
            }
            return true;
        };
    }

    private static void normalizeFieldsOrder(Field[] fs) {
        /*Ensure that 'left' field is always processed before 'right'.
        The problem is that ReflectionUtils.getAllFields uses HashSet, not LinkedHashSet,
        and loses information about fields order.

        This is a hard-coded logic and should be removed in the future.
         */
        int i = 0, left = -1, right = -1;
        for (Field f : fs) {
            if ("left".equals(f.getName())) {
                left = i;
                break;
            } else if ("right".equals(f.getName())) {
                right = i;
            }
            i++;
        }
        if (right > -1 && left > right) {
            //swap left & right
            Field f = fs[left];
            fs[left] = fs[right];
            fs[right] = f;
        }
    }
}
