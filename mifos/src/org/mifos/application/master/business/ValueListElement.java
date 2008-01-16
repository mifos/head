package org.mifos.application.master.business;

import java.io.Serializable;

public interface ValueListElement extends Serializable {

	public abstract Integer getId();

	public abstract void setId(Integer id);

	public abstract String getName();

	public abstract void setName(String name);
	
	public abstract String getValueKey();

	public abstract void setValueKey(String valueKey);

}
