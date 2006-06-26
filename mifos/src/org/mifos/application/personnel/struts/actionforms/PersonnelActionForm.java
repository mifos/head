/**

 * PersonnelActionForm.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.personnel.struts.actionforms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.actionforms.MifosSearchActionForm;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;

import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.master.util.valueobjects.SupportedLocales;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelHelper;
import org.mifos.application.personnel.util.valueobjects.PersonnelCustomField;
import org.mifos.application.personnel.util.valueobjects.PersonnelDetails;
import org.mifos.application.personnel.util.valueobjects.PersonnelLevel;

/**
 * @author ashishsm
 *
 */
public class PersonnelActionForm extends MifosSearchActionForm{
	
	/** The personnel details object for the personnel */
	private PersonnelDetails personnelDetails;
	
	/** The supportedlocales object which identifies the preferred locale of the user.*/
	private SupportedLocales preferredLocale;
	
	/**Denotes the systemId of the personnel */
	private String globalPersonnelNum;
	
	/**Denotes the emailId of the user */
	private String emailId;
	
	/**Denotes the user login name */
	private String userName;
	
	/**Denotes the user password */
	private String password;
	
	/**Denotes the user password. passowrd is entered twice for confirmation */
	private String passwordRepeat;
	
	/**Denotes the property for cancel button */
	private String cancelBtn;
	
	/**Denotes the user title */
	private String title;
	
	/**Denotes the property for any button*/
	private String btn;
	
	/**Denotes the date of birth of the user */
	private String dob;
	
	/**Denotes the date of joining MFI of the user */
	private String dateOfJoiningMFI;
	
	/**Denotes the level of the user */
	private PersonnelLevel level;
	
	/**Denotes the status id of the personnel.*/
	private String personnelStatus;
	
	/**Denotes the roles that user is assigned with. */
	private String[] personnelRoles;
	
	/** Denotes the office object to which this personnel belongs. */
	private Office office;
	
	/**Denotes the custom field values of the Personnel*/
	private List <PersonnelCustomField> customFieldSet;

	private String versionNo;
	/**
	 * Constructor: All the composition objects are intialized within the contructor
	 */
	public PersonnelActionForm(){
		this.office=new Office();
		this.personnelDetails = new PersonnelDetails();
		this.preferredLocale=new SupportedLocales();
		this.customFieldSet = new ArrayList<PersonnelCustomField>();
		this.level=new PersonnelLevel();
	}
	
	/**
     * Return the value of the cancelBtn attribute.
     * @return String
     */
	public String getCancelBtn() {
		return cancelBtn;
	}
	
	/**
     * Sets the value of canclBtn
     * @param cancelBtn
     */
	public void setCancelBtn(String cancelBtn) {
		this.cancelBtn = cancelBtn;
	}
	
	/**
	 * Returns the emailId.
	 * @return String
	 */
	public String getEmailId() {
		return emailId;
	}
	
	/**
	 * Sets the value of emailId
	 * @param emailId
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	/**
	 * Returns the password.
	 * @return String
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Sets the value of password
	 * @param password 
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * Returns the passwordRepeat.
	 * @return String
	 */
	public String getPasswordRepeat() {
		return passwordRepeat;
	}
	
	/**
	 * Sets the value of passwordRepeat
	 * @param password 
	 */
	public void setPasswordRepeat(String passwordRepeat) {
		this.passwordRepeat = passwordRepeat;
	}
	
	/**
	 * Returns the personnelDetails.
	 * @return PersonnelDetails
	 */
	public PersonnelDetails getPersonnelDetails() {
		return personnelDetails;
	}
	
	/**
	 * Sets the value of personnelDetails
	 * @param personnelDetails 
	 */
	public void setPersonnelDetails(PersonnelDetails personnelDetails) {
		this.personnelDetails = personnelDetails;
	}
	
	/**
	 * Returns the preferredLocale.
	 * @return SupportedLocales
	 */
	public SupportedLocales getPreferredLocale() {
		return preferredLocale;
	}
	
	/**
	 * Sets the value of preferredLocale
	 * @param preferredLocale 
	 */
	public void setPreferredLocale(SupportedLocales preferredLocale) {
		this.preferredLocale = preferredLocale;
	}
	
	/**
	 * Returns the userName.
	 * @return String
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * Sets the value of userName
	 * @param userName 
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
     * Return the value of the btn attribute.
     * @return String
     */
	public String getBtn() {
		return btn;
	}

	/**
     * Sets the value of the btn attribute.
     * @param btn
     */
	public void setBtn(String btn) {
		this.btn = btn;
	}

