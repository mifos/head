/**
 * 
 */
package org.mifos.framework.components.audit.util.helpers;

/**
 * @author krishankg
 *
 */
public class EntityType {
	
	String name=null;
	
	PropertyName[] propertyNames=null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PropertyName[] getPropertyNames() {
		return propertyNames;
	}

	public void setPropertyNames(PropertyName[] propertyNames) {
		this.propertyNames = propertyNames;
	}


}
