package org.mifos.application.master.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

public class GLCode extends ValueObject {

	/**Denotes the glcode id*/
	private Short glCodeId;
	/**Denotes the glcode name*/
	private String glCodeValue;
	
	/**
	 * @return Returns the glCodeId.
	 */
	public Short getGlCodeId() {
		return glCodeId;
	}
	/**
	 * @param glCodeId The glCodeId to set.
	 */
	public void setGlCodeId(Short glCodeId) {
		this.glCodeId = glCodeId;
	}
	/**
	 * @return Returns the glCodeValue.
	 */
	public String getGlCodeValue() {
		return glCodeValue;
	}
	/**
	 * @param glCodeValue The glCodeValue to set.
	 */
	public void setGlCodeValue(String glCodeValue) {
		this.glCodeValue = glCodeValue;
	}
	
	

}
