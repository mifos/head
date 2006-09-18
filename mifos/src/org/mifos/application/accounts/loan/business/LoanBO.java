/**

 * LoanBO.java    version: 1.0

 

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

package org.mifos.application.accounts.loan.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.exceptions.AccountExceptionConstants;
import org.mifos.application.accounts.exceptions.IDGenerationException;
import org.mifos.application.accounts.loan.exceptions.LoanExceptionConstants;
import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.accounts.loan.util.helpers.EMIInstallment;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.helpers.LoanPaymentTypes;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountPaymentData;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.FeeInstallment;
import org.mifos.application.accounts.util.helpers.InstallmentDate;
import org.mifos.application.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.accounts.util.helpers.OverDueAmounts;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeFormulaEntity;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.master.business.CollateralTypeEntity;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.GraceTypeConstants;
import org.mifos.application.productdefinition.util.helpers.InterestTypeConstants;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.interestcalculator.InterestCalculatorConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.repaymentschedule.MeetingScheduleHelper;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleInputsIfc;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.helpers.SchedulerHelper;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class LoanBO extends AccountBO {

	private final LoanOfferingBO loanOffering;

	private final LoanSummaryEntity loanSummary;

	private Money loanAmount;

	private Money loanBalance;

	private Short noOfInstallments;

	private Date disbursementDate;

	private MeetingBO loanMeeting;

	private Short intrestAtDisbursement;

	private GracePeriodTypeEntity gracePeriodType;

	private Short gracePeriodDuration;

	private Short gracePeriodPenalty;

	private final LoanPerformanceHistoryEntity performanceHistory;

	private InterestTypesEntity interestType;

	private Double interestRate;

	private Fund fund;

	private Integer businessActivityId;

	private CollateralTypeEntity collateralType;

	private String collateralNote;

	private Short groupFlag;

	private String stateSelected;

	private Set<LoanActivityEntity> loanActivityDetails;

	protected LoanBO() {
		super();
		this.loanOffering = null;
		this.loanSummary = null;
		this.performanceHistory = null;
		this.loanActivityDetails = new HashSet<LoanActivityEntity>();
	}

	public LoanBO(UserContext userContext, LoanOfferingBO loanOffering,
			CustomerBO customer, AccountState accountState, Money loanAmount,
			Short noOfinstallments, Date disbursementDate,
			boolean interestDeductedAtDisbursement, Double interesRate,
			Short gracePeriodDuration, Fund fund, List<FeeView> feeViews)
			throws AccountException {
		super(userContext, customer, AccountTypes.LOANACCOUNT, accountState);
		validate(loanOffering, loanAmount, noOfinstallments, disbursementDate,
				interesRate, gracePeriodDuration, fund, customer);
		setCreateDetails();
		this.loanOffering = loanOffering;
		this.loanAmount = loanAmount;
		this.loanBalance = loanAmount;
		this.noOfInstallments = noOfinstallments;
		this.interestType = loanOffering.getInterestTypes();
		this.interestRate = interesRate;
		setInterestDeductedAtDisbursement(interestDeductedAtDisbursement);
		setGracePeriodTypeAndDuration(interestDeductedAtDisbursement,
				gracePeriodDuration);
		this.gracePeriodPenalty = Short.valueOf("0");
		this.fund = fund;
		this.loanMeeting = buildLoanMeeting(customer.getCustomerMeeting()
				.getMeeting(), loanOffering.getPrdOfferingMeeting()
				.getMeeting(), disbursementDate);
		buildAccountFee(feeViews);
		this.disbursementDate = disbursementDate;
		this.performanceHistory = new LoanPerformanceHistoryEntity(this);
		this.loanActivityDetails = new HashSet<LoanActivityEntity>();
		generateMeetingSchedule();
		this.loanSummary = buildLoanSummary();
	}

	public Integer getBusinessActivityId() {
		return businessActivityId;
	}

	public void setBusinessActivityId(Integer businessActivityId) {
		this.businessActivityId = businessActivityId;
	}

	public String getCollateralNote() {
		return collateralNote;
	}

	public void setCollateralNote(String collateralNote) {
		this.collateralNote = collateralNote;
	}

	public CollateralTypeEntity getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(CollateralTypeEntity collateralType) {
		this.collateralType = collateralType;
	}

	public GracePeriodTypeEntity getGracePeriodType() {
		return gracePeriodType;
	}

	public void setGracePeriodType(GracePeriodTypeEntity gracePeriodType) {
		this.gracePeriodType = gracePeriodType;
	}

	public Date getDisbursementDate() {
		return disbursementDate;
	}

	public void setDisbursementDate(Date disbursementDate) {
		this.disbursementDate = disbursementDate;
	}

	public Fund getFund() {
		return fund;
	}

	public void setFund(Fund fund) {
		this.fund = fund;
	}

	public Short getGracePeriodDuration() {
		return gracePeriodDuration;
	}

	public void setGracePeriodDuration(Short gracePeriodDuration) {
		this.gracePeriodDuration = gracePeriodDuration;
	}

	public Short getGracePeriodPenalty() {
		return gracePeriodPenalty;
	}

	public void setGracePeriodPenalty(Short gracePeriodPenalty) {
		this.gracePeriodPenalty = gracePeriodPenalty;
	}

	public Short getGroupFlag() {
		return groupFlag;
	}

	public void setGroupFlag(Short groupFlag) {
		this.groupFlag = groupFlag;
	}

	public Double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	public InterestTypesEntity getInterestType() {
		return interestType;
	}

	public void setInterestType(InterestTypesEntity interestType) {
		this.interestType = interestType;
	}

	public boolean isInterestDeductedAtDisbursement() {
		return (intrestAtDisbursement != null && intrestAtDisbursement
				.shortValue() == LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT
				.shortValue()) ? true : false;
	}

	public void setInterestDeductedAtDisbursement(boolean interestDedAtDisb) {
		this.intrestAtDisbursement = interestDedAtDisb ? Constants.YES
				: Constants.NO;
	}

	public Money getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(Money loanAmount) {
		this.loanAmount = loanAmount;
	}

	public Money getLoanBalance() {
		return loanBalance;
	}

	public void setLoanBalance(Money loanBalance) {
		this.loanBalance = loanBalance;
	}

	public MeetingBO getLoanMeeting() {
		return loanMeeting;
	}

	public void setLoanMeeting(MeetingBO loanMeeting) {
		this.loanMeeting = loanMeeting;
	}

	public LoanOfferingBO getLoanOffering() {
		return loanOffering;
	}

	public LoanSummaryEntity getLoanSummary() {
		return loanSummary;
	}

	public Short getNoOfInstallments() {
		return noOfInstallments;
	}

	public void setNoOfInstallments(Short noOfInstallments) {
		this.noOfInstallments = noOfInstallments;
	}

	public String getStateSelected() {
		return stateSelected;
	}

	public void setStateSelected(String stateSelected) {
		this.stateSelected = stateSelected;
	}

	public LoanPerformanceHistoryEntity getPerformanceHistory() {
		return performanceHistory;
	}

	public Set<LoanActivityEntity> getLoanActivityDetails() {
		return loanActivityDetails;
	}

	public void addLoanActivity(LoanActivityEntity loanActivity) {
		this.loanActivityDetails.add(loanActivity);
	}

	@Override
	public AccountTypes getType() {
		return AccountTypes.LOANACCOUNT;
	}

	@Override
	public boolean isOpen() {
		return !(getAccountState().getId().equals(
				AccountState.LOANACC_CANCEL.getValue())
				|| getAccountState().getId().equals(
						AccountState.LOANACC_BADSTANDING.getValue())
				|| getAccountState().getId().equals(
						AccountState.LOANACC_OBLIGATIONSMET.getValue()) || getAccountState()
				.getId().equals(AccountState.LOANACC_WRITTENOFF.getValue()));
	}

	@Override
	public void updateTotalFeeAmount(Money totalFeeAmount) {
		LoanSummaryEntity loanSummaryEntity = this.getLoanSummary();
		loanSummaryEntity.setOriginalFees(loanSummaryEntity.getOriginalFees()
				.subtract(totalFeeAmount));
	}

	@Override
	public void updateTotalPenaltyAmount(Money totalPenaltyAmount) {
		LoanSummaryEntity loanSummaryEntity = this.getLoanSummary();
		loanSummaryEntity.setOriginalPenalty(loanSummaryEntity
				.getOriginalPenalty().subtract(totalPenaltyAmount));
	}

	@Override
	public boolean isAdjustPossibleOnLastTrxn() {
		// adjustment is possible only if account state is
		// 1. active in good standing.
		// 2. active in bad standing.
		if (!(getAccountState().getId().equals(
				AccountStates.LOANACC_ACTIVEINGOODSTANDING) || getAccountState()
				.getId().equals(AccountStates.LOANACC_BADSTANDING))) {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"State is not active hence adjustment is not possible");
			return false;
		}
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Total payments on this account is  "
						+ getAccountPayments().size());
		AccountPaymentEntity accountPayment = getLastPmnt();
		if (accountPayment != null) {
			for (AccountTrxnEntity accntTrxn : accountPayment.getAccountTrxns()) {
				LoanTrxnDetailEntity lntrxn = (LoanTrxnDetailEntity) accntTrxn;
				if (lntrxn.getInstallmentId().equals(Short.valueOf("0"))
						|| isAdjustmentForInterestDedAtDisb(lntrxn
								.getInstallmentId())) {
					return false;
				}
			}
		}
		if (null != getLastPmnt() && getLastPmntAmnt() != 0) {
			return true;
		}
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Adjustment is not possible ");
		return false;
	}

	@Override
	public void updateAccountActivity(Money principal, Money interest,
			Money fee, Money penalty, Short personnelId, String description) throws AccountException {
		try{
		PersonnelBO personnel = new PersonnelPersistence()
				.getPersonnel(personnelId);
		LoanActivityEntity loanActivity = new LoanActivityEntity(this,
				personnel, description, principal, loanSummary
						.getOriginalPrincipal().subtract(
								loanSummary.getPrincipalPaid()), interest,
				loanSummary.getOriginalInterest().subtract(
						loanSummary.getInterestPaid()), fee, loanSummary
						.getOriginalFees().subtract(loanSummary.getFeesPaid()),
				penalty, loanSummary.getOriginalPenalty().subtract(
						loanSummary.getPenaltyPaid()), DateUtils
						.getCurrentDateWithoutTimeStamp());
		this.addLoanActivity(loanActivity);
		}catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	@Override
	public void waiveAmountDue(WaiveEnum waiveType) throws AccountException {
		if (waiveType.equals(WaiveEnum.FEES)) {
			waiveFeeAmountDue();
		} else if (waiveType.equals(WaiveEnum.PENALTY)) {
			waivePenaltyAmountDue();
		}
	}

	@Override
	public void waiveAmountOverDue(WaiveEnum waiveType) throws AccountException {
		if (waiveType.equals(WaiveEnum.FEES)) {
			waiveFeeAmountOverDue();
		} else if (waiveType.equals(WaiveEnum.PENALTY)) {
			waivePenaltyAmountOverDue();
		}
	}

	@Override
	public Money getTotalPrincipalAmountInArrears() {
		Money amount = new Money();
		List<AccountActionDateEntity> actionDateList = getDetailsOfInstallmentsInArrears();
		for (AccountActionDateEntity accountActionDateEntity : actionDateList) {
			amount = amount.add(((LoanScheduleEntity) accountActionDateEntity)
					.getPrincipal());
		}
		return amount;
	}

	@Override
	public final void removeFees(Short feeId, Short personnelId)
			throws AccountException {
		List<Short> installmentIds = getApplicableInstallmentIdsForRemoveFees();
		Money totalFeeAmount = new Money();
		if (installmentIds != null && installmentIds.size() != 0
				&& isFeeActive(feeId)) {
			totalFeeAmount = updateAccountActionDateEntity(installmentIds,
					feeId);
			updateAccountFeesEntity(feeId);
			updateTotalFeeAmount(totalFeeAmount);
			FeeBO feesBO = getAccountFeesObject(feeId);
			String description = feesBO.getFeeName() + " "
					+ AccountConstants.FEES_REMOVED;
			updateAccountActivity(null, null, totalFeeAmount, null,
					personnelId, description);
			roundInstallments(installmentIds);
			try {
				(new AccountPersistence()).createOrUpdate(this);
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}
		}

	}

	@Override
	public Money updateAccountActionDateEntity(List<Short> intallmentIdList,
			Short feeId) {
		Money totalFeeAmount = new Money();
		Set<AccountActionDateEntity> accountActionDateEntitySet = this
				.getAccountActionDates();
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateEntitySet) {
			if (intallmentIdList.contains(accountActionDateEntity
					.getInstallmentId())) {
				totalFeeAmount = totalFeeAmount
						.add(((LoanScheduleEntity) accountActionDateEntity)
								.removeFees(feeId));
			}
		}
		return totalFeeAmount;
	}

	@Override
	public void applyCharge(Short feeId, Double charge) throws AccountException {
		List<AccountActionDateEntity> dueInstallments = null;
		if (feeId.equals(Short.valueOf(AccountConstants.MISC_FEES))
				|| feeId.equals(Short.valueOf(AccountConstants.MISC_PENALTY))) {
			dueInstallments = getTotalDueInstallments();
			if (dueInstallments.isEmpty())
				throw new AccountException(AccountConstants.NOMOREINSTALLMENTS);
			applyMiscCharge(feeId, new Money(String.valueOf(charge)),
					dueInstallments.get(0));
			roundInstallments(getIdList(dueInstallments));
		} else {
			dueInstallments = getDueInstallments();
			if (dueInstallments.isEmpty())
				throw new AccountException(AccountConstants.NOMOREINSTALLMENTS);
			FeeBO fee = new FeePersistence().getFee(feeId);
			if (fee.getFeeFrequency().getFeePayment() != null) {
				applyOneTimeFee(fee, charge, dueInstallments.get(0));
			} else {
				applyPeriodicFee(fee, charge, getDueInstallments());
			}
			roundInstallments(getIdList(dueInstallments));
		}
	}

	/**
	 * It calculates over due amounts till installment 1 less than the one
	 * passed,because whatever amount is associated with the current installment
	 * it is the due amount and not the over due amount. It calculates that by
	 * iterating over the accountActionDates associated and summing up all the
	 * principal and principalPaid till installment-1 and then returning the
	 * difference of the two.It also takes into consideration any miscellaneous
	 * fee or miscellaneous penalty.
	 * 
	 * @param installmentId -
	 *            Installment id till which we want over due amounts.
	 * 
	 */
	public OverDueAmounts getOverDueAmntsUptoInstallment(Short installmentId)
			throws AccountException {
		Set<AccountActionDateEntity> accountActionDateEntities = getAccountActionDates();
		OverDueAmounts totalOverDueAmounts = new OverDueAmounts();
		if (null != accountActionDateEntities
				&& accountActionDateEntities.size() > 0) {
			Iterator<AccountActionDateEntity> accountActionDatesIterator = accountActionDateEntities
					.iterator();
			while (accountActionDatesIterator.hasNext()) {
				LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountActionDatesIterator
						.next();

				if (accountActionDateEntity.getInstallmentId() < installmentId) {
					OverDueAmounts dueAmounts = new OverDueAmounts();
					dueAmounts = accountActionDateEntity.getDueAmnts();
					totalOverDueAmounts.add(dueAmounts);
				}
			}
		}
		return totalOverDueAmounts;
	}

	public void disburseLoan(String recieptNum, Date transactionDate,
			Short paymentTypeId, PersonnelBO personnel, Date receiptDate,
			Short rcvdPaymentTypeId) throws AccountException {
		AccountPaymentEntity accountPaymentEntity = null;
		addLoanActivity(buildLoanActivity(this.loanAmount, personnel,
				"Loan Disbursal", transactionDate));

		// if the trxn date is not equal to disbursementDate we need to
		// regenerate the installments
		if (!DateUtils.getDateWithoutTimeStamp(disbursementDate.getTime())
				.equals(
						DateUtils.getDateWithoutTimeStamp(transactionDate
								.getTime()))) {
			this.disbursementDate = transactionDate;
			regeneratePaymentSchedule();
		}
		this.disbursementDate = transactionDate;
		AccountStateEntity newState;
		try {
			newState = (AccountStateEntity) (new MasterPersistence())
					.getPersistentObject(AccountStateEntity.class,
							AccountStates.LOANACC_ACTIVEINGOODSTANDING);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}

		// update status change history also
		this
				.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
						this.getAccountState(), newState, personnel));
		this.setAccountState(newState);

		if (this.isInterestDeductedAtDisbursement()) {
			accountPaymentEntity = payInterestAtDisbursement(recieptNum,
					transactionDate, rcvdPaymentTypeId, personnel, receiptDate);
		} else {
			try {
				if (new LoanPersistance().getFeeAmountAtDisbursement(this
						.getAccountId()) > 0.0)
					accountPaymentEntity = insertOnlyFeeAtDisbursement(
							recieptNum, transactionDate, rcvdPaymentTypeId,
							personnel);
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}
		}
		if (null == accountPaymentEntity) {
			accountPaymentEntity = new AccountPaymentEntity(this,
					this.loanAmount, recieptNum, transactionDate,
					new PaymentTypeEntity(paymentTypeId));
		} else {
			accountPaymentEntity.setAmount(this.loanAmount
					.subtract(accountPaymentEntity.getAmount()));
		}

		// create trxn entry for disbursal
		LoanTrxnDetailEntity loanTrxnDetailEntity = null;
		try {
			loanTrxnDetailEntity = new LoanTrxnDetailEntity(
					accountPaymentEntity, transactionDate,
					(AccountActionEntity) new MasterPersistence()
							.getPersistentObject(AccountActionEntity.class,
									AccountConstants.ACTION_DISBURSAL),
					personnel, "-", Short.valueOf("0"), this.loanAmount);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}

		List<AccountTrxnEntity> loanTrxns = new ArrayList<AccountTrxnEntity>();
		loanTrxns.add(loanTrxnDetailEntity);

		accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);
		this.addAccountPayment(accountPaymentEntity);
		this.buildFinancialEntries(accountPaymentEntity.getAccountTrxns());

		// Client performance entry
		updateCustomerHistoryOnDisbursement(this.loanAmount);
		if (getPerformanceHistory() != null)
			getPerformanceHistory().setLoanMaturityDate(
					getLastInstallmentAccountAction().getActionDate());
		try {
			new AccountPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public Money getTotalEarlyRepayAmount() {
		Money amount = new Money();
		List<AccountActionDateEntity> dueInstallmentsList = getApplicableIdsForDueInstallments();
		List<AccountActionDateEntity> futureInstallmentsList = getApplicableIdsForFutureInstallments();
		for (AccountActionDateEntity accountActionDateEntity : dueInstallmentsList) {
			amount = amount.add(((LoanScheduleEntity) accountActionDateEntity)
					.getTotalDueWithFees());
		}

		for (AccountActionDateEntity accountActionDateEntity : futureInstallmentsList) {
			amount = amount.add(((LoanScheduleEntity) accountActionDateEntity)
					.getPrincipalDue());
		}
		return amount;
	}

	public void makeEarlyRepayment(Money totalAmount, String receiptNumber,
			Date recieptDate, String paymentTypeId, Short personnelId)
			throws AccountException {
		try {
			MasterPersistence masterPersistence = new MasterPersistence();
			PersonnelBO personnel = new PersonnelPersistence()
					.getPersonnel(personnelId);
			this.setUpdatedBy(personnelId);
			this.setUpdatedDate(new Date(System.currentTimeMillis()));
			AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(
					this, totalAmount, receiptNumber, recieptDate,
					new PaymentTypeEntity(Short.valueOf(paymentTypeId)));
			addAccountPayment(accountPaymentEntity);

			makeEarlyRepaymentForDueInstallments(accountPaymentEntity);
			makeEarlyRepaymentForFutureInstallments(accountPaymentEntity);

			if (getPerformanceHistory() != null)
				getPerformanceHistory().setNoOfPayments(
						getPerformanceHistory().getNoOfPayments() + 1);
			addLoanActivity(buildLoanActivity(accountPaymentEntity
					.getAccountTrxns(), personnel, "Loan Repayment", DateUtils
					.getCurrentDateWithoutTimeStamp()));
			buildFinancialEntries(accountPaymentEntity.getAccountTrxns());

			AccountStateEntity newAccountState = (AccountStateEntity) masterPersistence
					.getPersistentObject(AccountStateEntity.class,
							AccountStates.LOANACC_OBLIGATIONSMET);
			addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
					getAccountState(), newAccountState,
					new PersonnelPersistence().getPersonnel(personnelId)));
			setAccountState((AccountStateEntity) masterPersistence
					.getPersistentObject(AccountStateEntity.class,
							AccountStates.LOANACC_OBLIGATIONSMET));
			setClosedDate(new Date(System.currentTimeMillis()));

			// Client performance entry
			updateCustomerHistoryOnRepayment(totalAmount);

			new LoanPersistance().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public void handleArrears() throws AccountException {
		MasterPersistence masterPersistence = new MasterPersistence();
		AccountStateEntity stateEntity;
		try {
			stateEntity = (AccountStateEntity) masterPersistence
					.getPersistentObject(AccountStateEntity.class,
							AccountStates.LOANACC_BADSTANDING);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
		AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(
				this.getAccountState(), stateEntity, this.getPersonnel());
		this.addAccountStatusChangeHistory(historyEntity);
		this.setAccountState(stateEntity);
		String systemDate = DateHelper.getCurrentDate(Configuration
				.getInstance().getSystemConfig().getMFILocale());
		Date currrentDate = DateHelper.getLocaleDate(Configuration
				.getInstance().getSystemConfig().getMFILocale(), systemDate);
		this.setUpdatedDate(currrentDate);

		// Client performance entry
		updateCustomerHistoryOnArrears();

		try {
			new LoanPersistance().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public boolean isLastInstallment(Short installmentId) {
		Set<AccountActionDateEntity> accountActionDateSet = getAccountActionDates();
		List<Object> objectList = Arrays.asList(accountActionDateSet.toArray());
		AccountActionDateEntity accountActionDateEntity = (AccountActionDateEntity) objectList
				.get(objectList.size() - 1);
		if (installmentId.equals(accountActionDateEntity.getInstallmentId()))
			return true;
		return false;
	}

	public void writeOff(String comment) throws AccountException {
		try {
		Short personnelId = this.getUserContext().getId();
		PersonnelBO personnel = new PersonnelPersistence()
				.getPersonnel(personnelId);
		Short statusId = Short.valueOf(AccountStates.LOANACC_WRITTENOFF);
		this.setUpdatedBy(personnelId);
		this.setUpdatedDate(new Date(System.currentTimeMillis()));
		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(
				this, getEarlyClosureAmount(), null, null,
				new PaymentTypeEntity(Short.valueOf("1")));
		this.addAccountPayment(accountPaymentEntity);
		for (AccountActionDateEntity accountActionDateEntity : getListOfUnpaidInstallments()) {
			MasterPersistence masterPersistence = new MasterPersistence();
			LoanTrxnDetailEntity loanTrxnDetailEntity;
			
				loanTrxnDetailEntity = new LoanTrxnDetailEntity(
						accountPaymentEntity,
						(AccountActionEntity) masterPersistence
								.getPersistentObject(AccountActionEntity.class,
										AccountConstants.ACTION_WRITEOFF),
						accountActionDateEntity, personnel, "Loan Written Off");
			
			accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);
		}
		addLoanActivity(buildLoanActivity(accountPaymentEntity
				.getAccountTrxns(), personnel, "Loan Written Off", DateUtils
				.getCurrentDateWithoutTimeStamp()));
		buildFinancialEntries(accountPaymentEntity.getAccountTrxns());
		changeStatus(statusId, null, comment);

		// Client performance entry
		updateCustomerHistoryOnWriteOff();

		
			new LoanPersistance().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public void waiveFeeAmountDue() throws AccountException {
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountActionDateList
				.get(accountActionDateList.size() - 1);
		Money chargeWaived = accountActionDateEntity.waiveFeeCharges();
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateTotalFeeAmount(chargeWaived);
			updateAccountActivity(null, null, chargeWaived, null, userContext
					.getId(), "Amnt " + chargeWaived + " waived");
		}
		try {
			new LoanPersistance().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public void waivePenaltyAmountDue() throws AccountException {
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountActionDateList
				.get(accountActionDateList.size() - 1);
		Money chargeWaived = accountActionDateEntity.waivePenaltyCharges();
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateTotalPenaltyAmount(chargeWaived);
			updateAccountActivity(null, null, null, chargeWaived, userContext
					.getId(), "Amnt " + chargeWaived + " waived");
		}
		try {
			new LoanPersistance().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public void waiveFeeAmountOverDue() throws AccountException {
		Money chargeWaived = new Money();
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		accountActionDateList.remove(accountActionDateList.size() - 1);
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
			chargeWaived = chargeWaived
					.add(((LoanScheduleEntity) accountActionDateEntity)
							.waiveFeeCharges());
		}
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateTotalFeeAmount(chargeWaived);
			updateAccountActivity(null, null, chargeWaived, null, userContext
					.getId(), "Amnt " + chargeWaived + " waived");
		}
		try {
			new LoanPersistance().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public void waivePenaltyAmountOverDue() throws AccountException {
		Money chargeWaived = new Money();
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		accountActionDateList.remove(accountActionDateList.size() - 1);
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
			chargeWaived = chargeWaived
					.add(((LoanScheduleEntity) accountActionDateEntity)
							.waivePenaltyCharges());
		}
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateTotalPenaltyAmount(chargeWaived);
			updateAccountActivity(null, null, null, chargeWaived, userContext
					.getId(), "Amnt " + chargeWaived + " waived");
		}
		try {
			new LoanPersistance().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public Money getAmountTobePaidAtdisburtail(Date disbursalDate)
			throws AccountException {
		if (this.isInterestDeductedAtDisbursement()) {
			return getDueAmount(getAccountActionDate(Short.valueOf("1")));
		} else {
			try {
				return new Money(new LoanPersistance()
						.getFeeAmountAtDisbursement(this.getAccountId())
						.toString());
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}
		}

	}

	public Boolean hasPortfolioAtRisk() {
		List<AccountActionDateEntity> accountActionDateList = getDetailsOfInstallmentsInArrears();
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
			Calendar actionDate = new GregorianCalendar();
			actionDate.setTime(accountActionDateEntity.getActionDate());
			long diffInTermsOfDay = (Calendar.getInstance().getTimeInMillis() - actionDate
					.getTimeInMillis())
					/ (24 * 60 * 60 * 1000);
			if (diffInTermsOfDay > 30) {
				return true;
			}
		}
		return false;
	}

	public Money getRemainingPrincipalAmount() {
		return loanSummary.getOriginalPrincipal().subtract(
				loanSummary.getPrincipalPaid());
	}

	public Integer getDaysInArrears() {
		if (getAccountState().getId().equals(
				AccountStates.LOANACC_ACTIVEINGOODSTANDING)
				|| getAccountState().getId().equals(
						AccountStates.LOANACC_OBLIGATIONSMET)
				|| getAccountState().getId().equals(
						AccountStates.LOANACC_WRITTENOFF)
				|| getAccountState().getId().equals(
						AccountStates.LOANACC_RESCHEDULED)
				|| getAccountState().getId().equals(
						AccountStates.LOANACC_BADSTANDING)) {
			if (!getDetailsOfInstallmentsInArrears().isEmpty()) {
				AccountActionDateEntity accountActionDateEntity = getDetailsOfInstallmentsInArrears()
						.get(getDetailsOfInstallmentsInArrears().size() - 1);
				Calendar actionDate = new GregorianCalendar();
				actionDate.setTime(accountActionDateEntity.getActionDate());
				long diffInTermsOfDay = (Calendar.getInstance()
						.getTimeInMillis() - actionDate.getTimeInMillis())
						/ (24 * 60 * 60 * 1000);
				return Integer.valueOf(new Long(diffInTermsOfDay).toString());
			}
		}
		return 0;
	}

	public Boolean isAccountActive() {
		return (getAccountState().getId().equals(
				AccountStates.LOANACC_ACTIVEINGOODSTANDING) || getAccountState()
				.getId().equals(AccountStates.LOANACC_BADSTANDING)) ? true
				: false;
	}

	public Integer getMissedPaymentCount() {
		int noOfMissedPayments = 0;
		if (getAccountState().getId().equals(
				AccountStates.LOANACC_ACTIVEINGOODSTANDING)
				|| getAccountState().getId().equals(
						AccountStates.LOANACC_OBLIGATIONSMET)
				|| getAccountState().getId().equals(
						AccountStates.LOANACC_WRITTENOFF)
				|| getAccountState().getId().equals(
						AccountStates.LOANACC_RESCHEDULED)
				|| getAccountState().getId().equals(
						AccountStates.LOANACC_BADSTANDING)) {
			List<AccountActionDateEntity> accountActionDateList = getDetailsOfInstallmentsInArrears();
			if (!accountActionDateList.isEmpty())
				noOfMissedPayments = +accountActionDateList.size();
			noOfMissedPayments = noOfMissedPayments
					+ getNoOfBackDatedPayments();
		}
		return noOfMissedPayments;
	}

	public void save() throws AccountException {
		try {
			new LoanPersistance().createOrUpdate(this);
			this.globalAccountNum = generateId(userContext.getBranchGlobalNum());
			new LoanPersistance().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTION, e);
		} catch (IDGenerationException e) {
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTION, e);

		}
	}

	public void updateLoan() throws AccountException {
		if (getAccountState().getId().equals(
				AccountState.LOANACC_APPROVED.getValue())
				|| getAccountState().getId().equals(
						AccountState.LOANACC_DBTOLOANOFFICER.getValue())
				|| getAccountState().getId().equals(
						AccountState.LOANACC_PARTIALAPPLICATION.getValue())
				|| getAccountState().getId().equals(
						AccountState.LOANACC_PENDINGAPPROVAL.getValue())) {
			if (isDisbursementDateLessThanCurrentDate(disbursementDate))
				throw new AccountException(
						LoanExceptionConstants.INVALIDDISBURSEMENTDATE);
			regeneratePaymentSchedule();
		}
		update();
	}

	@Override
	protected void setLoanInput(RepaymentScheduleInputsIfc inputs,
			Date feeStartDate) {
		inputs.setDisbursementDate(getDisbursementDate());
		inputs
				.setIsInterestDedecutedAtDisburesement(isInterestDeductedAtDisbursement());
		inputs.setIsPrincipalInLastPayment(getLoanOffering()
				.isPrinDueLastInst());
		inputs.setRepaymentFrequency(getLoanMeeting());
		inputs.setNoOfInstallments(getNoOfInstallments());
		inputs.setPrincipal(getLoanAmount());
		inputs.setInterestRate(getInterestRate());
		inputs.setGraceType(getGracePeriodType().getId());
		inputs.setGracePeriod(getGracePeriodDuration());
	}

	@Override
	protected void updatePerformanceHistoryOnAdjustment(Integer noOfTrxnReversed) {
		if (getPerformanceHistory() != null) {
			getPerformanceHistory().setNoOfPayments(
					getPerformanceHistory().getNoOfPayments()
							- noOfTrxnReversed);
		}
	}

	@Override
	protected AccountPaymentEntity makePayment(PaymentData paymentData)
			throws AccountException {
		LoanPaymentTypes loanPaymentTypes = getLoanPaymentType(paymentData
				.getTotalAmount());
		if (loanPaymentTypes == null)
			throw new AccountException("errors.update",
					new String[] { getGlobalAccountNum() });
		else if (loanPaymentTypes.equals(LoanPaymentTypes.PARTIAL_PAYMENT)) {
			handlePartialPayment(paymentData);
		} else if (loanPaymentTypes.equals(LoanPaymentTypes.FULL_PAYMENT)) {
			handleFullPayment(paymentData);
		} else if (loanPaymentTypes.equals(LoanPaymentTypes.FUTURE_PAYMENT)) {
			handleFuturePayment(paymentData);
		}
		AccountActionDateEntity lastAccountAction = getLastInstallmentAccountAction();
		AccountPaymentEntity accountPayment = new AccountPaymentEntity(this,
				paymentData.getTotalAmount(), paymentData.getRecieptNum(),
				paymentData.getRecieptDate(), new PaymentTypeEntity(paymentData
						.getPaymentTypeId()));
		java.sql.Date paymentDate = new java.sql.Date(paymentData
				.getTransactionDate().getTime());
		for (AccountPaymentData accountPaymentData : paymentData
				.getAccountPayments()) {
			LoanScheduleEntity accountAction = (LoanScheduleEntity) getAccountActionDate(accountPaymentData
					.getInstallmentId());
			if (accountAction.isPaid())
				throw new AccountException("errors.update",
						new String[] { getGlobalAccountNum() });
			if (accountAction.getInstallmentId().equals(
					lastAccountAction.getInstallmentId())
					&& accountPaymentData.isPaid()) {
				changeLoanStatus(AccountState.LOANACC_OBLIGATIONSMET,
						paymentData.getPersonnel());
				this.setClosedDate(new Date(System.currentTimeMillis()));
				// Client performance entry
				updateCustomerHistoryOnLastInstlPayment(paymentData
						.getTotalAmount());
			}
			if (getState().equals(AccountState.LOANACC_BADSTANDING)
					&& (loanPaymentTypes.equals(LoanPaymentTypes.FULL_PAYMENT) || loanPaymentTypes
							.equals(LoanPaymentTypes.FUTURE_PAYMENT))) {
				changeLoanStatus(AccountState.LOANACC_ACTIVEINGOODSTANDING,
						paymentData.getPersonnel());
				// Client performance entry
				updateCustomerHistoryOnPayment();
			}
			LoanPaymentData loanPaymentData = (LoanPaymentData) accountPaymentData;
			accountAction.setPaymentDetails(loanPaymentData, paymentDate);
			accountPaymentData.setAccountActionDate(accountAction);
			LoanTrxnDetailEntity accountTrxnBO;
			try {
				accountTrxnBO = new LoanTrxnDetailEntity(accountPayment,
						loanPaymentData, paymentData.getPersonnel(),
						paymentData.getTransactionDate(),
						(AccountActionEntity) new MasterPersistence()
								.getPersistentObject(AccountActionEntity.class,
										AccountActionTypes.LOAN_REPAYMENT
												.getValue()), loanPaymentData
								.getAmountPaidWithFeeForInstallment(),
						"Payment rcvd.");
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}
			accountPayment.addAcountTrxn(accountTrxnBO);

			loanSummary.updatePaymentDetails(
					loanPaymentData.getPrincipalPaid(), loanPaymentData
							.getInterestPaid(), loanPaymentData
							.getPenaltyPaid().add(
									loanPaymentData.getMiscPenaltyPaid()),
					loanPaymentData.getFeeAmountPaidForInstallment().add(
							loanPaymentData.getMiscFeePaid()));

			performanceHistory.setNoOfPayments(getPerformanceHistory()
					.getNoOfPayments() + 1);
		}
		addLoanActivity(buildLoanActivity(accountPayment.getAccountTrxns(),
				paymentData.getPersonnel(), "Payment rcvd.", paymentData
						.getTransactionDate()));
		return accountPayment;
	}

	@Override
	protected Money getDueAmount(AccountActionDateEntity installment) {
		return ((LoanScheduleEntity) installment).getTotalDueWithFees();
	}

	@Override
	protected void updateInstallmentAfterAdjustment(
			List<AccountTrxnEntity> reversedTrxns) throws AccountException {
		if (null != reversedTrxns && reversedTrxns.size() > 0) {
			for (AccountTrxnEntity accntTrxn : reversedTrxns) {
				LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) accntTrxn;

				loanSummary.updatePaymentDetails(loanTrxn.getPrincipalAmount(),
						loanTrxn.getInterestAmount(), loanTrxn
								.getPenaltyAmount().add(
										loanTrxn.getMiscPenaltyAmount()),
						loanTrxn.getFeeAmount()
								.add(loanTrxn.getMiscFeeAmount()));
				LoanScheduleEntity accntActionDate = (LoanScheduleEntity) getAccountActionDate(loanTrxn
						.getInstallmentId());
				accntActionDate.updatePaymentDetails(loanTrxn
						.getPrincipalAmount(), loanTrxn.getInterestAmount(),
						loanTrxn.getPenaltyAmount(), loanTrxn
								.getMiscPenaltyAmount(), loanTrxn
								.getMiscFeeAmount());
				accntActionDate.setPaymentStatus(PaymentStatus.UNPAID
						.getValue());
				accntActionDate.setPaymentDate(null);
				if (null != accntActionDate.getAccountFeesActionDetails()
						&& accntActionDate.getAccountFeesActionDetails().size() > 0) {
					for (AccountFeesActionDetailEntity accntFeesAction : accntActionDate
							.getAccountFeesActionDetails()) {
						Money feeAmntAdjusted = loanTrxn.getFeesTrxn(
								accntFeesAction.getAccountFee()
										.getAccountFeeId()).getFeeAmount();
						accntFeesAction.setFeeAmountPaid(accntFeesAction
								.getFeeAmountPaid().add(feeAmntAdjusted));
					}
				}
			}
			PersonnelBO personnel;
			try {
				personnel = new PersonnelPersistence()
						.getPersonnel(getUserContext().getId());
			
			addLoanActivity(buildLoanActivity(reversedTrxns, personnel,
					"Loan Adjusted", DateUtils.getCurrentDateWithoutTimeStamp()));
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}
		}
	}

	@Override
	protected void regenerateFutureInstallments(Short nextIntallmentId)
			throws AccountException {
		if (!this.getAccountState().getId().equals(
				AccountStates.LOANACC_OBLIGATIONSMET)
				&& !this.getAccountState().getId().equals(
						AccountStates.LOANACC_WRITTENOFF)
				&& !this.getAccountState().getId().equals(
						AccountStates.LOANACC_CANCEL)) {
			SchedulerIntf scheduler;
			List<Date> meetingDates = null;
			try {
				scheduler = SchedulerHelper.getScheduler(getCustomer()
						.getCustomerMeeting().getMeeting());
				meetingDates = scheduler
						.getAllDates(getApplicableIdsForFutureInstallments()
								.size() + 1);
			} catch (SchedulerException e) {
				throw new AccountException(e);
			}
			meetingDates.remove(0);
			int count = 0;
			List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForFutureInstallments();
			for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
				accountActionDateEntity.setActionDate(new java.sql.Date(
						meetingDates.get(count++).getTime()));
			}
		}
	}

	protected final void roundInstallments(List<Short> installmentIdList) {
		if (!isPricipalAmountZero()) {
			LoanScheduleEntity lastAccountActionDate = (LoanScheduleEntity) getLastInstallmentAccountAction();
			Money diffAmount = new Money();
			int count = 0;
			for (AccountActionDateEntity accountActionDate : getAccountActionDates()) {
				LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
				if (installmentIdList.contains(loanScheduleEntity
						.getInstallmentId())) {
					if (isInterestDeductedAtDisbursement()
							&& loanScheduleEntity.getInstallmentId().equals(
									Short.valueOf("1")))
						continue;
					count++;
					if (count == installmentIdList.size()) {
						break;
					}
					Money totalAmount = loanScheduleEntity
							.getTotalDueWithFees();
					Money roundedTotalAmount = Money.round(totalAmount);
					loanScheduleEntity.setPrincipal(loanScheduleEntity
							.getPrincipal().subtract(
									totalAmount.subtract(roundedTotalAmount)));
					diffAmount = diffAmount.add(totalAmount
							.subtract(roundedTotalAmount));
				}
			}
			lastAccountActionDate.setPrincipal(lastAccountActionDate
					.getPrincipal().add(diffAmount));
		}
	}

	private Money getAccountFeeAmount(AccountFeesEntity accountFees,
			Money loanInterest) {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"FeeInstallmentGenerator:getAccountFeeAmount rate flat flag..");

		Money accountFeeAmount = new Money();
		Double feeAmount = accountFees.getFeeAmount();

		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"FeeInstallmentGenerator:getAccountFeeAmount feeAmount.."
						+ feeAmount);

		if (accountFees.getFees().getFeeType().equals(RateAmountFlag.AMOUNT)) {
			accountFeeAmount = new Money(feeAmount.toString());
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"FeeInstallmentGenerator:getAccountFeeAmount feeAmount Flat.."
							+ feeAmount);
		} else if (accountFees.getFees().getFeeType().equals(
				RateAmountFlag.RATE)) {
			RateFeeBO rateFeeBO = (RateFeeBO) new FeePersistence()
					.getRateFee(accountFees.getFees().getFeeId());
			accountFeeAmount = new Money(getRateBasedOnFormula(feeAmount,
					rateFeeBO.getFeeFormula(), loanInterest));
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"FeeInstallmentGenerator:getAccountFeeAmount feeAmount Formula.."
							+ feeAmount);
		}
		return accountFeeAmount;
	}

	@Override
	protected final List<FeeInstallment> handlePeriodic(
			AccountFeesEntity accountFees,
			List<InstallmentDate> installmentDates) throws AccountException {
		Money accountFeeAmount = accountFees.getAccountFeeAmount();
		MeetingBO feeMeetingFrequency = accountFees.getFees().getFeeFrequency()
				.getFeeMeetingFrequency();
		List<Date> feeDates = getFeeDates(feeMeetingFrequency, installmentDates);
		ListIterator<Date> feeDatesIterator = feeDates.listIterator();
		List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
		while (feeDatesIterator.hasNext()) {
			Date feeDate = feeDatesIterator.next();
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"FeeInstallmentGenerator:handlePeriodic date considered after removal.."
							+ feeDate);
			Short installmentId = getMatchingInstallmentId(installmentDates,
					feeDate);
			feeInstallmentList.add(buildFeeInstallment(installmentId,
					accountFeeAmount, accountFees));
		}
		return feeInstallmentList;
	}

	private LoanActivityEntity buildLoanActivity(
			Collection<AccountTrxnEntity> accountTrxnDetails,
			PersonnelBO personnel, String comments, Date trxnDate) {
		Date activityDate = trxnDate;
		Money principal = new Money();
		Money interest = new Money();
		Money penalty = new Money();
		Money fees = new Money();
		for (AccountTrxnEntity accountTrxn : accountTrxnDetails) {
			LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) accountTrxn;
			principal = principal
					.add(removeSign(loanTrxn.getPrincipalAmount()));
			interest = interest.add(removeSign(loanTrxn.getInterestAmount()));
			penalty = penalty.add(removeSign(loanTrxn.getPenaltyAmount())).add(
					removeSign(loanTrxn.getMiscPenaltyAmount()));
			fees = fees.add(removeSign(loanTrxn.getMiscFeeAmount()));
			for (FeesTrxnDetailEntity feesTrxn : loanTrxn.getFeesTrxnDetails()) {
				fees = fees.add(removeSign(feesTrxn.getFeeAmount()));
			}
		}
		return new LoanActivityEntity(this, personnel, comments, principal,
				loanSummary.getOriginalPrincipal().subtract(
						loanSummary.getPrincipalPaid()), interest, loanSummary
						.getOriginalInterest().subtract(
								loanSummary.getInterestPaid()), fees,
				loanSummary.getOriginalFees().subtract(
						loanSummary.getFeesPaid()), penalty, loanSummary
						.getOriginalPenalty().subtract(
								loanSummary.getPenaltyPaid()), activityDate);
	}

	private LoanActivityEntity buildLoanActivity(Money totalPrincipal,
			PersonnelBO personnel, String comments, Date trxnDate) {
		Date activityDate = trxnDate;
		Money principal = totalPrincipal;
		Money interest = new Money();
		Money penalty = new Money();
		Money fees = new Money();
		return new LoanActivityEntity(this, personnel, comments, principal,
				loanSummary.getOriginalPrincipal().subtract(
						loanSummary.getPrincipalPaid()), interest, loanSummary
						.getOriginalInterest().subtract(
								loanSummary.getInterestPaid()), fees,
				loanSummary.getOriginalFees().subtract(
						loanSummary.getFeesPaid()), penalty, loanSummary
						.getOriginalPenalty().subtract(
								loanSummary.getPenaltyPaid()), activityDate);
	}

	private Short getInstallmentSkipToStartRepayment() {
		if (isInterestDeductedAtDisbursement())
			return (short) 0;
		else
			return (short) (getGracePeriodDuration() + 1);
	}

	private String getRateBasedOnFormula(Double rate, FeeFormulaEntity formula,
			Money loanInterest) {
		Double amountToCalculateOn = 1.0;
		if (formula.getId().equals(FeeFormula.AMOUNT.getValue())) {
			amountToCalculateOn = loanAmount.getAmountDoubleValue();
		} else if (formula.getId().equals(
				FeeFormula.AMOUNT_AND_INTEREST.getValue())) {
			amountToCalculateOn = (loanAmount.add(loanInterest))
					.getAmountDoubleValue();
		} else if (formula.getId().equals(FeeFormula.INTEREST.getValue())) {
			amountToCalculateOn = loanInterest.getAmountDoubleValue();
		}
		Double rateAmount = (rate * amountToCalculateOn) / 100;
		return rateAmount.toString();
	}

	private void generateMeetingSchedule() throws AccountException {
		if (!isDisbursementDateValid())
			throw new AccountException(
					LoanExceptionConstants.INVALIDDISBURSEMENTDATE);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"RepamentSchedular:getRepaymentSchedule invoked ");
		List<InstallmentDate> installmentDates = getInstallmentDates(
				getLoanMeeting(), noOfInstallments,
				getInstallmentSkipToStartRepayment());
		MifosLogManager
				.getLogger(LoggerConstants.ACCOUNTSLOGGER)
				.debug(
						"RepamentSchedular:getRepaymentSchedule , installment dates obtained ");
		Money loanInterest = getLoanInterest(installmentDates.get(
				installmentDates.size() - 1).getInstallmentDueDate());
		List<EMIInstallment> EMIInstallments = generateEMI(loanInterest);
		MifosLogManager
				.getLogger(LoggerConstants.ACCOUNTSLOGGER)
				.debug(
						"RepamentSchedular:getRepaymentSchedule , emi installment  obtained ");
		validateSize(installmentDates, EMIInstallments);
		List<FeeInstallment> feeInstallment = new ArrayList<FeeInstallment>();
		if (getAccountFees().size() != 0) {
			populateAccountFeeAmount(getAccountFees(), loanInterest);
			feeInstallment = mergeFeeInstallments(getFeeInstallment(installmentDates));
		}
		MifosLogManager
				.getLogger(LoggerConstants.ACCOUNTSLOGGER)
				.debug(
						"RepamentSchedular:getRepaymentSchedule , fee installment obtained ");
		generateRepaymentSchedule(installmentDates, EMIInstallments,
				feeInstallment);
		MifosLogManager
				.getLogger(LoggerConstants.ACCOUNTSLOGGER)
				.debug(
						"RepamentSchedular:getRepaymentSchedule , repayment schedule generated  ");
		roundInstallments(installmentDates.get(installmentDates.size() - 1)
				.getInstallmentId());
	}

	private void populateAccountFeeAmount(Set<AccountFeesEntity> accountFees,
			Money loanInterest) {
		for (AccountFeesEntity accountFeesEntity : accountFees) {
			accountFeesEntity.setAccountFeeAmount(getAccountFeeAmount(
					accountFeesEntity, loanInterest));
		}
	}

	private void roundInstallments(Short lastInstallmentId) {
		if (!isPricipalAmountZero()) {
			Money diffAmount = new Money();
			AccountActionDateEntity lastAccountActionDate = null;
			for (AccountActionDateEntity accountActionDate : getAccountActionDates()) {
				LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
				if (loanScheduleEntity.getInstallmentId().equals(
						lastInstallmentId)) {
					lastAccountActionDate = loanScheduleEntity;
					continue;
				}
				Money totalAmount = loanScheduleEntity.getTotalDueWithFees();
				Money roundedTotalAmount = Money.round(totalAmount);
				loanScheduleEntity.setPrincipal(loanScheduleEntity
						.getPrincipal().subtract(
								totalAmount.subtract(roundedTotalAmount)));
				diffAmount = diffAmount.add(totalAmount
						.subtract(roundedTotalAmount));
			}
			((LoanScheduleEntity) lastAccountActionDate)
					.setPrincipal(((LoanScheduleEntity) lastAccountActionDate)
							.getPrincipal().add(diffAmount));
		}
	}

	private Boolean isPricipalAmountZero() {
		for (AccountActionDateEntity accountActionDate : getAccountActionDates()) {
			if (((LoanScheduleEntity) accountActionDate).getPrincipal()
					.getAmountDoubleValue() == 0.0)
				return true;
		}
		return false;
	}

	private Money getLoanInterest(Date installmentEndDate)
			throws AccountException {
		if (getLoanOffering().getInterestTypes().getId().equals(
				InterestTypeConstants.FLATINTERST.getValue()))
			return getFlatInterestAmount(installmentEndDate);
		return null;
	}

	private Money getFlatInterestAmount(Date installmentEndDate)
			throws AccountException {
		Double interestRate = getInterestRate();
		Double durationInYears = getTotalDurationInYears(installmentEndDate);
		Money interestRateM = new Money(Double.toString(interestRate));
		Money durationInYearsM = new Money(Double.toString(durationInYears));
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"FlatInterestCalculator:getInterest duration in years..."
						+ durationInYears);
		Money interest = getLoanAmount().multiply(
				interestRateM.multiply(durationInYearsM)).divide(
				new Money(Double.toString(100)));
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"FlatInterestCalculator:getInterest interest accumulated..."
						+ interest);
		return interest;
	}

	private double getTotalDurationInYears(Date installmentEndDate)
			throws AccountException {
		int interestDays = getInterestDays();
		int daysInWeek = getDaysInWeek();
		int daysInMonth = getDaysInMonth();
		String durationType = MeetingScheduleHelper.getReccurence(this
				.getLoanMeeting().getMeetingDetails().getRecurrenceType()
				.getRecurrenceId());
		int duration = getNoOfInstallments()
				* this.getLoanMeeting().getMeetingDetails().getRecurAfter();
		if (interestDays == InterestCalculatorConstants.INTEREST_DAYS_360) {
			if (durationType.equals(AccountConstants.WEEK_INSTALLMENT)) {
				double totalWeekDays = duration * daysInWeek;
				double durationInYears = totalWeekDays
						/ InterestCalculatorConstants.INTEREST_DAYS_360;
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER)
						.debug(
								"FlatInterestCalculator:getTotalDurationInYears total week days.."
										+ totalWeekDays);
				return durationInYears;
			} else if (durationType.equals(AccountConstants.MONTH_INSTALLMENT)) {
				double totalMonthDays = duration * daysInMonth;
				double durationInYears = totalMonthDays
						/ InterestCalculatorConstants.INTEREST_DAYS_360;
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER)
						.debug(
								"FlatInterestCalculator:getTotalDurationInYears total month days.."
										+ totalMonthDays);
				return durationInYears;
			}
			throw new AccountException(
					AccountConstants.NOT_SUPPORTED_DURATION_TYPE);
		} else if (interestDays == AccountConstants.INTEREST_DAYS_365) {
			if (durationType.equals(AccountConstants.WEEK_INSTALLMENT)) {
				MifosLogManager
						.getLogger(LoggerConstants.ACCOUNTSLOGGER)
						.debug(
								"FlatInterestCalculator:getTotalDurationInYears in interest week 365 days");
				double totalWeekDays = duration * daysInWeek;
				double durationInYears = totalWeekDays
						/ InterestCalculatorConstants.INTEREST_DAYS_365;
				return durationInYears;
			} else if (durationType.equals(AccountConstants.MONTH_INSTALLMENT)) {
				MifosLogManager
						.getLogger(LoggerConstants.ACCOUNTSLOGGER)
						.debug(
								"FlatInterestCalculator:getTotalDurationInYears in interest month 365 days");

				// will have to consider inc/dec time in some countries
				Long installmentStartTime = getDisbursementDate().getTime();
				Long installmentEndTime = installmentEndDate.getTime();
				Long diffTime = installmentEndTime - installmentStartTime;
				double daysDiff = diffTime / (1000 * 60 * 60 * 24);
				MifosLogManager
						.getLogger(LoggerConstants.ACCOUNTSLOGGER)
						.debug(
								"FlatInterestCalculator:getTotalDurationInYears start date..");
				MifosLogManager
						.getLogger(LoggerConstants.ACCOUNTSLOGGER)
						.debug(
								"FlatInterestCalculator:getTotalDurationInYears end date..");
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER)
						.debug(
								"FlatInterestCalculator:getTotalDurationInYears diff in days..."
										+ daysDiff);
				double durationInYears = daysDiff
						/ InterestCalculatorConstants.INTEREST_DAYS_365;
				return durationInYears;
			}
			throw new AccountException(
					AccountConstants.NOT_SUPPORTED_DURATION_TYPE);
		} else
			throw new AccountException(
					AccountConstants.NOT_SUPPORTED_INTEREST_DAYS);
	}

	// read from configuration
	private int getInterestDays() {
		return AccountConstants.INTEREST_DAYS;
	}

	// read from configuration
	private int getDaysInWeek() {
		return AccountConstants.DAYS_IN_WEEK;
	}

	// read from configuration
	private int getDaysInMonth() {
		return AccountConstants.DAYS_IN_MONTH;
	}

	private void generateRepaymentSchedule(
			List<InstallmentDate> installmentDates,
			List<EMIInstallment> EMIInstallments,
			List<FeeInstallment> feeInstallmentList) throws AccountException {
		int count = installmentDates.size();
		for (int i = 0; i < count; i++) {
			InstallmentDate installmentDate = installmentDates.get(i);
			EMIInstallment em = EMIInstallments.get(i);
			LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(
					this, getCustomer(), installmentDate.getInstallmentId(),
					new java.sql.Date(installmentDate.getInstallmentDueDate()
							.getTime()), PaymentStatus.UNPAID, em
							.getPrincipal(), em.getInterest());

			addAccountActionDate(loanScheduleEntity);
			for (FeeInstallment feeInstallment : feeInstallmentList) {
				if (feeInstallment.getInstallmentId() == installmentDate
						.getInstallmentId()
						&& !feeInstallment.getAccountFeesEntity().getFees()
								.isTimeOfDisbursement()) {
					LoanFeeScheduleEntity loanFeeScheduleEntity = new LoanFeeScheduleEntity(
							loanScheduleEntity, feeInstallment
									.getAccountFeesEntity().getFees(),
							feeInstallment.getAccountFeesEntity(),
							feeInstallment.getAccountFee());
					loanScheduleEntity
							.addAccountFeesAction(loanFeeScheduleEntity);
				} else if (feeInstallment.getInstallmentId() == installmentDate
						.getInstallmentId()
						&& isInterestDeductedAtDisbursement()
						&& feeInstallment.getAccountFeesEntity().getFees()
								.isTimeOfDisbursement()) {
					LoanFeeScheduleEntity loanFeeScheduleEntity = new LoanFeeScheduleEntity(
							loanScheduleEntity, feeInstallment
									.getAccountFeesEntity().getFees(),
							feeInstallment.getAccountFeesEntity(),
							feeInstallment.getAccountFee());
					loanScheduleEntity
							.addAccountFeesAction(loanFeeScheduleEntity);
				}
			}
		}
	}

	private void validateSize(List installmentDates, List EMIInstallments)
			throws AccountException {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"RepamentSchedular:validateSize : installment size  "
						+ installmentDates.size());
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"RepamentSchedular:validateSize : emi installment size  "
						+ EMIInstallments.size());
		if (installmentDates.size() != EMIInstallments.size())
			throw new AccountException(AccountConstants.DATES_MISMATCH);
	}

	private List<EMIInstallment> generateEMI(Money loanInterest)
			throws AccountException {
		if (isInterestDeductedAtDisbursement()
				&& !getLoanOffering().isPrinDueLastInst())
			return interestDeductedAtDisbursement(loanInterest);

		if (getLoanOffering().isPrinDueLastInst()
				&& !isInterestDeductedAtDisbursement())
			return principalInLastPayment(loanInterest);

		if (!getLoanOffering().isPrinDueLastInst()
				&& !isInterestDeductedAtDisbursement())
			return allInstallments(loanInterest);

		if (getLoanOffering().isPrinDueLastInst()
				&& isInterestDeductedAtDisbursement())
			return interestDeductedFirstPrincipalLast(loanInterest);

		throw new AccountException(
				AccountConstants.NOT_SUPPORTED_EMI_GENERATION);
	}

	private List<EMIInstallment> interestDeductedAtDisbursement(
			Money loanInterest) throws AccountException {
		List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
		// grace can only be none
		if (getGracePeriodType().getId().equals(
				GraceTypeConstants.GRACEONALLREPAYMENTS.getValue())
				|| getGracePeriodType().getId().equals(
						GraceTypeConstants.PRINCIPALONLYGRACE.getValue()))
			throw new AccountException(
					AccountConstants.INTERESTDEDUCTED_INVALIDGRACETYPE);

		if (getGracePeriodType().getId().equals(
				GraceTypeConstants.NONE.getValue())) {
			Money interestFirstInstallment = loanInterest;
			// principal starts only from the second installment
			Money principalPerInstallment = new Money(Double
					.toString(getLoanAmount().getAmountDoubleValue()
							/ (getNoOfInstallments() - 1)));
			EMIInstallment installment = new EMIInstallment();
			installment.setPrincipal(new Money());
			installment.setInterest(interestFirstInstallment);
			emiInstallments.add(installment);
			for (int i = 1; i < getNoOfInstallments(); i++) {
				installment = new EMIInstallment();
				installment.setPrincipal(principalPerInstallment);
				installment.setInterest(new Money());

				emiInstallments.add(installment);
			}
		}
		return emiInstallments;
	}

	private List<EMIInstallment> principalInLastPayment(Money loanInterest)
			throws AccountException {
		List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
		// grace can only be none
		if (getGracePeriodType().getId().equals(
				GraceTypeConstants.PRINCIPALONLYGRACE.getValue()))
			throw new AccountException(
					AccountConstants.PRINCIPALLASTPAYMENT_INVALIDGRACETYPE);
		if (getGracePeriodType().getId().equals(
				GraceTypeConstants.NONE.getValue())
				|| getGracePeriodType().getId().equals(
						GraceTypeConstants.GRACEONALLREPAYMENTS.getValue())) {
			Money principalLastInstallment = getLoanAmount();
			// principal starts only from the second installment
			Money interestPerInstallment = new Money(Double
					.toString(loanInterest.getAmountDoubleValue()
							/ getNoOfInstallments()));
			EMIInstallment installment = null;
			for (int i = 0; i < getNoOfInstallments() - 1; i++) {
				installment = new EMIInstallment();
				installment.setPrincipal(new Money());
				installment.setInterest(interestPerInstallment);
				emiInstallments.add(installment);
			}
			// principal set in the last installment
			installment = new EMIInstallment();
			installment.setPrincipal(principalLastInstallment);
			installment.setInterest(interestPerInstallment);
			emiInstallments.add(installment);
			return emiInstallments;
		}
		throw new AccountException(AccountConstants.NOT_SUPPORTED_GRACE_TYPE);
	}

	private List<EMIInstallment> allInstallments(Money loanInterest)
			throws AccountException {
		List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
		if (getGracePeriodType().getId().equals(
				GraceTypeConstants.GRACEONALLREPAYMENTS.getValue())
				|| getGracePeriodType().getId().equals(
						GraceTypeConstants.NONE.getValue())) {
			Money principalPerInstallment = new Money(Double
					.toString(getLoanAmount().getAmountDoubleValue()
							/ getNoOfInstallments()));
			Money interestPerInstallment = new Money(Double
					.toString(loanInterest.getAmountDoubleValue()
							/ getNoOfInstallments()));
			EMIInstallment installment = null;
			for (int i = 0; i < getNoOfInstallments(); i++) {
				installment = new EMIInstallment();
				installment.setPrincipal(principalPerInstallment);
				installment.setInterest(interestPerInstallment);
				emiInstallments.add(installment);
			}
			return emiInstallments;
		}

		if (getGracePeriodType().getId().equals(
				GraceTypeConstants.PRINCIPALONLYGRACE.getValue())) {
			Money principalPerInstallment = new Money(
					Double
							.toString(getLoanAmount().getAmountDoubleValue()
									/ (getNoOfInstallments() - getGracePeriodDuration())));
			Money interestPerInstallment = new Money(Double
					.toString(loanInterest.getAmountDoubleValue()
							/ getNoOfInstallments()));
			EMIInstallment installment = null;
			for (int i = 0; i < getGracePeriodDuration(); i++) {
				installment = new EMIInstallment();
				installment.setPrincipal(new Money());
				installment.setInterest(interestPerInstallment);
				emiInstallments.add(installment);
			}
			for (int i = getGracePeriodDuration(); i < getNoOfInstallments(); i++) {
				installment = new EMIInstallment();
				installment.setPrincipal(principalPerInstallment);
				installment.setInterest(interestPerInstallment);
				emiInstallments.add(installment);
			}
			return emiInstallments;
		}
		throw new AccountException(AccountConstants.NOT_SUPPORTED_GRACE_TYPE);
	}

	private List<EMIInstallment> interestDeductedFirstPrincipalLast(
			Money loanInterest) throws AccountException {
		List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
		if (getGracePeriodType().getId().equals(
				GraceTypeConstants.GRACEONALLREPAYMENTS.getValue())
				|| getGracePeriodType().getId().equals(
						GraceTypeConstants.PRINCIPALONLYGRACE.getValue()))
			throw new AccountException(
					AccountConstants.INTERESTDEDUCTED_PRINCIPALLAST);
		if (getGracePeriodType().getId().equals(
				GraceTypeConstants.NONE.getValue())) {
			Money principalLastInstallment = getLoanAmount();
			Money interestFirstInstallment = loanInterest;

			EMIInstallment installment = null;
			installment = new EMIInstallment();
			installment.setPrincipal(new Money());
			installment.setInterest(interestFirstInstallment);
			emiInstallments.add(installment);
			for (int i = 1; i < getNoOfInstallments() - 1; i++) {
				installment = new EMIInstallment();
				installment.setPrincipal(new Money());
				installment.setInterest(new Money());
				emiInstallments.add(installment);
			}
			installment = new EMIInstallment();
			installment.setPrincipal(principalLastInstallment);
			installment.setInterest(new Money());
			emiInstallments.add(installment);
			return emiInstallments;
		}
		throw new AccountException(AccountConstants.NOT_SUPPORTED_GRACE_TYPE);
	}

	private Boolean isDisbursementDateValid() throws AccountException {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"RepamentSchedular:isDisbursementDateValid invoked ");
		SchedulerIntf scheduler;
		Boolean isValid = false;
		try {
			scheduler = MeetingScheduleHelper.getSchedulerObject(
					convertMeeting(this.getCustomer().getCustomerMeeting()
							.getMeeting()), true);
			isValid = scheduler.isValidScheduleDate(disbursementDate);
		} catch (ApplicationException e) {
			e.printStackTrace();
			throw new AccountException(e);
		}
		return isValid;
	}

	private boolean isDisbursementDateLessThanCurrentDate(Date disbursementDate) {
		if (DateUtils.getDateWithoutTimeStamp(disbursementDate.getTime())
				.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) < 0)
			return true;
		return false;
	}

	private void applyPeriodicFee(FeeBO fee, Double charge,
			List<AccountActionDateEntity> dueInstallments)
			throws AccountException {
		AccountFeesEntity accountFee = getAccountFee(fee, charge);
		Set<AccountFeesEntity> accountFeeSet = new HashSet<AccountFeesEntity>();
		accountFeeSet.add(accountFee);
		populateAccountFeeAmount(accountFeeSet, loanSummary
				.getOriginalInterest());
		List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
		for (AccountActionDateEntity accountActionDateEntity : dueInstallments)
			installmentDates.add(new InstallmentDate(accountActionDateEntity
					.getInstallmentId(), accountActionDateEntity
					.getActionDate()));
		List<FeeInstallment> feeInstallmentList = mergeFeeInstallments(handlePeriodic(
				accountFee, installmentDates));
		Money totalFeeAmountApplied = applyFeeToInstallments(
				feeInstallmentList, dueInstallments);
		updateLoanSummary(fee.getFeeId(), totalFeeAmountApplied);
		updateLoanActivity(fee.getFeeId(), totalFeeAmountApplied, fee
				.getFeeName()
				+ " applied");
	}

	private void applyOneTimeFee(FeeBO fee, Double charge,
			AccountActionDateEntity accountActionDateEntity)
			throws AccountException {
		LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
		AccountFeesEntity accountFee = new AccountFeesEntity(this, fee, charge,
				FeeStatus.ACTIVE.getValue(), new Date(System
						.currentTimeMillis()), null);
		Set<AccountFeesEntity> accountFeeSet = new HashSet<AccountFeesEntity>();
		accountFeeSet.add(accountFee);
		populateAccountFeeAmount(accountFeeSet, loanSummary
				.getOriginalInterest());
		List<AccountActionDateEntity> loanScheduleEntityList = new ArrayList<AccountActionDateEntity>();
		loanScheduleEntityList.add(loanScheduleEntity);
		List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
		installmentDates.add(new InstallmentDate(accountActionDateEntity
				.getInstallmentId(), accountActionDateEntity.getActionDate()));
		List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
		feeInstallmentList.add(handleOneTime(accountFee, installmentDates));
		Money totalFeeAmountApplied = applyFeeToInstallments(
				feeInstallmentList, loanScheduleEntityList);
		filterTimeOfDisbursementFees(loanScheduleEntity, fee);
		updateLoanSummary(fee.getFeeId(), totalFeeAmountApplied);
		updateLoanActivity(fee.getFeeId(), totalFeeAmountApplied, fee
				.getFeeName()
				+ " applied");
	}

	private void applyMiscCharge(Short chargeType, Money charge,
			AccountActionDateEntity accountActionDateEntity)
			throws AccountException {
		LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
		loanScheduleEntity.applyMiscCharge(chargeType, charge);
		updateLoanSummary(chargeType, charge);
		updateLoanActivity(chargeType, charge, "");
	}

	private void updateLoanSummary(Short chargeType, Money charge) {
		if (chargeType != null
				&& chargeType.equals(Short
						.valueOf(AccountConstants.MISC_PENALTY)))
			getLoanSummary().updateOriginalPenalty(charge);
		else
			getLoanSummary().updateOriginalFees(charge);
	}

	private void updateLoanActivity(Short chargeType, Money charge,
			String comments) throws AccountException {
		try {
			PersonnelBO personnel = new PersonnelPersistence()
					.getPersonnel(getUserContext().getId());
			LoanActivityEntity loanActivityEntity = null;
			if (chargeType != null
					&& chargeType.equals(Short
							.valueOf(AccountConstants.MISC_PENALTY)))
				loanActivityEntity = new LoanActivityEntity(this, personnel,
						new Money(), new Money(), new Money(), charge,
						getLoanSummary(), AccountConstants.MISC_PENALTY_APPLIED);
			else if (chargeType != null
					&& chargeType.equals(Short
							.valueOf(AccountConstants.MISC_FEES)))
				loanActivityEntity = new LoanActivityEntity(this, personnel,
						new Money(), new Money(), charge, new Money(),
						getLoanSummary(), AccountConstants.MISC_FEES_APPLIED);
			else
				loanActivityEntity = new LoanActivityEntity(this, personnel,
						new Money(), new Money(), charge, new Money(),
						getLoanSummary(), comments);
			addLoanActivity(loanActivityEntity);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	private Money applyFeeToInstallments(
			List<FeeInstallment> feeInstallmentList,
			List<AccountActionDateEntity> accountActionDateList) {
		Date lastAppliedDate = null;
		Money totalFeeAmountApplied = new Money();
		AccountFeesEntity accountFeesEntity = null;
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
			for (FeeInstallment feeInstallment : feeInstallmentList)
				if (feeInstallment.getInstallmentId().equals(
						loanScheduleEntity.getInstallmentId())) {
					lastAppliedDate = loanScheduleEntity.getActionDate();
					totalFeeAmountApplied = totalFeeAmountApplied
							.add(feeInstallment.getAccountFee());
					AccountFeesActionDetailEntity accountFeesActionDetailEntity = new LoanFeeScheduleEntity(
							loanScheduleEntity, feeInstallment
									.getAccountFeesEntity().getFees(),
							feeInstallment.getAccountFeesEntity(),
							feeInstallment.getAccountFee());
					loanScheduleEntity
							.addAccountFeesAction(accountFeesActionDetailEntity);
					accountFeesEntity = feeInstallment.getAccountFeesEntity();
				}
		}
		accountFeesEntity.setLastAppliedDate(lastAppliedDate);
		addAccountFees(accountFeesEntity);
		return totalFeeAmountApplied;
	}

	private void filterTimeOfDisbursementFees(
			LoanScheduleEntity loanScheduleEntity, FeeBO fee) {
		Short paymentType = fee.getFeeFrequency().getFeePayment().getId();
		if (paymentType.equals(FeePayment.TIME_OF_DISBURSMENT.getValue())
				&& !isInterestDeductedAtDisbursement()) {
			Set<AccountFeesActionDetailEntity> accountFeesDetailSet = loanScheduleEntity
					.getAccountFeesActionDetails();
			for (Iterator<AccountFeesActionDetailEntity> iter = accountFeesDetailSet
					.iterator(); iter.hasNext();) {
				AccountFeesActionDetailEntity accountFeesActionDetailEntity = iter
						.next();
				if (fee.equals(accountFeesActionDetailEntity.getFee())) {
					iter.remove();
				}
			}
		}
	}

	private MeetingBO buildLoanMeeting(MeetingBO customerMeeting,
			MeetingBO loanOfferingMeeting, Date disbursementDate)
			throws AccountException {
		if (customerMeeting != null
				&& loanOfferingMeeting != null
				&& customerMeeting.getMeetingDetails().getRecurrenceType()
						.getRecurrenceId().equals(
								loanOfferingMeeting.getMeetingDetails()
										.getRecurrenceType().getRecurrenceId())
				&& isMultiple(loanOfferingMeeting.getMeetingDetails()
						.getRecurAfter(), customerMeeting.getMeetingDetails()
						.getRecurAfter())) {

			RecurrenceType meetingFrequency = RecurrenceType
					.getRecurrenceType(customerMeeting.getMeetingDetails()
							.getRecurrenceType().getRecurrenceId());
			MeetingType meetingType = MeetingType
					.getMeetingType(customerMeeting.getMeetingType()
							.getMeetingTypeId());
			Short recurAfter = loanOfferingMeeting.getMeetingDetails()
					.getRecurAfter();
			MeetingBO meetingToReturn = null;

			if (meetingFrequency.equals(RecurrenceType.MONTHLY)) {
				if (customerMeeting.isMonthlyOnDate())
					meetingToReturn = new MeetingBO(customerMeeting
							.getMeetingDetails().getDayNumber(), recurAfter,
							disbursementDate, meetingType);
				else
					meetingToReturn = new MeetingBO(customerMeeting
							.getMeetingDetails().getWeekDay(), customerMeeting
							.getMeetingDetails().getWeekRank(), recurAfter,
							disbursementDate, meetingType);
			} else if (meetingFrequency.equals(RecurrenceType.WEEKLY))
				meetingToReturn = new MeetingBO(customerMeeting
						.getMeetingDetails().getMeetingRecurrence()
						.getWeekDayValue(), recurAfter, disbursementDate,
						meetingType);
			else
				meetingToReturn = new MeetingBO(meetingFrequency, recurAfter,
						disbursementDate, meetingType);

			return meetingToReturn;
		} else {
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTION);
		}
	}

	private boolean isMultiple(Short valueToBeChecked,
			Short valueToBeCheckedWith) {
		return valueToBeChecked % valueToBeCheckedWith == 0;
	}

	private LoanSummaryEntity buildLoanSummary() {
		Money interest = new Money();
		Money fees = new Money();
		Set<AccountActionDateEntity> actionDates = getAccountActionDates();
		if (actionDates != null && actionDates.size() > 0) {
			for (AccountActionDateEntity accountActionDate : actionDates) {
				LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDate;
				interest = interest.add(loanSchedule.getInterest());
				fees = fees.add(loanSchedule.getTotalFees());
			}
		}
		fees = fees.add(getDisbursementFeeAmount());
		return new LoanSummaryEntity(this, loanAmount, interest, fees);
	}

	private Money getDisbursementFeeAmount() {
		Money fees = new Money();
		for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
			if (!isInterestDeductedAtDisbursement()
					&& accountFeesEntity.getFees().isTimeOfDisbursement()) {
				fees = fees.add(accountFeesEntity.getAccountFeeAmount());
			}
		}
		return fees;
	}

	private void validate(LoanOfferingBO loanOffering, Money loanAmount,
			Short noOfinstallments, Date disbursementDate, Double interestRate,
			Short gracePeriodDuration, Fund fund, CustomerBO customer)
			throws AccountException {
		if (loanOffering == null || loanAmount == null
				|| noOfinstallments == null || disbursementDate == null
				|| interestRate == null)
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTION);

		if (!customer.isActive())
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTION);
		if (!loanOffering.getPrdStatus().getOfferingStatusId().equals(
				PrdStatus.LOANACTIVE.getValue()))
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTION);
		if (isDisbursementDateLessThanCurrentDate(disbursementDate))
			throw new AccountException(
					LoanExceptionConstants.INVALIDDISBURSEMENTDATE);
	}

	private void buildAccountFee(List<FeeView> feeViews) {
		if (feeViews != null && feeViews.size() > 0) {
			for (FeeView feeView : feeViews) {
				FeeBO fee = new FeePersistence()
						.getFee(feeView.getFeeIdValue());
				this.addAccountFees(new AccountFeesEntity(this, fee, feeView
						.getAmountMoney()));
			}
		}
	}

	private void setGracePeriodTypeAndDuration(
			boolean interestDeductedAtDisbursement, Short gracePeriodDuration)
			throws AccountException {
		if (interestDeductedAtDisbursement) {
			this.gracePeriodType = new GracePeriodTypeEntity(
					GraceTypeConstants.NONE);
			this.gracePeriodDuration = (short) 0;
		} else {
			if (!loanOffering.getGracePeriodType().getId().equals(
					GraceTypeConstants.NONE.getValue()))
				if (gracePeriodDuration == null
						|| gracePeriodDuration >= loanOffering
								.getMaxNoInstallments())
					throw new AccountException(
							AccountExceptionConstants.CREATEEXCEPTION);
			this.gracePeriodType = loanOffering.getGracePeriodType();
			this.gracePeriodDuration = gracePeriodDuration;
		}
	}

	private Integer getNoOfBackDatedPayments() {
		int noOfMissedPayments = 0;
		for (AccountPaymentEntity accountPaymentEntity : getAccountPayments()) {
			Set<AccountTrxnEntity> accountTrxnEntityList = accountPaymentEntity
					.getAccountTrxns();
			for (AccountTrxnEntity accountTrxnEntity : accountTrxnEntityList) {
				if (accountTrxnEntity.getAccountActionEntity().getId().equals(
						AccountConstants.ACTION_LOAN_REPAYMENT)
						&& DateUtils
								.getDateWithoutTimeStamp(
										accountTrxnEntity.getActionDate()
												.getTime())
								.compareTo(
										DateUtils
												.getDateWithoutTimeStamp(accountTrxnEntity
														.getDueDate().getTime())) > 0) {
					noOfMissedPayments++;
				}
				if (accountTrxnEntity.getAccountActionEntity().getId().equals(
						AccountConstants.ACTION_LOAN_ADJUSTMENT)
						&& DateUtils
								.getDateWithoutTimeStamp(
										accountTrxnEntity.getRelatedTrxn()
												.getActionDate().getTime())
								.compareTo(
										DateUtils
												.getDateWithoutTimeStamp(accountTrxnEntity
														.getRelatedTrxn()
														.getDueDate().getTime())) > 0) {
					noOfMissedPayments--;
				}
			}
		}
		return noOfMissedPayments;
	}

	private void updateCustomerHistoryOnLastInstlPayment(Money totalAmount) {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerConstants.CLIENT_LEVEL_ID))
				&& getCustomer().getPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			clientPerfHistory.setLastLoanAmount(totalAmount);
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() - 1);
		}
	}

	private void updateCustomerHistoryOnPayment() {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerConstants.CLIENT_LEVEL_ID))
				&& getCustomer().getPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() - 1);
		}
	}

	private void updateCustomerHistoryOnDisbursement(Money disburseAmount) {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerConstants.CLIENT_LEVEL_ID))
				&& getCustomer().getPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() + 1);
			clientPerfHistory.setLoanCycleNumber(clientPerfHistory
					.getLoanCycleNumber() + 1);
		} else if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerConstants.GROUP_LEVEL_ID))
				&& getCustomer().getPerformanceHistory() != null) {
			GroupPerformanceHistoryEntity groupPerformanceHistoryEntity = (GroupPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			groupPerformanceHistoryEntity
					.setLastGroupLoanAmount(disburseAmount);
		}
	}

	private void updateCustomerHistoryOnRepayment(Money totalAmount) {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerConstants.CLIENT_LEVEL_ID))
				&& getCustomer().getPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			clientPerfHistory.setLastLoanAmount(totalAmount);
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() - 1);
		}
	}

	private void updateCustomerHistoryOnArrears() {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerConstants.CLIENT_LEVEL_ID))
				&& getCustomer().getPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() + 1);
		}
	}

	private void updateCustomerHistoryOnWriteOff() {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerConstants.CLIENT_LEVEL_ID))
				&& getCustomer().getPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			clientPerfHistory.setLoanCycleNumber(clientPerfHistory
					.getLoanCycleNumber() - 1);
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() - 1);
		}
	}

	private void regeneratePaymentSchedule() throws AccountException {
		Money miscFee = getMiscFee();
		Money miscPenalty = getMiscPenalty();
		try {
			new LoanPersistance().deleteInstallments(this
					.getAccountActionDates());
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
		this.resetAccountActionDates();
		Calendar date = new GregorianCalendar();
		date.setTime(disbursementDate);
		loanMeeting.setMeetingStartDate(date);
		generateMeetingSchedule();
		LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) getAccountActionDate(Short
				.valueOf("1"));
		loanScheduleEntity.setMiscFee(miscFee);
		loanScheduleEntity.setMiscPenalty(miscPenalty);
		Money interest = new Money();
		Money fees = new Money();
		Money penalty = new Money();
		Money principal = new Money();
		Set<AccountActionDateEntity> actionDates = getAccountActionDates();
		if (actionDates != null && actionDates.size() > 0) {
			for (AccountActionDateEntity accountActionDate : actionDates) {
				LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDate;
				principal = principal.add(loanSchedule.getPrincipal());
				interest = interest.add(loanSchedule.getInterest());
				fees = fees.add(loanSchedule.getTotalFees());
				penalty = penalty.add(loanSchedule.getTotalPenalty());
			}
		}
		fees = fees.add(getDisbursementFeeAmount());
		loanSummary.setOriginalInterest(interest);
		loanSummary.setOriginalFees(fees);
		loanSummary.setOriginalPenalty(penalty);
	}

	private AccountPaymentEntity payInterestAtDisbursement(String recieptNum,
			Date transactionDate, Short paymentTypeId, PersonnelBO personnel,
			Date receiptDate) throws AccountException {
		AccountActionDateEntity firstInstallment = null;
		for (AccountActionDateEntity accountActionDate : this
				.getAccountActionDates()) {
			if (accountActionDate.getInstallmentId().shortValue() == 1) {
				firstInstallment = accountActionDate;
				break;
			}
		}
		List<AccountActionDateEntity> installmentsToBePaid = new ArrayList<AccountActionDateEntity>();
		installmentsToBePaid.add(firstInstallment);

		PaymentData paymentData = getLoanAccountPaymentData(
				((LoanScheduleEntity) firstInstallment).getTotalDueWithFees(),
				installmentsToBePaid, personnel, recieptNum, paymentTypeId,
				receiptDate, transactionDate);

		return makePayment(paymentData);

	}

	private AccountActionDateEntity getLastInstallmentAccountAction() {
		AccountActionDateEntity nextAccountAction = null;
		if (getAccountActionDates() != null
				&& getAccountActionDates().size() > 0) {
			for (AccountActionDateEntity accountAction : getAccountActionDates()) {
				if (null == nextAccountAction)
					nextAccountAction = accountAction;
				else if (nextAccountAction.getInstallmentId() < accountAction
						.getInstallmentId())
					nextAccountAction = accountAction;
			}
		}
		return nextAccountAction;
	}

	private Money getMiscFee() {
		Money miscFee = new Money();
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
			if (loanSchedule.getMiscFee() != null) {
				miscFee = miscFee.add(loanSchedule.getMiscFee());
			}
		}
		return miscFee;
	}

	private Money getMiscPenalty() {
		Money miscPenalty = new Money();
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
			if (loanSchedule.getMiscPenalty() != null) {
				miscPenalty = miscPenalty.add(loanSchedule.getMiscPenalty());
			}
		}
		return miscPenalty;
	}

	private List<AccountActionDateEntity> getListOfUnpaidInstallments() {
		List<AccountActionDateEntity> unpaidInstallmentList = new ArrayList<AccountActionDateEntity>();
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			if (accountActionDateEntity.getPaymentStatus().equals(
					PaymentStatus.UNPAID.getValue())) {
				unpaidInstallmentList.add(accountActionDateEntity);
			}
		}
		return unpaidInstallmentList;
	}

	private Money getEarlyClosureAmount() {
		Money amount = new Money();
		for (AccountActionDateEntity accountActionDateEntity : getListOfUnpaidInstallments()) {
			amount = amount.add(((LoanScheduleEntity) accountActionDateEntity)
					.getPrincipal());
		}
		return amount;
	}

	private AccountPaymentEntity insertOnlyFeeAtDisbursement(String recieptNum,
			Date recieptDate, Short paymentTypeId, PersonnelBO personnel)
			throws AccountException {

		Money totalPayment = new Money();
		for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
			if (accountFeesEntity.isTimeOfDisbursement()) {
				totalPayment = totalPayment.add(accountFeesEntity
						.getAccountFeeAmount());
			}
		}

		loanSummary.updateFeePaid(totalPayment);

		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(
				this, totalPayment, recieptNum, recieptDate,
				new PaymentTypeEntity(paymentTypeId));

		LoanTrxnDetailEntity loanTrxnDetailEntity = null;
		try {
			loanTrxnDetailEntity = new LoanTrxnDetailEntity(
					accountPaymentEntity, recieptDate,
					(AccountActionEntity) new MasterPersistence()
							.getPersistentObject(AccountActionEntity.class,
									AccountConstants.ACTION_FEE_REPAYMENT),
					personnel, "-", Short.valueOf("0"), totalPayment,
					getAccountFees());
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}

		accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);

		addLoanActivity(buildLoanActivity(accountPaymentEntity
				.getAccountTrxns(), personnel, "Payment rcvd.", recieptDate));
		return accountPaymentEntity;
	}

	private PaymentData getLoanAccountPaymentData(Money totalAmount,
			List<AccountActionDateEntity> accountActions,
			PersonnelBO personnel, String recieptId, Short paymentId,
			Date receiptDate, Date transactionDate) {
		PaymentData paymentData = new PaymentData(totalAmount, personnel,
				paymentId, transactionDate);
		paymentData.setRecieptDate(receiptDate);
		paymentData.setRecieptNum(recieptId);
		return paymentData;
	}

	private LoanPaymentTypes getLoanPaymentType(Money amount) {
		if (amount.getAmountDoubleValue() == getTotalPaymentDue()
				.getAmountDoubleValue())
			return LoanPaymentTypes.FULL_PAYMENT;
		else if (amount.getAmountDoubleValue() < getTotalPaymentDue()
				.getAmountDoubleValue())
			return LoanPaymentTypes.PARTIAL_PAYMENT;
		else if (amount.getAmountDoubleValue() > getTotalPaymentDue()
				.getAmountDoubleValue()
				&& amount.getAmountDoubleValue() <= getTotalRepayableAmount()
						.getAmountDoubleValue())
			return LoanPaymentTypes.FUTURE_PAYMENT;
		return null;
	}

	private void handlePartialPayment(PaymentData paymentData) {
		Money totalAmount = paymentData.getTotalAmount();
		for (AccountActionDateEntity accountActionDate : getDetailsOfInstallmentsInArrears()) {
			if (totalAmount.getAmountDoubleValue() > 0.0) {
				LoanPaymentData loanPayment = new LoanPaymentData(
						accountActionDate, totalAmount);
				paymentData.addAccountPaymentData(loanPayment);
				totalAmount = totalAmount.subtract(loanPayment
						.getAmountPaidWithFeeForInstallment());
			}
		}
		AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
		if (nextInstallment != null
				&& nextInstallment.getPaymentStatus().equals(
						PaymentStatus.UNPAID.getValue())
				&& DateUtils.getDateWithoutTimeStamp(
						nextInstallment.getActionDate().getTime()).equals(
						DateUtils.getCurrentDateWithoutTimeStamp()))
			paymentData.addAccountPaymentData(new LoanPaymentData(
					nextInstallment, totalAmount));
	}

	private void handleFullPayment(PaymentData paymentData) {
		for (AccountActionDateEntity accountActionDate : getDetailsOfInstallmentsInArrears()) {
			paymentData.addAccountPaymentData(new LoanPaymentData(
					accountActionDate));
		}
		AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
		if (nextInstallment != null
				&& nextInstallment.getPaymentStatus().equals(
						PaymentStatus.UNPAID.getValue())
				&& DateUtils.getDateWithoutTimeStamp(
						nextInstallment.getActionDate().getTime()).equals(
						DateUtils.getCurrentDateWithoutTimeStamp()))
			paymentData.addAccountPaymentData(new LoanPaymentData(
					nextInstallment));
	}

	private void handleFuturePayment(PaymentData paymentData) {
		Money totalAmount = paymentData.getTotalAmount();
		for (AccountActionDateEntity accountActionDate : getDetailsOfInstallmentsInArrears()) {
			LoanPaymentData loanPayment = new LoanPaymentData(accountActionDate);
			paymentData.addAccountPaymentData(loanPayment);
			totalAmount = totalAmount.subtract(loanPayment
					.getAmountPaidWithFeeForInstallment());
		}
		AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
		if (nextInstallment != null
				&& nextInstallment.getPaymentStatus().equals(
						PaymentStatus.UNPAID.getValue())
				&& totalAmount.getAmountDoubleValue() > 0.0) {
			LoanPaymentData loanPayment;
			if (DateUtils.getDateWithoutTimeStamp(
					nextInstallment.getActionDate().getTime()).equals(
					DateUtils.getCurrentDateWithoutTimeStamp()))
				loanPayment = new LoanPaymentData(nextInstallment);
			else
				loanPayment = new LoanPaymentData(nextInstallment, totalAmount);
			paymentData.addAccountPaymentData(loanPayment);
			totalAmount = totalAmount.subtract(loanPayment
					.getAmountPaidWithFeeForInstallment());
		}
		for (AccountActionDateEntity accountActionDate : getApplicableIdsForFutureInstallments()) {
			if (totalAmount.getAmountDoubleValue() > 0.0) {
				LoanPaymentData loanPayment = new LoanPaymentData(
						accountActionDate, totalAmount);
				paymentData.addAccountPaymentData(loanPayment);
				totalAmount = totalAmount.subtract(loanPayment
						.getAmountPaidWithFeeForInstallment());
			}
		}
	}

	private void changeLoanStatus(AccountState newAccountState,
			PersonnelBO personnel) throws AccountException {
		AccountStateEntity accountState = this.getAccountState();
		try {
			setAccountState((AccountStateEntity) (new MasterPersistence())
					.getPersistentObject(AccountStateEntity.class,
							newAccountState.getValue()));
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
		this
				.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
						accountState, this.getAccountState(), personnel));
	}

	private Money getTotalRepayableAmount() {
		Money amount = new Money();
		for (AccountActionDateEntity accountActionDateEntity : getApplicableIdsForDueInstallments()) {
			amount = amount.add(((LoanScheduleEntity) accountActionDateEntity)
					.getTotalDueWithFees());
		}
		for (AccountActionDateEntity accountActionDateEntity : getApplicableIdsForFutureInstallments()) {
			amount = amount.add(((LoanScheduleEntity) accountActionDateEntity)
					.getTotalDueWithFees());
		}
		return amount;
	}

	private boolean isAdjustmentForInterestDedAtDisb(Short installmentId) {
		return installmentId.equals(Short.valueOf("1"))
				&& isInterestDeductedAtDisbursement();
	}

	private void makeEarlyRepaymentForDueInstallments(
			AccountPaymentEntity accountPaymentEntity) throws AccountException {
		MasterPersistence masterPersistence = new MasterPersistence();
		List<AccountActionDateEntity> dueInstallmentsList = getApplicableIdsForDueInstallments();
		for (AccountActionDateEntity accountActionDateEntity : dueInstallmentsList) {
			LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
			Money principal = loanSchedule.getPrincipalDue();
			Money interest = loanSchedule.getInterestDue();
			Money fees = loanSchedule.getTotalFeeDueWithMiscFeeDue();
			Money penalty = loanSchedule.getPenaltyDue();
			Money totalAmt = principal.add(interest).add(fees).add(penalty);

			LoanTrxnDetailEntity loanTrxnDetailEntity;
			try {
				loanTrxnDetailEntity = new LoanTrxnDetailEntity(
						accountPaymentEntity,
						(AccountActionEntity) masterPersistence
								.getPersistentObject(AccountActionEntity.class,
										AccountConstants.ACTION_LOAN_REPAYMENT),
						loanSchedule.getInstallmentId(), loanSchedule
								.getActionDate(), personnel, new Date(System
								.currentTimeMillis()), totalAmt,
						"Payment rcvd.", null, principal, interest,
						loanSchedule.getPenalty().subtract(
								loanSchedule.getPenaltyPaid()), loanSchedule
								.getMiscFeeDue(), loanSchedule
								.getMiscPenaltyDue());
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}
			for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : loanSchedule
					.getAccountFeesActionDetails()) {
				if (accountFeesActionDetailEntity.getFeeDue()
						.getAmountDoubleValue() > 0) {
					FeesTrxnDetailEntity feesTrxnDetailEntity = new FeesTrxnDetailEntity(
							loanTrxnDetailEntity, accountFeesActionDetailEntity
									.getAccountFee(),
							accountFeesActionDetailEntity.getFeeDue());
					loanTrxnDetailEntity
							.addFeesTrxnDetail(feesTrxnDetailEntity);
				}
			}

			accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);

			loanSchedule
					.makeEarlyRepaymentEnteries(LoanConstants.PAY_FEES_PENALTY_INTEREST);

			loanSummary
					.updatePaymentDetails(principal, interest, penalty, fees);
		}
	}

	private void makeEarlyRepaymentForFutureInstallments(
			AccountPaymentEntity accountPaymentEntity) throws AccountException {
		MasterPersistence masterPersistence = new MasterPersistence();
		List<AccountActionDateEntity> futureInstallmentsList = getApplicableIdsForFutureInstallments();
		for (AccountActionDateEntity accountActionDateEntity : futureInstallmentsList) {
			LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
			Money principal = loanSchedule.getPrincipalDue();
			Money interest = loanSchedule.getInterestDue();
			Money fees = loanSchedule.getTotalFeeDueWithMiscFeeDue();
			Money penalty = loanSchedule.getPenaltyDue();

			LoanTrxnDetailEntity loanTrxnDetailEntity;
			try {
				loanTrxnDetailEntity = new LoanTrxnDetailEntity(
						accountPaymentEntity,
						(AccountActionEntity) masterPersistence
								.getPersistentObject(AccountActionEntity.class,
										AccountConstants.ACTION_LOAN_REPAYMENT),
						loanSchedule.getInstallmentId(), loanSchedule
								.getActionDate(), personnel, new Date(System
								.currentTimeMillis()), principal,
						"Payment rcvd.", null, principal, new Money(),
						new Money(), new Money(), new Money());
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}

			accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);

			loanSchedule
					.makeEarlyRepaymentEnteries(LoanConstants.DONOT_PAY_FEES_PENALTY_INTEREST);

			loanSummary.decreaseBy(null, interest, penalty, fees);
			loanSummary.updatePaymentDetails(principal, null, null, null);

		}

	}
}
