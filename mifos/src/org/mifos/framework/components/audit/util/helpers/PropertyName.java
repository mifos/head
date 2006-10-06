/**
 * 
 */
package org.mifos.framework.components.audit.util.helpers;

public class PropertyName {
	
	String name=null;
	
	String displayKey=null;
	
	String doNotLog=null;
	
	String lookUp=null;
	
	String parentName=null;
	
	EntityName entityName=null;
	
	String methodName;

	public String getDisplayKey() {
		return displayKey;
	}

	public void setDisplayKey(String displayKey) {
		this.displayKey = displayKey;
	}

	public String getDoNotLog() {
		return doNotLog;
	}

	public void setDoNotLog(String doNotLog) {
		this.doNotLog = doNotLog;
	}

	public EntityName getEntityName() {
		return entityName;
	}

	public void setEntityName(EntityName entityName) {
		this.entityName = entityName;
	}

	public String getLookUp() {
		return lookUp;
	}

	public void setLookUp(String lookUp) {
		this.lookUp = lookUp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	
	

}
