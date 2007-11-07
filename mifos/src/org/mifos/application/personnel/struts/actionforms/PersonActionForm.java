package org.mifos.application.personnel.struts.actionforms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.GenericValidator;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.Localization;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class PersonActionForm extends BaseActionForm {

	private String input;

	private String searchString;

	private String personnelId;

	private String level;

	private String globalPersonnelNum;

	private String officeId;

	private String title;

	private String passwordRepeat;

	private String firstName;

	private String middleName;

	private String lastName;

	private String secondLastName;

	private String userPassword;

	private Address address;

	private String status;

	private String preferredLocale;

	private String searchId;

	private String loginName;

	private String emailId;

	private String governmentIdNumber;

	private String dobDD;
	
	private String dobMM;
	
	private String dobYY;

	private int age;

	private String maritalStatus;

	private String gender;
	
	private String dateOfJoiningMFIDD;
	
	private String dateOfJoiningMFIMM;
	
	private String dateOfJoiningMFIYY;

	private String dateOfJoiningBranch;

	private String[] personnelRoles;

	private List<CustomFieldView> customFields;
	
	public PersonActionForm() {
		super();

		address = new Address();
		customFields = new ArrayList<CustomFieldView>();
		personnelRoles = null;

	}

	public List<CustomFieldView> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<CustomFieldView> customFields) {
		this.customFields = customFields;
	}

	public CustomFieldView getCustomField(int i) {
		return getCustomField(customFields, i);
	}

	public String getDateOfJoiningBranch() {
		return dateOfJoiningBranch;
	}

	public void setDateOfJoiningBranch(String dateOfJoiningBranch) {
		this.dateOfJoiningBranch = dateOfJoiningBranch;
	}

	public String getDateOfJoiningMFI() {
		if (StringUtils.isNullAndEmptySafe(dateOfJoiningMFIDD)
				&& StringUtils.isNullAndEmptySafe(dateOfJoiningMFIMM)
				&& StringUtils.isNullAndEmptySafe(dateOfJoiningMFIYY)) {
			//	kim temporarily implemented this
			String dateSeparator = Localization.getInstance().getDateSeparator();
			return dateOfJoiningMFIDD + dateSeparator+ dateOfJoiningMFIMM + dateSeparator
				+ dateOfJoiningMFIYY;

			
		}
		else {
			return null;
		}
		
	}

	public void setDateOfJoiningMFI(String dateOfJoiningMFI) {
		Calendar cal = new GregorianCalendar();
		java.sql.Date date = DateUtils.getDateAsSentFromBrowser(dateOfJoiningMFI);
		cal.setTime(date);
		dateOfJoiningMFIDD = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		dateOfJoiningMFIMM = Integer.toString(cal.get(Calendar.MONTH) + 1);
		dateOfJoiningMFIYY = Integer.toString(cal.get(Calendar.YEAR));
	}

	public String getDob() {
		if (!StringUtils.isNullAndEmptySafe(dobDD)
				|| !StringUtils.isNullAndEmptySafe(dobMM)
				|| !StringUtils.isNullAndEmptySafe(dobYY)) {
			return null;
		}
		else {
			//			kim temporarily implemented this
			String dateSeparator = Localization.getInstance().getDateSeparator();
			return dobDD + dateSeparator + dobMM + dateSeparator + dobYY;
			
		}
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGlobalPersonnelNum() {
		return globalPersonnelNum;
	}

	public void setGlobalPersonnelNum(String globalPersonnelNum) {
		this.globalPersonnelNum = globalPersonnelNum;
	}

	public String getGovernmentIdNumber() {
		return governmentIdNumber;
	}

	public void setGovernmentIdNumber(String governmentIdNumber) {
		this.governmentIdNumber = governmentIdNumber;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getPersonnelId() {
		return personnelId;
	}

	public void setPersonnelId(String personnelId) {
		this.personnelId = personnelId;
	}

	public String[] getPersonnelRoles() {
		return personnelRoles;
	}

	public void setPersonnelRoles(String[] personnelRoles) {
		this.personnelRoles = personnelRoles;
	}

	public String getPreferredLocale() {
		return preferredLocale;
	}

	public void setPreferredLocale(String preferredLocale) {
		this.preferredLocale = preferredLocale;
	}

	public String getSearchId() {
		return searchId;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void clear() {
		this.personnelId = null;
		this.level = null;
		this.globalPersonnelNum = null;
		this.officeId = null;
		this.title = null;
		this.firstName = null;
		this.middleName = null;
		this.secondLastName = null;
		this.lastName = null;
		this.status = null;
		this.preferredLocale = null;
		this.searchId = null;
		this.loginName = null;
		this.userPassword = null;
		this.emailId = null;
		this.governmentIdNumber = null;
		this.dobDD = null;
		this.dobMM = null;
		this.dobYY = null;
		this.maritalStatus = null;
		this.gender = null;
		this.dateOfJoiningMFIDD = null;
		this.dateOfJoiningMFIMM = null;
		this.dateOfJoiningMFIYY = null;
		this.dateOfJoiningBranch = null;
		this.personnelRoles = new String[10];
		this.input=null;
		this.searchString=null;
		address = new Address();
		customFields = new ArrayList<CustomFieldView>();
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getSecondLastName() {
		return secondLastName;
	}

	public void setSecondLastName(String secondLastName) {
		this.secondLastName = secondLastName;
	}

	public String getPasswordRepeat() {
		return passwordRepeat;
	}

	public void setPasswordRepeat(String passwordRepeat) {
		this.passwordRepeat = passwordRepeat;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getAge() {
		if (getDob() != null) {
			return String.valueOf(DateUtils.DateDiffInYears(new java.sql.Date(
			DateUtils.getDate(getDob()).getTime())));
		} else
			return "";
	}

	public Name getName() {
		return new Name(firstName, middleName, secondLastName, lastName);
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter("method");
		request.setAttribute(Constants.CURRENTFLOWKEY, request
				.getParameter(Constants.CURRENTFLOWKEY));
		request.getSession().setAttribute(Constants.CURRENTFLOWKEY, request
				.getParameter(Constants.CURRENTFLOWKEY));

		if (method.equals(Methods.preview.toString())) {
			handleCreatePreviewValidations(errors, request);
		}
		if (method.equals(Methods.previewManage.toString())) {
			handleManagePreviewValidations(errors, request);
		}

		if (method.equals(Methods.search.toString())) {
			if( StringUtils.isNullOrEmpty(searchString))
			{
				try {
				cleanUpSearch(request);
				} catch (PageExpiredException e) {

					errors.add(ExceptionConstants.PAGEEXPIREDEXCEPTION,new ActionMessage(ExceptionConstants.PAGEEXPIREDEXCEPTION));
				}
				errors.add(PersonnelConstants.NO_SEARCH_STRING,new ActionMessage(PersonnelConstants.NO_SEARCH_STRING));
			}
		}

		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
			// update the role list also

			try {
				updateRoleLists(request);
			} catch (PageExpiredException e) {
				errors.add(ExceptionConstants.PAGEEXPIREDEXCEPTION,new ActionMessage(ExceptionConstants.PAGEEXPIREDEXCEPTION));
			}

		}

		return errors;

	}

	private void updateRoleLists(HttpServletRequest request)
			throws PageExpiredException {

		boolean addFlag = false;
		List<RoleBO> selectList = new ArrayList<RoleBO>();
		if (personnelRoles != null) {

			List<RoleBO> masterList = (List<RoleBO>) SessionUtils.getAttribute(
					PersonnelConstants.ROLEMASTERLIST, request);

			if( masterList!=null)
			for (RoleBO role : masterList) {
				for (String roleId : personnelRoles) {
					if (roleId != null
							&& role.getId().intValue() == Integer.valueOf(
									roleId).intValue()) {
						selectList.add(role);
						addFlag = true;
					}
				}
			}
		}
		if (addFlag)
			SessionUtils.setCollectionAttribute(PersonnelConstants.PERSONNEL_ROLES_LIST,
					selectList, request);
		else
			SessionUtils.setAttribute(PersonnelConstants.PERSONNEL_ROLES_LIST,
					null, request);

		personnelRoles = null;
	}

	private ActionErrors checkForPassword(ActionErrors errors) {

		// if password and confirm passowrd entries are made of only spaces,
		// throw an exception
		if (userPassword != null && passwordRepeat != null
				&& userPassword.length() == passwordRepeat.length()
				&& userPassword.length() != 0 && userPassword.trim().equals("")) {
			errors.add(PersonnelConstants.PASSWORD_MASK, new ActionMessage(
					PersonnelConstants.PASSWORD_MASK,
					PersonnelConstants.PASSWORD));
		}
		if (StringUtils.isNullAndEmptySafe(userPassword)
				&& StringUtils.isNullAndEmptySafe(passwordRepeat)
				&& !(userPassword.trim().equals(passwordRepeat.trim()))) {
			errors.add(PersonnelConstants.PASSWORD, new ActionMessage(
					PersonnelConstants.VALID_PASSWORD,
					PersonnelConstants.PASSWORD));
		}
		if ((StringUtils.isNullAndEmptySafe(passwordRepeat)
				&& !StringUtils.isNullAndEmptySafe(userPassword))
				|| ((!StringUtils.isNullAndEmptySafe(passwordRepeat)
				&& StringUtils.isNullAndEmptySafe(userPassword)))) {
			errors.add(PersonnelConstants.PASSWORD, new ActionMessage(
					PersonnelConstants.VALID_PASSWORD,
					PersonnelConstants.PASSWORD));
		}
		if (input.equals(PersonnelConstants.CREATE_USER)
				&& (StringUtils.isNullOrEmpty(userPassword) || StringUtils
						.isNullOrEmpty(passwordRepeat)))
			errors.add(PersonnelConstants.PASSWORD, new ActionMessage(
					PersonnelConstants.VALID_PASSWORD,
					PersonnelConstants.PASSWORD));
		if (input.equals(PersonnelConstants.CREATE_USER)&& (StringUtils.isNullAndEmptySafe(userPassword))){
			if( userPassword.length()<6)
				errors.add(PersonnelConstants.ERROR_PASSWORD_LENGTH, new ActionMessage(
						PersonnelConstants.ERROR_PASSWORD_LENGTH));
		}

		if (input.equals(PersonnelConstants.MANAGE_USER)) {
			if(userPassword.length()>0 && userPassword.length()<6){
				errors.add(PersonnelConstants.ERROR_PASSWORD_LENGTH, new ActionMessage(
						PersonnelConstants.ERROR_PASSWORD_LENGTH));
			}
		}

		return errors;
	}

	private void handleCreatePreviewValidations(ActionErrors errors,
			HttpServletRequest request) {
			validateNameDetail(errors);
			validateEmail(errors);
			validateDateOfBirth(errors);
			validateDateofJoiningMFI(errors);
			validateGender(errors);
			validateUserHirerchy(errors);
			validateloginName(errors);
			checkForPassword(errors);
			validateCustomFields(request, errors);
			validateConfigurableMandatoryFields(request, errors,EntityType.PERSONNEL);

	}

	private void validateNameDetail(ActionErrors errors) {
		if (StringUtils.isNullOrEmpty(firstName)) {
			errors.add(PersonnelConstants.ERROR_FIRSTNAME, new ActionMessage(
					PersonnelConstants.ERROR_FIRSTNAME));
		}
		if (StringUtils.isNullOrEmpty(lastName)) {
			errors.add(PersonnelConstants.ERROR_LASTNAME, new ActionMessage(
					PersonnelConstants.ERROR_LASTNAME));
		}
	}

	private void validateDateOfBirth(ActionErrors errors) {
		if (StringUtils.isNullOrEmpty(getDob())) {
			errors.add(PersonnelConstants.ERROR_DOB, new ActionMessage(
					PersonnelConstants.ERROR_DOB));
		}
		else if (!StringUtils.isNullOrEmpty(getDob())) {
			try {
				Date date = DateUtils.getDateAsSentFromBrowser(getDob());
				if (DateUtils.whichDirection(date) > 0) {
					throw new InvalidDateException(getDob());
				}
			}
			catch (InvalidDateException e) {
				errors.add(PersonnelConstants.INVALID_DOB, new ActionMessage(
						PersonnelConstants.INVALID_DOB));
			}
		}
	}
	
	private void validateDateofJoiningMFI(ActionErrors errors) {
		if (StringUtils.isNullOrEmpty(getDateOfJoiningMFI())) {
			return;
		}
		else {
			try {
				DateUtils.getDateAsSentFromBrowser(getDateOfJoiningMFI());
			}
			
			catch (InvalidDateException e) {
				errors.add(PersonnelConstants.ERROR_MFIDATE, new ActionMessage(
						PersonnelConstants.INVALID_MFIDATE));
			}
		}
	}

	private void validateGender(ActionErrors errors) {
		if (StringUtils.isNullOrEmpty(gender)) {
			errors.add(PersonnelConstants.ERROR_GENDER, new ActionMessage(
					PersonnelConstants.ERROR_GENDER));
		}
	}

	private void validateEmail(ActionErrors errors) {
		if (!StringUtils.isNullOrEmpty(emailId)&&!GenericValidator.isEmail(emailId)) {
			errors.add(PersonnelConstants.ERROR_VALID_EMAIL, new ActionMessage(
					PersonnelConstants.ERROR_VALID_EMAIL));
		}
	}
	private void validateUserHirerchy(ActionErrors errors) {
		if (StringUtils.isNullOrEmpty(level)) {
			errors.add(PersonnelConstants.ERROR_LEVEL, new ActionMessage(
					PersonnelConstants.ERROR_LEVEL));
		}
	}
	private void validateloginName(ActionErrors errors){
		if (StringUtils.isNullOrEmpty(loginName)) {
			errors.add(PersonnelConstants.ERROR_USER_NAME, new ActionMessage(
					PersonnelConstants.ERROR_USER_NAME));
		} else if (loginName.trim().contains(" ")) {
			errors.add(PersonnelConstants.INVALID_USER_NAME, new ActionMessage(
					PersonnelConstants.INVALID_USER_NAME));
		}
	}
	private void handleManagePreviewValidations(ActionErrors errors,
			HttpServletRequest request) {
		validateNameDetail(errors);
		validateEmail(errors);
		validateDateOfBirth(errors);
		validateDateofJoiningMFI(errors);
		validateGender(errors);
		validateStatus(errors);
		validateOffice(errors);
		validateUserHirerchy(errors);
		validateloginName(errors);
		checkForPassword(errors);
		validateCustomFields(request, errors);
		validateConfigurableMandatoryFields(request, errors,EntityType.PERSONNEL);
	}

	private void validateStatus(ActionErrors errors) {
		if (StringUtils.isNullOrEmpty(status)) {
			errors.add(PersonnelConstants.STATUS, new ActionMessage(
					CustomerConstants.ERRORS_MANDATORY,
					PersonnelConstants.STATUS));
		}

	}

	private void validateOffice(ActionErrors errors) {
		if (StringUtils.isNullOrEmpty(officeId)) {
			errors.add(PersonnelConstants.OFFICE, new ActionMessage(
					CustomerConstants.ERRORS_MANDATORY,
					PersonnelConstants.OFFICE));
		}
	}

	protected void validateCustomFields(HttpServletRequest request,
			ActionErrors errors) {
		List<CustomFieldDefinitionEntity> customFieldDefs = null;

		try {
			customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
					.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		} catch (PageExpiredException e) {

			// ignore it
		}
		if (customFieldDefs != null)
			for (CustomFieldView customField : customFields) {
				boolean isErrorFound = false;
				for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
					if (customField.getFieldId().equals(
							customFieldDef.getFieldId())
							&& customFieldDef.isMandatory())
						if (StringUtils.isNullOrEmpty(customField
								.getFieldValue())) {
							errors
									.add(
											CustomerConstants.CUSTOM_FIELD,
											new ActionMessage(
													OfficeConstants.ENTERADDTIONALINFO));
							isErrorFound = true;
							break;
						}

				}
				if (isErrorFound)
					break;
			}
	}

	private void validateConfigurableMandatoryFields(
			HttpServletRequest request, ActionErrors errors,
			EntityType entityType) {
		checkForMandatoryFields(entityType.getValue(), errors, request);
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public String getDobYY() {
		return dobYY;
	}
	
	public void setDob(String s) {
		Calendar cal = new GregorianCalendar();
		java.sql.Date dob = DateUtils.getDateAsSentFromBrowser(s);
		cal.setTime(dob);
		dobDD = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		dobMM = Integer.toString(cal.get(Calendar.MONTH) + 1);
		dobYY = Integer.toString(cal.get(Calendar.YEAR));
	}

	public void setDobYY(String dobYY) {
		this.dobYY = dobYY;
	}

	public String getDobMM() {
		return dobMM;
	}

	public void setDobMM(String dobMM) {
		this.dobMM = dobMM;
	}

	public String getDobDD() {
		return dobDD;
	}

	public void setDobDD(String dobDD) {
		this.dobDD = dobDD;
	}

	public String getDateOfJoiningMFIDD() {
		return dateOfJoiningMFIDD;
	}

	public void setDateOfJoiningMFIDD(String dateOfJoiningMFIDD) {
		this.dateOfJoiningMFIDD = dateOfJoiningMFIDD;
	}

	public String getDateOfJoiningMFIMM() {
		return dateOfJoiningMFIMM;
	}

	public void setDateOfJoiningMFIMM(String dateOfJoiningMFIMM) {
		this.dateOfJoiningMFIMM = dateOfJoiningMFIMM;
	}

	public String getDateOfJoiningMFIYY() {
		return dateOfJoiningMFIYY;
	}

	public void setDateOfJoiningMFIYY(String dateOfJoiningMFIYY) {
		this.dateOfJoiningMFIYY = dateOfJoiningMFIYY;
	}

}
