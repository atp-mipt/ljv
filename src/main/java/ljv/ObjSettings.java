package ljv;

import java.lang.reflect.*;

public class ObjSettings {
    private boolean fieldExistsAndIsPrimitive(LJV ljv, Field field, Object obj) {
        if (!ljv.canIgnoreField(field)) {
            try {
                //- The order of these statements matters.  If field is not
                //- accessible, we want an IllegalAccessException to be raised
                //- (and caught).  It is not correct to return true if
                //- field.getType( ).isPrimitive( )
                Object val = field.get(obj);
                if (field.getType().isPrimitive() || canTreatAsPrimitive(ljv, val))
                    //- Just calling ljv.canTreatAsPrimitive is not adequate --
                    //- val will be wrapped as a Boolean or Character, etc. if we
                    //- are dealing with a truly primitive type.
                    return true;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean hasPrimitiveFields(LJV ljv, Field[] fs, Object obj) {
        for (Field f : fs)
            if (fieldExistsAndIsPrimitive(ljv, f, obj))
                return true;
        return false;
    }

    private static boolean redefinesToString(Object obj) {
        Method[] ms = obj.getClass().getMethods();
        for (Method m : ms)
            if (m.getName().equals("toString") && m.getDeclaringClass() != Object.class)
                return true;
        return false;
    }


    public String className(Object obj, LJV LJV, boolean useToStringAsClassName) {
        if (obj == null)
            return "";

        Class<?> c = obj.getClass();
        if (useToStringAsClassName && redefinesToString(obj))
            return Quote.quote(obj.toString());
        else {
            String name = c.getName();
            if (!LJV.isShowPackageNamesInClasses() || c.getPackage() == LJV.class.getPackage()) {
                //- Strip away everything before the last .
                name = name.substring(name.lastIndexOf('.') + 1);

                if (!LJV.isQualifyNestedClassNames())
                    name = name.substring(name.lastIndexOf('$') + 1);
            }
            return name;
        }
    }

    public boolean canTreatAsPrimitive(LJV LJV, Object obj) {
        return obj == null || canTreatClassAsPrimitive(LJV, obj.getClass());
    }


    public boolean canTreatClassAsPrimitive(LJV LJV, Class<?> cz) {
        if (cz == null || cz.isPrimitive())
            return true;

        if (cz.isArray())
            return false;

        do {
            if (LJV.isTreatsAsPrimitive(cz)
                    || LJV.isTreatsAsPrimitive(cz.getPackage())
            )
                return true;

            if (cz == Object.class)
                return false;

            Class<?>[] ifs = cz.getInterfaces();
            for (Class<?> anIf : ifs)
                if (canTreatClassAsPrimitive(LJV, anIf))
                    return true;

            cz = cz.getSuperclass();
        } while (cz != null);
        return false;
    }

    public boolean looksLikePrimitiveArray(Object obj, LJV LJV) {
        Class<?> c = obj.getClass();
        if (c.getComponentType().isPrimitive())
            return true;

        for (int i = 0, len = Array.getLength(obj); i < len; i++)
            if (!canTreatAsPrimitive(LJV, Array.get(obj, i)))
                return false;
        return true;
    }
}
