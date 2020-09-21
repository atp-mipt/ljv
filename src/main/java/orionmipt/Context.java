package orionmipt;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


class Context implements Cloneable {

    private final Map<Object, String> classAttributeMap;
    private final Map<Object, String> fieldAttributeMap;
    private final Set<Object> pretendPrimitiveSet;
    private final Set<Object> ignoreSet;

    /**
     * Allow private, protected and package-access fields to be shown.
     * This is only possible if the security manager allows
     * <code>ReflectPermission("suppressAccessChecks")</code> permission.
     * This is usually the case when running from an application, but
     * not from an applet or servlet.
     */
    private boolean ignorePrivateFields = false;

    /**
     * Toggle whether to display the class name in the label for an
     * object (false, the default) or to use the result of calling
     * toString (true).
     */
    public boolean useToStringAsClassName = false;

    /**
     * Toggle whether to display qualified nested class names in the
     * label for an object from the same package as LJV (true) or
     * to display an abbreviated name (false, the default).
     */
    private boolean qualifyNestedClassNames = false;
    private boolean showPackageNamesInClasses = true;

    /**
     * Toggle whether or not to include the field name in the label for an
     * object.  This is currently all-or-nothing.  TODO: allow this to be
     * set on a per-class basis.
     */
    private boolean showFieldNamesInLabels = true;


    private Context(Map<Object, String> classAttributeMap,
                    Map<Object, String> fieldAttributeMap,
                    Set<Object> pretendPrimitiveSet,
                    Set<Object> ignoreSet) {
        this.classAttributeMap = classAttributeMap;
        this.fieldAttributeMap = fieldAttributeMap;
        this.pretendPrimitiveSet = pretendPrimitiveSet;
        this.ignoreSet = ignoreSet;
    }

    public Context() {
        this.classAttributeMap = new HashMap<>();
        this.fieldAttributeMap = new HashMap<>();
        this.pretendPrimitiveSet = new HashSet<>();
        this.ignoreSet = new HashSet<>();
    }

    public Context clone() {
        return new Context(
                new HashMap<>(classAttributeMap),
                new HashMap<>(fieldAttributeMap),
                new HashSet<>(pretendPrimitiveSet),
                new HashSet<>(ignoreSet)
        );
    }

    public ContextBuilder toBuilder() {
        return new ContextBuilder(this);
    }

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

    boolean canIgnoreField(Field field) {
        return
                Modifier.isStatic(field.getModifiers())
                        || ignoreSet.contains(field)
                        || ignoreSet.contains(field.getName())
                        || ignoreSet.contains(field.getType())
                        || ignoreSet.contains(field.getType().getPackage())
                ;
    }


    /**
     * Treat objects of this class as primitives; i.e., <code>toString</code>
     * is called on the object, and the result displayed in the label like
     * a primitive field.
     */
    public void treatAsPrimitive(Class<?> cz) {
        pretendPrimitiveSet.add(cz);
    }

    public boolean isTreatsAsPrimitive(Class<?> cz) {
        return pretendPrimitiveSet.contains(cz);
    }

    /**
     * Treat objects from this package as primitives; i.e.,
     * <code>toString</code> is called on the object, and the result displayed
     * in the label like a primitive field.
     */
    public void treatAsPrimitive(Package pk) {
        pretendPrimitiveSet.add(pk);
    }

    public boolean isTreatsAsPrimitive(Package pk) {
        return pretendPrimitiveSet.contains(pk);
    }


    public void ignorePrivateFields(boolean ignorePrivateFields) {
        this.ignorePrivateFields = ignorePrivateFields;
    }

    public boolean isIgnorePrivateFields() {
        return ignorePrivateFields;
    }


    public void showFieldNamesInLabels(boolean showFieldNamesInLabels) {
        this.showFieldNamesInLabels = showFieldNamesInLabels;
    }

    public boolean isShowFieldNamesInLabels() {
        return showFieldNamesInLabels;
    }


    public void qualifyNestedClassNames(boolean qualifyNestedClassNames) {
        this.qualifyNestedClassNames = qualifyNestedClassNames;
    }

    public boolean isQualifyNestedClassNames() {
        return qualifyNestedClassNames;
    }


    public void showPackageNamesInClasses(boolean showPackageNamesInClasses) {
        this.showPackageNamesInClasses = showPackageNamesInClasses;
    }

    public boolean isShowPackageNamesInClasses() {
        return showPackageNamesInClasses;
    }
}
