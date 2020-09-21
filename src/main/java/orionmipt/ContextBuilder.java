package orionmipt;

import java.lang.reflect.Field;

public class ContextBuilder {
    private final Context result;

    ContextBuilder() {
        result = new Context();
    }

    ContextBuilder(Context context) {
        result = context.clone();
    }

    public Context build() {
        return result;
    }

    /**
     * Set the DOT attributes for a class.  This allows you to change the
     * appearance of certain nodes in the output, but requires that you
     * know something about dot attributes.  Simple attributes are, e.g.,
     * "color=red".
     */
    public ContextBuilder addClassAttribute(Class<?> cz, String attrib) {
        result.setClassAttribute(cz, attrib);
        return this;
    }

    /**
     * Set the DOT attributes for a specific field. This allows you to
     * change the appearance of certain edges in the output, but requires
     * that you know something about dot attributes.  Simple attributes
     * are, e.g., "color=blue".
     */
    public ContextBuilder addFieldAttribute(Field field, String attrib) {
        result.setFieldAttribute(field, attrib);
        return this;
    }

    /**
     * Set the DOT attributes for all fields with this name.
     */
    public ContextBuilder addFieldAttribute(String field, String attrib) {
        result.setFieldAttribute(field, attrib);
        return this;
    }

    /**
     * Do not display this field.
     */
    public ContextBuilder ignoreField(Field field) {
        result.ignoreField(field);
        return this;
    }

    /**
     * Do not display any fields with this name.
     */
    public ContextBuilder ignoreField(String field) {
        result.ignoreField(field);
        return this;
    }

    /**
     * Do not display any fields from this class.
     */
    public ContextBuilder ignoreFields(Class<?> cz) {
        result.ignoreFields(cz);
        return this;
    }

    /**
     * Do not display any fields with this type.
     */
    public ContextBuilder ignoreClass(Class<?> cz) {
        result.ignoreClass(cz);
        return this;
    }

    /**
     * Do not display any fields that have a type from this package.
     */
    public ContextBuilder ignorePackage(Package pk) {
        result.ignorePackage(pk);
        return this;
    }


    /**
     * Treat objects of this class as primitives; i.e., <code>toString</code>
     * is called on the object, and the result displayed in the label like
     * a primitive field.
     */
    public ContextBuilder treatAsPrimitive(Class<?> cz) {
        result.treatAsPrimitive(cz);
        return this;
    }

    /**
     * Treat objects from this package as primitives; i.e.,
     * <code>toString</code> is called on the object, and the result displayed
     * in the label like a primitive field.
     */
    public ContextBuilder treatAsPrimitive(Package pk) {
        result.treatAsPrimitive(pk);
        return this;
    }

    public ContextBuilder ignorePrivateFields(boolean ignorePrivateFields) {
        result.ignorePrivateFields(ignorePrivateFields);
        return this;
    }


    public ContextBuilder showFieldNamesInLabels(boolean showFieldNamesInLabels) {
        result.showFieldNamesInLabels(showFieldNamesInLabels);
        return this;
    }


    public ContextBuilder qualifyNestedClassNames(boolean qualifyNestedClassNames) {
        result.qualifyNestedClassNames(qualifyNestedClassNames);
        return this;
    }


    public ContextBuilder showPackageNamesInClasses(boolean showPackageNamesInClasses) {
        result.showPackageNamesInClasses(showPackageNamesInClasses);
        return this;
    }
}
