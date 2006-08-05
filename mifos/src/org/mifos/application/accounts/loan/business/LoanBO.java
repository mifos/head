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
import java.util.Set;

import org.hibernate.Session;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateMachines;
import org.mifos.application.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.loan.exceptions.LoanExceptionConstants;
import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountPaymentData;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.accounts.util.helpers.OverDueAmounts;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.master.business.CollateralTypeEntity;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.master.util.valueobjects.AccountType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
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
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.helpers.SchedulerHelper;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class LoanBO extends AccountBO {
	
	private final LoanOfferingBO loanOffering;
	
	private final LoanSummaryEntity loanSummary;
	
	private Money loanAmount;
	
	private Money loanBalance;
	
	private Short noOfInstallments;

	private Date disbursementDate;
	
	private MeetingBO loanMeeting;

	private Short gracePeriodDuration;

	private Short intrestAtDisbursement;

	private Short gracePeriodPenalty;
	
	private final LoanPerformanceHistoryEntity performanceHistory;
	
	private Integer businessActivityId;

	private CollateralTypeEntity collateralType;

	private GracePeriodTypeEntity gracePeriodType;

	private Short groupFlag;

	private InterestTypesEntity interestType;

	private Money interestRateAmount;

	private Fund fund;

	private String collateralNote;

	private String stateSelected;

	public Set<LoanActivityEntity> loanActivityDetails;
	
	protected LoanBO() {
		super();
		this.loanOffering = null;
		this.loanSummary = null;
		this.performanceHistory = null;
		this.loanActivityDetails = new HashSet<LoanActivityEntity>();
	}

	public LoanBO(UserContext userContext, LoanOfferingBO loanOffering,
			CustomerBO customer, AccountType accountType,AccountState accountState) throws Exception {
		super(userContext, customer, accountType,accountState);
		this.loanActivityDetails = new HashSet<LoanActivityEntity>();
		this.loanOffering = loanOffering;
		this.loanSummary = new LoanSummaryEntity(this,loanAmount,new Money(),new Money());
		this.performanceHistory = new LoanPerformanceHistoryEntity(this) ;
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

	public Money getInterestRateAmount() {
		return interestRateAmount;
	}

	public void setInterestRateAmount(Money interestRateAmount) {
		this.interestRateAmount = interestRateAmount;
	}

	public InterestTypesEntity getInterestType() {
		return interestType;
	}

	public void setInterestType(InterestTypesEntity interestType) {
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

	protected AccountPaymentEntity makePayment(PaymentData paymentData)
			throws AccountException, SystemException {
		AccountActionDateEntity lastAccountAction = getLastInstallmentAccountAction();
		PaymentTypeEntity paymentTypeEntity = new PaymentTypeEntity();
		paymentTypeEntity.setId(paymentData.getPaymentTypeId());
		AccountPaymentEntity accountPayment = new AccountPaymentEntity(this,
				paymentData.getTotalAmount(), paymentData.getRecieptNum(),
				paymentData.getRecieptDate(), paymentTypeEntity);
		for (AccountPaymentData accountPaymentData : paymentData
				.getAccountPayments()) {
			LoanScheduleEntity accountAction = (LoanScheduleEntity) getAccountActionDate(accountPaymentData
					.getInstallmentId());
			if (accountAction.getPaymentStatus().equals(
					PaymentStatus.PAID.getValue()))
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
								paymentData.getPersonnel()));

				// Client performance entry
				updateCustomerHistoryOnLastInstlPayment(paymentData
						.getTotalAmount());
			}
			if (getAccountState().getId().shortValue() == AccountStates.LOANACC_BADSTANDING) {
				AccountStateEntity accountState = this.getAccountState();
				setAccountState(new AccountStateEntity(
						AccountStates.LOANACC_ACTIVEINGOODSTANDING));
				this
						.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
								accountState, this.getAccountState(),
								paymentData.getPersonnel()));

				// Client performance entry
				updateCustomerHistoryOnPayment();
			}
			LoanPaymentData loanPaymentData = (LoanPaymentData) accountPaymentData;
			accountAction.setPaymentDetails(loanPaymentData, new java.sql.Date(
					paymentData.getTransactionDate().getTime()));
			MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
					.getInstance().getPersistenceService(
							PersistenceServiceName.MasterDataService);
			accountPaymentData.setAccountActionDate(accountAction);
			LoanTrxnDetailEntity accountTrxnBO = new LoanTrxnDetailEntity(
					accountPayment, loanPaymentData,
					paymentData.getPersonnel(), paymentData
							.getTransactionDate(),
					(AccountActionEntity) masterPersistenceService.findById(
							AccountActionEntity.class,
							AccountConstants.ACTION_LOAN_REPAYMENT),
					loanPaymentData.getTotalPaidAmnt(), "Payment rcvd.");
			accountPayment.addAcountTrxn(accountTrxnBO);

			loanSummary.updatePaymentDetails(
					loanPaymentData.getPrincipalPaid(), loanPaymentData
							.getInterestPaid(), loanPaymentData
							.getPenaltyPaid().add(
									loanPaymentData.getMiscPenaltyPaid()),
					loanPaymentData.getTotalFees().add(
							loanPaymentData.getMiscFeePaid()));

			if (getPerformanceHistory() != null)
				getPerformanceHistory().setNoOfPayments(
						getPerformanceHistory().getNoOfPayments() + 1);

		}
		addLoanActivity(buildLoanActivity(accountPayment.getAccountTrxns(),
				paymentData.getPersonnel(), "Payment rcvd."));
		return accountPayment;
	}

	private LoanActivityEntity buildLoanActivity(
			Collection<AccountTrxnEntity> accountTrxnDetails,
			PersonnelBO personnel, String comments) {
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
		return new LoanActivityEntity(this,personnel, comments,
				principal, loanSummary.getOriginalPrincipal().subtract(
				loanSummary.getPrincipalPaid()),  interest,
				loanSummary.getOriginalInterest().subtract(
				loanSummary.getInterestPaid()), fees, loanSummary.getOriginalFees().subtract(
				loanSummary.getFeesPaid()),	penalty, loanSummary.getOriginalPenalty().subtract(
				loanSummary.getPenaltyPaid()));
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

	public void updateTotalFeeAmount(Money totalFeeAmount) {
		LoanSummaryEntity loanSummaryEntity = this.getLoanSummary();
		loanSummaryEntity.setOriginalFees(loanSummaryEntity.getOriginalFees()
				.subtract(totalFeeAmount));
	}

	public void roundInstallments(List<Short> installmentIdList) {
		if (!getLoanOffering().isPrincipalDueInLastInstallment()) {
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

	public void updateTotalPenaltyAmount(Money totalPenaltyAmount) {
		LoanSummaryEntity loanSummaryEntity = this.getLoanSummary();
		loanSummaryEntity.setOriginalPenalty(loanSummaryEntity
				.getOriginalPenalty().subtract(totalPenaltyAmount));
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
			PersonnelBO personnel = new PersonnelPersistenceService()
					.getPersonnel(getUserContext().getId());
			addLoanActivity(buildLoanActivity(reversedTrxns, personnel,
					"Loan Adjusted"));
		}
	}

	public void disburseLoan(String recieptNum, Date transactionDate,
			Short paymentTypeId, PersonnelBO personnel, Date receiptDate,
			Short rcvdPaymentTypeId) throws AccountException, SystemException,
			RepaymentScheduleException, FinancialException {
		AccountPaymentEntity accountPaymentEntity = null;

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
						this.getAccountState(), newState, personnel));
		this.setAccountState(newState);

		if (null != this.intrestAtDisbursement
				&& this.intrestAtDisbursement == 1) {
			accountPaymentEntity = payInterestAtDisbursement(recieptNum,
					transactionDate, rcvdPaymentTypeId, personnel, receiptDate);
		} else {
			if (new LoanPersistance().getFeeAmountAtDisbursement(this.getAccountId(),
					transactionDate) > 0.0)
				accountPaymentEntity = insertOnlyFeeAtDisbursement(recieptNum,
						transactionDate, rcvdPaymentTypeId, personnel);
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
		LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(
				accountPaymentEntity,
				transactionDate,
				new LoanPersistance()
						.getAccountActionEntity(AccountConstants.ACTION_DISBURSAL),
				personnel, "-", Short.valueOf("0"), this.loanAmount);

		List<AccountTrxnEntity> loanTrxns = new ArrayList<AccountTrxnEntity>();
		loanTrxns.add(loanTrxnDetailEntity);

		addLoanActivity(buildLoanActivity(loanTrxns, personnel,
				"Loan Disbursal"));
		accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);
		this.addAccountPayment(accountPaymentEntity);
		this.buildFinancialEntries(accountPaymentEntity.getAccountTrxns());

		// Client performance entry
		updateCustomerHistoryOnDisbursement(this.loanAmount);
		if (getPerformanceHistory() != null)
			getPerformanceHistory().setLoanMaturityDate(
					getLastInstallmentAccountAction().getActionDate());
		new AccountPersistanceService().update(this);
	}

	private void initializeAmounts(
			AccountActionDateEntity accountActionDateEntity) {
		LoanScheduleEntity entity = (LoanScheduleEntity) accountActionDateEntity;

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
		repaymntScheduleInputs.setGraceType(this.getGracePeriodType().getId());
		repaymntScheduleInputs.setIsInterestDedecutedAtDisburesement(this
				.getIntrestAtDisbursement().equals(Short.valueOf("1")) ? true
				: false);
		repaymntScheduleInputs.setIsPrincipalInLastPayment(false);
		repaymntScheduleInputs.setPrincipal(this.getLoanAmount());
		repaymntScheduleInputs.setInterestRate(this.getInterestRateAmount()
				.getAmountDoubleValue());
		repaymntScheduleInputs.setNoOfInstallments(this.getNoOfInstallments());
		repaymntScheduleInputs.setInterestType(this.getInterestType().getId());
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
		return RepaymentScheduleHelper.getActionDateEntity(repaymentSchedule,"Loan",this,getCustomer());
		// set the customer'sMeeting , this is required to check if the
		// disbursement date is valid
		// this would be null if customer does not have a meeting.

	}

	private AccountPaymentEntity insertOnlyFeeAtDisbursement(String recieptNum,
			Date recieptDate, Short paymentTypeId, PersonnelBO personnel) {

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

		LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(
				accountPaymentEntity,
				recieptDate,
				new LoanPersistance()
						.getAccountActionEntity(AccountConstants.ACTION_FEE_REPAYMENT),
				personnel, "-", Short.valueOf("0"), totalPayment,
				getAccountFees());

		accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);

		addLoanActivity(buildLoanActivity(accountPaymentEntity
				.getAccountTrxns(), personnel, "Payment rcvd."));
		return accountPaymentEntity;
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
			List<AccountActionDateEntity> accountActions,
			PersonnelBO personnel, String recieptId, Short paymentId,
			Date receiptDate, Date transactionDate) {
		PaymentData paymentData = new PaymentData(totalAmount, personnel,
				paymentId, transactionDate);
		paymentData.setRecieptDate(receiptDate);
		paymentData.setRecieptNum(recieptId);
		for (AccountActionDateEntity actionDate : accountActions) {
			LoanPaymentData loanPaymentData = new LoanPaymentData(actionDate);
			paymentData.addAccountPaymentData(loanPaymentData);
		}
		return paymentData;
	}

	protected Money getDueAmount(AccountActionDateEntity installment) {
		return ((LoanScheduleEntity) installment).getTotalDueWithFees();
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
					.getPrincipal());
		}
		return amount;
	}

	public void makeEarlyRepayment(Money totalAmount, String receiptNumber,
			Date recieptDate, String paymentTypeId, Short personnelId)
			throws ServiceException, AccountException {
		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.MasterDataService);
		PersonnelBO personnel = new PersonnelPersistenceService()
				.getPersonnel(personnelId);
		this.setUpdatedBy(personnelId);
		this.setUpdatedDate(new Date(System.currentTimeMillis()));
		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(
				this, totalAmount, receiptNumber, recieptDate,
				new PaymentTypeEntity(Short.valueOf(paymentTypeId)));
		this.addAccountPayment(accountPaymentEntity);
		List<AccountActionDateEntity> dueInstallmentsList = getApplicableIdsForDueInstallments();
		List<AccountActionDateEntity> futureInstallmentsList = getApplicableIdsForFutureInstallments();
		for (AccountActionDateEntity accountActionDateEntity : dueInstallmentsList) {
			LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
			Money principal = loanSchedule.getPrincipal();
			Money interest = loanSchedule.getInterest();
			Money fees = loanSchedule.getTotalFees();
			Money penalty = loanSchedule.getTotalPenalty();
			Money totalAmt = principal.add(interest).add(fees).add(penalty);
			loanSchedule
					.makeEarlyRepaymentEnteries(LoanConstants.PAY_FEES_PENALTY_INTEREST);

			LoanPaymentData loanPaymentData = new LoanPaymentData(
					accountActionDateEntity);

			LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(
					accountPaymentEntity, loanPaymentData, personnel, new Date(
							System.currentTimeMillis()),
					(AccountActionEntity) masterPersistenceService.findById(
							AccountActionEntity.class,
							AccountConstants.ACTION_LOAN_REPAYMENT), totalAmt,
					"Payment rcvd.");
			accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);
			loanSummary
					.updatePaymentDetails(principal, interest, penalty, fees);
		}
		for (AccountActionDateEntity accountActionDateEntity : futureInstallmentsList) {
			LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
			Money principal = loanSchedule.getPrincipal();
			Money interest = loanSchedule.getInterest();
			Money fees = loanSchedule.getTotalFees();
			Money penalty = loanSchedule.getTotalPenalty();

			loanSchedule
					.makeEarlyRepaymentEnteries(LoanConstants.DONOT_PAY_FEES_PENALTY_INTEREST);

			LoanPaymentData loanPaymentData = new LoanPaymentData(
					accountActionDateEntity);

			LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(
					accountPaymentEntity, loanPaymentData, personnel, new Date(
							System.currentTimeMillis()),
					(AccountActionEntity) masterPersistenceService.findById(
							AccountActionEntity.class,
							AccountConstants.ACTION_LOAN_REPAYMENT), principal,
					"Payment rcvd.");

			accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);

			loanSummary.decreaseBy(null, interest, penalty, fees);
			loanSummary.updatePaymentDetails(principal, null, null, null);

		}
		try {
			if (getPerformanceHistory() != null)
				getPerformanceHistory().setNoOfPayments(
						getPerformanceHistory().getNoOfPayments() + 1);
			addLoanActivity(buildLoanActivity(accountPaymentEntity
					.getAccountTrxns(), personnel, "Loan Repayment"));
			buildFinancialEntries(accountPaymentEntity.getAccountTrxns());
		} catch (FinancialException fe) {
			throw new AccountException("errors.update", fe);
		}

		AccountStateEntity newAccountState = (AccountStateEntity) masterPersistenceService
				.findById(AccountStateEntity.class,
						AccountStates.LOANACC_OBLIGATIONSMET);
		this
				.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
						this.getAccountState(), newAccountState,
						getPersonnelDBService().getPersonnel(personnelId)));
		this.setAccountState((AccountStateEntity) masterPersistenceService
				.findById(AccountStateEntity.class,
						AccountStates.LOANACC_OBLIGATIONSMET));
		this.setClosedDate(new Date(System.currentTimeMillis()));

		// Client performance entry
		updateCustomerHistoryOnRepayment(totalAmount);

		new LoanPersistance().createOrUpdate(this);
	}

	private void regeneratePaymentSchedule(Date transactionDate)
			throws RepaymentScheduleException {
		Set<AccountActionDateEntity> accntActionDateEntitySet = null;

		accntActionDateEntitySet = generateRepaymentSchedule(transactionDate);
		reAssociateFeePenaltyAtDisbursal(accntActionDateEntitySet);
		for (AccountActionDateEntity entity : accntActionDateEntitySet) {
			initializeAmounts(entity);
			if (entity.getPaymentStatus() == null) {
				entity.setPaymentStatus(PaymentStatus.UNPAID.getValue());
			}
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
				LoanScheduleEntity loanScheduleEntityExisting = (LoanScheduleEntity) entityExisting;
				LoanScheduleEntity loanScheduleEntityNew = (LoanScheduleEntity) entityExisting;
				loanScheduleEntityNew.setMiscFee(loanScheduleEntityExisting
						.getMiscFee());
				loanScheduleEntityNew.setMiscPenalty(loanScheduleEntityExisting
						.getMiscPenalty());
				if (null == this.intrestAtDisbursement
						|| this.intrestAtDisbursement != 1)
					reAssociateTimeOfDisbursalFee(entityNew);
			}
		}

	}

	private void reAssociateTimeOfDisbursalFee(AccountActionDateEntity entityNew) {
		LoanScheduleEntity entityExisting = (LoanScheduleEntity) getAccountActionDate(Short
				.valueOf("1"));
		for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
			if (accountFeesEntity.isTimeOfDisbursement()) {
				AccountFeesActionDetailEntity accountFeesAction = entityExisting
						.getAccountFeesAction(accountFeesEntity
								.getAccountFeeId());
				if (accountFeesAction != null) {
					AccountFeesActionDetailEntity accountFeesActionDetail = new LoanFeeScheduleEntity(
							entityNew, entityNew.getInstallmentId(),
							accountFeesEntity.getFees(), accountFeesEntity,
							accountFeesAction.getFeeAmount());
					accountFeesActionDetail.setFeeAmountPaid(new Money());
					((LoanScheduleEntity) entityNew)
							.addAccountFeesAction(accountFeesActionDetail);
				}
			}

		}
	}

	private AccountPaymentEntity payInterestAtDisbursement(String recieptNum,
			Date transactionDate, Short paymentTypeId, PersonnelBO personnel,
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

	public void updateAccountActivity(Money totalAmount, Short personnelId,
			String description) {
		PersonnelBO personnel = new PersonnelPersistenceService()
				.getPersonnel(personnelId);
		LoanActivityEntity loanActivity = new LoanActivityEntity(this,personnel, description,
				new Money(), loanSummary.getOriginalPrincipal().subtract(
				loanSummary.getPrincipalPaid()),  new Money(),
				loanSummary.getOriginalInterest().subtract(
				loanSummary.getInterestPaid()), totalAmount, loanSummary.getOriginalFees().subtract(
				loanSummary.getFeesPaid()),	new Money(), loanSummary.getOriginalPenalty().subtract(
				loanSummary.getPenaltyPaid()));
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

		new LoanPersistance().createOrUpdate(this);
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

	public void writeOff(String comment) throws ServiceException,
			SecurityException, PersistenceException, ApplicationException {
		Short personnelId = this.getUserContext().getId();
		PersonnelBO personnel = new PersonnelPersistenceService()
				.getPersonnel(personnelId);
		Short statusId = Short.valueOf(AccountStates.LOANACC_WRITTENOFF);
		this.setUpdatedBy(personnelId);
		this.setUpdatedDate(new Date(System.currentTimeMillis()));
		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(
				this, getEarlyClosureAmount(), null, null,
				new PaymentTypeEntity(Short.valueOf("1")));
		this.addAccountPayment(accountPaymentEntity);
		for (AccountActionDateEntity accountActionDateEntity : getListOfUnpaidInstallments()) {
			MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
					.getInstance().getPersistenceService(
							PersistenceServiceName.MasterDataService);
			LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(
					accountPaymentEntity,
					(AccountActionEntity) masterPersistenceService.findById(
							AccountActionEntity.class,
							AccountConstants.ACTION_WRITEOFF),
					accountActionDateEntity, personnel, "Loan Written Off");
			accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);
		}
		addLoanActivity(buildLoanActivity(accountPaymentEntity
				.getAccountTrxns(), personnel, "Loan Written Off"));
		buildFinancialEntries(accountPaymentEntity.getAccountTrxns());
		changeStatus(statusId, null, comment);

		// Client performance entry
		updateCustomerHistoryOnWriteOff();

		getAccountPersistenceService().update(this);
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

	@Override
	public void waiveAmountDue(WaiveEnum waiveType) throws ServiceException {
		if (waiveType.equals(WaiveEnum.FEES)) {
			waiveFeeAmountDue();
		} else if (waiveType.equals(WaiveEnum.PENALTY)) {
			waivePenaltyAmountDue();
		}
	}

	@Override
	public void waiveAmountOverDue(WaiveEnum waiveType) throws ServiceException {
		if (waiveType.equals(WaiveEnum.FEES)) {
			waiveFeeAmountOverDue();
		} else if (waiveType.equals(WaiveEnum.PENALTY)) {
			waivePenaltyAmountOverDue();
		}
	}

	public void waiveFeeAmountDue() throws ServiceException {
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountActionDateList
				.get(accountActionDateList.size() - 1);
		Money chargeWaived = accountActionDateEntity.waiveFeeCharges();
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateAccountActivity(chargeWaived, userContext.getId(), "Amnt "
					+ chargeWaived + " waived");
			updateTotalFeeAmount(chargeWaived);
		}
		getAccountPersistenceService().update(this);
	}

	public void waivePenaltyAmountDue() throws ServiceException {
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountActionDateList
				.get(accountActionDateList.size() - 1);
		Money chargeWaived = accountActionDateEntity.waivePenaltyCharges();
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateAccountActivity(chargeWaived, userContext.getId(), "Amnt "
					+ chargeWaived + " waived");
			updateTotalPenaltyAmount(chargeWaived);
		}
		getAccountPersistenceService().update(this);
	}

	public void waiveFeeAmountOverDue() throws ServiceException {
		Money chargeWaived = new Money();
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		accountActionDateList.remove(accountActionDateList.size() - 1);
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
			chargeWaived = chargeWaived
					.add(((LoanScheduleEntity) accountActionDateEntity)
							.waiveFeeCharges());
		}
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateAccountActivity(chargeWaived, userContext.getId(), "Amnt "
					+ chargeWaived + " waived");
			updateTotalFeeAmount(chargeWaived);
		}
		getAccountPersistenceService().update(this);
	}

	public void waivePenaltyAmountOverDue() throws ServiceException {
		Money chargeWaived = new Money();
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		accountActionDateList.remove(accountActionDateList.size() - 1);
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
			chargeWaived = chargeWaived
					.add(((LoanScheduleEntity) accountActionDateEntity)
							.waivePenaltyCharges());
		}
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateAccountActivity(chargeWaived, userContext.getId(), "Amnt "
					+ chargeWaived + " waived");
			updateTotalPenaltyAmount(chargeWaived);
		}
		getAccountPersistenceService().update(this);
	}

	@Override
	protected void regenerateFutureInstallments(Short nextIntallmentId)
			throws PersistenceException, SchedulerException {
		if (!this.getAccountState().getId().equals(
				AccountStates.LOANACC_OBLIGATIONSMET)
				&& !this.getAccountState().getId().equals(
						AccountStates.LOANACC_WRITTENOFF)
				&& !this.getAccountState().getId().equals(
						AccountStates.LOANACC_CANCEL)) {
			SchedulerIntf scheduler = SchedulerHelper
					.getScheduler(getCustomer().getCustomerMeeting()
							.getMeeting());
			List<Date> meetingDates = scheduler
					.getAllDates(getApplicableIdsForFutureInstallments().size() + 1);
			meetingDates.remove(0);
			int count = 0;
			List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForFutureInstallments();
			for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
				accountActionDateEntity.setActionDate(new java.sql.Date(
						meetingDates.get(count++).getTime()));
			}
		}
	}

	public Money getAmountTobePaidAtdisburtail(Date disbursalDate) {

		if (this.intrestAtDisbursement != null
				&& this.intrestAtDisbursement.intValue() == 1) {
			return getDueAmount(getAccountActionDate(Short.valueOf("1")));
		} else {
			return new Money(new LoanPersistance().getFeeAmountAtDisbursement(
					this.getAccountId(), disbursalDate).toString());
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
		if (!getDetailsOfInstallmentsInArrears().isEmpty()) {
			AccountActionDateEntity accountActionDateEntity = getDetailsOfInstallmentsInArrears()
					.get(getDetailsOfInstallmentsInArrears().size() - 1);
			Calendar actionDate = new GregorianCalendar();
			actionDate.setTime(accountActionDateEntity.getActionDate());
			long diffInTermsOfDay = (Calendar.getInstance().getTimeInMillis() - actionDate
					.getTimeInMillis())
					/ (24 * 60 * 60 * 1000);
			return Integer.valueOf(new Long(diffInTermsOfDay).toString());
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
		List<AccountActionDateEntity> accountActionDateList = getDetailsOfInstallmentsInArrears();
		if (!accountActionDateList.isEmpty())
			noOfMissedPayments = +accountActionDateList.size();
		noOfMissedPayments = noOfMissedPayments + getNoOfBackDatedPayments();
		return noOfMissedPayments;
	}

	public Money getTotalPrincipalAmountInArrears() {
		Money amount = new Money();
		List<AccountActionDateEntity> actionDateList = getDetailsOfInstallmentsInArrears();
		for (AccountActionDateEntity accountActionDateEntity : actionDateList) {
			amount = amount.add(((LoanScheduleEntity) accountActionDateEntity)
					.getPrincipal());
		}
		return amount;
	}

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
				&& getCustomer().getCustomerPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getCustomerPerformanceHistory();
			clientPerfHistory.setLastLoanAmount(totalAmount);
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() - 1);
		}
	}

	private void updateCustomerHistoryOnPayment() {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerConstants.CLIENT_LEVEL_ID))
				&& getCustomer().getCustomerPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getCustomerPerformanceHistory();
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() - 1);
		}
	}

	private void updateCustomerHistoryOnDisbursement(Money disburseAmount) {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerConstants.CLIENT_LEVEL_ID))
				&& getCustomer().getCustomerPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getCustomerPerformanceHistory();
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() + 1);
			clientPerfHistory.setLoanCycleNumber(clientPerfHistory
					.getLoanCycleNumber() + 1);
		} else if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerConstants.GROUP_LEVEL_ID))
				&& getCustomer().getCustomerPerformanceHistory() != null) {
			GroupPerformanceHistoryEntity groupPerformanceHistoryEntity = (GroupPerformanceHistoryEntity) getCustomer()
					.getCustomerPerformanceHistory();
			groupPerformanceHistoryEntity
					.setLastGroupLoanAmount(disburseAmount);
		}
	}

	private void updateCustomerHistoryOnRepayment(Money totalAmount) {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerConstants.CLIENT_LEVEL_ID))
				&& getCustomer().getCustomerPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getCustomerPerformanceHistory();
			clientPerfHistory.setLastLoanAmount(totalAmount);
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() - 1);
		}
	}

	private void updateCustomerHistoryOnArrears() {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerConstants.CLIENT_LEVEL_ID))
				&& getCustomer().getCustomerPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getCustomerPerformanceHistory();
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() + 1);
		}
	}

	private void updateCustomerHistoryOnWriteOff() {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerConstants.CLIENT_LEVEL_ID))
				&& getCustomer().getCustomerPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getCustomerPerformanceHistory();
			clientPerfHistory.setLoanCycleNumber(clientPerfHistory
					.getLoanCycleNumber() - 1);
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() - 1);
		}
	}

	protected void updatePerformanceHistoryOnAdjustment(Integer noOfTrxnReversed) {
		if (getPerformanceHistory() != null) {
			getPerformanceHistory().setNoOfPayments(
					getPerformanceHistory().getNoOfPayments()
							- noOfTrxnReversed);
		}
	}

	private PersonnelPersistenceService getPersonnelDBService()
			throws ServiceException {
		return (PersonnelPersistenceService) ServiceFactory.getInstance()
				.getPersistenceService(PersistenceServiceName.Personnel);
	}

	public void initializeStateMachine(Short localeId)
			throws StatesInitializationException {
		AccountStateMachines
				.getInstance()
				.initialize(
						localeId,
						getOffice().getOfficeId(),
						AccountTypes.LOANACCOUNT
								.getValue());
	}

	public List<AccountStateEntity> getStatusList() {
		List<AccountStateEntity> statusList = AccountStateMachines
				.getInstance().getStatusList(this.getAccountState(),
						AccountTypes.LOANACCOUNT.getValue());
		for (AccountStateEntity accStateObj : statusList) {
			accStateObj.setLocaleId(userContext.getLocaleId());
		}
		return statusList;
	}

	public String getStatusName(Short localeId, Short accountStateId)
			throws ApplicationException, SystemException {
		return AccountStateMachines.getInstance().getStatusName(localeId,
				accountStateId, AccountTypes.LOANACCOUNT.getValue());
	}

	public String getFlagName(Short flagId) throws ApplicationException,
			SystemException {
		return AccountStateMachines.getInstance().getFlagName(flagId,
				AccountTypes.LOANACCOUNT.getValue());
	}
}
