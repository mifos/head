package org.mifos.application.personnel.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.StringUtils;

public class PersonnelSettingsActionForm extends BaseActionForm {

	private String firstName;

	private String middleName;

	private String lastName;

	private String secondLastName;

	private String emailId;

	private String governmentIdNumber;

	private String dob;

	private String maritalStatus;

	private String gender;

	private String preferredLocale;

	private String userName;

	private String displayName;

	private Address address;

	public String getDisplayName() {
		return getName().getDisplayName();
	}

	public PersonnelSettingsActionForm() {
		address = new Address();
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGovernmentIdNumber() {
		return governmentIdNumber;
	}

	public void setGovernmentIdNumber(String governmentIdNumber) {
		this.governmentIdNumber = governmentIdNumber;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getPreferredLocale() {
		return preferredLocale;
	}

	public void setPreferredLocale(String preferredLocale) {
		this.preferredLocale = preferredLocale;
	}

	public String getSecondLastName() {
		return secondLastName;
	}

	public void setSecondLastName(String secondLastName) {
		this.secondLastName = secondLastName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAddressDetails() {
		return address.getDisplayAddress();
	}

	public Integer getGenderValue() {
		return getIntegerValue(getGender());
	}

	public Short getPreferredLocaleValue() {
		return getShortValue(getPreferredLocale());
	}

	public Name getName() {
		return new Name(firstName, middleName, secondLastName, lastName);
	}

	public Integer getMaritalStatusValue() {
		return getIntegerValue(getMaritalStatus());
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter(Methods.method.toString());
		if (method.equals(Methods.preview.toString())) {
			if (StringUtils.isNullOrEmpty(getFirstName()))
				addError(errors, PersonnelConstants.FIRSTNAME,
						PersonnelConstants.ERRORMANDATORY,
						PersonnelConstants.FIRST_NAME);
			else if (getFirstName().length() > PersonnelConstants.PERSONNELLENGTH)
				addError(errors, PersonnelConstants.FIRSTNAME,
						PersonnelConstants.MAXIMUM_LENGTH,
						PersonnelConstants.FIRST_NAME,
						PersonnelConstants.PERSONNELNAMELENGTH);
			if (StringUtils.isNullOrEmpty(getLastName()))
				addError(errors, PersonnelConstants.LASTNAME,
						PersonnelConstants.ERRORMANDATORY,
						PersonnelConstants.LAST_NAME);
			else if (getLastName().length() > PersonnelConstants.PERSONNELLENGTH)
				addError(errors, PersonnelConstants.LASTNAME,
						PersonnelConstants.MAXIMUM_LENGTH,
						PersonnelConstants.LAST_NAME,
						PersonnelConstants.PERSONNELNAMELENGTH);
			if (StringUtils.isNullOrEmpty(getGender()))
				addError(errors, PersonnelConstants.GENDERVALUE,
						PersonnelConstants.MANDATORYSELECT,
						PersonnelConstants.GENDERVALUE);
			if (getDisplayName().length() > PersonnelConstants.PERSONNELDISPLAYNAMELENGTH)
				addError(errors, PersonnelConstants.DISPLAYNAME,
						PersonnelConstants.MAXIMUM_LENGTH,
						PersonnelConstants.DISPLAY_NAME,
						PersonnelConstants.PERSONNELDISPLAYLENGTH);
			validateEmail(errors);
		}
		if (!method.equals(Methods.validate.toString()))
			request.setAttribute("methodCalled", method);
		return errors;
	}
	private void validateEmail(ActionErrors errors) {
		if (!StringUtils.isNullOrEmpty(emailId)&&!GenericValidator.isEmail(emailId)) {
			errors.add(PersonnelConstants.ERROR_VALID_EMAIL, new ActionMessage(
					PersonnelConstants.ERROR_VALID_EMAIL));
		}
	}
}