	/**
	 * Returns the user title.
	 * @return Integer
	 */	
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets the value of title
	 * @param title 
	 */	
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Returns the personnelRoles.
	 * @return String[]
	 */
	public String[] getPersonnelRoles() {
		return personnelRoles;
	}
	
	/**
	 * Sets the value of personnelRoles
	 * @param personnelRoles 
	 */
	public void setPersonnelRoles(String[] personnelRoles) {
		this.personnelRoles = personnelRoles;
	}
	
	/**
	 * Returns the office of which user belongs.
	 * @return Office
	 */
	public Office getOffice() {
		return office;
	}
	
	/**
	 * Sets the value of office
	 * @param office 
	 */
	public void setOffice(Office office) {
		this.office = office;
	}

	/**
	 * Returns the level for the personnel.
	 * @return PersonnelLevel
	 */
	public PersonnelLevel getLevel() {
		return level;
	}
	
	/**
	 * Sets the value of level
	 * @param level 
	 */
	public void setLevel(PersonnelLevel level) {
		this.level = level;
	}
	
	/**
	 * Returns the dob of the user.
	 * @return String
	 */
	public String getDob() {
		return dob;
	}
	
	/**
	 * Sets the value of dob
	 * @param dob 
	 */
	public void setDob(String dob) {
		this.dob = dob;
	}
	
	/**
	 * Returns the dateOfJoiningMFI of the user.
	 * @return String
	 */
	public String getDateOfJoiningMFI() {
		return dateOfJoiningMFI;
	}
	
	/**
	 * Sets the value of dateOfJoiningMFI
	 * @param dateOfJoiningMFI 
	 */
	public void setDateOfJoiningMFI(String dateOfJoiningMFI) {
		this.dateOfJoiningMFI = dateOfJoiningMFI;
	}
	
	/**
	 * Returns the globalPersonnelNum of the user.
	 * @return String
	 */
	public String getGlobalPersonnelNum() {
		return globalPersonnelNum;
	}
	
	/**
	 * Sets the value of globalPersonnelNum
	 * @param globalPersonnelNum 
	 */
	public void setGlobalPersonnelNum(String globalPersonnelNum) {
		this.globalPersonnelNum = globalPersonnelNum;
	}
	
   /**
 	* Return the value of the one of the custom field at index i in customFieldSet attribute.
 	* @param i index of the custom field
 	* @return PersonnelCustomField 
 	*/
	public PersonnelCustomField getCustomField(int i){
		while(i>=customFieldSet.size()){
			customFieldSet.add(new PersonnelCustomField ());
		}
		return (PersonnelCustomField)(customFieldSet.get(i));
	}
	
   /**
	* Return the value of the customFieldSet attribute.
	* @return Set
    */
	public Set getCustomFieldSet(){
		return new HashSet<PersonnelCustomField>(this.customFieldSet);
	}
	
	/**
	 * Sets the value of customFieldSet
	 * @param personnelStatus 
	 */
	public void setCustomFieldSet(List<PersonnelCustomField> customFieldSet) {
		this.customFieldSet = customFieldSet;
	}
	/**
	 * Returns the personnelStatus.
	 * @return String
	 */
	public String getPersonnelStatus() {
		return personnelStatus;
	}
	
	/**
	 * Sets the value of personnelStatus
	 * @param personnelStatus 
	 */
	public void setPersonnelStatus(String personnelStatus) {
		this.personnelStatus = personnelStatus;
	}
	
