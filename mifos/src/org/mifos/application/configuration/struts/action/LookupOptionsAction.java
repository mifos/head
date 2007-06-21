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


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.configuration.struts.actionform.LookupOptionsActionForm;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.master.business.CustomValueList;
import org.mifos.application.master.business.CustomValueListElement;
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
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.MifosValueList;
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
		
		// more still needs to be set up for CAN_DEFINE_LOOKUP_OPTIONS to work
		security.allow("load", SecurityConstants.CAN_DEFINE_LOOKUP_OPTIONS);
		security.allow("update", SecurityConstants.VIEW);
		security.allow("cancel", SecurityConstants.VIEW);
		security.allow("validate", SecurityConstants.VIEW);
		return security;
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
	

	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside load method");
		LookupOptionsActionForm lookupOptionsActionForm = (LookupOptionsActionForm) form;
		lookupOptionsActionForm.clear();
		Short localeId = getUserContext(request).getLocaleId();
		MasterPersistence masterPersistence = new MasterPersistence();
		PopulateConfigurationListBox(MasterConstants.SALUTATION, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_SALUTATION);
		PopulateConfigurationListBox(MasterConstants.PERSONNEL_TITLE, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_USER_TITLE);
		PopulateConfigurationListBox(MasterConstants.MARITAL_STATUS, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_MARITAL_STATUS);
		PopulateConfigurationListBox(MasterConstants.ETHINICITY, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_ETHNICITY);
		PopulateConfigurationListBox(MasterConstants.EDUCATION_LEVEL, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_EDUCATION_LEVEL);
		PopulateConfigurationListBox(MasterConstants.CITIZENSHIP, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_CITIZENSHIP);
		PopulateConfigurationListBox(MasterConstants.LOAN_PURPOSES, masterPersistence,
				localeId, request, lookupOptionsActionForm, ConfigurationConstants.CONFIG_PURPOSE_OF_LOAN);
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

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside validate method");
		ActionForwards actionForward = ActionForwards.load_failure;
		String method = (String) request.getAttribute("methodCalled");
		if (method != null) {
			if (method.equals(Methods.load.toString())) {
				actionForward = ActionForwards.load_failure;
			}
			else if (method.equals(Methods.update.toString())) {
				actionForward = ActionForwards.update_failure;
			}
		}
		logger.debug("outside validate method");
		return mapping.findForward(actionForward.toString());
	}
		
	private void ProcessOneConfigurationEntity(HttpServletRequest request, String[] updatedList, String valueListName, Short localeId) throws PersistenceException
	{
		Short valueListId = Short.parseShort(request.getParameter(valueListName));	
		if ((updatedList != null) && (updatedList.length > 0)) {
			CustomValueList customValueList = MifosValueList.mapUpdateStringArrayToCustomValueList(updatedList, valueListId, localeId);
			UpdateDatabase(customValueList, localeId);
		}
	}
	
	private void UpdateDatabase(CustomValueList valueList, Short localeId) throws PersistenceException 
	{
		MasterPersistence masterPersistence = new MasterPersistence();
		for (CustomValueListElement element : valueList.getCustomValueListElements()) {
			if (element.getLookUpId() != 0) {
				masterPersistence.updateValueListElementForLocale(element.getLookUpId(), localeId, element.getLookUpValue());
			} else {
				masterPersistence.addValueListElementForLocale(valueList.getEntityId(), element.getLookUpValue(), localeId);
			}
		}
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside update method");
		Short localeId = getUserContext(request).getLocaleId();
		LookupOptionsActionForm lookupOptionsActionForm = (LookupOptionsActionForm) form;
		
		ProcessOneConfigurationEntity(request, lookupOptionsActionForm.getSalutationList(), 
				ConfigurationConstants.CONFIG_SALUTATION, localeId);
		ProcessOneConfigurationEntity(request, lookupOptionsActionForm.getUserTitleList(), 
				ConfigurationConstants.CONFIG_USER_TITLE, localeId);
		ProcessOneConfigurationEntity(request, lookupOptionsActionForm.getMaritalStatusList(), 
				ConfigurationConstants.CONFIG_MARITAL_STATUS, localeId);
		ProcessOneConfigurationEntity(request, lookupOptionsActionForm.getEthnicityList(), 
				ConfigurationConstants.CONFIG_ETHNICITY, localeId);
		ProcessOneConfigurationEntity(request, lookupOptionsActionForm.getEducationLevelList(), 
				ConfigurationConstants.CONFIG_EDUCATION_LEVEL, localeId);
		ProcessOneConfigurationEntity(request, lookupOptionsActionForm.getCitizenshipList(), 
				ConfigurationConstants.CONFIG_CITIZENSHIP, localeId);
		ProcessOneConfigurationEntity(request, lookupOptionsActionForm.getPurposesOfLoanList(), 
				ConfigurationConstants.CONFIG_PURPOSE_OF_LOAN, localeId);
		ProcessOneConfigurationEntity(request, lookupOptionsActionForm.getHandicappedList(), 
				ConfigurationConstants.CONFIG_HANDICAPPED, localeId);
		ProcessOneConfigurationEntity(request, lookupOptionsActionForm.getCollateralTypeList(), 
				ConfigurationConstants.CONFIG_COLLATERAL_TYPE, localeId);
		ProcessOneConfigurationEntity(request, lookupOptionsActionForm.getOfficerTitleList(), 
				ConfigurationConstants.CONFIG_OFFICER_TITLE, localeId);
		ProcessOneConfigurationEntity(request, lookupOptionsActionForm.getAttendanceList(), 
				ConfigurationConstants.CONFIG_ATTENDANCE, localeId);

		return mapping.findForward(ActionForwards.update_success.toString());
	}

}

