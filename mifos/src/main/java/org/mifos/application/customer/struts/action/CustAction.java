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
 
package org.mifos.application.customer.struts.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFlagMapping;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerCustomFieldEntity;
import org.mifos.application.customer.business.CustomerPositionEntity;
import org.mifos.application.customer.business.CustomerPositionView;
import org.mifos.application.customer.business.PositionEntity;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.struts.actionforms.CustActionForm;
import org.mifos.application.customer.struts.actionforms.CustomerActionForm;
import org.mifos.application.customer.util.helpers.ChildrenStateType;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.service.FeeBusinessService;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class CustAction extends SearchAction {
	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.CUSTOMERLOGGER);

	@Override
	protected BusinessService getService() {
		return getCustomerBusinessService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("custAction");
		security.allow("getClosedAccounts", SecurityConstants.VIEW);
		security.allow("getBackToDetailsPage", SecurityConstants.VIEW);
		return security;
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward getClosedAccounts(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In CustAction::getClosedAccounts()");
		Integer customerId = getIntegerValue(((CustActionForm) form)
				.getCustomerId());
		CustomerBusinessService customerService = getCustomerBusinessService();
		List<AccountBO> loanAccountsList = customerService.getAllClosedAccount(
				customerId, AccountTypes.LOAN_ACCOUNT.getValue());
		List<AccountBO> savingsAccountList = customerService
				.getAllClosedAccount(customerId, AccountTypes.SAVINGS_ACCOUNT
						.getValue());
		for (AccountBO savingsBO : savingsAccountList) {
			setLocaleIdForToRetrieveMasterDataName(savingsBO, request);
		}
		for (AccountBO loanBO : loanAccountsList) {
			setLocaleIdForToRetrieveMasterDataName(loanBO, request);
		}
		SessionUtils.setCollectionAttribute(
				AccountConstants.CLOSEDLOANACCOUNTSLIST,
				loanAccountsList, request);
		SessionUtils.setCollectionAttribute(
				AccountConstants.CLOSEDSAVINGSACCOUNTSLIST,
				savingsAccountList, request);
		return mapping.findForward(ActionForwards.getAllClosedAccounts
				.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward getBackToDetailsPage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping
				.findForward(getCustomerDetailPage(((CustActionForm) form)
						.getInput()));
	}

	protected void loadCreateCustomFields(CustomerActionForm actionForm,
			EntityType entityType, HttpServletRequest request)
			throws ApplicationException {
		loadCustomFieldDefinitions(entityType, request);
		// Set Default values for custom fields
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

		for (CustomFieldDefinitionEntity fieldDef : customFieldDefs) {
			if (StringUtils.isNullAndEmptySafe(fieldDef.getDefaultValue())
					&& fieldDef.getFieldType().equals(
							CustomFieldType.DATE.getValue())) {
				customFields.add(new CustomFieldView(fieldDef.getFieldId(),
						DateUtils.getUserLocaleDate(getUserContext(request)
						.getPreferredLocale(), fieldDef
						.getDefaultValue()), fieldDef.getFieldType()));
			} else {
				customFields.add(new CustomFieldView(fieldDef.getFieldId(),
						fieldDef.getDefaultValue(), fieldDef.getFieldType()));
			}
		}
		actionForm.setCustomFields(customFields);
	}

	protected List<CustomFieldView> createCustomFieldViews(
			Set<CustomerCustomFieldEntity> customFieldEntities,
			HttpServletRequest request) throws ApplicationException {
		List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		Locale locale = getUserContext(request).getPreferredLocale();
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			for (CustomerCustomFieldEntity customFieldEntity : customFieldEntities) {
				if (customFieldDef.getFieldId().equals(
						customFieldEntity.getFieldId())) {
					if (customFieldDef.getFieldType().equals(
							CustomFieldType.DATE.getValue())) {
						customFields.add(new CustomFieldView(customFieldEntity
								.getFieldId(), DateUtils.getUserLocaleDate(locale, customFieldEntity.getFieldValue()),
								customFieldDef.getFieldType()));
					} else {
						customFields
								.add(new CustomFieldView(customFieldEntity
										.getFieldId(), customFieldEntity
										.getFieldValue(), customFieldDef
										.getFieldType()));
					}
				}
			}
		}
		return customFields;
	}

	protected void loadCustomFieldDefinitions(EntityType entityType,
			HttpServletRequest request) throws ApplicationException {
		MasterDataService masterDataService = (MasterDataService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.MasterDataService);
		List<CustomFieldDefinitionEntity> customFieldDefs = masterDataService
				.retrieveCustomFieldsDefinition(entityType);
		SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,
				customFieldDefs, request);
	}

	protected void loadFees(CustomerActionForm actionForm, HttpServletRequest request, FeeCategory feeCategory, MeetingBO meeting) throws ApplicationException{
		FeeBusinessService feeService = (FeeBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.FeesService);
		List<FeeBO> fees = feeService
				.retrieveCustomerFeesByCategaroyType(feeCategory);
		if(meeting!=null)
			fees = removeMismatchPeriodicFee(fees, meeting);
		List<FeeView> additionalFees = new ArrayList<FeeView>();
		List<FeeView> defaultFees = new ArrayList<FeeView>();
		for (FeeBO fee : fees) {
			if (fee.isCustomerDefaultFee())
				defaultFees.add(new FeeView(getUserContext(request),fee));
			else
				additionalFees.add(new FeeView(getUserContext(request),fee));
		}
		actionForm.setDefaultFees(defaultFees);
		SessionUtils.setCollectionAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,
				additionalFees, request);
	}

	private List<FeeBO> removeMismatchPeriodicFee(List<FeeBO> feeList, MeetingBO meeting) {
		List<FeeBO> fees = new ArrayList<FeeBO>();
		for (FeeBO fee : feeList) {
			if(fee.isOneTime() || (fee.isPeriodic() && isFrequencyMatches(fee, meeting)))
				fees.add(fee);
		}
		return fees;
	}
	
	private boolean isFrequencyMatches(FeeBO fee, MeetingBO meeting){
		return (fee.getFeeFrequency().getFeeMeetingFrequency().isMonthly() && meeting.isMonthly()) ||
					(fee.getFeeFrequency().getFeeMeetingFrequency().isWeekly() && meeting.isWeekly());
	}

	protected void loadFormedByPersonnel(Short officeId,
			HttpServletRequest request) throws ApplicationException {
		CustomerBusinessService customerService = (CustomerBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Customer);
		List<PersonnelView> formedByPersonnel;
		formedByPersonnel = customerService.getFormedByPersonnel(
				ClientConstants.LOAN_OFFICER_LEVEL, officeId);
		SessionUtils.setCollectionAttribute(CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST,
				formedByPersonnel, request);

	}

	protected void loadLoanOfficers(Short officeId, HttpServletRequest request)
			throws ApplicationException {
		PersonnelBusinessService personnelService = (PersonnelBusinessService) ServiceFactory
				.getInstance()
				.getBusinessService(BusinessServiceName.Personnel);
		UserContext userContext = getUserContext(request);
		List<PersonnelView> personnelList = personnelService
				.getActiveLoanOfficersInBranch(officeId, userContext.getId(),
						userContext.getLevelId());
		SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST,
				personnelList, request);
	}

	protected void convertCustomFieldDateToUniformPattern(
			List<CustomFieldView> customFields, Locale locale) {
		for (CustomFieldView customField : customFields) {
			if (customField.getFieldType().equals(
					CustomFieldType.DATE.getValue())
					&& StringUtils.isNullAndEmptySafe(customField
							.getFieldValue()))
				customField.convertDateToUniformPattern(locale);
		}
	}

	protected void checkPermissionForCreate(Short newState,
			UserContext userContext, Short flagSelected, Short recordOfficeId,
			Short recordLoanOfficerId) throws ApplicationException {
		if (!isPermissionAllowed(newState, userContext, flagSelected,
				recordOfficeId, recordLoanOfficerId))
			throw new CustomerException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	protected boolean isPermissionAllowed(Short newState,
			UserContext userContext, Short flagSelected, Short recordOfficeId,
			Short recordLoanOfficerId) {
		return ActivityMapper.getInstance().isSavePermittedForCustomer(
				newState.shortValue(), userContext, recordOfficeId,
				recordLoanOfficerId);
	}

	protected List<CustomerPositionView> createCustomerPositionViews(
			Set<CustomerPositionEntity> custPosEntities,
			HttpServletRequest request) throws ApplicationException {
		List<PositionEntity> positions = (List<PositionEntity>) SessionUtils
				.getAttribute(CustomerConstants.POSITIONS, request);
		List<CustomerPositionView> customerPositions = new ArrayList<CustomerPositionView>();
		for (PositionEntity position : positions)
			for (CustomerPositionEntity entity : custPosEntities) {
				if (position.getId().equals(entity.getPosition().getId())) {
					if (entity.getCustomer() != null)
						customerPositions.add(new CustomerPositionView(entity
								.getCustomer().getCustomerId(), entity
								.getPosition().getId()));
					else
						customerPositions.add(new CustomerPositionView(null,
								entity.getPosition().getId()));
				}
			}
		return customerPositions;
	}

	protected void loadPositions(HttpServletRequest request)
			throws ApplicationException {
		SessionUtils.setCollectionAttribute(CustomerConstants.POSITIONS,
				getMasterEntities(PositionEntity.class, getUserContext(request)
						.getLocaleId()), request);
	}

	protected void loadClients(HttpServletRequest request, CustomerBO customerBO)
			throws ApplicationException {
		List<CustomerBO> customerList;
		customerList = customerBO
				.getChildren(CustomerLevel.CLIENT, ChildrenStateType.OTHER_THAN_CANCELLED_AND_CLOSED);
		SessionUtils.setCollectionAttribute(CustomerConstants.CLIENT_LIST, customerList,
				request);
	}

	protected CustomerBusinessService getCustomerBusinessService() {
		return (CustomerBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Customer);
	}

	private String getCustomerDetailPage(String input) {
		String forward = null;
		if (input.equals("center"))
			forward = ActionForwards.center_detail_page.toString();
		else if (input.equals("group"))
			forward = ActionForwards.group_detail_page.toString();
		else if (input.equals("client"))
			forward = ActionForwards.client_detail_page.toString();
		return forward;
	}

	private void setLocaleIdForToRetrieveMasterDataName(AccountBO accountBO,
			HttpServletRequest request) {
		accountBO.getAccountState().setLocaleId(
				getUserContext(request).getLocaleId());
		for (AccountFlagMapping accountFlagMapping : accountBO
				.getAccountFlags()) {
			accountFlagMapping.getFlag().setLocaleId(
					getUserContext(request).getLocaleId());
		}
	}
}