	/**
	 * This method is used in addition to validation framework to do input data validations before proceeding.  
	 * @param mapping
	 * @param request
	 * @return ActionErrors
	 * @throws ApplicationException
	 */
	public ActionErrors customValidate(ActionMapping mapping, HttpServletRequest request) throws ApplicationException {
		String methodCalled= request.getParameter("method");
		if(null !=methodCalled) {
			if( (PersonnelConstants.METHOD_CANCEL).equals(methodCalled) || 
			    (PersonnelConstants.METHOD_GET).equals(methodCalled)	||
			    (PersonnelConstants.METHOD_CREATE).equals(methodCalled)||
			    (PersonnelConstants.METHOD_LOAD).equals(methodCalled) || 
			    (PersonnelConstants.METHOD_SEARCH_NEXT).equals(methodCalled)||
			    (PersonnelConstants.METHOD_SEARCH_PREV).equals(methodCalled)||
			    (PersonnelConstants.METHOD_MANAGE).equals(methodCalled) ||
			    (PersonnelConstants.METHOD_PREVIOUS).equals(methodCalled)||
			    (PersonnelConstants.METHOD_LOAD_SEARCH).equals(methodCalled)||
			    (PersonnelConstants.METHOD_LOAD_UNLOCK_USER).equals(methodCalled)||
			    (PersonnelConstants.METHOD_UNLOCK_USER_ACCOUNT).equals(methodCalled)||
			    (PersonnelConstants.METHOD_UPDATE).equals(methodCalled)||
			    (PersonnelConstants.METHOD_GET_DETAILS).equals(methodCalled)||
			    (PersonnelConstants.METHOD_EDIT_PERSONAL_INFO).equals(methodCalled)||
			    (PersonnelConstants.METHOD_PREV_PERSONAL_INFO).equals(methodCalled)||
			    (PersonnelConstants.METHOD_CHOOSE_OFFICE).equals(methodCalled)||
			    (PersonnelConstants.METHOD_LOAD_CHANGE_PASSWORD).equals(methodCalled)||
			    (PersonnelConstants.METHOD_UPDATE_SETTINGS).equals(methodCalled)||
			    ((PersonnelConstants.METHOD_SEARCH).equals(methodCalled) && input!=null && input.equals(PersonnelConstants.USER_CHANGE_LOG))){
					request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
				}else if((PersonnelConstants.METHOD_PREVIEW).equals(methodCalled) && input.equals(PersonnelConstants.CREATE_USER)){
					return handleCreatePreviewValidations(request);
				}else if((PersonnelConstants.METHOD_SEARCH).equals(methodCalled)){
					return handleSearchValidations(request);
				}else if((PersonnelConstants.METHOD_PREVIEW).equals(methodCalled) && input.equals(PersonnelConstants.MANAGE_USER)){
					return handleManagePreviewValidations(request);
				}
		}
		return null;	
	}
	/**
	 * This method is helper method to do data validations before searching for groups.
	 * If user has not entered any search String, it will display error message    
	 * @return ActionErrors
	 */
	private ActionErrors handleSearchValidations(HttpServletRequest request){
		ActionErrors errors = null;
		String searchString=getSearchNode("searchString");
		
		if(ValidateMethods.isNullOrBlank(searchString)){
			errors = new ActionErrors();
			errors.add(PersonnelConstants.NO_SEARCH_STRING,new ActionMessage(PersonnelConstants.NO_SEARCH_STRING));
		}
		request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
		return errors;
	}
	
	/**
	 * This is the helper method to check for password validations.
	 * @param personnelStatus 
	 */
	private ActionErrors handleManagePreviewValidations(HttpServletRequest request){
		ActionErrors errors = null;
		if(errors==null)
			errors=new ActionErrors();
		checkForMandatoryFields(EntityMasterConstants.Personnel,errors,request);
		return checkForPassword(errors);
	}
	
	/**
	 * This is the helper method to check for password validations.
	 * @param errors instance of ActionErrors 
	 */
	private ActionErrors checkForPassword(ActionErrors errors){
		
		//if password and confirm passowrd entries are made of only spaces, throw an exception
		if(password.length()==passwordRepeat.length() && password.length()!=0 && password.trim().equals("")){
			if(errors==null)
				errors = new ActionErrors();
			errors.add(PersonnelConstants.PASSWORD_MASK,new ActionMessage(PersonnelConstants.PASSWORD_MASK,PersonnelConstants.PASSWORD));
		}
			
		return errors;
	}
	/**
	 * This is the helper method to check for extra validations e.g. date validations and password validations
	 * needed at the time of create preview.
	 * @param request 
	 */
	private ActionErrors handleCreatePreviewValidations(HttpServletRequest request){
		java.sql.Date sqlDOB=null;
		ActionErrors errors=null;
		if(!PersonnelHelper.isNullOrBlank(dob)) {
			sqlDOB=DateHelper.getLocaleDate(getUserLocale(request),dob);
			Calendar currentCalendar = new GregorianCalendar();
			int year=currentCalendar.get(Calendar.YEAR);
			int month=currentCalendar.get(Calendar.MONTH);
			int day=currentCalendar.get(Calendar.DAY_OF_MONTH);
			currentCalendar = new GregorianCalendar(year,month,day);
			java.sql.Date currentDate=new java.sql.Date(currentCalendar.getTimeInMillis());
			if(currentDate.compareTo(sqlDOB) < 0 ) {
				errors=new ActionErrors();
				errors.add(PersonnelConstants.INVALID_DOB,new ActionMessage(PersonnelConstants.INVALID_DOB));
			}
		}
		if(errors==null){
			errors=new ActionErrors();
		}
		checkForMandatoryFields(EntityMasterConstants.Personnel,errors,request);
		return checkForPassword(errors);
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	
}
