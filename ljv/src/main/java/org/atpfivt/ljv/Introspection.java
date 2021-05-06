package org.atpfivt.ljv;

public interface Introspection {

    public String getObjClassName(Object obj, boolean useToStringAsClassName);

    public int getObjFieldsNum(Object obj);

    public boolean canBeConvertedToString(Object obj);

    public boolean canTreatObjAsPrimitive(Object obj);

    public boolean catTreatObjAsArrayOfPrimitives(Object obj);

    public boolean canTreatClassAsPrimitive(Class<?> cz);

    public boolean hasPrimitiveFields(Object obj);

}
