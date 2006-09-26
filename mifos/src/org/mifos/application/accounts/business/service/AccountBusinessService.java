package org.mifos.application.accounts.business.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateMachines;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.business.TransactionHistoryView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.ApplicableCharge;
import org.mifos.application.checklist.util.valueobjects.CheckListMaster;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeePaymentEntity;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;

public class AccountBusinessService extends BusinessService {

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

	public List<CheckListMaster> getStatusChecklist(Short accountStatusId,
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
			Short categoryType = getCategoryType(account.getCustomer());

			if (account.getAccountType().getAccountTypeId().equals(
					AccountTypes.LOANACCOUNT.getValue()))
				applicableChargeList = getLoanApplicableCharges(
						new AccountPersistence().getAllAppllicableFees(
								accountId, FeeCategory.LOAN.getValue()),
						userContext.getLocaleId(), (LoanBO) account);
			else if (account.getAccountType().getAccountTypeId().equals(
					AccountTypes.CUSTOMERACCOUNT.getValue()))
				applicableChargeList = getCustomerApplicableCharges(
						new AccountPersistence().getAllAppllicableFees(
								accountId, getCategoryType(account
										.getCustomer())), userContext
								.getLocaleId(), ((CustomerAccountBO) account)
								.getCustomer().getCustomerMeeting()
								.getMeeting().getMeetingDetails()
								.getRecurrenceType().getRecurrenceId());
			addMiscFeeAndPenalty(applicableChargeList);
		} catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
		return applicableChargeList;
	}

	private Short getCategoryType(CustomerBO customer) {
		if (customer.getCustomerLevel().getId().equals(
				CustomerLevel.CLIENT.getValue()))
			return FeeCategory.CLIENT.getValue();
		else if (customer.getCustomerLevel().getId().equals(
				CustomerLevel.GROUP.getValue()))
			return FeeCategory.GROUP.getValue();
		else if (customer.getCustomerLevel().getId().equals(
				CustomerLevel.CENTER.getValue()))
			return FeeCategory.CENTER.getValue();
		return null;
	}

	private List<ApplicableCharge> getCustomerApplicableCharges(
			List<FeeBO> feeList, Short locale, Short accountMeetingRecurrance) {
		List<ApplicableCharge> applicableChargeList = new ArrayList<ApplicableCharge>();
		if (feeList != null && !feeList.isEmpty()) {
			filterBasedOnRecurranceType(feeList, accountMeetingRecurrance);
			populaleApplicableCharge(applicableChargeList, feeList, locale);
		}
		return applicableChargeList;
	}

	private List<ApplicableCharge> getLoanApplicableCharges(
			List<FeeBO> feeList, Short locale, LoanBO loanBO) {
		List<ApplicableCharge> applicableChargeList = new ArrayList<ApplicableCharge>();
		if (feeList != null && !feeList.isEmpty()) {
			Short accountMeetingRecurrance = loanBO.getCustomer()
					.getCustomerMeeting().getMeeting().getMeetingDetails()
					.getRecurrenceType().getRecurrenceId();
			filterBasedOnRecurranceType(feeList, accountMeetingRecurrance);
			filterDisbursmentFee(feeList, loanBO);
			filterTimeOfFirstRepaymentFee(feeList, loanBO);
			populaleApplicableCharge(applicableChargeList, feeList, locale);
		}
		return applicableChargeList;
	}

	private void populaleApplicableCharge(
			List<ApplicableCharge> applicableChargeList, List<FeeBO> feeList,
			Short locale) {
		for (FeeBO fee : feeList) {
			ApplicableCharge applicableCharge = new ApplicableCharge();
			applicableCharge.setFeeId(fee.getFeeId().toString());
			applicableCharge.setFeeName(fee.getFeeName());
			if (fee.getFeeType().getValue().equals(
					RateAmountFlag.RATE.getValue())) {
				applicableCharge.setAmountOrRate(((RateFeeBO) fee).getRate()
						.toString());
				applicableCharge.setFormula(((RateFeeBO) fee).getFeeFormula()
						.getFormulaString(locale));
			} else {
				applicableCharge.setAmountOrRate(((AmountFeeBO) fee)
						.getFeeAmount().getAmount().toString());
			}
			MeetingBO meeting = fee.getFeeFrequency().getFeeMeetingFrequency();
			if (meeting != null) {
				applicableCharge.setPeriodicity(meeting
						.getSimpleMeetingSchedule());
			} else {
				applicableCharge.setPaymentType(fee.getFeeFrequency()
						.getFeePayment().getName(locale));
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
						.getRecurrenceType().getRecurrenceId();
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
					Short accountState = account.getAccountState().getId();
					if ((accountState
							.equals(AccountState.LOANACC_PARTIALAPPLICATION
									.getValue())
							|| accountState
									.equals(AccountState.LOANACC_PENDINGAPPROVAL
											.getValue())
							|| accountState
									.equals(AccountState.LOANACC_APPROVED
											.getValue()) || accountState
							.equals(AccountState.LOANACC_DBTOLOANOFFICER
									.getValue()))) {
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
}
