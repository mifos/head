/**
 
 * PersonnelAction.java    version: 1.0
 
 
 
 * Copyright (c) 2005-2006 Grameen Foundation USA
 
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
package org.mifos.application.personnel.struts.action;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.MifosSearchAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

import org.mifos.application.configuration.util.helpers.PathConstants;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.application.master.util.valueobjects.SupportedLocales;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.struts.actionforms.PersonnelActionForm;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelHelper;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.application.personnel.util.valueobjects.PersonnelCustomField;
import org.mifos.application.personnel.util.valueobjects.PersonnelDetails;
import org.mifos.application.personnel.util.valueobjects.PersonnelLevel;
import org.mifos.application.rolesandpermission.util.valueobjects.Role;

/**
 * This denotes the action class for the personnel module. It uses the base class functions to create, update. 
 * In addition includes methods to loadSearch etc.
 * It also includes implementations for cancel and validate
 * @author navitas
 */
public class PersonnelAction extends MifosSearchAction{
	
	/** 
	 *  Returns the path which uniquely identifies the element in the dependency.xml.
	 *  This method implementaion is the framework requirement. 
	 */
	protected String getPath() {
		return PathConstants.PERSONNEL_PATH;
	}

	/**
	 * Returns a map conatining extra methods apart from the ones provided by the framework.
	 * These methods would be called for extra functionality required by this specific module.
	 * e.g. It has an entry for method loadSearch which would be called when the user clicks 
	 * on view users on the admin page. 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#appendToMap()
	 */
	public Map<String,String> appendToMap()
	{
		Map<String,String>keyMethodMap = new HashMap<String,String>();
		keyMethodMap.putAll(super.appendToMap());
		keyMethodMap.put(PersonnelConstants.METHOD_LOAD_SEARCH, PersonnelConstants.METHOD_LOAD_SEARCH);
		keyMethodMap.put(PersonnelConstants.METHOD_LOAD_UNLOCK_USER, PersonnelConstants.METHOD_LOAD_UNLOCK_USER);
		keyMethodMap.put(PersonnelConstants.METHOD_UNLOCK_USER_ACCOUNT, PersonnelConstants.METHOD_UNLOCK_USER_ACCOUNT);
		keyMethodMap.put(PersonnelConstants.METHOD_CHOOSE_OFFICE, PersonnelConstants.METHOD_CHOOSE_OFFICE);
		keyMethodMap.put(PersonnelConstants.METHOD_GET_DETAILS, PersonnelConstants.METHOD_GET_DETAILS);
		keyMethodMap.put(PersonnelConstants.METHOD_EDIT_PERSONAL_INFO, PersonnelConstants.METHOD_EDIT_PERSONAL_INFO);
		keyMethodMap.put(PersonnelConstants.METHOD_PREV_PERSONAL_INFO, PersonnelConstants.METHOD_PREV_PERSONAL_INFO);
		keyMethodMap.put(PersonnelConstants.METHOD_PREVIEW_PERSONAL_INFO, PersonnelConstants.METHOD_PREVIEW_PERSONAL_INFO);
		keyMethodMap.put(PersonnelConstants.METHOD_UPDATE_SETTINGS, PersonnelConstants.METHOD_UPDATE_SETTINGS);
		keyMethodMap.put(PersonnelConstants.METHOD_LOAD_CHANGE_PASSWORD, PersonnelConstants.METHOD_LOAD_CHANGE_PASSWORD);
		return keyMethodMap;
	}
	
