package orionmipt;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


//defaultContext.ignorePrivateFields = true;
//defaultContext.treatAsPrimitive( Package.getPackage( "java.lang" ) );
class Context {
    /**
     * Set the DOT attributes for a class.  This allows you to change the
     * appearance of certain nodes in the output, but requires that you
     * know something about dot attributes.  Simple attributes are, e.g.,
     * "color=red".
     */
    public void setClassAttribute(Class<?> cz, String attrib) {
        classAttributeMap.put(cz, attrib);
    }

    public String getClassAtribute(Class<?> cz) {
        return classAttributeMap.get(cz);
    }

    /**
     * Set the DOT attributes for a specific field. This allows you to
     * change the appearance of certain edges in the output, but requires
     * that you know something about dot attributes.  Simple attributes
     * are, e.g., "color=blue".
     */
    public void setFieldAttribute(Field field, String attrib) {
        fieldAttributeMap.put(field, attrib);
    }

    public String getFieldAttribute(Field field) {
        return fieldAttributeMap.get(field);
    }

    /**
     * Set the DOT attributes for all fields with this name.
     */
    public void setFieldAttribute(String field, String attrib) {
        fieldAttributeMap.put(field, attrib);
    }

    public String getFieldAttribute(String field) {
        return fieldAttributeMap.get(field);
    }

    /**
     * Do not display this field.
     */
    public void ignoreField(Field field) {
        ignoreSet.add(field);
    }

    /**
     * Do not display any fields with this name.
     */
    public void ignoreField(String field) {
        ignoreSet.add(field);
    }

    /**
     * Do not display any fields from this class.
     */
    public void ignoreFields(Class<?> cz) {
        Field[] fs = cz.getDeclaredFields();
        for (int i = 0; i < fs.length; i++)
            ignoreField(fs[i]);
    }

    /**
     * Do not display any fields with this type.
     */
    public void ignoreClass(Class<?> cz) {
        ignoreSet.add(cz);
    }

    /**
     * Do not display any fields that have a type from this package.
     */
    public void ignorePackage(Package pk) {
        ignoreSet.add(pk);
    }

    /**
     * Treat objects of this class as primitives; i.e., <code>toString</code>
     * is called on the object, and the result displayed in the label like
     * a primitive field.
     */
    public void treatAsPrimitive(Class<?> cz) {
        pretendPrimitiveSet.add(cz);
    }

    /**
     * Treat objects from this package as primitives; i.e.,
     * <code>toString</code> is called on the object, and the result displayed
     * in the label like a primitive field.
     */
    public void treatAsPrimitive(Package pk) {
        pretendPrimitiveSet.add(pk);
    }

    private final Map<Object, String> classAttributeMap = new HashMap<>();
    private final Map<Object, String> fieldAttributeMap = new HashMap<>();
    private final Set<Object> pretendPrimitiveSet = new HashSet<>();
    private final Set<Object> ignoreSet = new HashSet<>();

    /**
     * Allow private, protected and package-access fields to be shown.
     * This is only possible if the security manager allows
     * <code>ReflectPermission("suppressAccessChecks")</code> permission.
     * This is usually the case when running from an application, but
     * not from an applet or servlet.
     */
    private boolean ignorePrivateFields = false;

    public void setIgnorePrivateFields(boolean ignorePrivateFields) {
        this.ignorePrivateFields = ignorePrivateFields;
    }

    public boolean isIgnorePrivateFields() {
        return ignorePrivateFields;
    }

    /**
     * Toggle whether or not to include the field name in the label for an
     * object.  This is currently all-or-nothing.  TODO: allow this to be
     * set on a per-class basis.
     */
    private boolean showFieldNamesInLabels = true;

    public void setShowFieldNamesInLabels(boolean showFieldNamesInLabels) {
        this.showFieldNamesInLabels = showFieldNamesInLabels;
    }

    public boolean isShowFieldNamesInLabels() {
        return showFieldNamesInLabels;
    }

    /**
     Toggle whether to display the class name in the label for an
     object (false, the default) or to use the result of calling
     toString (true).
     */
    //public boolean useToStringAsClassName = false;

    /**
     * Toggle whether to display qualified nested class names in the
     * label for an object from the same package as LJV (true) or
     * to display an abbreviated name (false, the default).
     */
    private boolean qualifyNestedClassNames = false;
    private boolean showPackageNamesInClasses = true;

    boolean canTreatAsPrimitive(Object obj) {
        return obj == null || canTreatClassAsPrimitive(obj.getClass());
    }


    private boolean canTreatClassAsPrimitive(Class<?> cz) {
        if (cz == null || cz.isPrimitive())
            return true;

        if (cz.isArray())
            return false;

        do {
            if (pretendPrimitiveSet.contains(cz)
                    || pretendPrimitiveSet.contains(cz.getPackage())
            )
                return true;

            if (cz == Object.class)
                return false;

            Class<?>[] ifs = cz.getInterfaces();
            for (int i = 0; i < ifs.length; i++)
                if (canTreatClassAsPrimitive(ifs[i]))
                    return true;

            cz = cz.getSuperclass();
        } while (cz != null);
        return false;
    }


    boolean looksLikePrimitiveArray(Object obj) {
        Class<?> c = obj.getClass();
        if (c.getComponentType().isPrimitive())
            return true;

        for (int i = 0, len = Array.getLength(obj); i < len; i++)
            if (!canTreatAsPrimitive(Array.get(obj, i)))
                return false;
        return true;
    }


    boolean canIgnoreField(Field field) {
        return
                Modifier.isStatic(field.getModifiers())
                        || ignoreSet.contains(field)
                        || ignoreSet.contains(field.getName())
                        || ignoreSet.contains(field.getType())
                        || ignoreSet.contains(field.getType().getPackage())
                ;
    }

    private static boolean redefinesToString(Object obj) {
        Method[] ms = obj.getClass().getMethods();
        for (int i = 0; i < ms.length; i++)
            if (ms[i].getName().equals("toString") && ms[i].getDeclaringClass() != Object.class)
                return true;
        return false;
    }


    protected String className(Object obj, boolean useToStringAsClassName) {
        if (obj == null)
            return "";

        Class<?> c = obj.getClass();
        if (useToStringAsClassName && redefinesToString(obj))
            return Quote.quote(obj.toString());
        else {
            String name = c.getName();
            if (!showPackageNamesInClasses || c.getPackage() == LJV.class.getPackage()) {
                //- Strip away everything before the last .
                name = name.substring(name.lastIndexOf('.') + 1);

                if (!qualifyNestedClassNames)
                    name = name.substring(name.lastIndexOf('$') + 1);
            }
            return name;
        }
    }

    public boolean isQualifyNestedClassNames() {
        return qualifyNestedClassNames;
    }

    public void setQualifyNestedClassNames(boolean qualifyNestedClassNames) {
        this.qualifyNestedClassNames = qualifyNestedClassNames;
    }

    public boolean isShowPackageNamesInClasses() {
        return showPackageNamesInClasses;
    }

    public void setShowPackageNamesInClasses(boolean showPackageNamesInClasses) {
        this.showPackageNamesInClasses = showPackageNamesInClasses;
    }
}
