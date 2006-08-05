/**

 * AccountsApplyChargesBusinessProcessor.java    version: 1.0



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
package org.mifos.application.accounts.business.handler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.business.CustomerActivityEntity;
import org.mifos.application.accounts.dao.AccountsApplyChargesDAO;
import org.mifos.application.accounts.exceptions.AccountsApplyChargesException;
import org.mifos.application.accounts.loan.business.util.helpers.LoanHeaderObject;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.helpers.LoanHelpers;
import org.mifos.application.accounts.loan.util.valueobjects.Loan;
import org.mifos.application.accounts.loan.util.valueobjects.LoanActivity;
import org.mifos.application.accounts.loan.util.valueobjects.LoanSummary;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.valueobjects.Account;
import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.accounts.util.valueobjects.AccountFeesActionDetail;
import org.mifos.application.accounts.util.valueobjects.AccountsApplyCharges;
import org.mifos.application.accounts.util.valueobjects.CustomerAccount;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerMaster;
import org.mifos.application.customer.util.valueobjects.CustomerMeeting;
import org.mifos.application.fees.dao.FeesDAO;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.business.util.helpers.HeaderObject;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.repaymentschedule.FeeInstallment;
import org.mifos.framework.components.repaymentschedule.RepaymentSchedule;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleConstansts;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleFactory;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleHelper;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleIfc;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleInputsIfc;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.dao.helpers.DataTypeConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This is business processor for the apply charges in account
 * 
 * @author rajenders
 * 
 */
