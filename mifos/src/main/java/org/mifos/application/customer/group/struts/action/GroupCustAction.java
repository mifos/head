/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.customer.group.struts.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerFlagDetailEntity;
import org.mifos.application.customer.business.CustomerPositionEntity;
import org.mifos.application.customer.business.CustomerPositionView;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.service.GroupBusinessService;
import org.mifos.application.customer.group.persistence.GroupPersistence;
import org.mifos.application.customer.group.struts.actionforms.GroupCustActionForm;
import org.mifos.application.customer.group.util.helpers.CenterSearchInput;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.struts.action.CustAction;
import org.mifos.application.customer.util.helpers.ChildrenStateType;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.config.ClientRules;
import org.mifos.config.ProcessFlowRules;

public class GroupCustAction extends CustAction {

	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.GROUP_LOGGER);
	
	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@Override
	protected BusinessService getService(){
		return getGroupBusinessService();
	}
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("groupCustAction");
		security.allow("hierarchyCheck", SecurityConstants.VIEW);
		security.allow("chooseOffice", SecurityConstants.VIEW);
		security.allow("load", SecurityConstants.VIEW);
		security.allow("loadMeeting",
				SecurityConstants.MEETING_CREATE_GROUP_MEETING);
		security.allow("preview", SecurityConstants.VIEW);
		security.allow("previous", SecurityConstants.VIEW);
		security.allow("create", SecurityConstants.VIEW);

		security.allow("getDetails", SecurityConstants.VIEW);
		security.allow("get", SecurityConstants.VIEW);
		security.allow("manage", SecurityConstants.GROUP_EDIT_GROUP);
		security.allow("previewManage", SecurityConstants.VIEW);
		security.allow("previousManage", SecurityConstants.VIEW);
		security.allow("update", SecurityConstants.GROUP_EDIT_GROUP);
		security.allow("loadSearch", SecurityConstants.VIEW);
		security.allow("search", SecurityConstants.VIEW);

		security.allow("loadChangeLog", SecurityConstants.VIEW);
		security.allow("cancelChangeLog", SecurityConstants.VIEW);
		return security;
	}
	
	@TransactionDemarcate (saveToken = true)
	public ActionForward hierarchyCheck(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionForwards actionForward = null;
		boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists(); 
		if(isCenterHierarchyExists){
			CenterSearchInput searchInputs = new CenterSearchInput(getUserContext(request).getBranchId(), GroupConstants.CREATE_NEW_GROUP);
			SessionUtils.setAttribute(GroupConstants.CENTER_SEARCH_INPUT, searchInputs, request.getSession());
			actionForward = ActionForwards.loadCenterSearch;
		}else
			actionForward = ActionForwards.loadCreateGroup;
		
		return mapping.findForward(actionForward.toString());
	}
	
	@TransactionDemarcate (saveToken = true)
	public ActionForward chooseOffice(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.chooseOffice_success
				.toString());
	}
	
	@TransactionDemarcate (saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GroupCustActionForm actionForm = (GroupCustActionForm) form;
		doCleanUp(actionForm, request);
		boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists();
		if(isCenterHierarchyExists){
			actionForm.setParentCustomer(getCustomerBusinessService().findBySystemId(actionForm.getCenterSystemId()));
			actionForm.getParentCustomer().getCustomerMeeting().getMeeting().isMonthly();
			actionForm.getParentCustomer().getCustomerMeeting().getMeeting().isWeekly();
			actionForm.setOfficeId(actionForm.getParentCustomer().getOffice().getOfficeId().toString());
			actionForm.setFormedByPersonnel(actionForm.getParentCustomer().getPersonnel().getPersonnelId().toString());
		}
		loadCreateMasterData(actionForm, request, isCenterHierarchyExists);

		SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST,
				isCenterHierarchyExists, request);		
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
		
		boolean isPendingApprovalDefined = ProcessFlowRules.isGroupPendingApprovalStateEnabled();
		SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, isPendingApprovalDefined, request);
		return mapping.findForward(ActionForwards.preview_success.toString());
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previous_success
				.toString());
	}
	
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GroupCustActionForm actionForm = (GroupCustActionForm) form;
		boolean isCenterHierarchyExists = (Boolean)SessionUtils.getAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, request);
		
		GroupBO group ;
		if(isCenterHierarchyExists)
			group = createGroupWithCenter(actionForm, request);
		else
			group = createGroupWithoutCenter(actionForm, request);
		
		new GroupPersistence().saveGroup(group);
		actionForm.setCustomerId(group.getCustomerId().toString());
		actionForm.setGlobalCustNum(group.getGlobalCustNum());
		SessionUtils.setAttribute(GroupConstants.IS_GROUP_LOAN_ALLOWED,
				ClientRules.getGroupCanApplyLoans(), request);
		return mapping.findForward(ActionForwards.create_success.toString());
	}	

	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In GroupCustAction get method " );
		GroupCustActionForm actionForm = (GroupCustActionForm) form;
		GroupBO groupBO;
		groupBO = getGroupBusinessService().findBySystemId(
					actionForm.getGlobalCustNum());
		groupBO.setUserContext(getUserContext(request));
		groupBO.getCustomerStatus().setLocaleId(
				getUserContext(request).getLocaleId());
		loadMasterDataForDetailsPage(request, groupBO, getUserContext(request)
				.getLocaleId());
		setLocaleForMasterEntities(groupBO, getUserContext(request)
				.getLocaleId());
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, groupBO, request);
		request.getSession().setAttribute(Constants.BUSINESS_KEY, groupBO);
		logger.debug("Exiting GroupCustAction get method " );
		
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		List<SurveyInstance> surveys = surveysPersistence.retrieveInstancesByCustomer(groupBO);
               boolean activeSurveys =
                       surveysPersistence.retrieveSurveysByTypeAndState(
                                       SurveyType.GROUP, SurveyState.ACTIVE).size() > 0;
		request.setAttribute(CustomerConstants.SURVEY_KEY, surveys);
               request.setAttribute(CustomerConstants.SURVEY_COUNT, activeSurveys);
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		clearActionForm((GroupCustActionForm) form);
		GroupBO group = (GroupBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		logger.debug("Entering GroupCustAction manage method and customer id: "+ group.getGlobalCustNum());
		GroupBO groupBO = (GroupBO) getCustomerBusinessService().findBySystemId(
				group.getGlobalCustNum(), CustomerLevel.GROUP.getValue());
		group = null;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, groupBO, request);

		loadUpdateMasterData(request,groupBO);
		setValuesInActionForm((GroupCustActionForm) form, request);
		logger.debug("Exiting GroupCustAction manage method ");
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previewManage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previewManage_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previousManage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previousManage_success
				.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	@CloseSession
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GroupBO group = (GroupBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		GroupCustActionForm actionForm = (GroupCustActionForm) form;
		Date trainedDate = null; 
		if(actionForm.getTrainedDate()!=null)
			trainedDate = getDateFromString(actionForm.getTrainedDate(), getUserContext(request)
				.getPreferredLocale());

		GroupBO groupBO;
		groupBO = getGroupBusinessService().findBySystemId(
				actionForm.getGlobalCustNum());
		checkVersionMismatch(group.getVersionNo(),groupBO.getVersionNo());
		groupBO.setVersionNo(group.getVersionNo());
		groupBO.setUserContext(getUserContext(request));
		setInitialObjectForAuditLogging(groupBO);
		groupBO.update(getUserContext(request),actionForm.getDisplayName(), 
		        actionForm.getLoanOfficerIdValue(), actionForm.getExternalId(),
		        actionForm.getTrainedValue(),trainedDate, actionForm.getAddress(), 
		        actionForm.getCustomFields(), actionForm.getCustomerPositions());
		return mapping.findForward(ActionForwards.update_success.toString());
	}
	
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionForwards forward = null;
		GroupCustActionForm actionForm = (GroupCustActionForm) form;
		String fromPage = actionForm.getInput();
		if (fromPage.equals(GroupConstants.MANAGE_GROUP) || fromPage.equals(GroupConstants.PREVIEW_MANAGE_GROUP)){
			forward=ActionForwards.cancelEdit_success;
		}else if(fromPage.equals(GroupConstants.CREATE_GROUP))
			forward=ActionForwards.cancelCreate_success;
		return mapping.findForward(forward.toString());
	}

	@TransactionDemarcate(conditionToken = true)
	public ActionForward loadSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GroupCustActionForm actionForm = (GroupCustActionForm) form;
		actionForm.setSearchString(null);
		cleanUpSearch(request);
		if (ClientRules.getClientCanExistOutsideGroup())
			SessionUtils.setAttribute(CustomerConstants.GROUP_HIERARCHY_REQUIRED,
					CustomerConstants.NO,request);
		else
			SessionUtils.setAttribute(CustomerConstants.GROUP_HIERARCHY_REQUIRED,
					CustomerConstants.YES,request);
		
		
		if ( actionForm.getInput()!=null && actionForm.getInput().equals(GroupConstants.GROUP_SEARCH_CLIENT_TRANSFER))
			return mapping.findForward(ActionForwards.loadTransferSearch_success.toString());
		else
		return mapping.findForward(ActionForwards.loadSearch_success.toString());
	}	
	
	@Override
	@TransactionDemarcate(joinToken = true)
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GroupCustActionForm actionForm = (GroupCustActionForm) form;
		UserContext userContext = getUserContext(request);
		ActionForward actionForward = super.search(mapping, form, request, response);
		String searchString = actionForm.getSearchString();
		if (searchString==null) checkSearchString(actionForm,request);
		addSeachValues(searchString,userContext.getBranchId().toString(),new OfficeBusinessService().getOffice(userContext.getBranchId()).getOfficeName(),request);
		searchString= StringUtils.normalizeSearchString(searchString);
		if (searchString.equals(""))  checkSearchString(actionForm,request);
		
		SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS,new GroupBusinessService().search(searchString,userContext.getId()),request);
		
		if ( actionForm.getInput()!=null && actionForm.getInput().equals(GroupConstants.GROUP_SEARCH_CLIENT_TRANSFER))
			return mapping.findForward(ActionForwards.transferSearch_success.toString());
		else
			if (actionForm.getInput() != null
				&& actionForm.getInput().equals(
						GroupConstants.GROUP_SEARCH_ADD_CLIENTS_TO_GROUPS)) {
			SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS,
					new GroupBusinessService().searchForAddingClientToGroup(searchString, userContext
							.getId()), request);
			return mapping.findForward(ActionForwards.addGroupSearch_success
					.toString());
		}
			else

	 return actionForward;
	}
	
	private void checkSearchString(GroupCustActionForm actionForm,HttpServletRequest request) throws CustomerException{
		if (actionForm.getInput()!=null&&actionForm.getInput().equals(
				GroupConstants.GROUP_SEARCH_CLIENT_TRANSFER))
			request.setAttribute(Constants.INPUT,CenterConstants.INPUT_SEARCH_TRANSFERGROUP);
		else {
			request.setAttribute(Constants.INPUT,null);
		}
		throw new CustomerException(CenterConstants.NO_SEARCH_STRING);

	}	
	
	private void loadMasterDataForDetailsPage(HttpServletRequest request,
			GroupBO groupBO, Short localeId) throws Exception{
		SessionUtils.setAttribute(GroupConstants.IS_GROUP_LOAN_ALLOWED,
				ClientRules.getGroupCanApplyLoans(), request);
		SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST,
				ClientRules.getCenterHierarchyExists(), request);
		SessionUtils.setCollectionAttribute(ClientConstants.LOANCYCLECOUNTER,
				getCustomerBusinessService().fetchLoanCycleCounter(
						groupBO), request);
		List<LoanBO> loanAccounts = groupBO.getOpenLoanAccounts();
		List<SavingsBO> savingsAccounts = groupBO.getOpenSavingAccounts();
		setLocaleIdToLoanStatus(loanAccounts, localeId);
		setLocaleIdToSavingsStatus(savingsAccounts, localeId);
		SessionUtils.setCollectionAttribute(GroupConstants.GROUPLOANACCOUNTSINUSE,
				loanAccounts, request);
		SessionUtils.setCollectionAttribute(GroupConstants.GROUPSAVINGSACCOUNTSINUSE,
				savingsAccounts, request);
		List<CustomerBO> allChildNodes = groupBO.getChildren(
				CustomerLevel.CLIENT,
				ChildrenStateType.OTHER_THAN_CANCELLED_AND_CLOSED);

		// bug #1417 - wrong client sort order. Client sort order on bulk
		// entry screens should match ordering on group details page.
		Collections.sort(allChildNodes, CustomerBO.searchIdComparator());

		SessionUtils.setCollectionAttribute(GroupConstants.CLIENT_LIST,
				allChildNodes, request);
		loadCustomFieldDefinitions(EntityType.GROUP, request);
	}

	private void loadCreateMasterData(GroupCustActionForm actionForm,
			HttpServletRequest request, boolean isCenterHierarchyExists) throws Exception {
		loadCreateCustomFields(actionForm, EntityType.GROUP, request);
		
		if(!isCenterHierarchyExists){
			loadLoanOfficers(actionForm.getOfficeIdValue(), request);
			loadFees(actionForm, request, FeeCategory.GROUP, null);
		}else
			loadFees(actionForm, request, FeeCategory.GROUP, actionForm.getParentCustomer().getCustomerMeeting().getMeeting());
		loadFormedByPersonnel(actionForm.getOfficeIdValue(), request);		
	}
	
	private void setLocaleIdToLoanStatus(List<LoanBO> accountList,
			Short localeId) {
		for (LoanBO accountBO : accountList)
			setLocaleForAccount(accountBO, localeId);
	}

	private void setLocaleIdToSavingsStatus(List<SavingsBO> accountList,
			Short localeId) {
		for (SavingsBO accountBO : accountList)
			setLocaleForAccount(accountBO, localeId);
	}

	private void setLocaleForAccount(AccountBO account, Short localeId) {
		account.getAccountState().setLocaleId(localeId);
	}

	private void setLocaleForMasterEntities(GroupBO groupBO, Short localeId) {
		for (CustomerPositionEntity customerPositionEntity : groupBO
				.getCustomerPositions())
			customerPositionEntity.getPosition().setLocaleId(localeId);
		for(CustomerFlagDetailEntity customerFlag : groupBO.getCustomerFlags())
			customerFlag.getStatusFlag().setLocaleId(localeId);
	}

	private GroupBusinessService getGroupBusinessService(){
		return (GroupBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Group);
	}

	private void loadUpdateMasterData(HttpServletRequest request, GroupBO group)
			throws ApplicationException, SystemException {
		boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists();
		if (!ClientRules.getCenterHierarchyExists()) {
			loadLoanOfficers(group.getOffice().getOfficeId(), request);
			SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST,
					isCenterHierarchyExists, request);	
		}
		loadCustomFieldDefinitions(EntityType.GROUP, request);
		loadPositions(request);
		loadClients(request,group);
	}

	private void setValuesInActionForm(GroupCustActionForm actionForm,
			HttpServletRequest request) throws Exception {
		GroupBO group = (GroupBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
		if (group.getPersonnel() != null) {
			actionForm.setLoanOfficerId(group.getPersonnel().getPersonnelId()
					.toString());
		}
		actionForm.setDisplayName(group.getDisplayName());
		actionForm.setCustomerId(group.getCustomerId().toString());
		actionForm.setGlobalCustNum(group.getGlobalCustNum());
		actionForm.setExternalId(group.getExternalId());
		actionForm.setAddress(group.getAddress());
		actionForm.setCustomerPositions(createCustomerPositionViews(group
				.getCustomerPositions(), request));
		actionForm.setCustomFields(createCustomFieldViews(group
				.getCustomFields(), request));
		if (group.isTrained()) 
			  actionForm.setTrained(GroupConstants.TRAINED);
		else 
			 actionForm.setTrained(GroupConstants.NOT_TRAINED); 
		if(group.getTrainedDate() != null){
			  actionForm.setTrainedDate(DateUtils.getUserLocaleDate(getUserContext(request).getPreferredLocale(), group.getTrainedDate().toString()));
		}
		 
	}

	private void doCleanUp(GroupCustActionForm actionForm,
			HttpServletRequest request) {
		clearActionForm(actionForm);
		SessionUtils.removeAttribute(CustomerConstants.CUSTOMER_MEETING, request.getSession());
	}
	//TODO should be moved to action form itself 
	private void clearActionForm(GroupCustActionForm actionForm) {
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
		actionForm.setTrained(null);
		actionForm.setTrainedDate(null);
		actionForm.setFormedByPersonnel(null);
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
	
		return mapping.findForward(method + "_failure");
		
	}
	
	private GroupBO createGroupWithoutCenter(GroupCustActionForm actionForm, HttpServletRequest request) throws Exception{
		UserContext userContext = getUserContext(request);
		Short personnelId = actionForm.getLoanOfficerIdValue()!=null ?actionForm.getLoanOfficerIdValue():userContext.getId();
		checkPermissionForCreate(actionForm.getStatusValue().getValue(),
				userContext, null, actionForm.getOfficeIdValue(), personnelId);
		List<CustomFieldView> customFields = actionForm.getCustomFields();
		convertCustomFieldDateToUniformPattern(customFields, userContext.getPreferredLocale());
		MeetingBO meeting = (MeetingBO) SessionUtils.getAttribute(
				CustomerConstants.CUSTOMER_MEETING, request);
		GroupBO group = new GroupBO(userContext, actionForm.getDisplayName(), actionForm.getStatusValue(),
				actionForm.getExternalId(), actionForm.isCustomerTrained(), actionForm.getTrainedDateValue(userContext.getPreferredLocale()),
				actionForm.getAddress(), customFields, actionForm.getFeesToApply(), 
				new PersonnelPersistence().getPersonnel(actionForm.getFormedByPersonnelValue()), 
				new OfficePersistence().getOffice(actionForm.getOfficeIdValue()), meeting, 
				new PersonnelPersistence().getPersonnel(actionForm.getLoanOfficerIdValue()));
		return group;
	}

	private GroupBO createGroupWithCenter(GroupCustActionForm actionForm, HttpServletRequest request) throws Exception{
		UserContext userContext = getUserContext(request);
		checkPermissionForCreate(actionForm.getStatusValue().getValue(),
				userContext, null, actionForm.getParentCustomer().getOffice().getOfficeId(), 
				actionForm.getParentCustomer().getPersonnel().getPersonnelId());
		
		List<CustomFieldView> customFields = actionForm.getCustomFields();
		convertCustomFieldDateToUniformPattern(customFields, userContext.getPreferredLocale());
		
		GroupBO group = new GroupBO(userContext, actionForm.getDisplayName(), actionForm.getStatusValue(),
				actionForm.getExternalId(), actionForm.isCustomerTrained(), 
				actionForm.getTrainedDateValue(userContext.getPreferredLocale()),
				actionForm.getAddress(), customFields, actionForm.getFeesToApply(), 
				new PersonnelPersistence().getPersonnel(actionForm.getFormedByPersonnelValue()), 
				actionForm.getParentCustomer());
		return group;
	}
}
