package org.atpfivt.ljv;


public interface Visualization {
    public void beginDOT();
    public void finishDOT();

    public void visitNull();

    public void visitArrayBegin(Object array, boolean hasPrimitiveValues);
    public void visitArrayElement(Object array, Object element, int elementIndex, boolean isPrimitive);
    public void visitArrayElementObjectConnection(Object array, int elementIndex, Object obj);
    public void visitArrayEnd(Object array);

    public void visitObjectBegin(Object obj, String className, int primitiveFieldsNum);
    public void visitObjectPrimitiveField(Object obj, String fieldName, String fieldValueStr);
    public void visitObjectEnd(Object obj);
    public void visitObjectFieldRelationWithNonPrimitiveObject(Object obj, String fieldName, String ljvFieldAttributes, Object relatedObject);

    public String generateResult();
}
