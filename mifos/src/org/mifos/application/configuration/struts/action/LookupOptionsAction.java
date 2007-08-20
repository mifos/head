/**

 * LookupOptionsAction.java    version: xxx



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

package org.mifos.application.configuration.struts.action;


import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.configuration.struts.actionform.LookupOptionsActionForm;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.configuration.util.helpers.LookupOptionData;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomValueList;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;


public class LookupOptionsAction extends BaseAction {

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.CONFIGURATION_LOGGER);
	
	

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@Override
	protected BusinessService getService() {
		return ServiceFactory.getInstance().getBusinessService(
				BusinessServiceName.Configuration);
	}
	
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("lookupOptionsAction");
		
		security.allow("load", SecurityConstants.CAN_DEFINE_LOOKUP_OPTIONS);
		security.allow("update", SecurityConstants.VIEW);
		security.allow("cancel", SecurityConstants.VIEW);
		security.allow("addEditLookupOption", SecurityConstants.VIEW);
		security.allow("addEditLookupOption_cancel", SecurityConstants.VIEW);
		return security;
	}
	
	private void setLookupType(String configurationEntity, HttpServletRequest request)
	{
		ResourceBundle resources = ResourceBundle.getBundle ("org.mifos.application.configuration.util.resources.ConfigurationUIResources", 
				getUserLocale(request));
		Short localeId = getUserContext(request).getLocaleId();

		if (configurationEntity.equals(ConfigurationConstants.CONFIG_SALUTATION))
		{
            request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, resources.getString("configuration.salutation"));
            request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_SALUTATION);
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_PERSONNEL_TITLE))
		{
			request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, resources.getString("configuration.usertitle"));
			request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_PERSONNEL_TITLE);
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_MARITAL_STATUS))
		{
			request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, resources.getString("configuration.maritalstatus"));
			request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_MARITAL_STATUS);
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_ETHNICITY))
		{
			String label = MessageLookup.getInstance().lookupLabel(ConfigurationConstants.ETHINICITY, localeId);
			request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, label);
			request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_ETHNICITY);
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_EDUCATION_LEVEL))
		{
			request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, resources.getString("configuration.educationlevel"));
			request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_EDUCATION_LEVEL);
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_CITIZENSHIP))
		{
			String label = MessageLookup.getInstance().lookupLabel(ConfigurationConstants.CITIZENSHIP, localeId);
			request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, label);
			request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_CITIZENSHIP);
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_LOAN_PURPOSE))
		{
			request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, resources.getString("configuration.purposeofloan"));
			request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_LOAN_PURPOSE);
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_COLLATERAL_TYPE))
		{
			request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, resources.getString("configuration.collateraltype"));
			request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_COLLATERAL_TYPE);
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_HANDICAPPED))
		{
			String label = MessageLookup.getInstance().lookupLabel(ConfigurationConstants.HANDICAPPED, localeId);
			request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, label);
			request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_HANDICAPPED);
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_ATTENDANCE))
		{
			request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, resources.getString("configuration.attendance"));
			request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_ATTENDANCE);
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_OFFICER_TITLE))
		{
			request.setAttribute(ConfigurationConstants.LOOKUP_TYPE, resources.getString("configuration.officertitle"));
			request.setAttribute(ConfigurationConstants.ENTITY, ConfigurationConstants.CONFIG_OFFICER_TITLE);
		}
	}
	
	private String getSelectedValue(String configurationEntity, LookupOptionsActionForm lookupOptionsActionForm)
	{
		String selectedValue = null;
		
		if (configurationEntity.equals(ConfigurationConstants.CONFIG_SALUTATION))
		{
			assert(lookupOptionsActionForm.getSalutationList().length ==1);
            selectedValue = lookupOptionsActionForm.getSalutationList()[0];
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_PERSONNEL_TITLE))
		{
			assert(lookupOptionsActionForm.getUserTitleList().length ==1);
            selectedValue = lookupOptionsActionForm.getUserTitleList()[0];
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_MARITAL_STATUS))
		{
			assert(lookupOptionsActionForm.getMaritalStatusList().length ==1);
            selectedValue = lookupOptionsActionForm.getMaritalStatusList()[0];
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_ETHNICITY))
		{
			assert(lookupOptionsActionForm.getEthnicityList().length ==1);
            selectedValue = lookupOptionsActionForm.getEthnicityList()[0];
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_EDUCATION_LEVEL))
		{
			assert(lookupOptionsActionForm.getEducationLevelList().length ==1);
            selectedValue = lookupOptionsActionForm.getEducationLevelList()[0];
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_CITIZENSHIP))
		{
			assert(lookupOptionsActionForm.getCitizenshipList().length ==1);
            selectedValue = lookupOptionsActionForm.getCitizenshipList()[0];
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_LOAN_PURPOSE))
		{
			assert(lookupOptionsActionForm.getPurposesOfLoanList().length ==1);
            selectedValue = lookupOptionsActionForm.getPurposesOfLoanList()[0];
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_COLLATERAL_TYPE))
		{
			assert(lookupOptionsActionForm.getCollateralTypeList().length ==1);
            selectedValue = lookupOptionsActionForm.getCollateralTypeList()[0];
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_HANDICAPPED))
		{
			assert(lookupOptionsActionForm.getHandicappedList().length ==1);
            selectedValue = lookupOptionsActionForm.getHandicappedList()[0];
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_ATTENDANCE))
		{
			assert(lookupOptionsActionForm.getAttendanceList().length ==1);
            selectedValue = lookupOptionsActionForm.getAttendanceList()[0];
		}
		else if (configurationEntity.equals(ConfigurationConstants.CONFIG_OFFICER_TITLE))
		{
			assert(lookupOptionsActionForm.getOfficerTitleList().length ==1);
            selectedValue = lookupOptionsActionForm.getOfficerTitleList()[0];
		}
		return selectedValue;
           
	}
	
	/**
	 * @return boolean -- Return true if we found the expected data to use, otherwise return false
	 */
	private boolean setLookupOptionData(String configurationEntity, LookupOptionData data, HttpServletRequest request,
			String addOrEdit, LookupOptionsActionForm lookupOptionsActionForm) throws Exception
	{
		assert( configurationEntity.equals(ConfigurationConstants.CONFIG_SALUTATION) ||
				configurationEntity.equals(ConfigurationConstants.CONFIG_PERSONNEL_TITLE) ||
				configurationEntity.equals(ConfigurationConstants.CONFIG_MARITAL_STATUS) ||
				configurationEntity.equals(ConfigurationConstants.CONFIG_ETHNICITY) ||
				configurationEntity.equals(ConfigurationConstants.CONFIG_EDUCATION_LEVEL) ||
				configurationEntity.equals(ConfigurationConstants.CONFIG_CITIZENSHIP) ||
				configurationEntity.equals(ConfigurationConstants.CONFIG_LOAN_PURPOSE) ||
				configurationEntity.equals(ConfigurationConstants.CONFIG_COLLATERAL_TYPE) ||
				configurationEntity.equals(ConfigurationConstants.CONFIG_HANDICAPPED) ||
				configurationEntity.equals(ConfigurationConstants.CONFIG_ATTENDANCE) ||
				configurationEntity.equals(ConfigurationConstants.CONFIG_OFFICER_TITLE));
		setLookupType(configurationEntity, request);
		data.setValueListId(Short.parseShort(SessionUtils.getAttribute(configurationEntity, request).toString()));
		if (addOrEdit.equals("add"))
		{
			data.setLookupValue("");
			data.setLookupId(0);
			lookupOptionsActionForm.setLookupValue("");
			return true;
		}
	    // edit
		String selectedValue = getSelectedValue(configurationEntity, lookupOptionsActionForm);
		if (selectedValue == null)
			return false;
		String[] spliteStrList = selectedValue.split(";");
		assert(spliteStrList.length == 2);
		data.setLookupValue(spliteStrList[1]);
		lookupOptionsActionForm.setLookupValue(spliteStrList[1]);
		data.setLookupId(Integer.parseInt(spliteStrList[0]));
		return true;
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward addEditLookupOption(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LookupOptionsActionForm lookupOptionsActionForm = (LookupOptionsActionForm) form;
		
		String entity = request.getParameter(ConfigurationConstants.ENTITY);
		String addOrEdit = request.getParameter(ConfigurationConstants.ADD_OR_EDIT);
		LookupOptionData data = new LookupOptionData();
		
		if (setLookupOptionData(entity, data, request, addOrEdit, lookupOptionsActionForm)) {
			SessionUtils.setAttribute(ConfigurationConstants.LOOKUP_OPTION_DATA, data, request);
			return mapping.findForward(ActionForwards.addEditLookupOption_success.toString());
		} else {
			return mapping.findForward(ActionForwards.addEditLookupOption_failure.toString());			
		}
	}
	
	@Override
	protected boolean isNewBizRequired(HttpServletRequest request)
			throws ServiceException {
		return false;
	}
	
	private void PopulateConfigurationListBox(String configurationEntity, 
			MasterPersistence masterPersistence, 
			Short localeId, HttpServletRequest request,
			LookupOptionsActionForm lookupOptionsActionForm, String configurationEntityConst ) throws Exception
	{
		CustomValueList valueList = masterPersistence.getLookUpEntity(
				configurationEntity, localeId);
		Short valueListId = valueList.getEntityId();
		// save this value and will be retrieved when update the data to db
		SessionUtils.setAttribute(configurationEntityConst,
				valueListId, request);
		if (configurationEntity.equals(MasterConstants.SALUTATION))
			lookupOptionsActionForm.setSalutations(valueList.getCustomValueListElements());
		else if (configurationEntity.equals(MasterConstants.PERSONNEL_TITLE))
			lookupOptionsActionForm.setUserTitles(valueList.getCustomValueListElements());
		else if (configurationEntity.equals(MasterConstants.MARITAL_STATUS))
			lookupOptionsActionForm.setMaritalStatuses(valueList.getCustomValueListElements());
		else if (configurationEntity.equals(MasterConstants.ETHINICITY))
			lookupOptionsActionForm.setEthnicities(valueList.getCustomValueListElements());
		else if (configurationEntity.equals(MasterConstants.EDUCATION_LEVEL))
			lookupOptionsActionForm.setEducationLevels(valueList.getCustomValueListElements());
		else if (configurationEntity.equals(MasterConstants.CITIZENSHIP))
			lookupOptionsActionForm.setCitizenships(valueList.getCustomValueListElements());
		else if (configurationEntity.equals(MasterConstants.LOAN_PURPOSES))
			lookupOptionsActionForm.setPurposesOfLoan(valueList.getCustomValueListElements());
		else if (configurationEntity.equals(MasterConstants.COLLATERAL_TYPES))
			lookupOptionsActionForm.setCollateralTypes(valueList.getCustomValueListElements());
		else if (configurationEntity.equals(MasterConstants.HANDICAPPED))
			lookupOptionsActionForm.setHandicappeds(valueList.getCustomValueListElements());
		else if (configurationEntity.equals(MasterConstants.ATTENDENCETYPES))
			lookupOptionsActionForm.setAttendances(valueList.getCustomValueListElements());
		else if (configurationEntity.equals(MasterConstants.OFFICER_TITLES))
			lookupOptionsActionForm.setOfficerTitles(valueList.getCustomValueListElements());
		else
			throw new Exception("Invalid configuration type in LookupOptionAction. Type is " + configurationEntity);
	}
	
	protected Locale getUserLocale(HttpServletRequest request) {
		Locale locale = null;
		HttpSession session = request.getSession();
		if (session != null) {
			UserContext userContext = (UserContext) session
					.getAttribute(LoginConstants.USERCONTEXT);
			if (null != userContext) {
				locale = userContext.getPreferredLocale();
				if (null == locale) {
					locale = userContext.getMfiLocale();
				}
			}
		}
		return locale;
	}
	
	
	
	private void setHiddenFields(HttpServletRequest request)
	{
		
	    request.setAttribute(ConfigurationConstants.CONFIG_SALUTATION, ConfigurationConstants.CONFIG_SALUTATION);
	    request.setAttribute(ConfigurationConstants.CONFIG_MARITAL_STATUS, ConfigurationConstants.CONFIG_MARITAL_STATUS);
	    request.setAttribute(ConfigurationConstants.CONFIG_PERSONNEL_TITLE, ConfigurationConstants.CONFIG_PERSONNEL_TITLE);
	    request.setAttribute(ConfigurationConstants.CONFIG_EDUCATION_LEVEL, ConfigurationConstants.CONFIG_EDUCATION_LEVEL);
	    request.setAttribute(ConfigurationConstants.CONFIG_CITIZENSHIP, ConfigurationConstants.CONFIG_CITIZENSHIP);
	    request.setAttribute(ConfigurationConstants.CONFIG_HANDICAPPED, ConfigurationConstants.CONFIG_HANDICAPPED);
	    request.setAttribute(ConfigurationConstants.CONFIG_ATTENDANCE, ConfigurationConstants.CONFIG_ATTENDANCE);
	    request.setAttribute(ConfigurationConstants.CONFIG_OFFICER_TITLE, ConfigurationConstants.CONFIG_OFFICER_TITLE);
	    request.setAttribute(ConfigurationConstants.CONFIG_LOAN_PURPOSE, ConfigurationConstants.CONFIG_LOAN_PURPOSE);
	    request.setAttribute(ConfigurationConstants.CONFIG_COLLATERAL_TYPE, ConfigurationConstants.CONFIG_COLLATERAL_TYPE);
	    request.setAttribute(ConfigurationConstants.CONFIG_ETHNICITY, ConfigurationConstants.CONFIG_ETHNICITY);
	   
	}
	
	private void setSpecialLables(Short localeId, LookupOptionsActionForm lookupOptionsActionForm)
	{
		lookupOptionsActionForm.setCitizenship(MessageLookup.getInstance().lookupLabel(ConfigurationConstants.CITIZENSHIP, localeId));
		lookupOptionsActionForm.setHandicapped(MessageLookup.getInstance().lookupLabel(ConfigurationConstants.HANDICAPPED, localeId));
		lookupOptionsActionForm.setEthnicity(MessageLookup.getInstance().lookupLabel(ConfigurationConstants.ETHINICITY, localeId));
		
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside load method");
		LookupOptionsActionForm lookupOptionsActionForm = (LookupOptionsActionForm) form;
		lookupOptionsActionForm.clear();
		setHiddenFields(request);
		
		Short localeId = getUserContext(request).getLocaleId();
		setSpecialLables(localeId, lookupOptionsActionForm);
		MasterPersistence masterPersistence = new MasterPersistence();
		PopulateConfigurationListBox(MasterConstants.SALUTATION, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_SALUTATION);
		PopulateConfigurationListBox(MasterConstants.PERSONNEL_TITLE, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_PERSONNEL_TITLE);
		PopulateConfigurationListBox(MasterConstants.MARITAL_STATUS, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_MARITAL_STATUS);
		PopulateConfigurationListBox(MasterConstants.ETHINICITY, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_ETHNICITY);
		PopulateConfigurationListBox(MasterConstants.EDUCATION_LEVEL, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_EDUCATION_LEVEL);
		PopulateConfigurationListBox(MasterConstants.CITIZENSHIP, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_CITIZENSHIP);
		PopulateConfigurationListBox(MasterConstants.LOAN_PURPOSES, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_LOAN_PURPOSE);
		PopulateConfigurationListBox(MasterConstants.COLLATERAL_TYPES, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_COLLATERAL_TYPE);
		PopulateConfigurationListBox(MasterConstants.HANDICAPPED, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_HANDICAPPED);
		PopulateConfigurationListBox(MasterConstants.ATTENDENCETYPES, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_ATTENDANCE);
		PopulateConfigurationListBox(MasterConstants.OFFICER_TITLES, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_OFFICER_TITLE);
		
		
		logger.debug("Outside load method");
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("cancel method called");
		return mapping.findForward(ActionForwards.cancel_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward addEditLookupOption_cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("addEditLookupOption_cancel method called");
		return mapping.findForward(ActionForwards.addEditLookupOption_cancel_success.toString());
	}

	
	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside validate method");
		ActionForwards actionForward = ActionForwards.addEditLookupOption_failure;
		
		
		String method = (String) request.getAttribute("method");
		if (method != null) {
			if (method.equals(Methods.load.toString())) {
				actionForward = ActionForwards.load_failure;
			}
			else if (method.equals(Methods.update.toString())) {		
				String entity = request.getParameter(ConfigurationConstants.ENTITY);
				setLookupType(entity, request);
				
				actionForward = ActionForwards.update_failure;
			}
		}
		
		logger.debug("outside validate method");
		return mapping.findForward(actionForward.toString());
	}
		
	
	private void UpdateDatabase(LookupOptionData data, Short localeId) throws PersistenceException 
	{
		MasterPersistence masterPersistence = new MasterPersistence();
		if (data.getLookupId() > 0)
			masterPersistence.updateValueListElementForLocale(data.getLookupId(), localeId, data.getLookupValue());
		else
			masterPersistence.addValueListElementForLocale(data.getValueListId(), data.getLookupValue(), localeId);
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside update method");
		//setHiddenFields(request);
		Short localeId = getUserContext(request).getLocaleId();
		LookupOptionsActionForm lookupOptionsActionForm = (LookupOptionsActionForm) form;
		LookupOptionData data = (LookupOptionData)SessionUtils.getAttribute(ConfigurationConstants.LOOKUP_OPTION_DATA, request);
		data.setLookupValue(lookupOptionsActionForm.getLookupValue());
		UpdateDatabase(data, localeId);
		return mapping.findForward(ActionForwards.update_success.toString());
	}

}

