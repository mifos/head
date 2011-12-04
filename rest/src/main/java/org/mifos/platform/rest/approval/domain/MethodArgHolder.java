package org.mifos.platform.rest.approval.domain;

public class MethodArgHolder {

    private Class<?>[] types;

    private Object[] values;

    public void setTypes(Class<?>[] types) {
        this.types = types;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public Object[] getValues() {
        return values;
    }

    public Class<?>[] getTypes() {
        return types;
    }

}
