package org.mifos.framework.util.valueobjects;

import java.io.Serializable;

/**
 * This is the interface which would be implemented by all the ValueObjects and
 * this interface will inturn extend serializable interface
 */
public class ValueObject implements Serializable, ReturnType {

	private String resultName;
	
	public String getResultName() {
		return this.resultName;
	}

	public void setResultName(String name) {
		this.resultName = name;
	}
	
}