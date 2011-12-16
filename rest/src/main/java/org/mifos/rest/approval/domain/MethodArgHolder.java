package org.mifos.rest.approval.domain;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class MethodArgHolder {

    private final Class<?>[] types;

    private final Object[] values;

    private final String[] names;

    @JsonCreator
    public MethodArgHolder(@JsonProperty("types") Class<?>[] types,
                           @JsonProperty("values") Object[] values,
                           @JsonProperty("names") String[] names) {
		this.types = types;
		this.values = values;
		this.names = names;
	}

    public Object[] getValues() {
        return values;
    }

    public Class<?>[] getTypes() {
        return types;
    }

    public String[] getNames() {
        return names;
    }

}
