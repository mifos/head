package org.mifos.application.admin.struts.action;

public class ViewConfigurationSettingsUIBean implements Comparable {

	private final String dbKey;
	private final String value;
	private final String uiString;

	public ViewConfigurationSettingsUIBean(String dbKey, String value,
			String uiString) {
		this.dbKey = dbKey;
		this.value = value;
		this.uiString = uiString;
	}

	public String getDbKey() {
		return dbKey;
	}

	public String getUiString() {
		return uiString;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((dbKey == null) ? 0 : dbKey.hashCode());
		result = PRIME * result
				+ ((uiString == null) ? 0 : uiString.hashCode());
		result = PRIME * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ViewConfigurationSettingsUIBean other = (ViewConfigurationSettingsUIBean) obj;
		if (dbKey == null) {
			if (other.dbKey != null)
				return false;
		}
		else if (!dbKey.equals(other.dbKey))
			return false;
		if (uiString == null) {
			if (other.uiString != null)
				return false;
		}
		else if (!uiString.equals(other.uiString))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}


	public int compareTo(Object o) {
		return uiString
				.compareTo(((ViewConfigurationSettingsUIBean) o).uiString);
	}
	
	@Override
	public String toString() {
		return "dbkey="+dbKey+" uiString="+uiString+" value="+value;
	}

}
