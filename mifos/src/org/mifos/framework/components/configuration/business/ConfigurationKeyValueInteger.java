package org.mifos.framework.components.configuration.business;

/**
 * ConfigurationKeyValueInteger object hold String->int key value pairs
 * that can be persisted to a database using Hibernate.  Helper methods
 * for manipulating these values can be found in 
 * {@link org.mifos.framework.components.configuration.persistence.ConfigurationPersistence}
 */
public class ConfigurationKeyValueInteger {
	private Integer configurationId;
	private String key;
	private Integer value;
	
	public ConfigurationKeyValueInteger() {
	}
	
	public ConfigurationKeyValueInteger(String key, int value) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("A null key is not allowed for ConfigurationKeyValueInteger");
		}
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}
	
	private void setKey(String key) {
		this.key = key;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
