package org.mifos.rest.approval.domain;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class MethodArgHolder {

    private Class<?>[] types;

    private Object[] values;

    @JsonCreator
    public MethodArgHolder(@JsonProperty("types") Class<?>[] types,
                           @JsonProperty("values") Object[] values) {
		this.types = types;
		this.values = values;
	}

    public Object[] getValues() {
        return values;
    }

    public Class<?>[] getTypes() {
        return types;
    }

}
