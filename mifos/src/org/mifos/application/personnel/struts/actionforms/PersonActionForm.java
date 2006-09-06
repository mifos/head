package org.mifos.application.personnel.struts.actionforms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;


public class PersonActionForm extends BaseActionForm {

	private String input;

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

	private String dob;

	private int age;
	
	private String maritalStatus;

	private String gender;

	private String dateOfJoiningMFI;

	private String dateOfJoiningBranch;
	
	private String[] personnelRoles;

	private List<CustomFieldView> customFields;

	public PersonActionForm() {
		super();

		address = new Address();
		customFields = new ArrayList<CustomFieldView>();

	}

	public CustomFieldView getCustomField(int i) {
		while (i >= customFields.size()) {
			customFields.add(new CustomFieldView());
		}
		return (CustomFieldView) (customFields.get(i));
	}

	public List<CustomFieldView> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<CustomFieldView> customFields) {
		this.customFields = customFields;
	}

	public String getDateOfJoiningBranch() {
		return dateOfJoiningBranch;
	}

	public void setDateOfJoiningBranch(String dateOfJoiningBranch) {
		this.dateOfJoiningBranch = dateOfJoiningBranch;
	}

	public String getDateOfJoiningMFI() {
		return dateOfJoiningMFI;
	}

	public void setDateOfJoiningMFI(String dateOfJoiningMFI) {
		this.dateOfJoiningMFI = dateOfJoiningMFI;
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
		this.dob = null;
		this.maritalStatus = null;
		this.gender = null;
		this.dateOfJoiningMFI = null;
		this.dateOfJoiningBranch = null;
		this.personnelRoles = new String[10];;
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
	
	public String getAge(){
		if (dob!=null&&!dob.equals("") ){
			Date date = DateHelper.getDate(dob);
			Calendar calendar = Calendar.getInstance();
			return String.valueOf(DateHelper.DateDiffInYears(new java.sql.Date(date.getTime())));
		}
		else
			return "";
			
	}
	
	public Name getName(){
		return new Name(firstName,middleName,secondLastName,lastName);
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter("method");
		if (method.equals(Methods.preview.toString())) {
			handleCreatePreviewValidations(request);
			errors.add(super.validate(mapping, request));
		}
		if (method.equals(Methods.previewManage.toString())) {
			handleManagePreviewValidations(errors,request);
			errors.add(super.validate(mapping, request));
		}
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
		}

		return errors;

	}

	private ActionErrors checkForPassword(ActionErrors errors) {

		// if password and confirm passowrd entries are made of only spaces,
		// throw an exception
		if (userPassword != null && passwordRepeat != null
				&& userPassword.length() == passwordRepeat.length()
				&& userPassword.length() != 0 && userPassword.trim().equals("")) {
			if (errors == null)
				errors = new ActionErrors();
			errors.add(PersonnelConstants.PASSWORD_MASK, new ActionMessage(
					PersonnelConstants.PASSWORD_MASK,
					PersonnelConstants.PASSWORD));
		}

		return errors;
	}

	/**
	 * This is the helper method to check for extra validations e.g. date
	 * validations and password validations needed at the time of create
	 * preview.
	 * 
	 * @param request
	 */
	private ActionErrors handleCreatePreviewValidations(
			HttpServletRequest request) {
		
		ActionErrors errors = null;
		if (!StringUtils.isNullOrEmpty(dob)) {
			
			//sqlDOB = DateHelper.getLocaleDate(userContext.getPereferedLocale(), dob);
			Date date = DateHelper.getDate(dob);
			Calendar currentCalendar = new GregorianCalendar();
			int year = currentCalendar.get(Calendar.YEAR);
			int month = currentCalendar.get(Calendar.MONTH);
			int day = currentCalendar.get(Calendar.DAY_OF_MONTH);
			currentCalendar = new GregorianCalendar(year, month, day);
			Date currentDate = new Date(currentCalendar
					.getTimeInMillis());
			if (currentDate.compareTo(date) < 0) {
				errors = new ActionErrors();
				errors.add(PersonnelConstants.INVALID_DOB, new ActionMessage(
						PersonnelConstants.INVALID_DOB));
			}
		}
		if (errors == null) {
			errors = new ActionErrors();
		}

		validateCustomFields(request, errors);

		return checkForPassword(errors);
	}

	
	/**
	 * This is the helper method to check for extra validations e.g. date validations and password validations
	 * needed at the time of create preview.
	 * @param request 
	 */
	private ActionErrors handleManagePreviewValidations(ActionErrors errors ,HttpServletRequest request){
		if(errors==null){
			errors=new ActionErrors();
		}
		validateConfigurableMandatoryFields(request , errors , EntityType.PERSONNEL);
		validateOffice(errors);
		validateCustomFields(request, errors);
		return checkForPassword(errors);
	}

	private void validateOffice(ActionErrors errors) {
		if (StringUtils.isNullOrEmpty(officeId)){
			errors.add(PersonnelConstants.OFFICE, new ActionMessage(CustomerConstants.ERRORS_MANDATORY, PersonnelConstants.OFFICE));
		}
	}

	protected void validateCustomFields(HttpServletRequest request,
			ActionErrors errors) {
		List<CustomFieldDefinitionEntity> customFieldDefs = null;

		try {
			customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
					.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		} catch (PageExpiredException e) {
			
			//ignore it 
		}
		if (customFieldDefs!=null)
		for (CustomFieldView customField : customFields) {
			boolean isErrorFound = false;
			for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
				if (customField.getFieldId()
						.equals(customFieldDef.getFieldId())
						&& customFieldDef.isMandatory())
					if (StringUtils.isNullOrEmpty(customField.getFieldValue())) {
						errors.add(CustomerConstants.CUSTOM_FIELD,
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

	
	private void validateConfigurableMandatoryFields(HttpServletRequest request, ActionErrors errors, EntityType entityType){
		checkForMandatoryFields(entityType.getValue(), errors, request);
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

}
