package org.mifos.application.master.business;

import org.mifos.application.master.MessageLookup;
import org.mifos.config.LocalizedTextLookup;


/**
 * An individual item from a database based list (aka lookup values). 
 * This class serves the same role as the {@link CustomValueListElement} class,
 * but it is used for both fixed an custom lists.
 */
public class BusinessActivityEntity implements ValueListElement, LocalizedTextLookup{

	private Integer id;

	// name is the valueKey, will be refactored later on
	private String name;
	
	private String valueKey;

	public BusinessActivityEntity(Integer id, String name, String valueKey) {
		this.id = id;
		this.name = name;
		this.valueKey = valueKey;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		String lookupName = MessageLookup.getInstance().lookup(this);
		
		/*
		 * Workaround for cases where the lookupValue name is null.
		 */
		if (lookupName == null) {
			return name;
		} else {
			return lookupName;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValueKey() {
		return valueKey;
	}

	public void setValueKey(String valueKey) {
		this.valueKey = valueKey;
	}

	public String getPropertiesKey() {
		return getValueKey();
	}

}