	/**
	 * This method is called before converting action form to value object on every method call.
	 * if on a particular method conversion is not required , it returns false, otherwise returns true
	 * @return Returns whether the action form should be converted or not
	 */
	protected boolean isActionFormToValueObjectConversionReq(String methodName) {
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Before converting action form to value object checking for method name " + methodName);
		if(null !=methodName && (methodName.equals(PersonnelConstants.METHOD_MANAGE)|| 
				methodName.equals(PersonnelConstants.METHOD_LOAD_UNLOCK_USER) || 
				methodName.equals(PersonnelConstants.METHOD_UNLOCK_USER_ACCOUNT)|| 
				methodName.equals(PersonnelConstants.METHOD_LOAD_SEARCH)||
				methodName.equals(PersonnelConstants.METHOD_SEARCH)||
				methodName.equals(PersonnelConstants.METHOD_GET_DETAILS)||
				methodName.equals(PersonnelConstants.METHOD_EDIT_PERSONAL_INFO)||
				methodName.equals(PersonnelConstants.METHOD_PREV_PERSONAL_INFO)||
				methodName.equals(PersonnelConstants.METHOD_UPDATE_SETTINGS)||
				methodName.equals(PersonnelConstants.METHOD_LOAD_CHANGE_PASSWORD)||
				methodName.equals(PersonnelConstants.METHOD_CANCEL))){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * This is called when one clicks to edit users link in the ui to navigate to a search page.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 * @throws Exception
	 */
	public ActionForward chooseOffice(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)throws Exception{
		return mapping.findForward(PersonnelConstants.CHOOSE_OFFICE_SUCCESS);
	}
	/**
	 * This is called when one clicks to edit users link in the ui to navigate to a search page.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 * @throws Exception
	 */
	public ActionForward loadSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)throws Exception{
		PersonnelActionForm actionForm= (PersonnelActionForm)form;
		actionForm.setSearchNode("searchString",null);
		return mapping.findForward(PersonnelConstants.LOAD_SEARCH_SUCCESS);
	}

	/**
	 * This is called when one clicks change password link on your settings page to change own password 
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 * @throws Exception
	 */
	public ActionForward loadChangePassword(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)throws Exception{
		return mapping.findForward(PersonnelConstants.LOAD_CHANGE_PASSWORD_SUCCESS);
	}
	
	/**
	 * This method is called when the user clicks on "Unlock the user" link in the UI. 
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 * @throws Exception
	 */
	public ActionForward loadUnLockUser(ActionMapping mapping, ActionForm form,	HttpServletRequest request,	HttpServletResponse response)throws Exception{
		//set the number of attempts, after which user is logged in
		request.setAttribute(PersonnelConstants.LOGIN_ATTEMPTS_COUNT,SecurityConstants.MAXTRIES);
		return mapping.findForward(PersonnelConstants.LOAD_UNLOCK_USER_SUCCESS);
	}
	
	/**
	 * This method is called when the user confirms that user account has to be unlocked  
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken=true)
	public ActionForward unLockUserAccount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(PersonnelConstants.METHOD_UNLOCK_USER_ACCOUNT);
		delegate(context,request);
		return mapping.findForward(PersonnelConstants.USER_DETAILS_PAGE);
	}
	
	/**
	 * This method is added because we might require to do some cleanup whenever the user is clicking on cancel.
	 * The operation carried out in this method is again based on the input parameter which comes as a hiiden field 
	 * from jsp, because the same cancel leads the user to differnt pages depending on which jsp pages it is clicked.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return -- Returns the actionforward where the request is supposed to be forwarded based on the input parameter.
	 * @throws Exception
	 */
	public ActionForward customCancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)throws Exception{
		PersonnelActionForm personnelActionForm = (PersonnelActionForm)form;
		String fromPage =personnelActionForm.getInput();
		return mapping.findForward(chooseCancelForward(fromPage));
	}
	
