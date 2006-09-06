/**

 * CenterCustomerAction.java    version: 1.0

 

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

package org.mifos.application.customer.center.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerPositionView;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.business.CenterPerformanceHistory;
import org.mifos.application.customer.center.business.service.CenterBusinessService;
import org.mifos.application.customer.center.dao.CenterDAO;
import org.mifos.application.customer.center.struts.actionforms.CenterCustActionForm;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.valueobjects.Center;
import org.mifos.application.customer.group.util.helpers.LinkParameters;
import org.mifos.application.customer.struts.action.CustAction;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.service.FeeBusinessService;
import org.mifos.application.fees.exceptions.FeeException;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class CenterCustAction extends CustAction {
	@Override
	protected BusinessService getService(){
		return getCenterBusinessService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	private CenterBusinessService getCenterBusinessService(){
		return (CenterBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Center);
	}

	public ActionForward chooseOffice(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.chooseOffice_success
				.toString());
	}

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		doCleanUp(actionForm, request);
		request.getSession().removeAttribute(CenterConstants.CENTER_MEETING);
		loadCreateMasterData(actionForm, request);
		actionForm.setMfiJoiningDate(DateHelper.getCurrentDate(getUserContext(request).getPereferedLocale()));
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward loadMeeting(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.loadMeeting_success
				.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		MeetingBO meeting = (MeetingBO) SessionUtils.getAttribute(
				CenterConstants.CENTER_MEETING, request.getSession());
		List<CustomFieldView> customFields = actionForm.getCustomFields();
		UserContext userContext = getUserContext(request);
		convertCustomFieldDateToUniformPattern(customFields, userContext.getPereferedLocale());
		
		CenterBO center = new CenterBO(userContext,
				actionForm.getDisplayName(), actionForm.getAddress(),
				customFields, actionForm.getFeesToApply(), actionForm
						.getExternalId(),
				getDateFromString(actionForm.getMfiJoiningDate(), userContext
						.getPereferedLocale()), actionForm.getOfficeIdValue(),
				meeting, actionForm.getLoanOfficerIdValue());
		center.save();
		actionForm.setCustomerId(center.getCustomerId().toString());
		actionForm.setGlobalCustNum(center.getGlobalCustNum());
		center = null;
		return mapping.findForward(ActionForwards.create_success.toString());
	}

	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		clearActionForm((CenterCustActionForm) form);
		CenterBO center = (CenterBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		CenterBO centerBO = getCenterBusinessService().getCenter(center.getCustomerId());
		center = null;
		
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,centerBO
				, request
						.getSession());
		loadUpdateMasterData(request , centerBO);
		setValuesInActionForm((CenterCustActionForm) form, request);		
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	private void setValuesInActionForm(CenterCustActionForm actionForm,
			HttpServletRequest request) throws Exception{ 
		CenterBO center = (CenterBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		actionForm.setLoanOfficerId(center.getPersonnel().getPersonnelId()
				.toString());
		actionForm.setCustomerId(center.getCustomerId().toString());
		actionForm.setGlobalCustNum(center.getGlobalCustNum());
		actionForm.setExternalId(center.getExternalId());

		if (center.getMfiJoiningDate() != null)
			actionForm.setMfiJoiningDate(DateHelper.getUserLocaleDate(
					getUserContext(request).getPereferedLocale(), center
							.getMfiJoiningDate().toString()));

		actionForm.setAddress(center.getAddress());
		actionForm.setCustomerPositions(createCustomerPositionViews(center
				.getCustomerPositions(), request));
		actionForm.setCustomFields(createCustomFieldViews(center
				.getCustomFields(), request));
	}

	public ActionForward editPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.editpreview_success
				.toString());
	}

	public ActionForward editPrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.editprevious_success
				.toString());
	}
	
	@CloseSession
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CenterBO center = (CenterBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		Date mfiJoiningDate = null; 
		if(actionForm.getMfiJoiningDate()!=null)
			mfiJoiningDate = getDateFromString(actionForm.getMfiJoiningDate(), getUserContext(request)
				.getPereferedLocale());
		center.update(getUserContext(request), actionForm.getLoanOfficerIdValue(), actionForm.getExternalId(), mfiJoiningDate, actionForm.getAddress(), actionForm.getCustomFields(), actionForm.getCustomerPositions());
		return mapping.findForward(ActionForwards.update_success.toString());
	}
	
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		return mapping.findForward(method + "_failure");
	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		ActionForwards forward = null;
		if (actionForm.getInput().equals(Methods.create.toString()))
			forward = ActionForwards.cancel_success;
		else if (actionForm.getInput().equals(Methods.manage.toString()))
			forward = ActionForwards.editcancel_success;
		return mapping.findForward(forward.toString());
	}

	private void loadCreateMasterData(CenterCustActionForm actionForm,
			HttpServletRequest request) throws ApplicationException,
			SystemException {
		loadLoanOfficers(actionForm.getOfficeIdValue(), request);
		loadCreateCustomFields(actionForm, request);
		loadFees(actionForm, request, FeeCategory.CENTER);
	}

	private void loadUpdateMasterData(HttpServletRequest request ,CenterBO center)
			throws ApplicationException, SystemException {
		loadLoanOfficers(center.getOffice().getOfficeId(), request);
		loadCustomFieldDefinitions(request);
		loadPositions(request);
		loadClients(request,center);
	}

	private void loadCreateCustomFields(CenterCustActionForm actionForm,
			HttpServletRequest request) throws SystemException, ApplicationException {
		loadCustomFieldDefinitions(request);
		// Set Default values for custom fields
		List<CustomFieldDefinitionEntity> customFieldDefs = 
			(List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request
						.getSession());
		List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

		for (CustomFieldDefinitionEntity fieldDef : customFieldDefs) {
			if (StringUtils.isNullAndEmptySafe(fieldDef.getDefaultValue())
					&& fieldDef.getFieldType().equals(
							CustomFieldType.DATE.getValue())) {
				customFields.add(new CustomFieldView(fieldDef.getFieldId(),
						DateHelper.getUserLocaleDate(getUserContext(request)
								.getPereferedLocale(), fieldDef
								.getDefaultValue()), fieldDef.getFieldType()));
			} else {
				customFields.add(new CustomFieldView(fieldDef.getFieldId(),
						fieldDef.getDefaultValue(), fieldDef.getFieldType()));
			}
		}
		actionForm.setCustomFields(customFields);
	}

	private void loadCustomFieldDefinitions(HttpServletRequest request)
			throws SystemException, ApplicationException {
		MasterDataService masterDataService = (MasterDataService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.MasterDataService);
		List<CustomFieldDefinitionEntity> customFieldDefs = masterDataService
				.retrieveCustomFieldsDefinition(EntityType.CENTER);
		SessionUtils.setAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,
				customFieldDefs, request.getSession());
	}

	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request.getSession());
		CustomerBusinessService customerBusinessService = ((CustomerBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Customer));
		CenterBO centerBO =(CenterBO) customerBusinessService.getBySystemId(actionForm.getGlobalCustNum(),CustomerLevel.CENTER.getValue());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,centerBO
				, request
						.getSession());
		centerBO.getCustomerStatus().setLocaleId(getUserContext(request).getLocaleId());
		SessionUtils.setAttribute(CenterConstants.GROUP_LIST,centerBO.getChildren(CustomerLevel.GROUP.getValue()),request
				.getSession());
	  CenterPerformanceHistory centerPerformanceHistory = 	customerBusinessService.getCenterPerformanceHistory(centerBO.getSearchId(),centerBO.getOffice().getOfficeId());
	  SessionUtils.setAttribute(CenterConstants.PERFORMANCE_HISTORY,centerPerformanceHistory,request
				.getSession());
	  
	  //set localeId in center saving accounts 
	  UserContext userContext = getUserContext(request);
	  setLocaleIdToSavingsStatus(centerBO.getActiveSavingsAccounts(),userContext.getLocaleId());
	  
	  //	TODO : get value object 
	  CenterDAO centerDAO =  new CenterDAO();
	  Center center = centerDAO.findBySystemId(centerBO.getGlobalCustNum());
	  
	  
	  SessionUtils.setAttribute("CustomerVO",center
				, request
						.getSession());  
	  
	  SessionUtils.setAttribute(CustomerConstants.LINK_VALUES,getLinkValues(center),request
						.getSession());
		return mapping.findForward(ActionForwards.get_success.toString());
	}
	private void setLocaleIdToSavingsStatus(List<SavingsBO> accountList,
			Short localeId) {
		for (SavingsBO accountBO : accountList)
			setLocaleForAccount((AccountBO) accountBO, localeId);
	}

	private void setLocaleForAccount(AccountBO account, Short localeId) {
		account.getAccountState().setLocaleId(localeId);
	}
	private void doCleanUp(CenterCustActionForm actionForm,
			HttpServletRequest request) {
		clearActionForm(actionForm);
		SessionUtils.setAttribute(CenterConstants.CENTER_MEETING, null, request
				.getSession());
	}

	private void clearActionForm(CenterCustActionForm actionForm) {
		actionForm.setDefaultFees(new ArrayList<FeeView>());
		actionForm.setAdditionalFees(new ArrayList<FeeView>());
		actionForm.setCustomerPositions(new ArrayList<CustomerPositionView>());
		actionForm.setCustomFields(new ArrayList<CustomFieldView>());
		actionForm.setAddress(new Address());
		actionForm.setDisplayName(null);
		actionForm.setMfiJoiningDate(null);
		actionForm.setGlobalCustNum(null);
		actionForm.setCustomerId(null);
		actionForm.setExternalId(null);
		actionForm.setLoanOfficerId(null);
	}
	
	//TODO: remove this function after complete migration 
	LinkParameters getLinkValues(Center center){
		LinkParameters linkParams = new LinkParameters();
		linkParams.setCustomerId(center.getCustomerId());
		linkParams.setCustomerName(center.getDisplayName());
		linkParams.setGlobalCustNum(center.getGlobalCustNum());
		linkParams.setCustomerOfficeId(center.getOffice().getOfficeId());
		linkParams.setCustomerOfficeName(center.getOffice().getOfficeName());
		linkParams.setLevelId(CustomerConstants.CENTER_LEVEL_ID);
		Customer parent = center.getParentCustomer();
		if(parent!=null){
			linkParams.setCustomerParentGCNum(parent.getGlobalCustNum());
			linkParams.setCustomerParentName(parent.getDisplayName());
		}
		return linkParams;
	}
}
