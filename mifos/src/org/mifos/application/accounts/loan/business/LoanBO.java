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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateFlagEntity;
import org.mifos.application.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.loan.exceptions.LoanExceptionConstants;
import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.valueobjects.LoanPerfHistory;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountPaymentData;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.accounts.util.helpers.OverDueAmounts;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.master.util.valueobjects.CollateralType;
import org.mifos.application.master.util.valueobjects.InterestTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.valueobjects.GracePeriodType;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.repaymentschedule.RepaymentSchedule;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleFactory;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleHelper;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleIfc;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleInputsIfc;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class LoanBO extends AccountBO {

	public LoanBO() {
		loanPersistance = new LoanPersistance();
		loanInstallments = new ArrayList();
		loanActivityDetails = new HashSet<LoanActivityEntity>();
	}

	public LoanBO(UserContext userContext) {
		super(userContext);
		loanActivityDetails = new HashSet<LoanActivityEntity>();
	}

	private LoanPersistance loanPersistance;

	private Integer businessActivityId;

	private CollateralType collateralType;

	private GracePeriodType gracePeriodType;

	private Short groupFlag;

	private Money loanAmount;

	private Money loanBalance;

	private InterestTypes interestType;

	private Money interestRateAmount;

	private Fund fund;

	private MeetingBO loanMeeting;

	private Short noOfInstallments;

	private Date disbursementDate;

	private String collateralNote;

	private Short gracePeriodDuration;

	private Short intrestAtDisbursement;

	private Short gracePeriodPenalty;

	private LoanPerfHistory perfHistory;

	private List loanInstallments;

	private Short status;

	private AccountBO account;

	private LoanSummaryEntity loanSummary;

	private String stateSelected;

	private LoanOfferingBO loanOffering;

	public Set<LoanActivityEntity> loanActivityDetails;

	public AccountBO getAccount() {
		return account;
	}

	public void setAccount(AccountBO account) {
		this.account = account;
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

	public CollateralType getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(CollateralType collateralType) {
		this.collateralType = collateralType;
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

	public GracePeriodType getGracePeriodType() {
		return gracePeriodType;
	}

	public void setGracePeriodType(GracePeriodType gracePeriodType) {
		this.gracePeriodType = gracePeriodType;
	}

	public Short getGroupFlag() {
		return groupFlag;
	}

	public void setGroupFlag(Short groupFlag) {
		this.groupFlag = groupFlag;
	}

	public Money getInterestRateAmount() {
		return interestRateAmount;
	}

	public void setInterestRateAmount(Money interestRateAmount) {
		this.interestRateAmount = interestRateAmount;
	}

	public InterestTypes getInterestType() {
		return interestType;
	}

	public void setInterestType(InterestTypes interestType) {
		this.interestType = interestType;
	}

	public Short getIntrestAtDisbursement() {
		return intrestAtDisbursement;
	}

	public void setIntrestAtDisbursement(Short intrestAtDisbursement) {
		this.intrestAtDisbursement = intrestAtDisbursement;
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

	public List getLoanInstallments() {
		return loanInstallments;
	}

	public void setLoanInstallments(List loanInstallments) {
		this.loanInstallments = loanInstallments;
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

	public void setLoanOffering(LoanOfferingBO loanOffering) {
		this.loanOffering = loanOffering;
	}

	public LoanSummaryEntity getLoanSummary() {
		return loanSummary;
	}

	public void setLoanSummary(LoanSummaryEntity loanSummary) {
		this.loanSummary = loanSummary;
	}

	public Short getNoOfInstallments() {
		return noOfInstallments;
	}

	public void setNoOfInstallments(Short noOfInstallments) {
		this.noOfInstallments = noOfInstallments;
	}

	public LoanPerfHistory getPerfHistory() {
		return perfHistory;
	}

	public void setPerfHistory(LoanPerfHistory perfHistory) {
		this.perfHistory = perfHistory;
	}

	public String getStateSelected() {
		return stateSelected;
	}

	public void setStateSelected(String stateSelected) {
		this.stateSelected = stateSelected;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public Set<LoanActivityEntity> getLoanActivityDetails() {
		return loanActivityDetails;
	}

	private void setLoanActivityDetails(
			Set<LoanActivityEntity> loanActivityDetails) {
		this.loanActivityDetails = loanActivityDetails;
	}

	public void addLoanActivity(LoanActivityEntity loanActivity) {
		loanActivity.setAccount(this);
		this.loanActivityDetails.add(loanActivity);
	}

	protected AccountPaymentEntity makePayment(PaymentData paymentData)
			throws AccountException, SystemException {
		AccountActionDateEntity lastAccountAction = getLastInstallmentAccountAction();
		AccountPaymentEntity accountPayment = new AccountPaymentEntity();
		accountPayment.setPaymentDetails(paymentData.getTotalAmount(),
				paymentData.getRecieptNum(), paymentData.getRecieptDate(),
				paymentData.getPaymentTypeId());
		for (AccountPaymentData accountPaymentData : paymentData
				.getAccountPayments()) {
			AccountActionDateEntity accountAction = getAccountActionDate(accountPaymentData
					.getInstallmentId());
			if (accountAction.getPaymentStatus().equals(
					AccountConstants.PAYMENT_PAID))
				throw new AccountException("errors.update",
						new String[] { getGlobalAccountNum() });
			if (accountAction.getInstallmentId().equals(
					lastAccountAction.getInstallmentId())) {
				AccountStateEntity accountState = this.getAccountState();
				setAccountState(new AccountStateEntity(
						AccountStates.LOANACC_OBLIGATIONSMET));
				this.setClosedDate(new Date(System.currentTimeMillis()));
				this
						.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
								accountState, this.getAccountState(),
								paymentData.getPersonnelId()));
			}
			if (getAccountState().getId().shortValue() == AccountStates.LOANACC_BADSTANDING) {
				AccountStateEntity accountState = this.getAccountState();
				setAccountState(new AccountStateEntity(
						AccountStates.LOANACC_ACTIVEINGOODSTANDING));
				this
						.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
								accountState, this.getAccountState(),
								paymentData.getPersonnelId()));
			}
			LoanPaymentData loanPaymentData = (LoanPaymentData) accountPaymentData;
			accountAction.setPaymentDetails(loanPaymentData, new java.sql.Date(
					paymentData.getTransactionDate().getTime()));

			LoanTrxnDetailEntity accountTrxnBO = new LoanTrxnDetailEntity();
			accountTrxnBO.setAccount(this);
			Money totalFees = accountTrxnBO.setPaymentDetails(accountAction,
					loanPaymentData, paymentData.getPersonnelId(), paymentData
							.getTransactionDate());
			accountTrxnBO.setSumAmount(totalFees);
			accountPayment.addAcountTrxn(accountTrxnBO);

			loanSummary.updatePaymentDetails(
					loanPaymentData.getPrincipalPaid(), loanPaymentData
							.getInterestPaid(), loanPaymentData
							.getPenaltyPaid().add(
									loanPaymentData.getMiscPenaltyPaid()),
					totalFees.add(loanPaymentData.getMiscFeePaid()));

		}
		addLoanActivity(buildLoanActivity(accountPayment.getAccountTrxns(),
				paymentData.getPersonnelId(), "Payment rcvd."));
		return accountPayment;
	}

	private LoanActivityEntity buildLoanActivity(
			Collection<AccountTrxnEntity> accountTrxnDetails,
			Short personnelId, String comments) {
		LoanActivityEntity loanActivity = new LoanActivityEntity();
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
		loanActivity.setActivityDetails(loanSummary, personnelId, principal,
				interest, fees, penalty, comments);
		loanActivity.setAccount(this);
		return loanActivity;
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
			throws ApplicationException, SystemException {
		Set<AccountActionDateEntity> accountActionDateEntities = getAccountActionDates();

		OverDueAmounts totalOverDueAmounts = new OverDueAmounts();

		if (null != accountActionDateEntities
				&& accountActionDateEntities.size() > 0) {
			Iterator<AccountActionDateEntity> accountActionDatesIterator = accountActionDateEntities
					.iterator();

			while (accountActionDatesIterator.hasNext()) {
				AccountActionDateEntity accountActionDateEntity = accountActionDatesIterator
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

	public void updateTotalFeeAmount(Money totalFeeAmount) {		
		LoanSummaryEntity loanSummaryEntity = this.getLoanSummary();
		loanSummaryEntity.setOriginalFees(loanSummaryEntity.getOriginalFees()
				.subtract(totalFeeAmount));
	}
	
	public void updateTotalPenaltyAmount(Money totalPenaltyAmount) {
		LoanSummaryEntity loanSummaryEntity = this.getLoanSummary();
		loanSummaryEntity.setOriginalPenalty(loanSummaryEntity.getOriginalPenalty().subtract(totalPenaltyAmount));
	}

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
						|| (lntrxn.getInstallmentId()
								.equals(Short.valueOf("1")) && lntrxn
								.getPrincipalAmount().getAmountDoubleValue() == 0.0))
					return false;
			}
		}
		if (null != getLastPmnt() && getLastPmntAmnt() != 0) {

			return true;
		}
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Adjustment is not possible ");
		return false;
	}

	protected void updateInstallmentAfterAdjustment(
			List<AccountTrxnEntity> reversedTrxns) {
		if (null != reversedTrxns && reversedTrxns.size() > 0) {
			for (AccountTrxnEntity accntTrxn : reversedTrxns) {
				LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) accntTrxn;

				loanSummary.updatePaymentDetails(loanTrxn.getPrincipalAmount(),
						loanTrxn.getInterestAmount(), loanTrxn
								.getPenaltyAmount().add(
										loanTrxn.getMiscPenaltyAmount()),
						loanTrxn.getFeeAmount()
								.add(loanTrxn.getMiscFeeAmount()));

				AccountActionDateEntity accntActionDate = getAccountActionDate(loanTrxn
						.getInstallmentId());
				accntActionDate.updatePaymentDetails(loanTrxn
						.getPrincipalAmount(), loanTrxn.getInterestAmount(),
						loanTrxn.getPenaltyAmount(), loanTrxn
								.getMiscPenaltyAmount(), loanTrxn
								.getMiscFeeAmount());
				accntActionDate
						.setPaymentStatus(AccountConstants.PAYMENT_UNPAID);
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
			addLoanActivity(buildLoanActivity(reversedTrxns, getUserContext()
					.getId(), "Loan Adjusted"));
		}
	}

	public void disburseLoan(String recieptNum, Date transactionDate,
			Short paymentTypeId, Short personnelId, Date receiptDate,Short rcvdPaymentTypeId)
			throws AccountException, SystemException,
			RepaymentScheduleException, FinancialException {
		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity();

		// if the trxn date is not equal to disbursementDate we need to
		// regenerate the installments
		if (!this.disbursementDate.equals(transactionDate)) {
			regeneratePaymentSchedule(transactionDate);
		}

		this.disbursementDate = transactionDate;
		AccountStateEntity newState = new AccountStateEntity(
				AccountStates.LOANACC_ACTIVEINGOODSTANDING);
		// update status change history also
		this
				.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
						this.getAccountState(), newState, personnelId));
		this.setAccountState(newState);

		// create trxn entry for disbursal
		LoanTrxnDetailEntity loanTrxnDetailEntity = getLoanTrxnDetailEntity(
				AccountConstants.ACTION_DISBURSAL, transactionDate,
				paymentTypeId, personnelId);
		loanTrxnDetailEntity.setDesbursementDetails(this.loanAmount,
				this.loanAmount);
		// loanTrxnDetailEntity.setRunningBalance(loanSummary);
		List<AccountTrxnEntity> loanTrxns = new ArrayList<AccountTrxnEntity>();
		loanTrxns.add(loanTrxnDetailEntity);
		addLoanActivity(buildLoanActivity(loanTrxns, personnelId,
				"Loan Disbursal"));
		if (null != this.intrestAtDisbursement
				&& this.intrestAtDisbursement == 1) {
			accountPaymentEntity = payInterestAtDisbursement(recieptNum,
					transactionDate, rcvdPaymentTypeId, personnelId, receiptDate);
		} else {
			if (loanPersistance.getFeeAmountAtDisbursement(this.getAccountId(),
					transactionDate) > 0.0)
				accountPaymentEntity = insertOnlyFeeAtDisbursement(recieptNum,
						transactionDate, rcvdPaymentTypeId, personnelId);
		}

		if (null == accountPaymentEntity) {
			accountPaymentEntity = new AccountPaymentEntity();
		}

		accountPaymentEntity.setPaymentDetails(this.loanAmount
				.subtract(accountPaymentEntity.getAmount()), recieptNum,
				transactionDate, paymentTypeId);
		accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);
		this.addAccountPayment(accountPaymentEntity);
		this.buildFinancialEntries(accountPaymentEntity.getAccountTrxns());
		new AccountPersistanceService().update(this);

	}

	private void initializeAmounts(AccountActionDateEntity entity) {
		if (entity.getDeposit() == null) {
			entity.setDeposit(new Money());
		}
		if (entity.getPrincipal() == null) {
			entity.setPrincipal(new Money());
		}
		if (entity.getInterest() == null) {
			entity.setInterest(new Money());
		}
		if (entity.getPenalty() == null) {
			entity.setPenalty(new Money());
		}
		if (entity.getMiscFee() == null) {
			entity.setMiscFee(new Money());
		}
		if (entity.getDepositPaid() == null) {
			entity.setDepositPaid(new Money());
		}
		if (entity.getPrincipalPaid() == null) {
			entity.setPrincipalPaid(new Money());
		}
		if (entity.getInterestPaid() == null) {
			entity.setInterestPaid(new Money());
		}
		if (entity.getPenaltyPaid() == null) {
			entity.setPenaltyPaid(new Money());
		}
		if (entity.getMiscFeePaid() == null) {
			entity.setMiscFeePaid(new Money());
		}
		if (entity.getMiscPenalty() == null) {
			entity.setMiscPenalty(new Money());
		}

		if (entity.getMiscPenaltyPaid() == null) {
			entity.setMiscPenaltyPaid(new Money());
		}
	}

	private Set<AccountActionDateEntity> generateRepaymentSchedule(
			Date transactionDate) throws RepaymentScheduleException {
		// get the repayment schedule input object which would be passed to
		// repayment schedule generator
		RepaymentScheduleInputsIfc repaymntScheduleInputs = RepaymentScheduleFactory
				.getRepaymentScheduleInputs();
		RepaymentScheduleIfc repaymentScheduler = RepaymentScheduleFactory
				.getRepaymentScheduler();
		repaymentScheduler.setRepaymentScheduleInputs(repaymntScheduleInputs);
		MeetingBO meeting = this.getCustomer().getCustomerMeeting()
				.getMeeting();
		repaymntScheduleInputs.setMeeting(convertM2StyleToM1(meeting));
		// set the inputs for repaymentSchedule
		repaymntScheduleInputs.setGracePeriod(this.getGracePeriodDuration());
		repaymntScheduleInputs.setGraceType(this.getGracePeriodType()
				.getGracePeriodTypeId());
		repaymntScheduleInputs.setIsInterestDedecutedAtDisburesement(this
				.getIntrestAtDisbursement().equals(Short.valueOf("1")) ? true
				: false);
		repaymntScheduleInputs.setIsPrincipalInLastPayment(false);
		repaymntScheduleInputs.setPrincipal(this.getLoanAmount());
		repaymntScheduleInputs.setInterestRate(this.getInterestRateAmount()
				.getAmountDoubleValue());
		repaymntScheduleInputs.setNoOfInstallments(this.getNoOfInstallments());
		repaymntScheduleInputs.setInterestType(this.getInterestType()
				.getInterestTypeId());
		repaymntScheduleInputs.setMiscFees(getMiscFee());
		repaymntScheduleInputs.setMiscPenlty(getMiscPenalty());
		// TODO convert accountfee to m1 style
		repaymntScheduleInputs.setAccountFee(getAccountFeesSet());
		repaymntScheduleInputs.setDisbursementDate(transactionDate);
		Meeting loanMeeting = convertM2StyleToM1(this.getLoanMeeting());
		java.util.Date dt = transactionDate;
		java.util.Calendar cal = new java.util.GregorianCalendar();
		cal.setTime(dt);
		loanMeeting.setMeetingStartDate(cal);
		this.getLoanMeeting().setMeetingStartDate(cal);

		boolean isDisbursementDateValid = false;
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"id disbursement date valid" + isDisbursementDateValid);
		if (!repaymentScheduler.isDisbursementDateValid()) {
			throw new RepaymentScheduleException(
					LoanExceptionConstants.INVALIDDISBURSEMENTDATE);
		}

		repaymntScheduleInputs.setRepaymentFrequency(loanMeeting);

		RepaymentSchedule repaymentSchedule = repaymentScheduler
				.getRepaymentSchedule();
		return RepaymentScheduleHelper.getActionDateEntity(repaymentSchedule);
		// set the customer'sMeeting , this is required to check if the
		// disbursement date is valid
		// this would be null if customer does not have a meeting.

	}

	private Meeting convertM2StyleToM1(MeetingBO meeting) {

		Meeting meetingM1 = null;
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			meetingM1 = (Meeting) session.get(Meeting.class, meeting
					.getMeetingId());
		} catch (HibernateProcessException e) {
			e.printStackTrace();
		} finally {
			try {
				HibernateUtil.closeSession(session);
			} catch (HibernateProcessException e) {
				e.printStackTrace();
			}
		}
		return meetingM1;
	}

	private AccountPaymentEntity insertOnlyFeeAtDisbursement(String recieptNum,
			Date recieptDate, Short paymentTypeId, Short personnelId) {
		Set<AccountFeesEntity> accountFees = this.getAccountFees();
		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity();
		LoanTrxnDetailEntity loanTrxnDetailEntity = getLoanTrxnDetailEntity(
				AccountConstants.ACTION_FEE_REPAYMENT, recieptDate,
				paymentTypeId, personnelId);
		loanTrxnDetailEntity.setMiscFeeAmount(new Money());
		accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);
		Money totalPayment = new Money();
		for (AccountFeesEntity accountFeesEntity : accountFees) {
			if (accountFeesEntity.isTimeOfDisbursement()) {
				totalPayment = totalPayment.add(accountFeesEntity
						.getAccountFeeAmount());
				FeesTrxnDetailEntity feesTrxnDetailEntity = new FeesTrxnDetailEntity();
				feesTrxnDetailEntity.setFeeDetails(accountFeesEntity,
						accountFeesEntity.getAccountFeeAmount());
				loanTrxnDetailEntity.addFeesTrxnDetail(feesTrxnDetailEntity);
			}
		}
		loanTrxnDetailEntity.setAmount(totalPayment);
		loanSummary.updateFeePaid(totalPayment);
		// loanTrxnDetailEntity.setRunningBalance(loanSummary);
		accountPaymentEntity.setPaymentDetails(totalPayment, recieptNum,
				recieptDate, paymentTypeId);
		addLoanActivity(buildLoanActivity(accountPaymentEntity
				.getAccountTrxns(), personnelId, "Payment rcvd."));
		return accountPaymentEntity;
	}

	private LoanTrxnDetailEntity getLoanTrxnDetailEntity(short accountAction,
			Date recieptDate, Short paymentTypeId, Short personnelId) {
		// set the loantrxn details
		LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity();
		loanTrxnDetailEntity.setAccount(this);
		AccountActionEntity action = loanPersistance
				.getAccountActionEntity(accountAction);
		PersonnelBO personnel = new PersonnelPersistenceService()
				.getPersonnel(personnelId);
		loanTrxnDetailEntity.setActionDate(recieptDate);
		loanTrxnDetailEntity.setDueDate(recieptDate);
		loanTrxnDetailEntity.setPersonnel(personnel);
		// loanTrxnDetailEntity.setp
		loanTrxnDetailEntity.setAccountActionEntity(action);
		loanTrxnDetailEntity.setCustomer(this.getCustomer());
		loanTrxnDetailEntity.setComments("Loan Disburesed");
		loanTrxnDetailEntity.setTrxnCreatedDate(new Timestamp(System
				.currentTimeMillis()));
		loanTrxnDetailEntity.setInstallmentId(Short.valueOf("0"));
		return loanTrxnDetailEntity;
	}

	// TODO this method will go once scheduler is moved to m2 style
	private AccountFees getAccountFees(Integer accountFeeId) {
		AccountFees accountFees = new AccountFees();
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			accountFees = (AccountFees) session.get(AccountFees.class,
					accountFeeId);
			Fees fees = accountFees.getFees();
			initializeMeetings(fees);
			if (null != fees) {
				fees.getFeeFrequency().getFeeFrequencyId();
			}
		} catch (HibernateProcessException e) {
			e.printStackTrace();
		} finally {
			try {
				HibernateUtil.closeSession(session);
			} catch (HibernateProcessException e) {
				e.printStackTrace();
			}
		}
		Hibernate.initialize(accountFees);
		return accountFees;
	}

	private void initializeMeetings(Fees fees) {

		if (fees.getFeeFrequency().getFeeFrequencyTypeId().equals(
				FeeFrequencyType.PERIODIC.getValue())) {
			Meeting meeting = fees.getFeeFrequency().getFeeMeetingFrequency();
			meeting.getMeetingType().getMeetingPurpose();
		}

	}

	private Set<AccountFees> getAccountFeesSet() {
		Set<AccountFees> accountFeesSet = new HashSet<AccountFees>();
		for (AccountFeesEntity accountFeesEntity : this.getAccountFees()) {
			if (this.intrestAtDisbursement != null
					&& this.intrestAtDisbursement
							.equals(LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT))
				initAccFeeIntDeductedAtDisbursal(accountFeesEntity,
						accountFeesSet);
			else
				initAccFeeIntNotDeductedAtDisbursal(accountFeesEntity,
						accountFeesSet);

		}

		return accountFeesSet;
	}

	private PaymentData getLoanAccountPaymentData(Money totalAmount,
			List<AccountActionDateEntity> accountActions, Short personnelId,
			String recieptId, Short paymentId, Date receiptDate,
			Date transactionDate) {
		PaymentData paymentData = new PaymentData(totalAmount, personnelId,
				paymentId, transactionDate);
		paymentData.setRecieptDate(receiptDate);
		paymentData.setRecieptNum(recieptId);
		for (AccountActionDateEntity actionDate : accountActions) {
			LoanPaymentData loanPaymentData = new LoanPaymentData(actionDate);
			paymentData.addAccountPaymentData(loanPaymentData);
		}
		return paymentData;
	}
	protected Money getDueAmount(AccountActionDateEntity installment){
		return installment.getTotalDueWithFees();
	}
		
	public Money getTotalEarlyRepayAmount() {
		Money amount = new Money();
		List<AccountActionDateEntity> dueInstallmentsList = getApplicableIdsForDueInstallments();
		List<AccountActionDateEntity> futureInstallmentsList = getApplicableIdsForFutureInstallments();
		for (AccountActionDateEntity accountActionDateEntity : dueInstallmentsList) {
			amount = amount.add(accountActionDateEntity.getTotalDueWithFees());
		}

		for (AccountActionDateEntity accountActionDateEntity : futureInstallmentsList) {
			amount = amount.add(accountActionDateEntity.getPrincipal());
		}
		return amount;
	}

	public void makeEarlyRepayment(Money totalAmount, String receiptNumber,
			Date recieptDate, String paymentTypeId, Short personnelId)
			throws ServiceException, AccountException {
		this.setUpdatedBy(personnelId);
		this.setUpdatedDate(new Date(System.currentTimeMillis()));
		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity();
		accountPaymentEntity.setPaymentDetails(totalAmount, receiptNumber,
				recieptDate, Short.valueOf(paymentTypeId));
		this.addAccountPayment(accountPaymentEntity);
		List<AccountActionDateEntity> dueInstallmentsList = getApplicableIdsForDueInstallments();
		List<AccountActionDateEntity> futureInstallmentsList = getApplicableIdsForFutureInstallments();
		for (AccountActionDateEntity accountActionDateEntity : dueInstallmentsList) {
			Money principal = accountActionDateEntity.getPrincipal();
			Money interest = accountActionDateEntity.getInterest();
			Money fees = accountActionDateEntity.getTotalFees();
			Money penalty = accountActionDateEntity.getTotalPenalty();

			accountActionDateEntity
					.makeEarlyRepaymentEnteries(LoanConstants.PAY_FEES_PENALTY_INTEREST);

			LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity();
			accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);
			loanTrxnDetailEntity.setAccount(this);
			loanTrxnDetailEntity.setAmount(principal.add(interest).add(fees)
					.add(penalty));

			LoanPaymentData loanPaymentData = new LoanPaymentData(
					accountActionDateEntity);
			loanTrxnDetailEntity.setPaymentDetails(accountActionDateEntity,
					loanPaymentData, personnelId, new Date(System
							.currentTimeMillis()));

			loanSummary
					.updatePaymentDetails(principal, interest, penalty, fees);

			// loanTrxnDetailEntity.setRunningBalance(loanSummary);
		}
		for (AccountActionDateEntity accountActionDateEntity : futureInstallmentsList) {
			Money principal = accountActionDateEntity.getPrincipal();
			Money interest = accountActionDateEntity.getInterest();
			Money fees = accountActionDateEntity.getTotalFees();
			Money penalty = accountActionDateEntity.getTotalPenalty();

			accountActionDateEntity
					.makeEarlyRepaymentEnteries(LoanConstants.DONOT_PAY_FEES_PENALTY_INTEREST);

			LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity();
			accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);
			loanTrxnDetailEntity.setAccount(this);
			loanTrxnDetailEntity.setAmount(principal);

			LoanPaymentData loanPaymentData = new LoanPaymentData(
					accountActionDateEntity);

			loanTrxnDetailEntity.setPaymentDetails(accountActionDateEntity,
					loanPaymentData, personnelId, new Date(System
							.currentTimeMillis()));

			loanSummary.decreaseBy(null, interest, penalty, fees);
			loanSummary.updatePaymentDetails(principal, null, null, null);

			// loanTrxnDetailEntity.setRunningBalance(loanSummary);
		}
		try {
			addLoanActivity(buildLoanActivity(accountPaymentEntity
					.getAccountTrxns(), personnelId, "Loan Repayment"));
			buildFinancialEntries(accountPaymentEntity.getAccountTrxns());
		} catch (FinancialException fe) {
			throw new AccountException("errors.update", fe);
		}

		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.MasterDataService);
		AccountStateEntity newAccountState = (AccountStateEntity) masterPersistenceService
				.findById(AccountStateEntity.class,
						AccountStates.LOANACC_OBLIGATIONSMET);
		this
				.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
						this.getAccountState(), newAccountState, personnelId));
		this.setAccountState((AccountStateEntity) masterPersistenceService
				.findById(AccountStateEntity.class,
						AccountStates.LOANACC_OBLIGATIONSMET));
		this.setClosedDate(new Date(System.currentTimeMillis()));

		loanPersistance.createOrUpdate(this);
	}

	private void regeneratePaymentSchedule(Date transactionDate)
			throws RepaymentScheduleException {
		Set<AccountActionDateEntity> accntActionDateEntitySet = null;

		accntActionDateEntitySet = generateRepaymentSchedule(transactionDate);
		reAssociateFeePenaltyAtDisbursal(accntActionDateEntitySet);
		for (AccountActionDateEntity entity : accntActionDateEntitySet) {
			initializeAmounts(entity);
			if (entity.getPaymentStatus() == null) {
				entity.setPaymentStatus(AccountConstants.PAYMENT_UNPAID);
			}
			entity.setCustomer(this.getCustomer());

		}
		Session session = HibernateUtil.getSessionTL();

		if (null != accntActionDateEntitySet) {
			for (AccountActionDateEntity entity : this.getAccountActionDates()) {
				session.delete(entity);
			}
			this.resetAccountActionDates();
			for (AccountActionDateEntity entity : accntActionDateEntitySet) {
				this.addAccountActionDate(entity);
			}
		}

	}

	private void reAssociateFeePenaltyAtDisbursal(
			Set<AccountActionDateEntity> accntActionDateEntitySet) {
		AccountActionDateEntity entityExisting = getAccountActionDate(Short
				.valueOf("1"));
		for (AccountActionDateEntity entityNew : accntActionDateEntitySet) {
			if (entityNew.getInstallmentId().equals(Short.valueOf("1"))) {
				entityNew.setMiscFee(entityExisting.getMiscFee());
				entityNew.setMiscPenalty(entityExisting.getMiscPenalty());
				if (null == this.intrestAtDisbursement
						|| this.intrestAtDisbursement != 1)
					reAssociateTimeOfDisbursalFee(entityNew);
			}
		}

	}

	private void reAssociateTimeOfDisbursalFee(AccountActionDateEntity entityNew) {
		AccountActionDateEntity entityExisting = getAccountActionDate(Short
				.valueOf("1"));
		for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
			if (accountFeesEntity.isTimeOfDisbursement()) {
				AccountFeesActionDetailEntity accountFeesAction = entityExisting
						.getAccountFeesAction(accountFeesEntity
								.getAccountFeeId());
				if (accountFeesAction != null) {
					AccountFeesActionDetailEntity accountFeesActionDetail = new AccountFeesActionDetailEntity();
					accountFeesActionDetail.setAccountActionDate(entityNew);
					accountFeesActionDetail.setAccountFee(accountFeesEntity);
					accountFeesActionDetail.setFee(accountFeesEntity.getFees());
					accountFeesActionDetail.setFeeAmount(accountFeesAction
							.getFeeAmount());
					accountFeesActionDetail.setFeeAmountPaid(new Money());
					accountFeesActionDetail.setInstallmentId(entityNew
							.getInstallmentId());
					entityNew.addAccountFeesAction(accountFeesActionDetail);
				}
			}

		}
	}

	private AccountPaymentEntity payInterestAtDisbursement(String recieptNum,
			Date transactionDate, Short paymentTypeId, Short personnelId,
			Date receiptDate) throws SystemException, AccountException {
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

		PaymentData paymentData = getLoanAccountPaymentData(firstInstallment
				.getTotalDueWithFees(), installmentsToBePaid, personnelId,
				recieptNum, paymentTypeId, receiptDate, transactionDate);

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

	public void updateAccountActivity(Money totalAmount, Short personnelId,
			String description) {
		PersonnelBO personnel = new PersonnelPersistenceService()
				.getPersonnel(personnelId);
		LoanSummaryEntity loanSummaryEntity = ((LoanBO) this).getLoanSummary();
		LoanActivityEntity loanActivity = new LoanActivityEntity();
		loanActivity.setComments(description);
		loanActivity.setPersonnel(personnel);
		loanActivity.setPrincipal(new Money());
		loanActivity.setInterest(new Money());
		loanActivity.setFee(totalAmount);
		loanActivity.setFeeOutstanding(loanSummaryEntity.getOriginalFees()
				.subtract(loanSummaryEntity.getFeesPaid()));
		loanActivity.setPenaltyOutstanding(loanSummaryEntity
				.getOriginalPenalty().subtract(
						loanSummaryEntity.getPenaltyPaid()));
		loanActivity.setInterestOutstanding(loanSummaryEntity
				.getOriginalInterest().subtract(
						loanSummaryEntity.getInterestPaid()));
		loanActivity.setPrincipalOutstanding(loanSummaryEntity
				.getOriginalPrincipal().subtract(
						loanSummaryEntity.getPrincipalPaid()));
		this.addLoanActivity(loanActivity);
	}

	private void initAccFeeIntDeductedAtDisbursal(
			AccountFeesEntity accountFeesEntity, Set<AccountFees> accountFeesSet) {
		addFee(accountFeesEntity, accountFeesSet);

	}

	private void initAccFeeIntNotDeductedAtDisbursal(
			AccountFeesEntity accountFeesEntity, Set<AccountFees> accountFeesSet) {
		if (!accountFeesEntity.isTimeOfDisbursement())
			addFee(accountFeesEntity, accountFeesSet);

	}

	private void addFee(AccountFeesEntity accountFeesEntity,
			Set<AccountFees> accountFeesSet) {
		if (accountFeesEntity.getFeeStatus() == null
				|| accountFeesEntity.getFeeStatus().equals(
						AccountConstants.ACTIVE_FEES))
			accountFeesSet.add(getAccountFees(accountFeesEntity
					.getAccountFeeId()));

	}

	/**
	 * @author shemeerb Add an account Status Change History Object to it with
	 *         the required parameters. Set the State of the Account to Active
	 *         in Bad Standing. Set the Updated date in the account object to
	 *         current date.
	 * @param account -
	 *            The AccountBO Object returned from the query
	 * @throws ServiceException
	 */
	public void handleArrears() throws ServiceException {

		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.MasterDataService);
		AccountStateEntity stateEntity = (AccountStateEntity) masterPersistenceService
				.findById(AccountStateEntity.class,
						AccountStates.LOANACC_BADSTANDING);
		AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(
				this.getAccountState(), stateEntity, this.getPersonnel()
						.getPersonnelId());
		this.addAccountStatusChangeHistory(historyEntity);
		this.setAccountState(stateEntity);
		String systemDate = DateHelper.getCurrentDate(Configuration
				.getInstance().getSystemConfig().getMFILocale());
		Date currrentDate = DateHelper.getLocaleDate(Configuration
				.getInstance().getSystemConfig().getMFILocale(), systemDate);
		this.setUpdatedDate(currrentDate);
		loanPersistance.createOrUpdate(this);
	}

	public boolean isInterestDeductedAtDisbursement() {
		return (intrestAtDisbursement != null && intrestAtDisbursement
				.shortValue() == LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT
				.shortValue()) ? true : false;

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

	private Money getMiscFee() {
		Money miscFee = new Money();
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			if (accountActionDateEntity.getMiscFee() != null) {
				miscFee = miscFee.add(accountActionDateEntity.getMiscFee());
			}
		}
		return miscFee;
	}

	private Money getMiscPenalty() {
		Money miscPenalty = new Money();
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			if (accountActionDateEntity.getMiscPenalty() != null) {
				miscPenalty = miscPenalty.add(accountActionDateEntity
						.getMiscPenalty());
			}
		}
		return miscPenalty;
	}

	public void writeOff(String comment) throws ServiceException,
			SecurityException, PersistenceException, FinancialException {
		Short personnelId = this.getUserContext().getId();
		Short statusId = Short.valueOf(AccountStates.LOANACC_WRITTENOFF);
		this.setUpdatedBy(personnelId);
		this.setUpdatedDate(new Date(System.currentTimeMillis()));
		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity();
		accountPaymentEntity.setPaymentDetails(getEarlyClosureAmount(), null,
				null, Short.valueOf("1"));
		this.addAccountPayment(accountPaymentEntity);

		for (AccountActionDateEntity accountActionDateEntity : getListOfUnpaidInstallments()) {
			LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity();
			accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);
			loanTrxnDetailEntity.setAccount(this);
			loanTrxnDetailEntity.setLoanTrxnDetailsForWriteOff(
					accountActionDateEntity, personnelId);
		}
		addLoanActivity(buildLoanActivity(accountPaymentEntity
				.getAccountTrxns(), personnelId, "Loan Written Off"));
		buildFinancialEntries(accountPaymentEntity.getAccountTrxns());
		changeStatus(statusId, null, comment);
		getAccountPersistenceService().update(this);
	}

	private List<AccountActionDateEntity> getListOfUnpaidInstallments() {
		List<AccountActionDateEntity> unpaidInstallmentList = new ArrayList<AccountActionDateEntity>();
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			if (accountActionDateEntity.getPaymentStatus().equals(
					AccountConstants.PAYMENT_UNPAID)) {
				unpaidInstallmentList.add(accountActionDateEntity);
			}
		}
		return unpaidInstallmentList;
	}

	private void changeStatus(Short newStatusId, Short flagId, String comment)
			throws SecurityException, ServiceException, PersistenceException {
		if (null != getCustomer().getPersonnel().getPersonnelId())
			checkPermissionForStatusChange(newStatusId, this.getUserContext(),
					flagId, getOffice().getOfficeId(), getCustomer()
							.getPersonnel().getPersonnelId());
		else
			checkPermissionForStatusChange(newStatusId, this.getUserContext(),
					flagId, getOffice().getOfficeId(), this.getUserContext()
							.getId());

		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.MasterDataService);
		AccountStateEntity accountStateEntity = (AccountStateEntity) masterPersistenceService
				.findById(AccountStateEntity.class, newStatusId);
		accountStateEntity.setLocaleId(this.getUserContext().getLocaleId());
		AccountStateFlagEntity accountStateFlagEntity = null;
		if (flagId != null) {
			accountStateFlagEntity = (AccountStateFlagEntity) masterPersistenceService
					.findById(AccountStateFlagEntity.class, flagId);
		}
		AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(
				this.getAccountState(), accountStateEntity, this.getPersonnel()
						.getPersonnelId());
		AccountNotesEntity accountNotesEntity = createAccountNotes(comment);
		this.addAccountStatusChangeHistory(historyEntity);
		this.setAccountState(accountStateEntity);
		this.addAccountNotes(accountNotesEntity);
		if (accountStateFlagEntity != null) {
			accountStateFlagEntity.setLocaleId(this.getUserContext()
					.getLocaleId());
			this.addAccountFlag(accountStateFlagEntity);
		}
		this.setClosedDate(new Date(System.currentTimeMillis()));
	}

	private AccountNotesEntity createAccountNotes(String comment)
			throws ServiceException {
		AccountNotesEntity accountNotes = new AccountNotesEntity();
		accountNotes.setCommentDate(new java.sql.Date(System
				.currentTimeMillis()));
		accountNotes.setPersonnel(this.getPersonnel());
		accountNotes.setComment(comment);
		return accountNotes;
	}

	private void checkPermissionForStatusChange(Short newState,
			UserContext userContext, Short flagSelected, Short recordOfficeId,
			Short recordLoanOfficerId) throws SecurityException {
		if (!isPermissionAllowed(newState, userContext, flagSelected,
				recordOfficeId, recordLoanOfficerId))
			throw new SecurityException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	private boolean isPermissionAllowed(Short newState,
			UserContext userContext, Short flagSelected, Short recordOfficeId,
			Short recordLoanOfficerId) {
		return ActivityMapper.getInstance().isStateChangePermittedForAccount(
				newState.shortValue(),
				null != flagSelected ? flagSelected.shortValue() : 0,
				userContext, recordOfficeId, recordLoanOfficerId);
	}

	private Money getEarlyClosureAmount() {
		Money amount = new Money();
		for (AccountActionDateEntity accountActionDateEntity : getListOfUnpaidInstallments()) {
			amount = amount.add(accountActionDateEntity.getPrincipal());
		}
		return amount;
	}
	
	@Override
	public void waiveAmountDue(WaiveEnum waiveType) throws ServiceException {				
		if(waiveType.equals(WaiveEnum.FEES)){					
			waiveFeeAmountDue();
		}
		else if(waiveType.equals(WaiveEnum.PENALTY)){			
			waivePenaltyAmountDue();
		}		
	}

	@Override
	public void waiveAmountOverDue(WaiveEnum waiveType) throws ServiceException {		
		if(waiveType.equals(WaiveEnum.FEES)){
			waiveFeeAmountOverDue();
		}
		else if(waiveType.equals(WaiveEnum.PENALTY)){
			waivePenaltyAmountOverDue();
		}			
	}
	
	public void waiveFeeAmountDue() throws ServiceException{
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		AccountActionDateEntity accountActionDateEntity = accountActionDateList
				.get(accountActionDateList.size() - 1);
		Money chargeWaived = accountActionDateEntity.waiveFeeCharges();
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateAccountActivity(chargeWaived, userContext.getId(), "Amnt "+chargeWaived+" waived" );
			updateTotalFeeAmount(chargeWaived);			
		}		
		getAccountPersistenceService().update(this);
	}
	
	public void waivePenaltyAmountDue() throws ServiceException{
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		AccountActionDateEntity accountActionDateEntity = accountActionDateList
				.get(accountActionDateList.size() - 1);
		Money chargeWaived = accountActionDateEntity.waivePenaltyCharges();
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateAccountActivity(chargeWaived, userContext.getId(), "Amnt "+chargeWaived+" waived" );			
			updateTotalPenaltyAmount(chargeWaived);
		}
		getAccountPersistenceService().update(this);
	}
	
	public void waiveFeeAmountOverDue() throws ServiceException{
		Money chargeWaived = new Money();
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		accountActionDateList.remove(accountActionDateList.size() - 1);
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
			chargeWaived = chargeWaived.add(accountActionDateEntity.waiveFeeCharges());
		}
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateAccountActivity(chargeWaived, userContext.getId(), "Amnt "+chargeWaived+" waived" );
			updateTotalFeeAmount(chargeWaived);	
		}
		getAccountPersistenceService().update(this);
	}
	public void waivePenaltyAmountOverDue() throws ServiceException{
		Money chargeWaived = new Money();
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		accountActionDateList.remove(accountActionDateList.size() - 1);
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
			chargeWaived = chargeWaived.add(accountActionDateEntity.waivePenaltyCharges());
		}
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateAccountActivity(chargeWaived, userContext.getId(), "Amnt "+chargeWaived+" waived" );	
			updateTotalPenaltyAmount(chargeWaived);
		}
		getAccountPersistenceService().update(this);
	}
	
	
	@Override
	protected void regenerateFutureInstallments(List<Date> meetingDates,Short nextIntallmentId) {
		if (!this.getAccountState().getId().equals(
				AccountStates.LOANACC_OBLIGATIONSMET)
				&& !this.getAccountState().getId().equals(
						AccountStates.LOANACC_WRITTENOFF)
				&& !this.getAccountState().getId().equals(
						AccountStates.LOANACC_CANCEL)) {
			int count = 0;
			List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForFutureInstallments();
			for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
				accountActionDateEntity.setActionDate(new java.sql.Date(
						meetingDates.get(count++).getTime()));
			}
		}
	}
	
	public Money getAmountTobePaidAtdisburtail(Date disbursalDate){
		
		if ( this.intrestAtDisbursement !=null && this.intrestAtDisbursement.intValue()==1){
			return       getDueAmount( getAccountActionDate(Short.valueOf("1")));
		}
		else
		{
			return  new Money(loanPersistance.getFeeAmountAtDisbursement(this.getAccountId(),disbursalDate).toString());
		}
		
	}
}
