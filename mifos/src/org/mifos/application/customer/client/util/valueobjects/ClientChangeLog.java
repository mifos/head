/**
 * 
 */
package org.mifos.application.customer.client.util.valueobjects;

import java.sql.Date;
import java.util.Locale;

import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.valueobjects.ValueObject;

public class ClientChangeLog extends ValueObject {
	/**
	 * 
	 */
	public ClientChangeLog() {
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 54763476765476571L;
	
	private Date date;
	
	private String fieldName;
	
	private String oldValue;
	
	private String newValue;
	
	private String userName;
	
	private Locale mfiLocale=null;
	
	private String mfiDate=null; 

	/**
	 * @return Returns the date.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date The date to set.
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return Returns the fieldName.
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName The fieldName to set.
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return Returns the newValue.
	 */
	public String getNewValue() {
		return newValue;
	}

	/**
	 * @param newValue The newValue to set.
	 */
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	/**
	 * @return Returns the oldValue.
	 */
	public String getOldValue() {
		return oldValue;
	}

	/**
	 * @param oldValue The oldValue to set.
	 */
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName The userName to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return Returns the locale.
	 */
	public Locale getMfiLocale() {
		return mfiLocale;
	}

	/**
	 * @param locale The locale to set.
	 */
	public void setMfiLocale(Locale mfiLocale) {
		this.mfiLocale = mfiLocale;
	}

	/**
	 * @return Returns the mfiDate.
	 */
	public String getMfiDate() {
		return DateHelper.getUserLocaleDate(getMfiLocale(),getDate().toString()); 
	}
	
	
}
