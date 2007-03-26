package org.mifos.framework.components.audit.util.helpers;

import java.util.Locale;

import org.mifos.framework.struts.tags.MifosTagUtils;
import org.mifos.framework.util.helpers.DateUtils;

public class AuditLogView {

	private String date;
	private String field;
	private String oldValue;
	private String newValue;
	private String user;
	private Locale mfiLocale;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getMfiDate() {
		return DateUtils.getUserLocaleDate(getMfiLocale(), getDate().toString());
	}

	public Locale getMfiLocale() {
		return mfiLocale;
	}
	public void setMfiLocale(Locale mfiLocale) {
		this.mfiLocale = mfiLocale;
	}
	public String getNewValue() {
		return MifosTagUtils.xmlEscape(newValue);
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	public String getOldValue() {
		return MifosTagUtils.xmlEscape(oldValue);
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}

	
}
