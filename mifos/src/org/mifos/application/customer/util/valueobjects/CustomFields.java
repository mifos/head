/**
 *
 */
package org.mifos.application.customer.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

public class CustomFields extends ValueObject {

	/**Denotes the id of the custom field*/
	private int fieldId;
	/**Denotes the id of the customer*/
	private int customerId;
	/**Denotes the custom field value for the customer*/
	private String value;

	/**
	 * Method which returns the customerId
	 * @return Returns the customerId.
	 */
	public int getCustomerId() {
		return customerId;
	}
	/**
	 * Method which sets the customerId
	 * @param customerId The customerId to set.
	 */
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	/**
	 * Method which returns the fieldId
	 * @return Returns the fieldId.
	 */
	public int getFieldId() {
		return fieldId;
	}
	/**
	 * Method which sets the fieldId
	 * @param fieldId The fieldId to set.
	 */
	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}
	/**
	 * Method which returns the value
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}
	/**
	 * Method which sets the value
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
