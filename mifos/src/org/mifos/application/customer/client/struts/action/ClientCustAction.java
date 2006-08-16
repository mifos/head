/**

* ClientCustAction.java version: 1.0



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

package org.mifos.application.customer.client.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.service.CenterBusinessService;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.client.business.service.ClientBusinessService;
import org.mifos.application.customer.client.struts.actionforms.ClientCustActionForm;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.struts.action.CustAction;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.master.business.SpouseFatherLookupEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.valueobjects.SpouseFatherLookup;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class ClientCustAction extends CustAction {

private CustomerBusinessService customerService;
	
private PersonnelPersistenceService personnelPersistenceService;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.CENTERLOGGER);

	public ClientCustAction() throws ServiceException {
		customerService = (CustomerBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Customer);
		personnelPersistenceService = (PersonnelPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.Personnel);
	}
	@Override
	protected BusinessService getService() throws ServiceException {
		return getCenterBusinessService();
	}
	private ClientBusinessService getCenterBusinessService()
	throws ServiceException {
		return (ClientBusinessService) ServiceFactory.getInstance()
		.getBusinessService(BusinessServiceName.Client);
	}
	
	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}
	
	public ActionForward chooseOffice(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		actionForm.setIsClientUnderGroup(ClientConstants.NO);
		return mapping.findForward(ActionForwards.chooseOffice_success.toString());
	}
	
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		doCleanUp(actionForm, request);
		request.getSession().removeAttribute(ClientConstants.CLIENT_MEETING);
		System.out.println("-------------------------------actionForm.getIsClientUnderGroup(): "+actionForm.getIsClientUnderGroup());
		
		if(actionForm.getIsClientUnderGroup().equals(ClientConstants.YES)){
			actionForm.setParentGroup(customerService.getCustomer(Integer.valueOf(actionForm.getParentGroupId())));
		}
		loadCreateMasterData(actionForm, request);
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	private void loadCreateMasterData(ClientCustActionForm actionForm, HttpServletRequest request) throws ApplicationException, SystemException {
		SessionUtils.setAttribute(ClientConstants.SALUTATION_ENTITY,	customerService.retrieveMasterEntities(
				MasterConstants.SALUTATION, getUserContext(request).getLocaleId()), request.getSession());
		SessionUtils.setAttribute(ClientConstants.MARITAL_STATUS_ENTITY,	customerService.retrieveMasterEntities(
						MasterConstants.MARITAL_STATUS, getUserContext(request).getLocaleId()), request.getSession());
		SessionUtils.setAttribute(ClientConstants.CITIZENSHIP_ENTITY,	customerService.retrieveMasterEntities(
				MasterConstants.CITIZENSHIP, getUserContext(request).getLocaleId()), request.getSession());
		SessionUtils.setAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY,	customerService.retrieveMasterEntities(
				MasterConstants.BUSINESS_ACTIVITIES, getUserContext(request).getLocaleId()), request.getSession());
		SessionUtils.setAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY,	customerService.retrieveMasterEntities(
				MasterConstants.EDUCATION_LEVEL, getUserContext(request).getLocaleId()), request.getSession());
		SessionUtils.setAttribute(ClientConstants.GENDER_ENTITY,	customerService.retrieveMasterEntities(
				MasterConstants.GENDER, getUserContext(request).getLocaleId()), request.getSession());
		SessionUtils.setAttribute(ClientConstants.SPOUSE_FATHER_ENTITY,
				getMasterEntities(SpouseFatherLookupEntity.class, getUserContext(
						request).getLocaleId()), request.getSession());
		System.out.println("getMasterEntities(SpouseFatherLookupEntity.lass, getUserContext(request).getLocaleId()), request.getSession());: "+
				(getMasterEntities(SpouseFatherLookupEntity.class, getUserContext(request).getLocaleId()).size()));
		/*SessionUtils.setAttribute(ClientConstants.SPOUSE_FATHER_ENTITY,	customerService.retrieveMasterEntities(
				MasterConstants.SPOUSE_FATHER, getUserContext(request).getLocaleId()), request.getSession());*/
		SessionUtils.setAttribute(ClientConstants.HANDICAPPED_ENTITY,	customerService.retrieveMasterEntities(
				MasterConstants.HANDICAPPED, getUserContext(request).getLocaleId()), request.getSession());
		SessionUtils.setAttribute(ClientConstants.ETHINICITY_ENTITY,	customerService.retrieveMasterEntities(
				MasterConstants.ETHINICITY, getUserContext(request).getLocaleId()), request.getSession());
		loadCreateCustomFields(actionForm, request);
		loadFees(actionForm, request);
	}
	
	private void doCleanUp(ClientCustActionForm actionForm,
			HttpServletRequest request) {
		clearActionForm(actionForm);
		SessionUtils.setAttribute(ClientConstants.CLIENT_MEETING, null, request
				.getSession());
	}

	private Date getDateFromString(String strDate, Locale locale) {
		Date date = null;
		if (StringUtils.isNullAndEmptySafe(strDate))
			date = new Date(DateHelper.getLocaleDate(locale, strDate).getTime());
		return date;
	}

	private void clearActionForm(ClientCustActionForm actionForm) {
		actionForm.setDefaultFees(new ArrayList<FeeView>());
		actionForm.setAdditionalFees(new ArrayList<FeeView>());
		actionForm.setCustomFields(new ArrayList<CustomFieldView>());
		actionForm.setAddress(new Address());
		actionForm.setDisplayName(null);
		actionForm.setMfiJoiningDate(null);
		actionForm.setGlobalCustNum(null);
		actionForm.setCustomerId(null);
		actionForm.setExternalId(null);
		actionForm.setLoanOfficerId(null);
		actionForm.setParentGroup(null);
		actionForm.setClientName(new ClientNameDetailView());
		actionForm.setSpouseName(new ClientNameDetailView());
		actionForm.setClientDetailView(new ClientDetailView());
	}
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		return mapping.findForward(method + "_failure");
	}
}
