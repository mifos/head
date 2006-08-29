package org.mifos.application.personnel.business;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.master.util.valueobjects.SupportedLocales;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.framework.business.BusinessObject;

public class PersonnelBO extends BusinessObject {

	private final Short personnelId;

	private PersonnelLevelEntity level;

	private final String globalPersonnelNum;

	private OfficeBO office;

	private Integer title;

	private String displayName;

	private PersonnelStatusEntity status;

	private SupportedLocales preferredLocale;

	private String searchId;

	private Integer maxChildCount;

	private String password;

	private byte[] encriptedPassword;

	private final String userName;

	private String emailId;

	private Short passwordChanged;

	private Date lastLogin;

	private Short locked;

	private Short noOfTries;

	private PersonnelDetailsEntity personnelDetails;

	private Set<PersonnelRoleEntity> personnelRoles;

	private Set<PersonnelCustomFieldEntity> customFields;

	protected PersonnelBO() {
		this.level = null;
		this.personnelDetails = new PersonnelDetailsEntity();
		this.preferredLocale = new SupportedLocales();
		this.customFields = new HashSet<PersonnelCustomFieldEntity>();
		this.personnelId = null;
		this.globalPersonnelNum = null;
		this.userName = null;
	}

	public Set<PersonnelCustomFieldEntity> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Set<PersonnelCustomFieldEntity> customFields) {
		this.customFields = customFields;
	}

	public String getEmailId() {
		return emailId;
	}

	public String getGlobalPersonnelNum() {
		return globalPersonnelNum;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public PersonnelLevelEntity getLevel() {
		return level;
	}

	public Integer getMaxChildCount() {
		return maxChildCount;
	}

	public OfficeBO getOffice() {
		return office;
	}

	public String getPassword() {
		return password;
	}

	public boolean isPasswordChanged() {
		return this.passwordChanged > 0;
	}

	public PersonnelDetailsEntity getPersonnelDetails() {
		return personnelDetails;
	}

	public void setPersonnelDetails(PersonnelDetailsEntity personnelDetails) {
		this.personnelDetails = personnelDetails;
	}

	public Short getPersonnelId() {
		return personnelId;
	}

	public SupportedLocales getPreferredLocale() {
		return preferredLocale;
	}

	public Integer getTitle() {
		return title;
	}

	public String getUserName() {
		return userName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public boolean isLocked() {
		return this.locked > 0;
	}

	public void lock() {
		this.locked = Short.valueOf("1");
	}

	public void unLock() {
		this.locked = Short.valueOf("0");
	}

	public Set<PersonnelRoleEntity> getPersonnelRoles() {
		return personnelRoles;
	}

	public void setPersonnelRoles(Set<PersonnelRoleEntity> personnelRoles) {
		this.personnelRoles = personnelRoles;
	}

	private void updateCustomFields(List<CustomFieldView> customfields) {
		if (this.customFields != null && customfields != null) {
			for (CustomFieldView fieldView : customfields)
				for (PersonnelCustomFieldEntity fieldEntity : this.customFields)
					if (fieldView.getFieldId().equals(fieldEntity.getFieldId()))
						fieldEntity.setFieldValue(fieldView.getFieldValue());
		}
	}

	public void setEncriptedPassword(byte[] encriptedPassword) {
		this.encriptedPassword = encriptedPassword;
	}

	public PersonnelStatusEntity getStatus() {
		return status;
	}
}
