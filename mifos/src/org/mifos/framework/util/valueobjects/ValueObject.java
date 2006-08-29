package org.mifos.framework.util.valueobjects;

import java.io.Serializable;

/**
 * This is the interface which would be implemented by all the ValueObjects and
 * this interface will inturn extend serializable interface
 */
public abstract class ValueObject implements Serializable, ReturnType {

	public String getResultName() {
		return null;
	}

}