	/**
	 * This method is called whenever logged in user wants to modify infomration of himself.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return -- action forward
	 * @throws Exception
	 */
	public ActionForward editPersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)throws Exception{
		return mapping.findForward(PersonnelConstants.EDIT_PERSONAL_INFO_SUCCESS);
	}
	
	/**
	 * This method is called to perview personnel information of logged in user
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return -- action forward
	 * @throws Exception
	 */
	public ActionForward previewPersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)throws Exception{
		PersonnelActionForm personnelActionForm = (PersonnelActionForm)form;
		//set language name in request
		setLanguageNameinRequest(request,personnelActionForm);
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		
		//store display address in  request
		String displayAddress = new PersonnelHelper().getDisplayAddress(personnelActionForm.getPersonnelDetails());
		request.setAttribute(PersonnelConstants.DISPLAY_ADDRESS,displayAddress);
		
		//store age
		setPersonnelAge(request,((Personnel)context.getValueObject()).getDob());
		return mapping.findForward(PersonnelConstants.PREVIEW_PERSONAL_INFO_SUCCESS);
	}
	
	/**
	 * This method is called whenever from preview edit personnel info is called
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return -- action forward
	 * @throws Exception
	 */
	public ActionForward prevPersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)throws Exception{
		return mapping.findForward(PersonnelConstants.EDIT_PERSONAL_INFO_SUCCESS);
	}
	
	/**
	 * This method is called on search.
	 * The operation carried out in this method is again based on the input parameter which comes as a hiiden field 
	 * from jsp, because the same cancel leads the user to differnt pages depending on which jsp pages it is clicked.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return -- Returns the actionforward where the request is supposed to be forwarded based on the input parameter.
	 * @throws Exception
	 */
	public ActionForward customSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)throws Exception{
		PersonnelActionForm personnelActionForm = (PersonnelActionForm)form;
		String fromPage =personnelActionForm.getInput();
		if(fromPage.equals(PersonnelConstants.USER_CHANGE_LOG)){
			personnelActionForm.setSearchNode(Constants.SEARCH_NAME,PersonnelConstants.USER_CHANGE_LOG);
			return mapping.findForward(PersonnelConstants.USER_CHANGE_LOG);
		}
		return null;
	}
	
	/**
	 * This is called when one your settings link.
	 * It retrieves the logged in user details.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 * @throws Exception
	 */
	@TransactionDemarcate(saveToken = true)
	public ActionForward getDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)throws Exception{
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(PersonnelConstants.METHOD_GET_DETAILS);
		delegate(context,request);
		//set user age
		setPersonnelAge(request,((Personnel)context.getValueObject()).getPersonnelDetails().getDob());
		//store display address in  request
		String displayAddress = new PersonnelHelper().getDisplayAddress(((Personnel)context.getValueObject()).getPersonnelDetails());
		request.setAttribute(PersonnelConstants.DISPLAY_ADDRESS,displayAddress);
		return mapping.findForward(PersonnelConstants.GETDETAILS_SUCCESS);
	}
	/** 
	 * This method is called to retrieve personnel details.
	 * It calls get of base class that in turn calls get in business processor to get all group all details
	 * @param mapping indicates action mapping defined in struts-config.xml 
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 * @throws Exception
	 */
	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping,ActionForm form,	HttpServletRequest request,	HttpServletResponse response)throws Exception{
		ActionForward forward = super.get(mapping,form,request,response);
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		//set user age
		setPersonnelAge(request,((Personnel)context.getValueObject()).getPersonnelDetails().getDob());
		//store display address in  request
		String displayAddress = new PersonnelHelper().getDisplayAddress(((Personnel)context.getValueObject()).getPersonnelDetails());
		request.setAttribute(PersonnelConstants.DISPLAY_ADDRESS,displayAddress);
		return forward;
	}
	
	/** 
	 * This method is called to create a new personnel.
	 * It calls create of base class that in turn calls create in business processor to create new personnel
	 * @param mapping indicates action mapping defined in struts-config.xml 
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken=true)
	public ActionForward create(ActionMapping mapping,ActionForm form,	HttpServletRequest request,	HttpServletResponse response)throws Exception{
		ActionForward forward = super.create(mapping,form,request,response);
		doPersonnelCleanUp(request);
		return forward;
	}
	
	/** 
	 * This method is called to updates a new personnel.
	 * It calls update of base class that in turn calls update in business processor to update personnel
	 * @param mapping indicates action mapping defined in struts-config.xml 
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,	HttpServletResponse response)throws Exception{
		ActionForward forward = super.update(mapping,form,request,response);
		doPersonnelCleanUp(request);
		return forward;
	}

	/** 
	 * This method is called to update settings of the logged in usesr
	 * It calls updateSettings in business processor to update information
	 * @param mapping indicates action mapping defined in struts-config.xml 
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward updateSettings(ActionMapping mapping, ActionForm form, HttpServletRequest request,	HttpServletResponse response)throws Exception{
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(PersonnelConstants.METHOD_UPDATE_SETTINGS);
		delegate(context,request);
		return mapping.findForward(PersonnelConstants.UPDATE_SETTINGS_SUCCESS);
	}
	/** 
	 * This method is called on cancel to choose cancel forward.
	 * Based on input page on which cancel has been clicked it forwards to appropriate page
	 * @param fromPage 
	 * @return page to which it has to forward
	 */
	private String chooseCancelForward(String fromPage){
		String forward=null;
		if (fromPage.equals(PersonnelConstants.CREATE_USER)){
			forward=PersonnelConstants.ADMIN_PAGE;
		}else if (fromPage.equals(PersonnelConstants.MANAGE_USER) || fromPage.equals(PersonnelConstants.PREVIEW_MANAGE_USER)){
			forward=PersonnelConstants.USER_DETAILS_PAGE;
		}else if (fromPage.equals(PersonnelConstants.UNLOCK_USER) || fromPage.equals(PersonnelConstants.USER_CHANGE_LOG)){
			forward=PersonnelConstants.USER_DETAILS_PAGE;
		}
		return forward;
	}
	
	 /**
	  *	Method which is called to decide the pages on which the errors on failure of validation will be displayed 
	  * this method forwards as per the respective input page 
	  * @param mapping indicates action mapping defined in struts-config.xml 
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */
	public ActionForward customValidate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) {
		String forward = null;	
		String methodCalled= (String)request.getAttribute("methodCalled");
		PersonnelActionForm personnelActionForm = (PersonnelActionForm)form;
		String fromPage =personnelActionForm.getInput();
			 //deciding forward page
		if(null !=methodCalled) {
			if((PersonnelConstants.METHOD_PREVIEW).equals(methodCalled)){
				if(PersonnelConstants.CREATE_USER.equals(fromPage)){
					Context context = (Context)request.getAttribute(Constants.CONTEXT);
					setSelectedRoles(context,(PersonnelActionForm)form);
					forward =PersonnelConstants.CREATE_USER_FAILURE;
				}
				else if(PersonnelConstants.MANAGE_USER.equals(fromPage))
					forward = PersonnelConstants.MANAGE_USER_FAILURE;
			}else if(PersonnelConstants.METHOD_PREVIEW_PERSONAL_INFO.equals(methodCalled)){
				forward = PersonnelConstants.PREVIEW_PERSONAL_INFO_FAILURE;
			}
		}
		return mapping.findForward(forward); 
	}
	
	/** 
	 * This method is called before every preview method
	 * It prepares data to show on preview e.g. display address 
	 * It also chooses which preview page is to be shown based on input.
	 * @param mapping indicates action mapping defined in struts-config.xml 
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward customPreview(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response)throws Exception{
		// choose preview page to be shown	
		PersonnelActionForm personnelActionForm=(PersonnelActionForm)form;
		
		//trim entered strings
		new PersonnelHelper().trimEnteredString(personnelActionForm);
		
		//check for roles, if user has not selected any role this time, change it to null
		this.checkForRoles(request,personnelActionForm);
		
		//load names for master data that user selected on create user page
		loadNamesForSelectedMasterData(request,personnelActionForm);
		
		//setting input page to context. this is used by previewInitial in buisness processor
		String fromPage= personnelActionForm.getInput();
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		context.addBusinessResults(PersonnelConstants.INPUT_PAGE,fromPage);
		
		//store display address in  request
		String displayAddress = new PersonnelHelper().getDisplayAddress(personnelActionForm.getPersonnelDetails());
		request.setAttribute(PersonnelConstants.DISPLAY_ADDRESS,displayAddress);
		 
		//set user age
		if(fromPage.equals(PersonnelConstants.CREATE_USER) || fromPage.equals(PersonnelConstants.MANAGE_USER))
			setPersonnelAge(request,((Personnel)context.getValueObject()).getDob());
		
		return mapping.findForward(this.ChoosePreviewForward(fromPage));
	}

	/** 
	 * This method is helper method that removes roles from the action form if user has not 
	 * selected them in last request.
	 * @param request Contains the request parameters
	 * @param personnelActionForm The form bean associated with this action
	 */	 
	private void checkForRoles(HttpServletRequest request, PersonnelActionForm personnelActionForm){
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		Object obj=request.getParameter("personnelRoles");
		if (obj==null){
			personnelActionForm.setPersonnelRoles(null);
		}
		context.addBusinessResults(PersonnelConstants.PERSONNEL_ROLES_LIST,personnelActionForm.getPersonnelRoles());
	}
	
	/** 
	 * This method is helper method that loads data to show on preview page
	 * @param request Contains the request parameters
	 * @param personnelActionForm The form bean associated with this action
	 */	
	private void loadNamesForSelectedMasterData(HttpServletRequest request,PersonnelActionForm personnelActionForm){
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		
		//load language name
		setLanguageNameinRequest(request,personnelActionForm);
		//load role names
		setSelectedRoles(context,personnelActionForm);
	}
	
	/** 
	 * This method is helper method that sets the language name of selected locale in request.
	 * @param request Contains the request parameters
	 * @param personnelActionForm The form bean associated with this action
	 */	
	private void setLanguageNameinRequest(HttpServletRequest request, PersonnelActionForm personnelActionForm){
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		SearchResults searchResults = context.getSearchResultBasedOnName(PersonnelConstants.LANGUAGE_LIST);
		Short localeId = personnelActionForm.getPreferredLocale().getLocaleId();
		
		//load language name for the user preferred locale
		if(searchResults!=null && localeId!=null){
			List<SupportedLocales> locales = (List<SupportedLocales>)searchResults.getValue();
			SupportedLocales sl=null;
			for(int i=0;i<locales.size();i++){
				sl = (SupportedLocales)locales.get(i);
				if(sl.getLocaleId().shortValue()==localeId.shortValue()){
					request.setAttribute(PersonnelConstants.LANGUAGE_NAME,sl.getLanguage().getLanguageName());
				}
			}
			
		}
	}
	/**
	* This is the helper method to clear action form
	* @return Set
    */
	private void clearActionForm(PersonnelActionForm actionForm){
		actionForm.setPersonnelDetails(new PersonnelDetails());
		actionForm.setDateOfJoiningMFI(null);
		actionForm.setDob(null);
		actionForm.setEmailId("");
		actionForm.setGlobalPersonnelNum("");
		actionForm.setPassword("");
		actionForm.setPasswordRepeat("");
		actionForm.setTitle("");
		actionForm.setUserName("");
		actionForm.setLevel(new PersonnelLevel());
		actionForm.setPreferredLocale(new SupportedLocales());
		actionForm.setCustomFieldSet(new ArrayList<PersonnelCustomField>());
	}
	/** 
	 * This method is helper method that sets the selected roles in request to show on preview page
	 * @param request Contains the request parameters
	 * @param personnelActionForm The form bean associated with this action
	 */
	private void setSelectedRoles(Context context,PersonnelActionForm personnelActionForm){
		SearchResults sr = context.getSearchResultBasedOnName(PersonnelConstants.ROLES_LIST);
		if(sr!=null){
			List<Role> roleNames = new PersonnelHelper().getSelectedRoles((List)sr.getValue(),personnelActionForm.getPersonnelRoles());
			context.addAttribute(new PersonnelHelper().getResultObject(PersonnelConstants.PERSONNEL_ROLES_LIST,roleNames));
		}
	}
	
	/** 
	 * This method is helper method that chooses forwards for preview method based on input page 
	 * @param fromPage Contains the value of input attribute
	 * @return forward page as string
	 */
	private String ChoosePreviewForward(String fromPage){
		String forward=null;
		if (fromPage.equals(PersonnelConstants.CREATE_USER)){
			forward=PersonnelConstants.PREVIEW_CREATE_USER;
		}else if (fromPage.equals(PersonnelConstants.MANAGE_USER)){
			forward=PersonnelConstants.PREVIEW_MANAGE_USER;
		}
		return forward;
	}
	
	/** 
	 * This method is helper method that chooses forwards for previous method based on input page 
	 * @param fromPage Contains the value of input attribute
	 * @param gpActionForm The form bean associated with this action
	 * @return forward page as string
	 */
	private String ChoosePreviousForward(String fromPage){
		String forward=null;
		if (fromPage.equals(PersonnelConstants.CREATE_USER)){
			forward=PersonnelConstants.LOAD_SUCCESS;
		}else if (fromPage.equals(PersonnelConstants.MANAGE_USER)){
			forward=PersonnelConstants.MANAGE_SUCCESS;
		}
		return forward;
	}
	
	/**
	 * This method is called on previous method
	 * It chooses forwards for previous page based on input attribute
	 * @param mapping indicates action mapping defined in struts-config.xml 
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response  
	 * @return - Actionforward which decides page to forward to next as the same method is called from different jsp pages.
	 * @throws Exception
	 */
	public ActionForward customPrevious(ActionMapping mapping, ActionForm form,	HttpServletRequest request,	HttpServletResponse response) throws Exception{
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		PersonnelHelper personnelHelper = new PersonnelHelper();
		SearchResults searchResults = context.getSearchResultBasedOnName(PersonnelConstants.ROLES_LIST);
		if(searchResults!=null){
			List personnelRoles = personnelHelper.getSelectedRoles((List<Role>)searchResults.getValue(),((PersonnelActionForm)form).getPersonnelRoles());
			context.addAttribute(new PersonnelHelper().getResultObject(PersonnelConstants.PERSONNEL_ROLES_LIST,personnelRoles));
		}
		
		PersonnelActionForm personnelActionForm = (PersonnelActionForm)form;
		String fromPage =personnelActionForm.getInput();
		return mapping.findForward(this.ChoosePreviousForward(fromPage));
	}
	
	/**
	 * This method is called on previous method
	 * It chooses forwards for previous page based on input attribute
	 * @param mapping indicates action mapping defined in struts-config.xml 
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response  
	 * @return - Actionforward which decides page to forward to next as the same method is called from different jsp pages.
	 * @throws Exception
	 */
	public ActionForward customLoad(ActionMapping mapping, ActionForm form,	HttpServletRequest request,	HttpServletResponse response) throws Exception{
		PersonnelActionForm actionForm = (PersonnelActionForm) form;
		this.clearActionForm(actionForm);
		return null;
	}
	
	/**
	 * This method is called when the user clicks on create new user.
	 * It checks for office type under which user is being created.
	 * If the office type is not branch office it by default selects user hierachy as non loan officer.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 * @throws Exception
	 */
	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,	HttpServletRequest request,	HttpServletResponse response)throws Exception{
		ActionForward forward = super.load(mapping,form,request,response);
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		PersonnelActionForm actionForm = (PersonnelActionForm)form;
		Office office = (Office)context.getSearchResultBasedOnName(PersonnelConstants.PERSONNEL_OFFICE).getValue();
		
		if(office.getLevel().getLevelId().shortValue()!=OfficeConstants.BRANCHOFFICE){
			EntityMaster em = (EntityMaster)context.getSearchResultBasedOnName(PersonnelConstants.PERSONNEL_LEVEL_LIST).getValue();
			List<LookUpMaster> lookUpMaster = em.getLookUpMaster();
			if(lookUpMaster!=null){
				for(int i=0;i<lookUpMaster.size();i++){
					LookUpMaster master =lookUpMaster.get(i);
					if(master.getId().shortValue()!=PersonnelConstants.LOAN_OFFICER)
						actionForm.getLevel().setLevelId(master.getId().shortValue());
				}
			}
		}
		return forward;
	}
	
	/** 
	 * This method is helper method that sets the personnel age in request
	 * @param request Contains the request parameters
	 */
	private void setPersonnelAge(HttpServletRequest request,Date date){
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		request.getSession().removeAttribute(PersonnelConstants.PERSONNEL_AGE);
		int age = DateHelper.DateDiffInYears(date);
		if(age<0)
			age=0;
		SessionUtils.setAttribute(PersonnelConstants.PERSONNEL_AGE,age,request.getSession());
	}

	/** 
	 * This method is helper method that removes unnecessary things from context, when not needed
	 * @param request Contains the request parameters
	 */
	private void doPersonnelCleanUp(HttpServletRequest request){
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		context.removeAttribute(PersonnelConstants.PERSONNEL_ROLES_LIST);
	}

}
