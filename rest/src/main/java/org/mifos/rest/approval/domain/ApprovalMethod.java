package org.mifos.rest.approval.domain;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ApprovalMethod {

    private String name;

    private Class<?> type;

    MethodArgHolder argsHolder;

    @JsonCreator
	public ApprovalMethod(
	        @JsonProperty("name")  String name,
	        @JsonProperty("type")  Class<?> type,
	        @JsonProperty("argsHolder") MethodArgHolder argsHolder) {
		this.name = name;
		this.type = type;
		this.argsHolder = argsHolder;
	}

	public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public MethodArgHolder getArgsHolder() {
        return argsHolder;
    }
}
