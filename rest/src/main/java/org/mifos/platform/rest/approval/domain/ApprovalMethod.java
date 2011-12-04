package org.mifos.platform.rest.approval.domain;

public class ApprovalMethod {

    private String name;

    private Class<?> type;

    MethodArgHolder argsHolder;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public MethodArgHolder getArgsHolder() {
        return argsHolder;
    }

    public void setArgsHolder(MethodArgHolder argsHolder) {
        this.argsHolder = argsHolder;
    }
}