public class AccountsApplyChargesBusinessProcessor extends
		MifosBusinessProcessor {

	/**
	 * This methos would load the master data for the apply charges page
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void loadInitial(Context context) throws SystemException,
			ApplicationException {

		// get dao

		try {

			AccountsApplyChargesDAO dao = (AccountsApplyChargesDAO) getDAO(context
					.getPath());
			dao.loadFeeMasterData(context);
			dao.loadFormulaMaster(context);

		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {

			throw new AccountsApplyChargesException(
					AccountConstants.UNEXPECTEDERROR);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#create(org.mifos.framework.util.valueobjects.Context)
	 */
	@Override
	// TODO needs refactored to segrate loan and customer fee handling.
	public void create(Context context) throws SystemException,
			ApplicationException {
		try {
			AccountsApplyCharges aac = (AccountsApplyCharges) context
					.getValueObject();
			AccountsApplyChargesDAO aacdao = (AccountsApplyChargesDAO) getDAO(context
					.getPath());
			FeesDAO feedao = new FeesDAO();
			Short feeId = aac.getChargeType();

			Integer accountId = aac.getAccountId();
			
			if (null == feeId || accountId==null) {
				throw new AccountsApplyChargesException(
						AccountConstants.UNEXPECTEDERROR);
			}
			
			Account account = aacdao.getAccount(accountId);

			// Checking if user has selected misc fee or penalty.
			if (feeId.equals(AccountConstants.MISC_FEES)
					|| feeId.equals(AccountConstants.MISC_PENALTY)) {


				// Getting the accountActionDate object for next installment
				AccountActionDate accountActionDate = aacdao
						.getNextInstallmentDetail(accountId);
				if (accountActionDate == null) {
					throw new AccountsApplyChargesException(
							AccountConstants.NOMOREINSTALLMENTS);
				}


				if (accountActionDate != null) {
					if (feeId.equals(AccountConstants.MISC_PENALTY)) {
						accountActionDate.setMiscPenalty(accountActionDate
								.getMiscPenalty().add(
										new Money(Configuration.getInstance()
												.getSystemConfig()
												.getCurrency(), aac
												.getChargeAmount())));
					} else {
						accountActionDate.setMiscFee(accountActionDate
								.getMiscFee().add(
										new Money(Configuration.getInstance()
												.getSystemConfig()
												.getCurrency(), aac
												.getChargeAmount())));
					}

					accountActionDate.setAccountFeesActionDetail(null);
					List<AccountActionDate> accountActionDateList = new ArrayList<AccountActionDate>();
					accountActionDateList.add(accountActionDate);

					LoanSummary loanSummary = null;
					LoanActivity loanActivity = null;
					if (account instanceof Loan) {
						Loan loan = (Loan) account;
						loanSummary = loan.getLoanSummary();
						loanActivity = new LoanActivity();

						if (feeId.equals(AccountConstants.MISC_PENALTY)) {
							loanSummary.setOriginalPenalty(loanSummary
									.getOriginalPenalty().add(
											new Money(Configuration
													.getInstance()
													.getSystemConfig()
													.getCurrency(), aac
													.getChargeAmount())));
							loanActivity
									.setComments(AccountConstants.MISC_PENALTY_APPLIED);
							loanActivity.setPenalty(new Money(Configuration
									.getInstance().getSystemConfig()
									.getCurrency(), aac.getChargeAmount()));
						} else {
							loanSummary.setOriginalFees(loanSummary
									.getOriginalFees().add(
											new Money(Configuration
													.getInstance()
													.getSystemConfig()
													.getCurrency(), aac
													.getChargeAmount())));
							loanActivity
									.setComments(AccountConstants.MISC_FEES_APPLIED);
							loanActivity.setFee(new Money(Configuration
									.getInstance().getSystemConfig()
									.getCurrency(), aac.getChargeAmount()));
						}
						loanActivity.setAccount(account);
						loanActivity.setPersonnelId(context.getUserContext()
								.getId());
						loanActivity.setPrincipal(new Money());
						loanActivity.setInterest(new Money());
						loanActivity.setFeeOutstanding(loanSummary
								.getOriginalFees().subtract(
										loanSummary.getFeesPaid()));
						loanActivity.setInterestOutstanding(loanSummary
								.getOriginalInterest().subtract(
										loanSummary.getInterestPaid()));
						loanActivity.setPenaltyOutstanding(loanSummary
								.getOriginalPenalty().subtract(
										loanSummary.getPenaltyPaid()));
						loanActivity.setPrincipalOutstanding(loanSummary
								.getOriginalPrincipal().subtract(
										loanSummary.getPrincipalPaid()));
						handleRoundingOfInstallments(account,
								accountActionDateList);
					}

					CustomerActivityEntity customerActivityEntity = null;
					if (account instanceof CustomerAccount) {
						if (feeId.equals(AccountConstants.MISC_PENALTY)) {
							customerActivityEntity = new CustomerActivityEntity(
									new PersonnelPersistenceService()
											.getPersonnel(context
													.getUserContext().getId()),
									AccountConstants.MISC_PENALTY_APPLIED,
									new Money(String.valueOf(aac
											.getChargeAmount())));
							customerActivityEntity
									.setCustomerAccount((CustomerAccountBO) new AccountPersistanceService()
											.getAccount(accountId));
						} else {
							customerActivityEntity = new CustomerActivityEntity(
									new PersonnelPersistenceService()
											.getPersonnel(context
													.getUserContext().getId()),
									AccountConstants.MISC_FEES_APPLIED,
									new Money(String.valueOf(aac
											.getChargeAmount())));
							customerActivityEntity
									.setCustomerAccount((CustomerAccountBO) new AccountPersistanceService()
											.getAccount(accountId));
						}
					}

					aacdao.saveAccountActionDateList(accountActionDateList,
							null, loanSummary, loanActivity,
							customerActivityEntity);
				}

			} else {
				Fees fee = null;
				if (null != feeId) {
					fee = feedao.getFees(feeId);
				}
				if (null == fee) {
					throw new AccountsApplyChargesException(
							AccountConstants.UNEXPECTEDERROR);
				}

				List<AccountActionDate> accountActionDateList = null;
				if (fee.getFeeFrequency().getFeeFrequencyTypeId().equals(
						FeeFrequencyType.ONETIME.getValue())
						&& (fee.getFeeFrequency().getFeePaymentId().equals(
								FeePayment.TIME_OF_DISBURSMENT.getValue()) || fee
								.getFeeFrequency().getFeePaymentId().equals(
										FeePayment.TIME_OF_FIRSTLOANREPAYMENT
												.getValue()))) {
					accountActionDateList = new ArrayList<AccountActionDate>();
					for (AccountActionDate accountActionDate : account
							.getAccountActionDateSet()) {
						if (accountActionDate.getInstallmentId().equals(
								Short.valueOf("1"))) {
							accountActionDateList.add(accountActionDate);
							break;
						}
					}
				} else {
					accountActionDateList = aacdao
							.getAccountActionDate(accountId);
				}
				if (null == accountActionDateList || accountActionDateList.isEmpty())
					throw new AccountsApplyChargesException(
							AccountConstants.NOMOREINSTALLMENTS);

				AccountFees accountFee = null;
				Set<AccountFees> accountFeeSet = null;
				if (!checkForFeesInAccountFeesForGivenAccount(account, feeId)) {
					accountFee = new AccountFees();
					accountFee.setFees(fee);
					accountFee.setAccountFeeAmount(new Money(Configuration
								.getInstance().getSystemConfig().getCurrency(),
								aac.getChargeAmount()));
					accountFee.setFeeAmount(aac.getChargeAmount());
					accountFee.setFeeStatus(FeeStatus.ACTIVE.getValue());
					accountFee.setStatusChangeDate(new Date(System
								.currentTimeMillis()));
					accountFee.setLastAppliedDate(accountActionDateList.get(0)
							.getActionDate());
					accountFee.setAccount(account);
					accountFeeSet = new HashSet<AccountFees>();
					accountFeeSet.add(accountFee);
				} else {
					Set<AccountFees> accountFeeSetFromAccount = account
							.getAccountFeesSet();
					for (AccountFees accountFees : accountFeeSetFromAccount) {
						if (accountFees.getFees().getFeeId().equals(feeId)) {
							accountFees.setFeeStatus(FeeStatus.ACTIVE
									.getValue());
							accountFees.setStatusChangeDate(new Date(System
									.currentTimeMillis()));
							accountFee = accountFees;
							accountFee.setFeeAmount(aac.getChargeAmount());
							accountFee.setLastAppliedDate(accountActionDateList
									.get(0).getActionDate());
							accountFeeSet = new HashSet<AccountFees>();
							accountFeeSet.add(accountFees);
						}
					}
				}
				Date feeStartDate = new Date(System.currentTimeMillis());
				if (fee.getFeeFrequency().getFeeFrequencyTypeId().equals(
						FeeFrequencyType.ONETIME.getValue())
						&& (fee.getFeeFrequency().getFeePaymentId().equals(
								FeePayment.TIME_OF_DISBURSMENT.getValue()) || fee
								.getFeeFrequency().getFeePaymentId().equals(
										FeePayment.TIME_OF_FIRSTLOANREPAYMENT
												.getValue()))) {
					feeStartDate = ((Loan) account).getDisbursementDate();
				} else {
					if (doesFeeInstallmentStartDateNeedToBeChanged(
							accountActionDateList, fee)
							|| aacdao
									.doesLastPaidInstallmentFallsOnCurrentDate(
											accountId,
											PaymentStatus.PAID.getValue())) {
						Calendar feeDate = new GregorianCalendar();
						feeDate.setTime(feeStartDate);
						Calendar calendarTypeNewDate = new GregorianCalendar(
								feeDate.get(Calendar.YEAR), feeDate
										.get(Calendar.MONTH), (feeDate
										.get(Calendar.DATE) + 1));
						feeStartDate = calendarTypeNewDate.getTime();
					}
				}
				if (null == accountActionDateList
						|| accountActionDateList.isEmpty())
					throw new AccountsApplyChargesException(
							AccountConstants.NOMOREINSTALLMENTS);

				RepaymentSchedule repaymentSchedule = getFeeInstallment(
						accountFeeSet, account, feeStartDate);
				List<FeeInstallment> feeInstallment = repaymentSchedule
						.getRepaymentFeeInstallment();
				if (account instanceof Loan
						&& ((Loan) account).getIntrestAtDisbursement().equals(
								LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT)
						&& fee.getFeeFrequency().getFeeFrequencyTypeId()
								.equals(FeeFrequencyType.ONETIME.getValue())
						&& fee.getFeeFrequency().getFeePaymentId().equals(
								FeePayment.TIME_OF_DISBURSMENT.getValue())) {
					accountActionDateList = RepaymentScheduleHelper
							.getAccountActionFee(accountActionDateList,
									feeInstallment,"Loan");

				} else {
					accountActionDateList = removeTimeOfDisbursmentFees(
							RepaymentScheduleHelper.getAccountActionFee(
									accountActionDateList, feeInstallment,"customer"),
							accountFee.getFees());
				}

				Money feeAmount = new Money();
				if (account instanceof Loan) {
					if (!fee.getFeeFrequency().getFeeFrequencyTypeId().equals(
							FeeFrequencyType.ONETIME.getValue())) {
						for (AccountActionDate accountActionDate : accountActionDateList) {
							feeAmount = feeAmount.add(accountActionDate
									.getTotalFeesAmountForId(accountFee
											.getFees().getFeeId()));
						}
					} else {
						feeAmount = accountFee.getAccountFeeAmount();
					}
				}

				/* save the loan summary also */
				LoanSummary loanSummary = null;
				LoanActivity loanActivity = null;
				if (account instanceof Loan) {
					handleRoundingOfInstallments(account, accountActionDateList);
					Loan loan = (Loan) account;
					loanSummary = loan.getLoanSummary();
					loanSummary.setOriginalFees(loanSummary.getOriginalFees()
							.add(repaymentSchedule.getFees()));

					loanActivity = new LoanActivity();
					loanActivity.setAccount(account);
					loanActivity.setComments(fee.getFeeName() + " "
							+ AccountConstants.FEES_APPLIED);
					loanActivity.setPrincipal(new Money());
					loanActivity.setInterest(new Money());
					loanActivity.setFee(feeAmount);
					loanActivity.setFeeOutstanding(loanSummary
							.getOriginalFees().subtract(
									loanSummary.getFeesPaid()));
					loanActivity.setInterestOutstanding(loanSummary
							.getOriginalInterest().subtract(
									loanSummary.getInterestPaid()));
					loanActivity.setPenaltyOutstanding(loanSummary
							.getOriginalPenalty().subtract(
									loanSummary.getPenaltyPaid()));
					loanActivity.setPrincipalOutstanding(loanSummary
							.getOriginalPrincipal().subtract(
									loanSummary.getPrincipalPaid()));
					loanActivity.setPersonnelId(context.getUserContext()
							.getId());
				}

				CustomerActivityEntity customerActivityEntity=updateCustomerDetails(account,accountActionDateList,fee,aac.getChargeAmount(),context.getUserContext().getId(),accountFee);
				// save the list
				aacdao.saveAccountActionDateList(accountActionDateList,
						accountFee, loanSummary, loanActivity,
						customerActivityEntity);
			}
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AccountsApplyChargesException(
					AccountConstants.UNEXPECTEDERROR);
		}

	}

	public RepaymentSchedule getFeeInstallment(Set acSet, Account account,
			Date feeStartDate) {
		RepaymentScheduleInputsIfc inputs = null;
		RepaymentScheduleIfc repaymentScheduler = null;

		inputs = RepaymentScheduleFactory.getRepaymentScheduleInputs();
		repaymentScheduler = RepaymentScheduleFactory.getRepaymentScheduler();
		RepaymentSchedule repaymentSchedule = null;
		try {

			// repayment frequency of the account has to be set here
			// need to separate here based on account type (customer/account)

			if (null != account.getAccountTypeId()) {

				Short accountType = account.getAccountTypeId();

				if (accountType.equals(AccountTypes.LOANACCOUNT.getValue())) {
					setLoanInput(account, inputs);
				} else if (accountType.equals(AccountTypes.CUSTOMERACCOUNT.getValue())) {
					setCustomerInput(account, inputs);
				} else if (accountType.equals(AccountTypes.SAVINGSACCOUNT.getValue())) {

				}

			} else {
				// TODO add message later

				throw new AccountsApplyChargesException();

			}

			inputs.setAccountFee(acSet);// input account fees from ui
			repaymentScheduler.setRepaymentScheduleInputs(inputs);
			inputs.setFeeStartDate(feeStartDate);
			repaymentSchedule = repaymentScheduler.getRepaymentSchedule();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return repaymentSchedule;
	}

	Date getDate(String dt) {
		Date date = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			date = df.parse(dt);
		} catch (Exception e) {
			// System.out.println("error in parsing.." + e.getMessage());
		}
		return date;
	}

	private void setLoanInput(Account account, RepaymentScheduleInputsIfc inputs) {
		Meeting meeting = null;

		Loan loan = (Loan) account;
		if (null != account.getAccountTypeId()
				&& account.getAccountTypeId() == 1) {
			meeting = loan.getLoanMeeting();
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(loan.getDisbursementDate());
			meeting.setMeetingStartDate(calendar);
		} else {
			meeting = account.getCustomer().getCustomerMeeting().getMeeting();
		}
		inputs.setDisbursementDate(loan.getDisbursementDate());
		inputs.setIsInterestDedecutedAtDisburesement(loan
				.getIntrestAtDisbursement().equals(
						LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT) ? true
				: false);
		inputs.setIsPrincipalInLastPayment(loan.getLoanOffering()
				.getPrinDueLastInstFlag().equals(Short.valueOf("1")) ? true
				: false);
		inputs.setRepaymentFrequency(meeting);
		inputs.setNoOfInstallments(loan.getNoOfInstallments());
		inputs.setPrincipal(loan.getLoanAmount());
		inputs.setInterestRate(loan.getInterestRateAmount());
		inputs.setGraceType(loan.getGracePeriodTypeId());
		inputs.setGracePeriod(loan.getGracePeriodDuration());
	}

	private void setCustomerInput(Account account,
			RepaymentScheduleInputsIfc inputs) throws ApplicationException {
		Meeting meeting = null;

		Customer customer = account.getCustomer();
		if (null != customer) {
			CustomerMeeting cm = customer.getCustomerMeeting();

			if (null != cm) {
				meeting = cm.getMeeting();
			} else {
				throw new AccountsApplyChargesException(
						AccountConstants.KEYNOMEETING);

			}

		} else {
			throw new AccountsApplyChargesException(
					AccountConstants.KEYNOMEETING);
		}
		inputs.setRepaymentFrequency(meeting);
		inputs
				.setMeetingToConsider(RepaymentScheduleConstansts.MEETING_CUSTOMER);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#fetchHeader(org.mifos.framework.util.valueobjects.Context,
	 *      java.lang.String)
	 */
	public HeaderObject fetchHeader(Context context, String businessAction)
			throws SystemException, ApplicationException {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"inside fetch header with method : " + businessAction);
		if ("load".equals(businessAction)) {
			AccountsApplyCharges applyCharges = (AccountsApplyCharges) context
					.getValueObject();
			Account account = (Account) DAO.getEntity(
					"org.mifos.application.accounts.util.valueobjects.Account",
					applyCharges.getAccountId(), DataTypeConstants.Integer);
			LoanHeaderObject loanHeader = new LoanHeaderObject();
			CustomerMaster customerMaster = (CustomerMaster) context
					.getBusinessResults(AccountConstants.CUSTOMERMASTER);
			List<CustomerMaster> customerMasterList = CustomerUtilDAO
					.getParentHierarchy(account.getCustomer().getCustomerId());
			Office office = (Office) DAO.getEntity(
					"org.mifos.application.office.util.valueobjects.Office",
					account.getOfficeId(), DataTypeConstants.Short);
			String OfficeName = office.getOfficeName();
			loanHeader.setCustomerMasterList(customerMasterList);
			loanHeader.setOfficeName(OfficeName);
			loanHeader.setOfficeId(account.getOfficeId());

			return loanHeader;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#getBusinessActionList()
	 */
	protected List<String> getBusinessActionList() {
		List headerMethodList = new ArrayList();
		headerMethodList.add("load");
		return headerMethodList;
	}

	private boolean checkForFeesInAccountFeesForGivenAccount(Account account,
			Short feeId) {
		boolean flag = false;
		Set<AccountFees> accountFeesSet = account.getAccountFeesSet();
		if (accountFeesSet != null) {
			for (AccountFees accountFees : accountFeesSet) {
				Fees fees = accountFees.getFees();
				if (fees.getFeeId().equals(feeId)
						&& !fees.getFeeFrequency().getFeeFrequencyTypeId()
								.equals(FeeFrequencyType.ONETIME.getValue())) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	/*
	 * Removes time of disbursment fees from accountActionDateSet.
	 */
	private List<AccountActionDate> removeTimeOfDisbursmentFees(
			List<AccountActionDate> accountActionDateList, Fees fees) {
		Short paymentType = fees.getFeeFrequency().getFeePaymentId();
		if (null != accountActionDateList
				&& !accountActionDateList.isEmpty()
				&& paymentType != null
				&& paymentType
						.equals(FeePayment.TIME_OF_DISBURSMENT.getValue())) {
			for (AccountActionDate accountActionDate : accountActionDateList) {
				Set<AccountFeesActionDetail> accountFeesActionDetailSet = accountActionDate
						.getAccountFeesActionDetail();
				Iterator<AccountFeesActionDetail> iterator = accountFeesActionDetailSet
						.iterator();
				while (iterator.hasNext()) {
					AccountFeesActionDetail accountFeesActionDetail = iterator
							.next();
					if (fees.getFeeId().equals(
							accountFeesActionDetail.getFeeId())) {
						iterator.remove();
					}
				}
			}
		}
		return accountActionDateList;
	}

	/*
	 * This method iterates over accountActionDateList and checks the first's
	 * installment date with the current date. If it finds them equall , removes
	 * that record from the list and returns true otherwise returns false.
	 */
	private boolean doesFeeInstallmentStartDateNeedToBeChanged(
			List<AccountActionDate> accountActionDateList, Fees fee) {
		Calendar currentDate = new GregorianCalendar();
		Iterator<AccountActionDate> iterator = accountActionDateList.iterator();
		AccountActionDate accountActionDate = iterator.next();
		Date date = accountActionDate.getActionDate();
		Calendar actionDate = new GregorianCalendar();
		actionDate.setTime(date);

		if (currentDate.get(Calendar.DATE) == actionDate.get(Calendar.DATE)
				&& currentDate.get(Calendar.MONTH) == actionDate
						.get(Calendar.MONTH)
				&& currentDate.get(Calendar.YEAR) == actionDate
						.get(Calendar.YEAR)) {
			iterator.remove();
			return true;
		}
		return false;
	}

	private AccountActionDate getLastInstallment(
			Set<AccountActionDate> accountActionDates) {
		AccountActionDate lastInstallment = null;
		for (AccountActionDate accountActionDate : accountActionDates) {
			lastInstallment = accountActionDate;
		}
		return lastInstallment;
	}

	private void handleRoundingOfInstallments(Account account,
			List<AccountActionDate> accountActionDateList) {
		Loan loan = (Loan) account;
		Set<AccountActionDate> accountActionDateSet = loan
				.getAccountActionDateSet();
		AccountActionDate lastInstallment = getLastInstallment(accountActionDateSet);
		boolean flag = true;
		for (AccountActionDate accountActionDate : accountActionDateList) {
			if (accountActionDate.equals(lastInstallment))
				flag = false;
		}
		if (flag)
			accountActionDateList.add(lastInstallment);
		Boolean isInterestDeductedAtDisbursment = loan
				.getIntrestAtDisbursement().equals(
						LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT) ? true
				: false;
		Boolean isPrincipalDueInLastInstallment = loan.getLoanOffering()
				.getPrinDueLastInstFlag().equals(Short.valueOf("1")) ? true
				: false;
		LoanHelpers.roundAccountActionsDate(isInterestDeductedAtDisbursment,
				isPrincipalDueInLastInstallment, accountActionDateList);
	}
	
	private CustomerActivityEntity updateCustomerDetails(Account account,List<AccountActionDate> accountActionDateList,Fees fee,Double amount,Short personnelId,AccountFees accountFees) {
		CustomerActivityEntity customerActivityEntity=null;
		if(account instanceof CustomerAccount){
			Short state=account.getCustomer().getStatusId();
			if(state.equals(CustomerConstants.CENTER_ACTIVE_STATE) || 
					state.equals(CustomerConstants.GROUP_ACTIVE_STATE) || 
					state.equals(GroupConstants.HOLD) || 
					state.equals(CustomerConstants.CLIENT_APPROVED) || 
					state.equals(CustomerConstants.CLIENT_ONHOLD)){
				customerActivityEntity=new CustomerActivityEntity(new PersonnelPersistenceService().getPersonnel(personnelId),
						fee.getFeeName()+" "+AccountConstants.FEES_APPLIED,
						new Money(String.valueOf(amount)));
				customerActivityEntity.setCustomerAccount((CustomerAccountBO)new AccountPersistanceService().getAccount(account.getAccountId()));
			}
			else{
				for(AccountActionDate accountActionDate : accountActionDateList)
					accountActionDate.setAccountFeesActionDetail(null);
				accountFees.setLastAppliedDate(null);
			}
		}
		return customerActivityEntity;
	}

}
