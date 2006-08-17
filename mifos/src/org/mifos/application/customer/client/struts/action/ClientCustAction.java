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

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.service.CenterBusinessService;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.client.business.service.ClientBusinessService;
import org.mifos.application.customer.client.struts.actionforms.ClientCreationActionForm;
import org.mifos.application.customer.client.struts.actionforms.ClientCustActionForm;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.struts.action.CustAction;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.master.business.SpouseFatherLookupEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.valueobjects.SpouseFatherLookup;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.valueobjects.Context;

public class ClientCustAction extends CustAction {


	
private PersonnelPersistenceService personnelPersistenceService;
	private CustomerBusinessService customerService;
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
		return getClientBusinessService();
	}
	private ClientBusinessService getClientBusinessService()
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
		actionForm.setGroupFlag(ClientConstants.NO);
		return mapping.findForward(ActionForwards.chooseOffice_success.toString());
	}
	
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		doCleanUp(actionForm, request);
		request.getSession().removeAttribute(ClientConstants.CLIENT_MEETING);
		if(actionForm.getGroupFlag().equals(ClientConstants.YES)){
			actionForm.setParentGroup(customerService.getCustomer(Integer.valueOf(actionForm.getParentGroupId())));
			
		}
		loadCreateMasterData(actionForm, request);
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	public ActionForward next(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.next_success.toString());
	}
	
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		HttpSession session = request.getSession();
		if(Configuration.getInstance().getCustomerConfig(getUserContext(request).getBranchId()).isPendingApprovalStateDefinedForClient() == true){
			session.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, CustomerConstants.YES);

		}
		else
			session.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, CustomerConstants.NO);
		actionForm.setAge(new CustomerHelper().calculateAge(DateHelper.getLocaleDate(getUserContext(request).getPereferedLocale(), actionForm.getDateOfBirth())));
		return mapping.findForward(ActionForwards.preview_success.toString());
	}
	
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = null;
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		String fromPage = actionForm.getInput();
		if( ClientConstants.INPUT_PERSONAL_INFO.equals(fromPage) ||  ClientConstants.INPUT_MFI_INFO.equals(fromPage) || CenterConstants.INPUT_CREATE.equals(fromPage) )
			forward =ActionForwards.cancelCreate_success.toString();
		else if( ClientConstants.INPUT_EDIT_PERSONAL_INFO.equals(fromPage) ||  ClientConstants.INPUT_EDIT_MFI_INFO.equals(fromPage))
			forward = ActionForwards.cancelEdit_success.toString();
		return mapping.findForward(forward);
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
		SessionUtils.setAttribute(ClientConstants.HANDICAPPED_ENTITY,	customerService.retrieveMasterEntities(
				MasterConstants.HANDICAPPED, getUserContext(request).getLocaleId()), request.getSession());
		SessionUtils.setAttribute(ClientConstants.ETHINICITY_ENTITY,	customerService.retrieveMasterEntities(
				MasterConstants.ETHINICITY, getUserContext(request).getLocaleId()), request.getSession());
		loadCreateCustomFields(actionForm, EntityType.CLIENT, request);
		loadFees(actionForm, request);
		loadFormedByPersonnel(actionForm, request);
		if(actionForm.getGroupFlag().equals(ClientConstants.NO)){
			loadLoanOfficers(actionForm.getOfficeIdValue() ,request);
		}
	}
	
	public ActionForward retrievePictureOnPreview(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		ClientCustActionForm actionForm = (ClientCustActionForm)form;
		InputStream in = actionForm.getCustomerPicture();
		in.mark(0);
		response.setContentType("image/jpeg");
		BufferedOutputStream out = new BufferedOutputStream( response.getOutputStream() );
		byte[] by = new byte[1024*4]; // 4K buffer buf, 0, buf.length
		int index = in.read(by , 0 , 1024*4);
		while ( index != -1) {
			out.write(by , 0, index);
			 index = in.read ( by , 0 , 1024*4 );
		}
		out.flush();
		out.close();
		in.reset();
		String forward = ClientConstants.CUSTOMER_PICTURE_PAGE;
		return mapping.findForward(forward);
	}
	
	public ActionForward prevPersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse httpservletresponse)
	throws Exception
	{
		return mapping.findForward(ActionForwards.prevPersonalInfo_success.toString());
	}

	public ActionForward prevMFIInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse httpservletresponse)
	throws Exception
	{
		return mapping.findForward(ActionForwards.prevMFIInfo_success.toString());
	}
	private void doCleanUp(ClientCustActionForm actionForm,
			HttpServletRequest request) {
		clearActionForm(actionForm);
		SessionUtils.setAttribute(ClientConstants.CLIENT_MEETING, null, request
				.getSession());
	}

	private void clearActionForm(ClientCustActionForm actionForm) {
		actionForm.setDefaultFees(new ArrayList<FeeView>());
		actionForm.setAdditionalFees(new ArrayList<FeeView>());
		actionForm.setCustomFields(new ArrayList<CustomFieldView>());
		actionForm.setAddress(new Address());
		actionForm.setDisplayName(null);
		actionForm.setDateOfBirth(null);
		actionForm.setMfiJoiningDate(null);
		actionForm.setGlobalCustNum(null);
		actionForm.setCustomerId(null);
		actionForm.setExternalId(null);
		actionForm.setLoanOfficerId(null);
		actionForm.setFormedByPersonnel(null);
		actionForm.setTrained(null);
		actionForm.setTrainedDate(null);
		actionForm.setParentGroup(null);
		actionForm.setClientName(new ClientNameDetailView());
		actionForm.setSpouseName(new ClientNameDetailView());
		actionForm.setClientDetailView(new ClientDetailView());
		actionForm.setNextOrPreview("next");
	}
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		return mapping.findForward(method + "_failure");
	}
}
