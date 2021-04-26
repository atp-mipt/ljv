package org.atpfivt.ljv;

import java.lang.reflect.Field;


public interface Visualization {
    public void beginDOT();
    public void finishDOT();

    public void visitArrayBegin(Object array);
    public void visitArrayElement(Object array, Object elem);
    public void visitArrayEnd(Object array);

    public void visitObjectBegin(Object obj);
    public void visitObjectField(Object obj, Field field);
    public void visitObjectEnd(Object obj);

    public String generateResult();
}
