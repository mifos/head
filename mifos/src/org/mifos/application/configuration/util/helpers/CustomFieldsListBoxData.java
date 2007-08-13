package org.mifos.application.configuration.util.helpers;

import java.io.Serializable;

public class CustomFieldsListBoxData implements Serializable {
	
	private String name;
	private Short id;
	
	public Short getId() {
		return id;
	}
	public void setId(Short id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

}
