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
 
package org.mifos.application.accounts.business.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateMachines;
import org.mifos.application.accounts.business.TransactionHistoryView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.ApplicableCharge;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.checklist.business.AccountCheckListBO;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeePaymentEntity;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;

public class AccountBusinessService implements BusinessService {

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public AccountBO findBySystemId(String accountGlobalNum)
			throws ServiceException {
		try {
			return new AccountPersistence().findBySystemId(accountGlobalNum);
		} catch (PersistenceException e) {
			throw new ServiceException(
					AccountExceptionConstants.FINDBYGLOBALACCNTEXCEPTION, e,
					new Object[] { accountGlobalNum });
		}
	}

	public List<TransactionHistoryView> getTrxnHistory(AccountBO accountBO,
			UserContext uc) {
		accountBO.setUserContext(uc);
		return accountBO.getTransactionHistoryView();
	}

	public AccountBO getAccount(Integer accountId) throws ServiceException {
		try {
			return new AccountPersistence().getAccount(accountId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public AccountActionEntity getAccountAction(Short actionType, Short localeId)
			throws ServiceException {
		AccountActionEntity accountAction = null;
		try {
			accountAction = (AccountActionEntity) new MasterPersistence()
					.getPersistentObject(AccountActionEntity.class, actionType);
			accountAction.setLocaleId(localeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
		return accountAction;
	}

	public QueryResult getAllAccountNotes(Integer accountId)
			throws ServiceException {
		try {
			return new AccountPersistence().getAllAccountNotes(accountId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<AccountStateEntity> retrieveAllAccountStateList(
			AccountTypes accountTypes) throws ServiceException {
		try {
			return new AccountPersistence()
					.retrieveAllAccountStateList(accountTypes.getValue());
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public List<AccountStateEntity> retrieveAllActiveAccountStateList(
			AccountTypes accountTypes) throws ServiceException {
		try {
			return new AccountPersistence()
					.retrieveAllActiveAccountStateList(accountTypes.getValue());
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<AccountCheckListBO> getStatusChecklist(Short accountStatusId,
			Short accountTypeId) throws ServiceException {
		try {
			return new AccountPersistence().getStatusChecklist(accountStatusId,
					accountTypeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<ApplicableCharge> getAppllicableFees(Integer accountId,
			UserContext userContext) throws ServiceException {
		List<ApplicableCharge> applicableChargeList = null;
		try {
			AccountBO account = new AccountPersistence().getAccount(accountId);
			FeeCategory categoryType = getCategoryType(account.getCustomer());

			if (account.getType() == AccountTypes.LOAN_ACCOUNT) {
				applicableChargeList = getLoanApplicableCharges(
						new AccountPersistence().getAllApplicableFees(
								accountId, FeeCategory.LOAN),
						userContext, (LoanBO) account);
			} else if (account.getType() == AccountTypes.CUSTOMER_ACCOUNT) {
				if (account.getCustomer().getCustomerMeeting() == null)
					throw new ServiceException(
							AccountExceptionConstants.APPLY_CAHRGE_NO_CUSTOMER_MEETING_EXCEPTION);
				applicableChargeList = getCustomerApplicableCharges(
						new AccountPersistence().getAllApplicableFees(
								accountId, categoryType), 
								userContext,
						((CustomerAccountBO) account).getCustomer()
								.getCustomerMeeting().getMeeting()
								.getMeetingDetails().getRecurrenceType()
								.getRecurrenceId());
			}
			addMiscFeeAndPenalty(applicableChargeList);
		} catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
		return applicableChargeList;
	}

	private FeeCategory getCategoryType(CustomerBO customer) {
		if (customer.getCustomerLevel().getId().equals(
				CustomerLevel.CLIENT.getValue()))
			return FeeCategory.CLIENT;
		else if (customer.getCustomerLevel().getId().equals(
				CustomerLevel.GROUP.getValue()))
			return FeeCategory.GROUP;
		else if (customer.getCustomerLevel().getId().equals(
				CustomerLevel.CENTER.getValue()))
			return FeeCategory.CENTER;
		return null;
	}

	private List<ApplicableCharge> getCustomerApplicableCharges(
			List<FeeBO> feeList, UserContext userContext, Short accountMeetingRecurrance) {
		List<ApplicableCharge> applicableChargeList = new ArrayList<ApplicableCharge>();
		if (feeList != null && !feeList.isEmpty()) {
			filterBasedOnRecurranceType(feeList, accountMeetingRecurrance);
			populaleApplicableCharge(applicableChargeList, feeList, userContext);
		}
		return applicableChargeList;
	}

	private List<ApplicableCharge> getLoanApplicableCharges(
			List<FeeBO> feeList, UserContext userContext, LoanBO loanBO) {
		List<ApplicableCharge> applicableChargeList = new ArrayList<ApplicableCharge>();
		if (feeList != null && !feeList.isEmpty()) {
			Short accountMeetingRecurrance = loanBO.getCustomer()
					.getCustomerMeeting().getMeeting().getMeetingDetails()
					.getRecurrenceType().getRecurrenceId();
			filterBasedOnRecurranceType(feeList, accountMeetingRecurrance);
			filterDisbursmentFee(feeList, loanBO);
			filterTimeOfFirstRepaymentFee(feeList, loanBO);
			populaleApplicableCharge(applicableChargeList, feeList, userContext);
		}
		return applicableChargeList;
	}

	private void populaleApplicableCharge(
			List<ApplicableCharge> applicableChargeList, List<FeeBO> feeList,
			UserContext userContext) {
		for (FeeBO fee : feeList) {
			ApplicableCharge applicableCharge = new ApplicableCharge();
			applicableCharge.setFeeId(fee.getFeeId().toString());
			applicableCharge.setFeeName(fee.getFeeName());
			if (fee.getFeeType().getValue().equals(
					RateAmountFlag.RATE.getValue())) {
				applicableCharge.setAmountOrRate(((RateFeeBO) fee).getRate()
						.toString());
				applicableCharge.setFormula(((RateFeeBO) fee).getFeeFormula()
						.getFormulaString(userContext.getLocaleId()));
			} else {
				applicableCharge.setAmountOrRate(((AmountFeeBO) fee)
						.getFeeAmount().getAmount().toString());
			}
			MeetingBO meeting = fee.getFeeFrequency().getFeeMeetingFrequency();
			if (meeting != null) {
				applicableCharge.setPeriodicity(new MeetingHelper().getDetailMessageWithFrequency(meeting, userContext));
			} else {
				applicableCharge.setPaymentType(fee.getFeeFrequency()
						.getFeePayment().getName());
			}
			applicableChargeList.add(applicableCharge);
		}

	}

	private void filterBasedOnRecurranceType(List<FeeBO> feeList,
			Short accountMeetingRecurrance) {
		for (Iterator<FeeBO> iter = feeList.iterator(); iter.hasNext();) {
			FeeBO fee = iter.next();
			if (fee.getFeeFrequency().getFeeFrequencyType().getId().equals(
					FeeFrequencyType.PERIODIC.getValue())) {
				Short feeRecurrance = fee.getFeeFrequency()
						.getFeeMeetingFrequency().getMeetingDetails()
						.getRecurrenceTypeEnum().getValue();
				if (!feeRecurrance.equals(accountMeetingRecurrance))
					iter.remove();
			}
		}
	}

	private void filterDisbursmentFee(List<FeeBO> feeList, AccountBO account) {
		for (Iterator<FeeBO> iter = feeList.iterator(); iter.hasNext();) {
			FeeBO fee = iter.next();
			FeePaymentEntity feePaymentEntity = fee.getFeeFrequency()
					.getFeePayment();
			if (feePaymentEntity != null) {
				Short paymentType = feePaymentEntity.getId();
				if (paymentType.equals(FeePayment.TIME_OF_DISBURSMENT
						.getValue())) {
					AccountState accountState = account.getState();
					if (accountState == AccountState.LOAN_PARTIAL_APPLICATION
						|| accountState == AccountState.LOAN_PENDING_APPROVAL
						|| accountState == AccountState.LOAN_APPROVED 
						|| accountState == AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER
						) {
						continue;
					} else {
						iter.remove();
					}
				}
			}
		}
	}

	private void filterTimeOfFirstRepaymentFee(List<FeeBO> feeList,
			LoanBO loanBO) {
		for (Iterator<FeeBO> iter = feeList.iterator(); iter.hasNext();) {
			FeeBO fee = iter.next();
			FeePaymentEntity feePaymentEntity = fee.getFeeFrequency()
					.getFeePayment();
			if (feePaymentEntity != null) {
				Short paymentType = feePaymentEntity.getId();
				if (paymentType.equals(FeePayment.TIME_OF_FIRSTLOANREPAYMENT
						.getValue())
						&& loanBO.isCurrentDateGreaterThanFirstInstallment())
					iter.remove();
			}

		}
	}

	private void addMiscFeeAndPenalty(
			List<ApplicableCharge> applicableChargeList) {
		ApplicableCharge applicableCharge = new ApplicableCharge();
		applicableCharge.setFeeId(AccountConstants.MISC_FEES);
		applicableCharge.setFeeName("Misc Fees");
		applicableChargeList.add(applicableCharge);
		applicableCharge = new ApplicableCharge();
		applicableCharge.setFeeId(AccountConstants.MISC_PENALTY);
		applicableCharge.setFeeName("Misc Penalty");
		applicableChargeList.add(applicableCharge);
	}

	public void initializeStateMachine(Short localeId, Short officeId,
			AccountTypes accountType, CustomerLevel customerLevel)
			throws ServiceException {
		try {
			AccountStateMachines.getInstance().initialize(localeId, officeId,
					accountType, customerLevel);
		} catch (StatesInitializationException e) {
			throw new ServiceException(e);
		}
	}

	public String getStatusName(Short localeId, AccountState accountState,
			AccountTypes accountType) {
		return AccountStateMachines.getInstance().getAccountStatusName(
				localeId, accountState, accountType);
	}

	public String getFlagName(Short localeId,
			AccountStateFlag accountStateFlag, AccountTypes accountType) {
		return AccountStateMachines.getInstance().getAccountFlagName(localeId,
				accountStateFlag, accountType);
	}

	public List<AccountStateEntity> getStatusList(
			AccountStateEntity accountStateEntity, AccountTypes accountType,
			Short localeId) {
		List<AccountStateEntity> statusList = AccountStateMachines
				.getInstance().getStatusList(accountStateEntity, accountType);
		if (null != statusList) {
			for (AccountStateEntity accountState : statusList) {
				accountState.setLocaleId(localeId);
			}
		}
		return statusList;
	}
	
	public void checkPermissionForStatusChange(Short newState,
			UserContext userContext, Short flagSelected, Short recordOfficeId,
			Short recordLoanOfficerId) throws ServiceException {
		if (!isPermissionAllowedForStatusChange(newState, userContext, flagSelected,
				recordOfficeId, recordLoanOfficerId))
			throw new ServiceException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	private boolean isPermissionAllowedForStatusChange(Short newState,
			UserContext userContext, Short flagSelected, Short recordOfficeId,
			Short recordLoanOfficerId) {
		return ActivityMapper.getInstance().isStateChangePermittedForAccount(
				newState.shortValue(),
				null != flagSelected ? flagSelected.shortValue() : 0,
				userContext, recordOfficeId, recordLoanOfficerId);
	}
	
	public void checkPermissionForAdjustment(AccountTypes accountTypes,
			CustomerLevel customerLevel, UserContext userContext,
			Short recordOfficeId, Short recordLoanOfficerId)
			throws ApplicationException {
		if (!isPermissionAllowedForAdjustment(accountTypes, customerLevel, userContext,
				recordOfficeId, recordLoanOfficerId))
			throw new ServiceException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	private boolean isPermissionAllowedForAdjustment(AccountTypes accountTypes,
			CustomerLevel customerLevel, UserContext userContext,
			Short recordOfficeId, Short recordLoanOfficerId) {
		return ActivityMapper.getInstance().isAdjustmentPermittedForAccounts(
				accountTypes, customerLevel, userContext, recordOfficeId,
				recordLoanOfficerId);
	}
	
	public void checkPermissionForWaiveDue(WaiveEnum waiveEnum, AccountTypes accountTypes, CustomerLevel customerLevel,
			UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) throws ApplicationException {
		if (!isPermissionAllowedForWaiveDue(waiveEnum, accountTypes,customerLevel, userContext,
				recordOfficeId, recordLoanOfficerId))
			throw new CustomerException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	private boolean isPermissionAllowedForWaiveDue(WaiveEnum waiveEnum, AccountTypes accountTypes, CustomerLevel customerLevel,
			UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) {
		return ActivityMapper.getInstance().isWaiveDuePermittedForCustomers(waiveEnum, accountTypes,
				customerLevel, userContext, recordOfficeId,
				recordLoanOfficerId);
	}
	
	public void checkPermissionForRemoveFees(AccountTypes accountTypes, CustomerLevel customerLevel,
			UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) throws ApplicationException {
		if (!isPermissionAllowedForRemoveFees(accountTypes, customerLevel, userContext,
				recordOfficeId, recordLoanOfficerId))
			throw new CustomerException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	private boolean isPermissionAllowedForRemoveFees(AccountTypes accountTypes, CustomerLevel customerLevel,
			UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) {
		return ActivityMapper.getInstance().isRemoveFeesPermittedForAccounts(accountTypes,
				customerLevel, userContext, recordOfficeId,
				recordLoanOfficerId);
	}

	public List<CustomFieldDefinitionEntity> retrieveCustomFieldsDefinition(EntityType entityType)
	throws ServiceException {

	try {
		List<CustomFieldDefinitionEntity> customFields = new AccountPersistence().retrieveCustomFieldsDefinition(entityType.getValue());
		new AccountPersistence().initialize(customFields);
		return customFields;
	} catch (PersistenceException e) {
		throw new ServiceException(e);
	}
	}

	public List<CustomerBO> getCoSigningClientsForGlim(Integer accountId)
			throws ServiceException {
		try {
			return new AccountPersistence()
					.getCoSigningClientsForGlim(accountId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	
}
