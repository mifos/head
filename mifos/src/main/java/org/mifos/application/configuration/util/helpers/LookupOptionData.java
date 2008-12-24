package org.mifos.application.configuration.util.helpers;

import java.io.Serializable;

public class LookupOptionData implements Serializable {
	private Short valueListId;
	private String lookupValue;
	private Integer lookupId;
	public Integer getLookupId() {
		return lookupId;
	}
	public void setLookupId(Integer lookupId) {
		this.lookupId = lookupId;
	}
	public String getLookupValue() {
		return lookupValue;
	}
	public void setLookupValue(String lookupValue) {
		this.lookupValue = lookupValue;
	}
	public Short getValueListId() {
		return valueListId;
	}
	public void setValueListId(Short valueListId) {
		this.valueListId = valueListId;
	}

}
