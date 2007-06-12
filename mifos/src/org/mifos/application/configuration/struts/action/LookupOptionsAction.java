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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.configuration.struts.actionform.LookupOptionsActionForm;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
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
		// TODO: define LookupOption specific security
		//security.allow("create",
		//		SecurityConstants.DEFINE_LOOKUP_OPTION_FORM_INSTANCE);
		
		security.allow("load", SecurityConstants.CAN_DEFINE_LABELS);
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
	
	private String[] getAValueList(MifosLookUpEntity entity)
	{
		// TODO: replace dummy data with lookup from the database
		List<String> selectList = new ArrayList<String>();
		if (entity.getEntityType().equals(ConfigurationConstants.SALUTATION))
		{
			selectList.add("Mr");
			selectList.add("Mrs");
		}
		else if (entity.getEntityType().equals(ConfigurationConstants.USER_TITLE))
		{
			selectList.add("Teller");
			selectList.add("IT Manager");
		}
		else if (entity.getEntityType().equals(ConfigurationConstants.MARITAL_STATUS))
		{
			selectList.add("Single");
			selectList.add("IT Married");
			selectList.add("Divorced");
		}
		else if (entity.getEntityType().equals(ConfigurationConstants.ETHINICITY))
		{
			selectList.add("Asian");
			selectList.add("Native American");
			selectList.add("African American");
		}
		else if (entity.getEntityType().equals(ConfigurationConstants.EDUCATION_LEVEL))
		{
			selectList.add("Master");
			selectList.add("PhD");
		}
		else if (entity.getEntityType().equals(ConfigurationConstants.CITIZENSHIP))
		{
			selectList.add("Indian");
			selectList.add("American");
			selectList.add("Pakistan");
		}
		else if (entity.getEntityType().equals(ConfigurationConstants.PURPOSE_OF_LOAN))
		{
			selectList.add("Agriculture");
			selectList.add("Horticulture");
		}
		else if (entity.getEntityType().equals(ConfigurationConstants.COLLATERAL_TYPE))
		{
			selectList.add("Savings");
			selectList.add("Insurance");
		}
		else if (entity.getEntityType().equals(ConfigurationConstants.HANDICAPPED))
		{
			selectList.add("Partial");
		}
		else if (entity.getEntityType().equals(ConfigurationConstants.ATTENDANCE))
		{
			selectList.add("Present");
			selectList.add("Absent");
			selectList.add("Late");
		}
		else if (entity.getEntityType().equals(ConfigurationConstants.OFFICER_TITLE))
		{
			selectList.add("Vice President");
			selectList.add("Treasurer");
		}
		String[] list = new String[selectList.size()];
		list = selectList.toArray(list);
		return list;
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside load method");
		MifosLookUpEntity entity = new MifosLookUpEntity();
		LookupOptionsActionForm lookupOptionsActionForm = (LookupOptionsActionForm) form;
		lookupOptionsActionForm.clear();
		entity.setEntityType(ConfigurationConstants.SALUTATION);
		lookupOptionsActionForm.setSalutations(getAValueList(entity));
		entity.setEntityType(ConfigurationConstants.USER_TITLE);
		lookupOptionsActionForm.setUserTitles(getAValueList(entity));
		entity.setEntityType(ConfigurationConstants.MARITAL_STATUS);
		lookupOptionsActionForm.setMaritalStatuses(getAValueList(entity));
		entity.setEntityType(ConfigurationConstants.ETHINICITY);
		lookupOptionsActionForm.setEthnicities(getAValueList(entity));
		entity.setEntityType(ConfigurationConstants.EDUCATION_LEVEL);
		lookupOptionsActionForm.setEducationLevels(getAValueList(entity));
		entity.setEntityType(ConfigurationConstants.CITIZENSHIP);
		lookupOptionsActionForm.setCitizenships(getAValueList(entity));
		entity.setEntityType(ConfigurationConstants.PURPOSE_OF_LOAN);
		lookupOptionsActionForm.setPurposesOfLoan(getAValueList(entity));
		entity.setEntityType(ConfigurationConstants.COLLATERAL_TYPE);
		lookupOptionsActionForm.setCollateralTypes(getAValueList(entity));
		entity.setEntityType(ConfigurationConstants.HANDICAPPED);
		lookupOptionsActionForm.setHandicappeds(getAValueList(entity));
		entity.setEntityType(ConfigurationConstants.ATTENDANCE);
		lookupOptionsActionForm.setAttendances(getAValueList(entity));
		entity.setEntityType(ConfigurationConstants.OFFICER_TITLE);
		lookupOptionsActionForm.setOfficerTitles(getAValueList(entity));
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

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside update method");
	
		return mapping.findForward(ActionForwards.update_success.toString());
	}

}

