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
import org.mifos.application.customer.business.CustomerPositionEntity;
import org.mifos.application.customer.business.CustomerPositionView;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.business.CenterPerformanceHistory;
import org.mifos.application.customer.center.business.service.CenterBusinessService;
import org.mifos.application.customer.center.struts.actionforms.CenterCustActionForm;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.struts.action.CustAction;
import org.mifos.application.customer.util.helpers.ChildrenStateType;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

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

	@TransactionDemarcate(saveToken = true)
	public ActionForward chooseOffice(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.chooseOffice_success
				.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		doCleanUp(actionForm, request);
		SessionUtils.removeAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		loadCreateMasterData(actionForm, request);
		actionForm.setMfiJoiningDate(DateHelper.getCurrentDate(getUserContext(request).getPereferedLocale()));
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward loadMeeting(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.loadMeeting_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		MeetingBO meeting = (MeetingBO) SessionUtils.getAttribute(
				CustomerConstants.CUSTOMER_MEETING, request);
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

	@TransactionDemarcate(joinToken = true)
	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		clearActionForm((CenterCustActionForm) form);
		CenterBO center = (CenterBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		CenterBO centerBO = getCenterBusinessService().getCenter(center.getCustomerId());
		center = null;

		SessionUtils.setAttribute(Constants.BUSINESS_KEY,centerBO
				, request);
		loadUpdateMasterData(request , centerBO);
		setValuesInActionForm((CenterCustActionForm) form, request);
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	private void setValuesInActionForm(CenterCustActionForm actionForm,
			HttpServletRequest request) throws Exception{
		CenterBO center = (CenterBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		if(center.getPersonnel()!=null)
			actionForm.setLoanOfficerId(center.getPersonnel().getPersonnelId()
					.toString());
		else
			actionForm.setLoanOfficerId(null);
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

	@TransactionDemarcate(joinToken = true)
	public ActionForward editPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.editpreview_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward editPrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.editprevious_success
				.toString());
	}

	@CloseSession
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CenterBO center = (CenterBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		Date mfiJoiningDate = null;
		if(actionForm.getMfiJoiningDate()!=null)
			mfiJoiningDate = getDateFromString(actionForm.getMfiJoiningDate(), getUserContext(request)
				.getPereferedLocale());
		CenterBO centerBO = ((CenterBusinessService)getService()).findBySystemId(center.getGlobalCustNum());
		centerBO.setVersionNo(center.getVersionNo());
		centerBO.setUserContext(getUserContext(request));
		setInitialObjectForAuditLogging(centerBO);
		centerBO.update(getUserContext(request), actionForm.getLoanOfficerIdValue(), actionForm.getExternalId(), mfiJoiningDate, actionForm.getAddress(), actionForm.getCustomFields(), actionForm.getCustomerPositions());
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		return mapping.findForward(method + "_failure");
	}

	@TransactionDemarcate(validateAndResetToken = true)
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
			HttpServletRequest request) throws Exception {
		loadLoanOfficers(actionForm.getOfficeIdValue(), request);
		loadCreateCustomFields(actionForm, request);
		loadFees(actionForm, request, FeeCategory.CENTER);
	}

	private void loadUpdateMasterData(HttpServletRequest request ,CenterBO center)
			throws Exception {
		loadLoanOfficers(center.getOffice().getOfficeId(), request);
		loadCustomFieldDefinitions(request);
		loadPositions(request);
		loadClients(request,center);
	}

	private void loadCreateCustomFields(CenterCustActionForm actionForm,
			HttpServletRequest request) throws Exception {
		loadCustomFieldDefinitions(request);
		// Set Default values for custom fields
		List<CustomFieldDefinitionEntity> customFieldDefs =
			(List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
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
			throws Exception {
		MasterDataService masterDataService = (MasterDataService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.MasterDataService);
		List<CustomFieldDefinitionEntity> customFieldDefs = masterDataService
				.retrieveCustomFieldsDefinition(EntityType.CENTER);
		SessionUtils.setAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,
				customFieldDefs, request);
	}

	@TransactionDemarcate (saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CenterCustActionForm actionForm = (CenterCustActionForm) form;

		SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
		CustomerBusinessService customerBusinessService = ((CustomerBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Customer));
		CenterBO centerBO =(CenterBO) customerBusinessService.findBySystemId(actionForm.getGlobalCustNum(),CustomerLevel.CENTER.getValue());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, centerBO, request);
		centerBO.getCustomerStatus().setLocaleId(getUserContext(request).getLocaleId());
		SessionUtils.setAttribute(CenterConstants.GROUP_LIST,centerBO.getChildren(CustomerLevel.GROUP, ChildrenStateType.OTHER_THAN_CANCELLED_AND_CLOSED),request);

	  CenterPerformanceHistory centerPerformanceHistory = 	customerBusinessService.getCenterPerformanceHistory(centerBO.getSearchId(),centerBO.getOffice().getOfficeId());
	  SessionUtils.setAttribute(CenterConstants.PERFORMANCE_HISTORY,centerPerformanceHistory,request);

	  //set localeId in center saving accounts

	  loadCustomFieldDefinitions(request);

	  UserContext userContext = getUserContext(request);
	  
	  loadMasterDataForDetailsPage(request,centerBO,getUserContext(request).getLocaleId());
	  initCustomerPosition(centerBO,userContext.getLocaleId());
	  return mapping.findForward(ActionForwards.get_success.toString());
	}

	@TransactionDemarcate(conditionToken = true)
	public ActionForward loadSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		actionForm.setSearchString(null);
		cleanUpSearch(request);
		if (actionForm.getInput().equals(
				CenterConstants.INPUT_SEARCH_TRANSFERGROUP))
			 return mapping.findForward(ActionForwards.loadTransferSearch_success
					.toString());
		else
			return mapping.findForward(ActionForwards.loadSearch_success
					.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		ActionForward actionForward = super.search(mapping, form, request,
				response);
		String searchString = actionForm.getSearchString();

		if (searchString == null)checkSearchString(actionForm,request);
			
		searchString = StringUtils.normalizeSearchString(searchString);
		if (searchString.equals(""))
			checkSearchString(actionForm,request);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SessionUtils.setAttribute(Constants.SEARCH_RESULTS,
				new CenterBusinessService().search(searchString, userContext
						.getId()), request);
		addSeachValues(searchString, userContext.getBranchId().toString(),
				new OfficeBusinessService()
						.getOffice(userContext.getBranchId()).getOfficeName(),
				request);
		if (actionForm.getInput().equals(
				CenterConstants.INPUT_SEARCH_TRANSFERGROUP))
			actionForward = mapping.findForward(ActionForwards.transferSearch_success
					.toString());;
		return actionForward;
	}
	
	private void checkSearchString(CenterCustActionForm actionForm,HttpServletRequest request) throws CustomerException{
		if (actionForm.getInput()!=null&&actionForm.getInput().equals(
				CenterConstants.INPUT_SEARCH_TRANSFERGROUP))
			request.setAttribute(Constants.INPUT,CenterConstants.INPUT_SEARCH_TRANSFERGROUP);
		else {
			request.setAttribute(Constants.INPUT,null);
		}
		throw new CustomerException(CenterConstants.NO_SEARCH_STING);

	}

	private void loadMasterDataForDetailsPage(HttpServletRequest request,
			CenterBO centerBO, Short localeId) throws Exception {
		List<SavingsBO> savingsAccounts = centerBO.getOpenSavingAccounts();
		setLocaleIdToSavingsStatus(savingsAccounts, localeId);
		SessionUtils.setAttribute(ClientConstants.CUSTOMERSAVINGSACCOUNTSINUSE,
				savingsAccounts, request);
	}

	private void initCustomerPosition(CenterBO centerBO,Short localeId){

		for (CustomerPositionEntity customerPosition : centerBO.getCustomerPositions()) {

			customerPosition.getPosition().setLocaleId(localeId);
		}
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
			HttpServletRequest request) throws Exception {
		clearActionForm(actionForm);
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
}
