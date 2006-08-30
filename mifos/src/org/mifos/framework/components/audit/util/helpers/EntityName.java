/**
 * 
 */
package org.mifos.framework.components.audit.util.helpers;

public class EntityName {
	
	String name=null;
	
	ClassPath classPath=null;
	
	PkColumn pkColumn=null;

	public ClassPath getClassPath() {
		return classPath;
	}

	public void setClassPath(ClassPath classPath) {
		this.classPath = classPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PkColumn getPkColumn() {
		return pkColumn;
	}

	public void setPkColumn(PkColumn pkColumn) {
		this.pkColumn = pkColumn;
	}
	
	

}
