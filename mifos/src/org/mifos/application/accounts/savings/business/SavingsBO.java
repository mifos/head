/**

 * SavingsBO.java    version: 1.0

 

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
package org.mifos.application.accounts.savings.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFlagMapping;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateFlagEntity;
import org.mifos.application.accounts.business.AccountStateMachines;
import org.mifos.application.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.exceptions.AccountExceptionConstants;
import org.mifos.application.accounts.exceptions.IDGenerationException;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.persistence.service.SavingsPersistenceService;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountPaymentData;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.master.util.valueobjects.AccountType;
import org.mifos.application.master.util.valueobjects.InterestCalcType;
import org.mifos.application.master.util.valueobjects.RecommendedAmntUnit;
import org.mifos.application.master.util.valueobjects.SavingsType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.application.meeting.business.MeetingRecurrenceEntity;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.interestcalculator.InterestCalculatorConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.helpers.SchedulerHelper;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class SavingsBO extends AccountBO {

	private Money recommendedAmount;

	private Money savingsBalance;

	private SavingsOfferingBO savingsOffering;

	private SavingsPerformanceEntity savingsPerformance;

	private Date activationDate;

	private RecommendedAmntUnit recommendedAmntUnit;

	private SavingsType savingsType;

	private Money interestToBePosted;

	private Date lastIntCalcDate;

	private Date lastIntPostDate;

	private Date nextIntCalcDate;

	private Date nextIntPostDate;

	private Date interIntCalcDate;

	private Money minAmntForInt;

	private Double interestRate;

	private InterestCalcType interestCalcType;

	private MeetingBO timePerForInstcalc;

	// private MeetingBO freqOfPostIntcalc;

	private Set<SavingsActivityEntity> savingsActivityDetails;

	private SavingsPersistenceService dbService;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	private SavingsHelper helper = new SavingsHelper();

	protected SavingsBO() {
		savingsActivityDetails = new HashSet<SavingsActivityEntity>();
	}

	public SavingsBO(UserContext userContext) {
		super(userContext);
		savingsActivityDetails = new HashSet<SavingsActivityEntity>();
	}

	public Money getRecommendedAmount() {
		return recommendedAmount;
	}

	public void setRecommendedAmount(Money recommendedAmount) {
		this.recommendedAmount = recommendedAmount;
	}

	public Money getSavingsBalance() {
		return savingsBalance;
	}

	public void setSavingsBalance(Money savingsBalance) {
		this.savingsBalance = savingsBalance;
	}

	public SavingsOfferingBO getSavingsOffering() {
		return savingsOffering;
	}

	public void setSavingsOffering(SavingsOfferingBO savingsOffering) {
		this.savingsOffering = savingsOffering;
	}

	public SavingsPerformanceEntity getSavingsPerformance() {
		return savingsPerformance;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public RecommendedAmntUnit getRecommendedAmntUnit() {
		return recommendedAmntUnit;
	}

	public void setRecommendedAmntUnit(RecommendedAmntUnit recommendedAmntUnit) {
		this.recommendedAmntUnit = recommendedAmntUnit;
	}

	public SavingsType getSavingsType() {
		return savingsType;
	}

	public void setSavingsType(SavingsType savingsType) {
		this.savingsType = savingsType;
	}

	public Money getInterestToBePosted() {
		return interestToBePosted;
	}

	public void setInterestToBePosted(Money interestToBePosted) {
		this.interestToBePosted = interestToBePosted;
	}

	public Date getInterIntCalcDate() {
		return interIntCalcDate;
	}

	public void setInterIntCalcDate(Date interIntCalcDate) {
		this.interIntCalcDate = interIntCalcDate;
	}

	public Date getLastIntCalcDate() {
		return lastIntCalcDate;
	}

	public void setLastIntCalcDate(Date lastIntCalcDate) {
		this.lastIntCalcDate = lastIntCalcDate;
	}

	public Date getLastIntPostDate() {
		return lastIntPostDate;
	}

	public void setLastIntPostDate(Date lastIntPostDate) {
		this.lastIntPostDate = lastIntPostDate;
	}

	public Date getNextIntCalcDate() {
		return nextIntCalcDate;
	}

	public void setNextIntCalcDate(Date nextIntCalcDate) {
		this.nextIntCalcDate = nextIntCalcDate;
	}

	public Date getNextIntPostDate() {
		return nextIntPostDate;
	}

	public void setNextIntPostDate(Date nextIntPostDate) {
		this.nextIntPostDate = nextIntPostDate;
	}

	/*
	 * public MeetingBO getFreqOfPostIntcalc() { return freqOfPostIntcalc; }
	 */

	public InterestCalcType getInterestCalcType() {
		return interestCalcType;
	}

	public Double getInterestRate() {
		return interestRate;
	}

	public Money getMinAmntForInt() {
		return minAmntForInt;
	}

	public MeetingBO getTimePerForInstcalc() {
		return timePerForInstcalc;
	}

	/*
	 * private void setFreqOfPostIntcalc(MeetingBO freqOfPostIntcalc) {
	 * this.freqOfPostIntcalc = freqOfPostIntcalc; }
	 */
	private void setInterestCalcType(InterestCalcType interestCalcType) {
		this.interestCalcType = interestCalcType;
	}

	private void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	private void setMinAmntForInt(Money minAmntForInt) {
		this.minAmntForInt = minAmntForInt;
	}

	private void setTimePerForInstcalc(MeetingBO timePerForInstcalc) {
		this.timePerForInstcalc = timePerForInstcalc;
	}

	public Set<SavingsActivityEntity> getSavingsActivityDetails() {
		return savingsActivityDetails;
	}

	public void setSavingsActivityDetails(
			Set<SavingsActivityEntity> savingsActivityDetails) {
		this.savingsActivityDetails = savingsActivityDetails;
	}

	public void addSavingsActivityDetails(SavingsActivityEntity savingsActivity) {
		if (savingsActivityDetails == null)
			savingsActivityDetails = new HashSet<SavingsActivityEntity>();
		savingsActivity.setAccount(this);
		savingsActivityDetails.add(savingsActivity);
	}

	@Override
	public AccountTypes getType(){
		return AccountTypes.SAVINGSACCOUNT;
	}
	
	@Override
	public boolean isOpen(){
		return !(getAccountState().getId().equals(AccountState.SAVINGS_ACC_CANCEL.getValue()) 
				|| getAccountState().getId().equals(AccountState.SAVINGS_ACC_CLOSED.getValue()));
	}
	
	private void setSavingsOfferingDetails() {
		setMinAmntForInt(getSavingsOffering().getMinAmntForInt());
		setInterestRate(getSavingsOffering().getInterestRate());
		setInterestCalcType(getSavingsOffering().getInterestCalcType());
		setTimePerForInstcalc(getMeeting(getSavingsOffering()
				.getTimePerForInstcalc().getMeeting()));
		// setFreqOfPostIntcalc(getMeeting(getSavingsOffering().getFreqOfPostIntcalc().getMeeting()));
	}

	private MeetingBO getMeeting(MeetingBO offeringMeeting) {
		MeetingBO meeting = new MeetingBO();
		meeting.setMeetingStartDate(offeringMeeting.getMeetingStartDate());
		meeting.setMeetingStartTime(offeringMeeting.getMeetingStartTime());
		meeting.setMeetingEndDate(offeringMeeting.getMeetingEndDate());
		meeting.setMeetingEndTime(offeringMeeting.getMeetingEndTime());
		meeting.setMeetingPlace(offeringMeeting.getMeetingPlace());
		meeting.setMeetingType(offeringMeeting.getMeetingType());

		MeetingDetailsEntity meetingDetails = new MeetingDetailsEntity();
		meetingDetails.setRecurAfter(offeringMeeting.getMeetingDetails()
				.getRecurAfter());
		meetingDetails.setRecurrenceType(offeringMeeting.getMeetingDetails()
				.getRecurrenceType());

		MeetingRecurrenceEntity meetingRecurrence = new MeetingRecurrenceEntity();
		meetingRecurrence.setWeekDay(offeringMeeting.getMeetingDetails()
				.getMeetingRecurrence().getWeekDay());
		meetingRecurrence.setRankOfDays(offeringMeeting.getMeetingDetails()
				.getMeetingRecurrence().getRankOfDays());
		meetingRecurrence.setDayNumber(offeringMeeting.getMeetingDetails()
				.getMeetingRecurrence().getDayNumber());

		meetingDetails.setMeetingRecurrence(meetingRecurrence);
		meeting.setMeetingDetails(meetingDetails);

		return meeting;
	}

	private void setSavingsPerformance(
			SavingsPerformanceEntity savingsPerformance) {
		if (savingsPerformance != null)
			savingsPerformance.setSavings(this);
		this.savingsPerformance = savingsPerformance;
	}

	private SavingsPerformanceEntity createSavingsPerformance() {
		SavingsPerformanceEntity savingsPerformance = new SavingsPerformanceEntity();
		logger
				.info("In SavingsBO::createSavingsPerformance(), SavingsPerformanceEntity created successfully ");
		return savingsPerformance;
	}

	/* Need to remove while refactoring */
	public void setOffice(OfficeBO office) {
		this.office = office;
	}

	/* Need to remove while refactoring */
	public void setGlobalAccountNum(String globalAccountNum) {
		this.globalAccountNum = globalAccountNum;
	}

	/* Need to remove while refactoring */
	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	/* Need to remove while refactoring */
	public void setCustomer(CustomerBO customer) {
		this.customer = customer;
	}

	/* Need to remove while refactoring */
	public void setPersonnel(PersonnelBO personnel) {
		this.personnel = personnel;
	}

	public void save() throws AccountException{
		logger.info("In SavingsBO::save(), Before Saving , accountId: "
				+ getAccountId());
		try {
			this.setGlobalAccountNum(generateId(userContext.getBranchGlobalNum()));
		} catch (IDGenerationException e) {
			throw new AccountException(e);
		}
		logger.info("In SavingsBO::save(), Generated globalAccountNum: "
				+ getGlobalAccountNum());
		setOffice(getCustomer().getOffice());
		setAccountType(new AccountType(AccountTypes.SAVINGSACCOUNT.getValue()));
		setSavingsPerformance(createSavingsPerformance());
		setSavingsBalance(new Money());
		this.setCreatedBy(userContext.getId());
		this.setCreatedDate(new Date());
		this.setSavingsType(getSavingsOffering().getSavingsType());
		this.setRecommendedAmntUnit(getSavingsOffering()
				.getRecommendedAmntUnit());
		this.setSavingsOfferingDetails();

		// generated the deposit action dates only if savings account is being
		// saved in approved state
		if (isActive())
			setValuesForActiveState();

		// security check befor saving
		checkPermissionForSave(this.getAccountState(), userContext, null);
		this
				.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
						this.getAccountState(), this.getAccountState(),
						(new PersonnelPersistence()).getPersonnel(
								userContext.getId())));
		try {
			(new SavingsPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
		logger.info("In SavingsBO::save(), Successfully saved , accountId: "
				+ getAccountId());
	}

	public void update() throws AccountException{
		logger.debug("In SavingsBO::update(), accountId: " + getAccountId());
		this.setUpdatedBy(userContext.getId());
		this.setUpdatedDate(new Date());
		try {
			(new SavingsPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
		logger
				.info("In SavingsBO::update(), successfully updated , accountId: "
						+ getAccountId());
	}

	public void updateAndGenerateSchedule() throws AccountException {
		logger.debug("In SavingsBO::updateSchedule(), accountId: "
				+ getAccountId());
		for (AccountActionDateEntity accountDate : this.getAccountActionDates()) {
			if (accountDate.getActionDate().compareTo(helper.getCurrentDate()) >= 0)
				((SavingsScheduleEntity) accountDate)
						.setDeposit(getRecommendedAmount());
		}
		update();
		logger
				.info("In SavingsBO::updateSchedule(), successfully updated Deposit Schedule, accountId: "
						+ getAccountId());
	}

	public boolean isMandatory() {
		logger.debug("In SavingsBO::isMandatory(), savingTypeId: "
				+ getSavingsType().getSavingsTypeId());
		return getSavingsType().getSavingsTypeId().shortValue() == ProductDefinitionConstants.MANDATORY
				.shortValue();
	}

	public boolean isDepositScheduleBeRegenerated() {
		logger
				.debug("In SavingsBO::isDepositScheduleBeRegenerated(), accountStateId: "
						+ getAccountState().getId());
		return (getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_APPROVED || getAccountState()
				.getId().shortValue() == AccountStates.SAVINGS_ACC_INACTIVE);
	}

	public boolean isActive() {
		return getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_APPROVED;
	}

	public Money calculateInterestForClosure(Date closureDate)
			throws AccountException{
		Money interestCalculated = calculateInterest(null, closureDate,
				getInterestRate(), null);
		return getInterestToBePosted() == null ? interestCalculated
				: getInterestToBePosted().add(interestCalculated);
	}

	public void postInterest() throws AccountException {
		if (getInterestToBePosted() != null
				&& getInterestToBePosted().getAmountDoubleValue() > 0) {
			Money interestPosted = getInterestToBePosted();
			setSavingsBalance(getSavingsBalance().add(interestPosted));
			setInterestToBePosted(new Money());
			setLastIntPostDate(getNextIntPostDate());
			try {
				setNextIntPostDate(helper.getNextScheduleDate(getActivationDate(),
						getLastIntPostDate(), getMeeting(getSavingsOffering()
								.getFreqOfPostIntcalc().getMeeting())));
			} catch (SchedulerException e) {
				throw new AccountException(e);
			}
			PaymentTypeEntity paymentType=null;
			try {
				paymentType = (PaymentTypeEntity) HibernateUtil
						.getSession().get(PaymentTypeEntity.class,
								SavingsConstants.DEFAULT_PAYMENT_TYPE);
			} catch (HibernateException e) {
				throw new AccountException(e);
			} catch (HibernateProcessException e) {
				throw new AccountException(e);
			}
			makeEntriesForInterestPosting(interestPosted, paymentType,
					getCustomer(), null);
			getSavingsPerformance().setTotalInterestDetails(interestPosted);
			try {
				(new SavingsPersistence()).createOrUpdate(this);
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}
			logger.info("In SavingsBO::postInterest(), accountId: "
					+ getAccountId() + ", Interest Of Amount: "
					+ interestPosted + " successfully");
		}
	}

	public void updateInterestAccrued() throws AccountException {
		if (getInterestToBePosted() == null)
			setInterestToBePosted(new Money());
		Money interestCalculated = calculateInterest(null,
				getNextIntCalcDate(), getInterestRate(), null);
		setInterestToBePosted(getInterestToBePosted().add(interestCalculated));
		setLastIntCalcDate(getNextIntCalcDate());
		try {
			setNextIntCalcDate(helper.getNextScheduleDate(getActivationDate(),
					getLastIntCalcDate(), getTimePerForInstcalc()));
		} catch (SchedulerException e) {
			throw new AccountException(e);
		}
		try {
			(new SavingsPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
		logger.info("In SavingsBO::updateInterestAccrued(), accountId: "
				+ getAccountId() + ", Interest Amount: " + interestCalculated
				+ " calculated.");
	}

	private Money calculateInterest(Date fromDate, Date toDate,
			double interestRate, SavingsTrxnDetailEntity adjustedTrxn)
			throws AccountException{
		if (fromDate == null)
			fromDate = getFromDate();

		SavingsTrxnDetailEntity trxn = null;
		try{
			if (getActivationDate().equals(fromDate)) {
				trxn = (new SavingsPersistence()).retrieveFirstTransaction(getAccountId());
				if (trxn != null)
					fromDate = trxn.getActionDate();
			} else {
				trxn = (new SavingsPersistence()).retrieveLastTransaction(getAccountId(),
						fromDate);
				if (trxn == null) {
					trxn = (new SavingsPersistence()).retrieveFirstTransaction(getAccountId());
					if (trxn != null)
						fromDate = trxn.getActionDate();
				}
			}
		}catch(PersistenceException pe){
			throw new AccountException(pe);
		}
		Money principal = null;
		Money interestAmount = new Money();
		if (trxn != null) {
			trxn = (trxn.getAccountPayment().getAmount().getAmountDoubleValue() > 0) ? getLastTrxnForPayment(trxn
					.getAccountPayment())
					: getLastTrxnForAdjustedPayment(trxn.getAccountPayment());
			if (getInterestCalcType().getInterestCalculationTypeID().equals(
					ProductDefinitionConstants.MINIMUM_BALANCE))
				principal = getMinimumBalance(fromDate, toDate, trxn,
						adjustedTrxn);
			else if (getInterestCalcType().getInterestCalculationTypeID()
					.equals(ProductDefinitionConstants.AVERAGE_BALANCE))
				principal = getAverageBalance(fromDate, toDate, trxn,
						adjustedTrxn);
			// Do not Calculate interest if principal amount is less than the
			// minimum amount needed for interest calculation
			if (getMinAmntForInt() != null
					&& principal != null
					&& (getMinAmntForInt().getAmountDoubleValue() == 0 || principal
							.getAmountDoubleValue() >= getMinAmntForInt()
							.getAmountDoubleValue()))
					interestAmount = calculateInterestForDays(principal,
							interestRate, fromDate, toDate);
		}
		logger.info("In SavingsBO::calculateInterest(), accountId: "
				+ getAccountId() + ", from date:" + fromDate + ", toDate:"
				+ toDate + "InterestAmt: " + interestAmount);
		return interestAmount;
	}

	private Date getFromDate() {
		return (getInterIntCalcDate() != null) ? getInterIntCalcDate()
				: (getLastIntCalcDate() != null ? getLastIntCalcDate()
						: getActivationDate());
	}

	public void closeAccount(AccountPaymentEntity payment,
			AccountNotesEntity notes, CustomerBO customer)
			throws AccountException {
		logger.debug("In SavingsBO::closeAccount(), accountId: "
				+ getAccountId());
		PersonnelBO loggedInUser = (new PersonnelPersistence()).getPersonnel(
				userContext.getId());
		AccountStateEntity accountState = this.getAccountState();
		try {
			this.setAccountState((new SavingsPersistence()).getAccountStatusObject(
					AccountStates.SAVINGS_ACC_CLOSED));
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
		setInterestToBePosted(payment.getAmount().subtract(getSavingsBalance()));
		MasterPersistence masterPersistence=new MasterPersistence();
		if (getInterestToBePosted() != null
				&& getInterestToBePosted().getAmountDoubleValue() > 0)
			makeEntriesForInterestPosting(getInterestToBePosted(), payment
					.getPaymentType(), customer, loggedInUser);
		if (payment.getAmount().getAmountDoubleValue() > 0) {
			payment.addAcountTrxn(helper.createAccountPaymentTrxn(payment,
					new Money(),
					(AccountActionEntity) masterPersistence.findById(
							AccountActionEntity.class,
							AccountConstants.ACTION_SAVINGS_WITHDRAWAL),
					customer, loggedInUser));
			payment.setCreatedDate(helper.getCurrentDate());
			this.addAccountPayment(payment);
			addSavingsActivityDetails(buildSavingsActivity(payment.getAmount(),
					new Money(), AccountConstants.ACTION_SAVINGS_WITHDRAWAL,
					loggedInUser));
			getSavingsPerformance().setTotalWithdrawals(
					getSavingsPerformance().getTotalWithdrawals().add(
							payment.getAmount()));
			buildFinancialEntries(payment.getAccountTrxns());
		}
		notes.setCommentDate(new java.sql.Date(helper.getCurrentDate()
				.getTime()));
		notes.setPersonnel(loggedInUser);
		this.addAccountNotes(notes);
		this.setLastIntCalcDate(helper.getCurrentDate());
		this.setLastIntPostDate(helper.getCurrentDate());
		this.setInterIntCalcDate(null);
		this.setSavingsBalance(new Money());
		this.setInterestToBePosted(new Money());
		this.setClosedDate(new Date(System.currentTimeMillis()));
		this
				.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
						accountState, this.getAccountState(), loggedInUser));
		this.update();
		logger
				.debug("In SavingsBO::close(), account closed successfully ; accountId: "
						+ getAccountId());
	}

	private void makeEntriesForInterestPosting(Money interestAmt,
			PaymentTypeEntity paymentType, CustomerBO customer,
			PersonnelBO loggedInUser) throws 
			AccountException {
		AccountPaymentEntity interestPayment = helper.createAccountPayment(
				this, interestAmt, paymentType, loggedInUser);
		MasterPersistence masterPersistence = new MasterPersistence();
		interestPayment.addAcountTrxn(helper.createAccountPaymentTrxn(
				interestPayment, interestAmt,
				(AccountActionEntity) masterPersistence.findById(
						AccountActionEntity.class,
						AccountConstants.ACTION_SAVINGS_INTEREST_POSTING),
				customer, loggedInUser));
		this.addAccountPayment(interestPayment);
		if (userContext == null)
			addSavingsActivityDetails(buildSavingsActivity(interestAmt,
					getSavingsBalance().add(interestAmt),
					AccountConstants.ACTION_SAVINGS_INTEREST_POSTING, null));
		else
			addSavingsActivityDetails(buildSavingsActivity(interestAmt,
					getSavingsBalance().add(interestAmt),
					AccountConstants.ACTION_SAVINGS_INTEREST_POSTING,
					loggedInUser));
		buildFinancialEntries(interestPayment.getAccountTrxns());
	}

	private int getLastTrxnIndexForDay(List<AccountTrxnEntity> accountTrxnList,
			int i) {
		AccountTrxnEntity accountTrxn;
		do {
			accountTrxn = accountTrxnList.get(i);
			i++;
		} while (i < accountTrxnList.size()
				&& accountTrxn.getActionDate().equals(
						accountTrxnList.get(i).getActionDate()));
		return i - 1;
	}

	protected Money getMinimumBalance(Date fromDate, Date toDate,
			SavingsTrxnDetailEntity initialTrxn,
			SavingsTrxnDetailEntity adjustedTrxn) {
		logger.debug("In SavingsBO::getMinimumBalance(), accountId: "
				+ getAccountId());
		Money minBal = initialTrxn.getBalance();
		List<AccountTrxnEntity> accountTrxnList = getAccountTrxnsOrderByTrxnDate();

		if (adjustedTrxn != null
				&& getLastPmnt().getPaymentId().equals(
						initialTrxn.getAccountPayment().getPaymentId()))
			minBal = adjustedTrxn.getBalance();

		for (int i = 0; i < accountTrxnList.size(); i++) {
			if ((accountTrxnList.get(i).getActionDate()).compareTo(fromDate) >= 0
					&& DateUtils.getDateWithoutTimeStamp(
							accountTrxnList.get(i).getActionDate().getTime())
							.compareTo(
									DateUtils.getDateWithoutTimeStamp(toDate
											.getTime())) < 0) {
				Money lastTrxnAmt = getLastTrxnBalance(accountTrxnList, i);
				i = getLastTrxnIndexForDay(accountTrxnList, i);

				SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accountTrxnList
						.get(i);

				if ((initialTrxn.getActionDate().equals(
						savingsTrxn.getActionDate()) && initialTrxn
						.getAccountTrxnId().intValue() < savingsTrxn
						.getAccountTrxnId().intValue())
						|| (minBal.getAmountDoubleValue() > lastTrxnAmt
								.getAmountDoubleValue()))
					minBal = lastTrxnAmt;

				if (adjustedTrxn != null
						&& getLastPmnt().getPaymentId().equals(
								savingsTrxn.getAccountPayment().getPaymentId())
						&& (minBal.getAmountDoubleValue() > lastTrxnAmt
								.getAmountDoubleValue()))
					minBal = adjustedTrxn.getBalance();
			}
		}
		logger.debug("In SavingsBO::getMinimumBalance(), accountId: "
				+ getAccountId() + "fromDate: " + fromDate + ", toDate: "
				+ toDate + ", Min Bal: " + minBal);
		return minBal;
	}

	protected Money getAverageBalance(Date fromDate, Date toDate,
			SavingsTrxnDetailEntity initialTrxn,
			SavingsTrxnDetailEntity adjustedTrxn) {
		logger.debug("In SavingsBO::getAverageBalance(), accountId: "
				+ getAccountId());
		int noOfDays = 0;
		List<AccountTrxnEntity> accountTrxnList = getAccountTrxnsOrderByTrxnDate();
		Money initialBalance = initialTrxn.getBalance();

		if (adjustedTrxn != null
				&& getLastPmnt().getPaymentId().equals(
						initialTrxn.getAccountPayment().getPaymentId()))
			initialBalance = adjustedTrxn.getBalance();

		Money totalBalance = new Money();
		for (int i = 0; i < accountTrxnList.size(); i++) {
			if (accountTrxnList.get(i).getActionDate().compareTo(fromDate) >= 0
					&& DateUtils.getDateWithoutTimeStamp(
							accountTrxnList.get(i).getActionDate().getTime())
							.compareTo(
									DateUtils.getDateWithoutTimeStamp(toDate
											.getTime())) < 0) {

				Money lastTrxnAmt = getLastTrxnBalance(accountTrxnList, i);

				i = getLastTrxnIndexForDay(accountTrxnList, i);
				SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accountTrxnList
						.get(i);
				int days = helper.calculateDays(fromDate, savingsTrxn
						.getActionDate());
				fromDate = savingsTrxn.getActionDate();
				if (initialTrxn.getActionDate().equals(
						savingsTrxn.getActionDate())
						&& initialTrxn.getAccountTrxnId() < savingsTrxn
								.getAccountTrxnId()) {

					initialBalance = lastTrxnAmt;
				}

				totalBalance = totalBalance.add(new Money(Configuration
						.getInstance().getSystemConfig().getCurrency(),
						initialBalance.getAmountDoubleValue() * days));
				initialBalance = lastTrxnAmt;
				if (adjustedTrxn != null
						&& getLastPmnt().getPaymentId().equals(
								savingsTrxn.getAccountPayment().getPaymentId()))
					initialBalance = adjustedTrxn.getBalance();
				noOfDays += days;
			}
		}
		int days = helper.calculateDays(fromDate, toDate);

		totalBalance = totalBalance.add(new Money(Configuration.getInstance()
				.getSystemConfig().getCurrency(), initialBalance
				.getAmountDoubleValue()
				* days));
		noOfDays += days;

		return (noOfDays == 0 ? initialBalance : new Money(Configuration
				.getInstance().getSystemConfig().getCurrency(), totalBalance
				.getAmountDoubleValue()
				/ noOfDays));
	}

	private Money getLastTrxnBalance(List<AccountTrxnEntity> accountTrxnList,
			int i) {
		SavingsTrxnDetailEntity accountTrxn = null;
		SavingsTrxnDetailEntity lastTrxn = null;

		do {
			accountTrxn = (SavingsTrxnDetailEntity) accountTrxnList.get(i);
			if (lastTrxn == null
					|| (accountTrxn.getAccountPayment().getPaymentId() != null
							&& lastTrxn.getAccountPayment().getPaymentId() != null && accountTrxn
							.getAccountPayment().getPaymentId().intValue() > lastTrxn
							.getAccountPayment().getPaymentId().intValue()))
				lastTrxn = accountTrxn;
			else if (accountTrxn.getAccountPayment().getPaymentId() != null
					&& accountTrxn.getAccountPayment().getPaymentId().equals(
							lastTrxn.getAccountPayment().getPaymentId())) {
				if (accountTrxn.getAccountActionEntity().getId().equals(
						AccountConstants.ACTION_SAVINGS_DEPOSIT)) {
					if (lastTrxn.getBalance().getAmountDoubleValue() < accountTrxn
							.getBalance().getAmountDoubleValue())
						lastTrxn = accountTrxn;
				} else
					lastTrxn = accountTrxn;
			}
			i++;
		} while (i < accountTrxnList.size()
				&& accountTrxn.getActionDate().equals(
						accountTrxnList.get(i).getActionDate()));
		return lastTrxn.getBalance();
	}

	private Money calculateInterestForDays(Money principal,
			double interestRate, Date fromDate, Date toDate) throws AccountException
			 {
			int days = helper.calculateDays(fromDate, toDate);
			return getInterest(principal,
					interestRate, days, InterestCalculatorConstants.DAYS);
	}
	
	private Money getInterest(Money principal,
			double interestRate, int duration, String durationType) {
		double intRate = interestRate;
		if(durationType.equals(InterestCalculatorConstants.DAYS)){
			intRate = (intRate/(InterestCalculatorConstants.INTEREST_DAYS))* duration;
		}
		else{
			intRate = (intRate/12)*duration;
		}
		Money interestAmount = principal.multiply(new Double(1+(intRate/100.0))).subtract(principal);
		return interestAmount;
	}


	public void generateAndUpdateDepositActionsForClient(CustomerBO client)
			throws AccountException {
		if (client.getCustomerMeeting().getMeeting() != null) {
			if (!(getCustomer().getCustomerLevel().getId().shortValue() == CustomerConstants.GROUP_LEVEL_ID && getRecommendedAmntUnit()
					.getRecommendedAmntUnitId().shortValue() == ProductDefinitionConstants.COMPLETEGROUP
					.shortValue())) {
				generateDepositAccountActions(client, client
						.getCustomerMeeting().getMeeting());
				this.update();
			}
		}
	}

	private void generateDepositAccountActions() throws AccountException{
		logger.debug("In SavingsBO::generateDepositAccountActions()");
		// deposit happens on each meeting date of the customer. If for
		// center/group with individual deposits, insert row for every client
		if (getCustomer().getCustomerMeeting() != null
				&& getCustomer().getCustomerMeeting().getMeeting() != null) {
			MeetingBO depositSchedule = getCustomer().getCustomerMeeting()
					.getMeeting();

			depositSchedule.setMeetingStartDate(Calendar.getInstance());
			if (getCustomer().getCustomerLevel().getId().equals(
					CustomerConstants.CLIENT_LEVEL_ID)
					|| (getCustomer().getCustomerLevel().getId().equals(
							CustomerConstants.GROUP_LEVEL_ID) && getRecommendedAmntUnit()
							.getRecommendedAmntUnitId().shortValue() == ProductDefinitionConstants.COMPLETEGROUP)) {
				generateDepositAccountActions(getCustomer(), depositSchedule);
			} else {
				List<CustomerBO> children;
				try {
					children = getCustomer().getChildren(
							CustomerConstants.CLIENT_LEVEL_ID);
				} catch (PersistenceException e) {
					throw new AccountException(e);
				}
				for (CustomerBO customer : children) {
					generateDepositAccountActions(customer, depositSchedule);
				}
			}
		}
	}

	private void generateDepositAccountActions(CustomerBO customer,
			MeetingBO meeting) throws AccountException{
		SchedulerIntf scheduler;
		List<Date> depositDates=null;
		try {
			scheduler = SchedulerHelper.getScheduler(meeting);
			depositDates = scheduler.getAllDates();
		} catch (SchedulerException e) {
			throw new AccountException(e);
		}
		
		short installmentNumber = 1;
		for (Date dt : depositDates) {
			AccountActionDateEntity actionDate = helper.createActionDateObject(
					this, customer, installmentNumber++, dt, userContext
							.getId(), getRecommendedAmount());
			addAccountActionDate(actionDate);
			logger
					.debug("In SavingsBO::generateDepositAccountActions(), Successfully added account action on date: "
							+ dt);
		}
	}

	private void generateDepositAccountActions(CustomerBO customer,
			MeetingBO meeting, Short installmentId) throws AccountException {
		SchedulerIntf scheduler;
		List<Date> depositDates;
		try {
			scheduler = SchedulerHelper.getScheduler(meeting);
			depositDates = scheduler.getAllDates(new Date(meeting
					.getMeetingEndDate().getTimeInMillis()));
		} catch (SchedulerException e) {
			throw new AccountException(e);
		}
		if (skipFirstInstallment(depositDates.get(0)))
			depositDates.remove(0);
		short installmentNumber = installmentId;
		for (Date dt : depositDates) {
			AccountActionDateEntity actionDate = helper.createActionDateObject(
					this, customer, installmentNumber++, dt, userContext
							.getId(), getRecommendedAmount());

			addAccountActionDate(actionDate);
			logger
					.debug("In SavingsBO::generateDepositAccountActions(), Successfully added account action on date: "
							+ dt);
		}
	}

	private boolean skipFirstInstallment(Date date) {
		Calendar fistDay = DateUtils.getFistDayOfNextYear(Calendar
				.getInstance());
		Calendar firstInstallmentDate = Calendar.getInstance();
		firstInstallmentDate.setTime(date);
		Calendar firstInstallmentDateWithoutTimestamp = new GregorianCalendar(
				firstInstallmentDate.get(Calendar.YEAR), firstInstallmentDate
						.get(Calendar.MONTH), firstInstallmentDate
						.get(Calendar.DATE), 0, 0, 0);

		return firstInstallmentDateWithoutTimestamp.compareTo(fistDay) < 0 ? true
				: false;

	}

	protected AccountPaymentEntity makePayment(PaymentData paymentData)
			throws AccountException{
		MasterPersistenceService masterPersistenceService = new MasterPersistenceService();
		Money totalAmount = paymentData.getTotalAmount();
		Money enteredAmount = totalAmount;
		Date transactionDate = paymentData.getTransactionDate();
		List<AccountPaymentData> accountPayments = paymentData
				.getAccountPayments();

		CustomerBO customer = paymentData.getCustomer();
		AccountPaymentEntity accountPayment = new AccountPaymentEntity(this,
				totalAmount, paymentData.getRecieptNum(), paymentData
						.getRecieptDate(), new PaymentTypeEntity(paymentData
						.getPaymentTypeId()));
		if (this.getAccountState().getId().equals(
				AccountStates.SAVINGS_ACC_INACTIVE))
			try{
				this.setAccountState((new SavingsPersistence()).getAccountStatusObject(
					AccountStates.SAVINGS_ACC_APPROVED));
			}catch(PersistenceException pe){
				throw new AccountException(pe);
			}
		if (totalAmount.getAmountDoubleValue() > 0
				&& paymentData.getAccountPayments().size() <= 0) {
			SavingsTrxnDetailEntity accountTrxn = buildUnscheduledDeposit(
					accountPayment, totalAmount, paymentData.getPersonnel(),
					customer, transactionDate);
			accountPayment.addAcountTrxn(accountTrxn);
			addSavingsActivityDetails(buildSavingsActivity(totalAmount,
					getSavingsBalance(),
					AccountConstants.ACTION_SAVINGS_DEPOSIT, paymentData
							.getPersonnel()));
			return accountPayment;
		}
		for (AccountPaymentData accountPaymentData : accountPayments) {
			SavingsScheduleEntity accountAction = (SavingsScheduleEntity) getAccountActionDate(
					accountPaymentData.getInstallmentId(), customer
							.getCustomerId());
			if (accountAction != null
					&& enteredAmount.getAmountDoubleValue() > 0.0) {
				if (accountAction.getPaymentStatus().equals(
						PaymentStatus.PAID.getValue()))
					throw new AccountException("errors.update",
							new String[] { getGlobalAccountNum() });
				Money depositAmount = new Money();
				PaymentStatus paymentStatus = PaymentStatus.UNPAID;
				if (enteredAmount.getAmountDoubleValue() >= accountAction
						.getTotalDepositDue().getAmountDoubleValue()) {
					depositAmount = accountAction.getTotalDepositDue();
					enteredAmount = enteredAmount.subtract(accountAction
							.getTotalDepositDue());
					paymentStatus = PaymentStatus.PAID;
				} else {
					depositAmount = enteredAmount;
					enteredAmount = new Money();
				}
				if (getSavingsType().getSavingsTypeId().equals(
						ProductDefinitionConstants.VOLUNTARY)
						&& depositAmount.getAmountDoubleValue() > 0.0)
					paymentStatus = PaymentStatus.PAID;
				savingsBalance = savingsBalance.add(depositAmount);

				savingsPerformance.setPaymentDetails(depositAmount);
				accountAction.setPaymentDetails(depositAmount, paymentStatus,
						new java.sql.Date(transactionDate.getTime()));

				SavingsTrxnDetailEntity accountTrxn = new SavingsTrxnDetailEntity(
						accountPayment,
						paymentData.getPersonnel(),
						paymentData.getTransactionDate(),
						(AccountActionEntity) masterPersistenceService
								.findById(AccountActionEntity.class,
										AccountConstants.ACTION_SAVINGS_DEPOSIT),
						depositAmount, accountAction.getInstallmentId(),
						accountAction.getActionDate());

				accountPayment.addAcountTrxn(accountTrxn);
			}
		}
		if (enteredAmount.getAmountDoubleValue() > 0.0) {
			SavingsTrxnDetailEntity accountTrxn = buildUnscheduledDeposit(
					accountPayment, enteredAmount, paymentData.getPersonnel(),
					customer, transactionDate);
			accountPayment.addAcountTrxn(accountTrxn);
		}
		addSavingsActivityDetails(buildSavingsActivity(totalAmount,
				getSavingsBalance(), AccountConstants.ACTION_SAVINGS_DEPOSIT,
				paymentData.getPersonnel()));
		return accountPayment;
	}

	private SavingsTrxnDetailEntity buildUnscheduledDeposit(
			AccountPaymentEntity accountPayment, Money depositAmount,
			PersonnelBO personnel, CustomerBO customer, Date transactionDate){
		MasterPersistenceService masterPersistenceService = new MasterPersistenceService();
		SavingsTrxnDetailEntity accountTrxn = new SavingsTrxnDetailEntity(
				accountPayment, personnel, transactionDate,
				(AccountActionEntity) masterPersistenceService.findById(
						AccountActionEntity.class,
						AccountConstants.ACTION_SAVINGS_DEPOSIT),
				depositAmount, null, transactionDate);
		savingsBalance = savingsBalance.add(depositAmount);
		savingsPerformance.setPaymentDetails(depositAmount);
		return accountTrxn;
	}

	public void withdraw(PaymentData accountPaymentData)
			throws AccountException{
		MasterPersistence masterPersistence=new MasterPersistence();
		Money totalAmount = accountPaymentData.getTotalAmount();
		Date transactionDate = accountPaymentData.getTransactionDate();
		if (totalAmount.getAmountDoubleValue() > savingsBalance
				.getAmountDoubleValue()) {
			throw new AccountException("errors.insufficentbalance",
					new String[] { getGlobalAccountNum() });
		}
		Double maxWithdrawAmount = getSavingsOffering().getMaxAmntWithdrawl()
				.getAmountDoubleValue();
		if (maxWithdrawAmount != null && maxWithdrawAmount != 0
				&& totalAmount.getAmountDoubleValue() > maxWithdrawAmount) {
			throw new AccountException("errors.exceedmaxwithdrawal",
					new String[] { getGlobalAccountNum() });
		}
		savingsBalance = savingsBalance.subtract(totalAmount);

		savingsPerformance.setWithdrawDetails(totalAmount);

		CustomerBO customer = accountPaymentData.getCustomer();
		AccountPaymentEntity accountPayment = new AccountPaymentEntity(this,
				totalAmount, accountPaymentData.getRecieptNum(),
				accountPaymentData.getRecieptDate(), new PaymentTypeEntity(
						accountPaymentData.getPaymentTypeId()));

		SavingsTrxnDetailEntity accountTrxnBO = new SavingsTrxnDetailEntity(
				accountPayment, accountPaymentData.getPersonnel(),
				accountPaymentData.getTransactionDate(),
				(AccountActionEntity) masterPersistence.findById(
						AccountActionEntity.class,
						AccountConstants.ACTION_SAVINGS_WITHDRAWAL),
				totalAmount);

		accountPayment.addAcountTrxn(accountTrxnBO);
		addAccountPayment(accountPayment);
		addSavingsActivityDetails(buildSavingsActivity(totalAmount,
				getSavingsBalance(),
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, accountPaymentData
						.getPersonnel()));
		buildFinancialEntries(accountPayment.getAccountTrxns());
		if (this.getAccountState().getId().equals(
				AccountStates.SAVINGS_ACC_INACTIVE))
			try {
				this.setAccountState((new SavingsPersistence()).getAccountStatusObject(
						AccountStates.SAVINGS_ACC_APPROVED));
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}
		try {
			(new SavingsPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public List<AccountStateEntity> getStatusList() {
		List<AccountStateEntity> statusList = AccountStateMachines
				.getInstance().getStatusList(this.getAccountState(),
						AccountTypes.SAVINGSACCOUNT.getValue());
		if (null != statusList) {
			for (AccountStateEntity accStateObj : statusList) {
				accStateObj.setLocaleId(userContext.getLocaleId());
			}
		}
		return statusList;
	}

	public void initializeStateMachine(Short localeId) throws AccountException {
		try {
			AccountStateMachines.getInstance().initialize(localeId,
					getOffice().getOfficeId(),
					AccountTypes.SAVINGSACCOUNT.getValue(),null);
		} catch (StatesInitializationException e) {
			throw new AccountException(e);
		}
	}

	@Override
	public String getStatusName(Short localeId, Short accountStateId){
			return AccountStateMachines.getInstance().getStatusName(localeId,
					accountStateId, AccountTypes.SAVINGSACCOUNT.getValue());
	}

	@Override
	public String getFlagName(Short flagId){
			return AccountStateMachines.getInstance().getFlagName(flagId,
					AccountTypes.SAVINGSACCOUNT.getValue());
	}

	private void setValuesForActiveState() throws AccountException{
		this.setActivationDate(new Date(new java.util.Date().getTime()));
		this.generateDepositAccountActions();
		try {
			this.setNextIntCalcDate(helper.getNextScheduleDate(getActivationDate(),
					null, getTimePerForInstcalc()));
			this.setNextIntPostDate(helper.getNextScheduleDate(getActivationDate(),
					null, getMeeting(getSavingsOffering().getFreqOfPostIntcalc()
							.getMeeting())));
		} catch (SchedulerException e) {
			throw new AccountException(e);
		}
	}

	public AccountStateEntity retrieveAccountStateEntityMasterObject(
			AccountStateEntity accountStateEntity) {
		return AccountStateMachines.getInstance()
				.retrieveAccountStateEntityMasterObject(accountStateEntity,
						AccountTypes.SAVINGSACCOUNT.getValue());
	}

	private void activationDateHelper(Short newStatusId)
			throws AccountException {
		if (Configuration.getInstance().getAccountConfig(
				getOffice().getOfficeId())
				.isPendingApprovalStateDefinedForSavings()) {
			if (this.getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_PENDINGAPPROVAL
					&& newStatusId.shortValue() == AccountStates.SAVINGS_ACC_APPROVED) {
				setValuesForActiveState();
			}
		} else {
			if (this.getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_PARTIALAPPLICATION
					&& newStatusId.shortValue() == AccountStates.SAVINGS_ACC_APPROVED) {
				setValuesForActiveState();
			}
		}
	}

	public void changeStatus(AccountStateEntity newState,
			AccountNotesEntity accountNotesEntity,
			AccountStateFlagEntity flagSelected, UserContext userContext)
			throws AccountException {
		// permission Check
		checkPermissionForStatusChange(newState, userContext, flagSelected);

		activationDateHelper(newState.getId());
		if (null != flagSelected)
			setFlag(flagSelected);
		setStatus(newState, accountNotesEntity);
		this.update();
	}

	private void setFlag(AccountStateFlagEntity accountStateFlagEntity) {
		// remove all previous flags except the blacklisted ones denoted by
		// retained flag
		Iterator iter = this.getAccountFlags().iterator();
		while (iter.hasNext()) {
			AccountFlagMapping currentFlag = (AccountFlagMapping) iter.next();
			if (!currentFlag.getFlag().isFlagRetained())
				iter.remove();
		}
		this.addAccountFlag(accountStateFlagEntity);
	}

	private void setStatus(AccountStateEntity accountStateEntity,
			AccountNotesEntity accountNotesEntity) {
		AccountStateEntity accountState = this.getAccountState();
		this.setAccountState(this
				.retrieveAccountStateEntityMasterObject(accountStateEntity));
		this.addAccountNotes(accountNotesEntity);
		if (accountStateEntity.getId().equals(Short.valueOf("15"))
				|| accountStateEntity.getId().equals(Short.valueOf("17")))
			this.setClosedDate(new Date(System.currentTimeMillis()));
		this
				.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
						accountState, this.getAccountState(),
						(new PersonnelPersistence()).getPersonnel(
								userContext.getId())));
	}

	public void adjustLastUserAction(Money amountAdjustedTo,
			String adjustmentComment) throws AccountException {
		logger
				.debug("In SavingsBO::generateDepositAccountActions(), accountId: "
						+ getAccountId());
		if (!isAdjustPossibleOnLastTrxn(amountAdjustedTo)) {
			throw new AccountException(
					AccountExceptionConstants.CANNOTADJUST);
		}
		Date trxnDate = getTrxnDate(getLastPmnt());
		Money oldInterest = calculateInterestForAdjustment(trxnDate, null);
		adjustExistingPayment(amountAdjustedTo, adjustmentComment);
		AccountPaymentEntity newPayment = createAdjustmentPayment(
				amountAdjustedTo, adjustmentComment);
		adjustInterest(oldInterest, trxnDate, newPayment);
		this.update();
	}

	protected void adjustInterest(Money oldInterest, Date trxnDate,
			AccountPaymentEntity newPayment)
			throws AccountException {
		if (getLastIntCalcDate() != null
				&& trxnDate.compareTo(getLastIntCalcDate()) < 0) {
			SavingsTrxnDetailEntity adjustedTrxn = null;
			if (newPayment == null || newPayment.getAccountTrxns() == null
					|| newPayment.getAccountTrxns().size() == 0)
				adjustedTrxn = getLastTrxnForAdjustedPayment(getLastPmnt());
			else
				adjustedTrxn = getLastTrxnForPayment(newPayment);
			Money newInterest = calculateInterestForAdjustment(trxnDate,
					adjustedTrxn);
			setInterestToBePosted(getInterestToBePosted().add(
					newInterest.subtract(oldInterest)));
		}
	}

	private SavingsTrxnDetailEntity getLastTrxnForPayment(
			AccountPaymentEntity payment) {
		Short accountAction = helper.getPaymentActionType(payment);
		if (accountAction.equals(AccountConstants.ACTION_SAVINGS_WITHDRAWAL)) {
			for (AccountTrxnEntity accountTrxn : payment.getAccountTrxns())
				return (SavingsTrxnDetailEntity) accountTrxn;
		} else if (accountAction
				.equals(AccountConstants.ACTION_SAVINGS_DEPOSIT)) {
			SavingsTrxnDetailEntity lastTrxn = null;
			for (AccountTrxnEntity trxn : payment.getAccountTrxns()) {
				SavingsTrxnDetailEntity accountTrxn = (SavingsTrxnDetailEntity) trxn;
				if (lastTrxn == null
						|| accountTrxn.getBalance().getAmountDoubleValue() > lastTrxn
								.getBalance().getAmountDoubleValue())
					lastTrxn = accountTrxn;
			}
			return lastTrxn;
		}

		return null;
	}

	private SavingsTrxnDetailEntity getLastTrxnForAdjustedPayment(
			AccountPaymentEntity payment) {
		Short accountAction = helper.getPaymentActionType(payment);
		if (accountAction.equals(AccountConstants.ACTION_SAVINGS_WITHDRAWAL)) {
			for (AccountTrxnEntity accountTrxn : payment.getAccountTrxns()) {
				if (accountTrxn.getAccountActionEntity().getId().equals(
						AccountConstants.ACTION_SAVINGS_ADJUSTMENT))
					return (SavingsTrxnDetailEntity) accountTrxn;
			}
		} else if (accountAction
				.equals(AccountConstants.ACTION_SAVINGS_DEPOSIT)) {
			SavingsTrxnDetailEntity lastTrxn = null;
			for (AccountTrxnEntity trxn : payment.getAccountTrxns()) {
				SavingsTrxnDetailEntity accountTrxn = (SavingsTrxnDetailEntity) trxn;
				if (lastTrxn == null
						|| (accountTrxn
								.getAccountActionEntity()
								.getId()
								.equals(
										AccountConstants.ACTION_SAVINGS_ADJUSTMENT) && accountTrxn
								.getBalance().getAmountDoubleValue() < lastTrxn
								.getBalance().getAmountDoubleValue()))
					lastTrxn = accountTrxn;
			}
			return lastTrxn;
		}
		return null;
	}

	private Date getTrxnDate(AccountPaymentEntity payment) {
		Date trxnDate = null;
		for (AccountTrxnEntity accntTrxn : payment.getAccountTrxns())
			trxnDate = accntTrxn.getActionDate();
		return trxnDate;
	}

	private Date getFromDateForInterestAdjustment(Date trxnDate)
			throws AccountException {
		Date fromDate = getLastIntCalcDate();
		do {
			try {
				fromDate = helper.getPrevScheduleDate(getActivationDate(),
						fromDate, timePerForInstcalc);
			} catch (SchedulerException e) {
				throw new AccountException(e);
			}
			if (fromDate != null)
				fromDate = new Timestamp(fromDate.getTime());
		} while (fromDate != null && trxnDate.compareTo(fromDate) < 0);
		return (fromDate == null) ? getActivationDate() : fromDate;
	}

	protected Money calculateInterestForAdjustment(Date trxnDate,
			SavingsTrxnDetailEntity adjustedTrxn) throws AccountException{
		Money oldInterest = new Money();
		if (getLastIntCalcDate() != null
				&& trxnDate.compareTo(getLastIntCalcDate()) < 0) {
			Date toDate = null;
			Date fromDate = getFromDateForInterestAdjustment(trxnDate);
			try {
				for (toDate = new Timestamp(helper.getNextScheduleDate(
						getActivationDate(), fromDate, timePerForInstcalc)
						.getTime()); toDate.compareTo(getLastIntCalcDate()) <= 0; toDate = new Timestamp(
						helper.getNextScheduleDate(getActivationDate(), toDate,
								timePerForInstcalc).getTime())) {

					oldInterest = oldInterest.add(calculateInterest(fromDate,
							toDate, getInterestRate(), adjustedTrxn));
					// //////////Removed if for adjustedTrxn!=null
					fromDate = toDate;
				}
			} catch (SchedulerException e) {
				throw new AccountException(e);
			} 
		}
		return oldInterest;
	}

	protected void adjustExistingPayment(Money amountAdjustedTo,
			String adjustmentComment) throws AccountException {
		AccountPaymentEntity lastPayment = getLastPmnt();
		Short actionType = helper.getPaymentActionType(lastPayment);
		PersonnelBO personnel = (new PersonnelPersistence()).getPersonnel(
				userContext.getId());
		for (AccountTrxnEntity accntTrxn : lastPayment.getAccountTrxns()) {
			if (actionType.equals(AccountConstants.ACTION_SAVINGS_DEPOSIT))
				adjustForDeposit(accntTrxn);
			else if (actionType
					.equals(AccountConstants.ACTION_SAVINGS_WITHDRAWAL))
				adjustForWithdrawal(accntTrxn);
		}
		addSavingsActivityDetails(buildSavingsActivity(lastPayment.getAmount(),
				getSavingsBalance(),
				AccountConstants.ACTION_SAVINGS_ADJUSTMENT, personnel));
		logger.debug("transaction count before adding reversal transactions: "
				+ lastPayment.getAccountTrxns().size());
		List<AccountTrxnEntity> newlyAddedTrxns = lastPayment
				.reversalAdjustment(adjustmentComment);
		// TODO Please validate the code commented below. Why is personnel being
		// changed.
		/*
		 * for (AccountTrxnEntity accountTrxn : newlyAddedTrxns) {
		 * accountTrxn.setPersonnel(personnel); }
		 */
		buildFinancialEntries(new HashSet<AccountTrxnEntity>(newlyAddedTrxns));
	}

	protected AccountPaymentEntity createAdjustmentPayment(
			Money amountAdjustedTo, String adjustmentComment)
			throws AccountException {
		AccountPaymentEntity lastPayment = getLastPmnt();
		AccountPaymentEntity newAccountPayment = null;
		PersonnelBO personnel = (new PersonnelPersistence()).getPersonnel(
				userContext.getId());
		if (amountAdjustedTo.getAmountDoubleValue() > 0) {
			newAccountPayment = helper.createAccountPayment(this,
					amountAdjustedTo, lastPayment.getPaymentType(),
					(new PersonnelPersistence()).getPersonnel(userContext.getId()));
		}
		if (newAccountPayment != null) {
			newAccountPayment.setAmount(amountAdjustedTo);
			Set<AccountTrxnEntity> accountTrxns = createTrxnsForAmountAdjusted(
					newAccountPayment, lastPayment, amountAdjustedTo);
			for (AccountTrxnEntity accountTrxn : accountTrxns) {
				newAccountPayment.addAcountTrxn(accountTrxn);
			}
			this.addAccountPayment(newAccountPayment);
			buildFinancialEntries(newAccountPayment.getAccountTrxns());

			addSavingsActivityDetails(buildSavingsActivity(amountAdjustedTo,
					getSavingsBalance(), helper
							.getPaymentActionType(lastPayment), personnel));
		}
		return newAccountPayment;
	}

	private List<AccountActionDateEntity> getAccountActions(Date dueDate,
			Integer customerId) {
		List<AccountActionDateEntity> accountActions = new ArrayList<AccountActionDateEntity>();
		for (AccountActionDateEntity accountAction : getAccountActionDates()) {
			if (accountAction.getActionDate().compareTo(dueDate) <= 0
					&& accountAction.getPaymentStatus().equals(
							PaymentStatus.UNPAID.getValue())
					&& accountAction.getCustomer().getCustomerId().equals(
							customerId))
				accountActions.add(accountAction);
		}
		return accountActions;
	}

	protected Set<AccountTrxnEntity> createTrxnsForAmountAdjusted(
			AccountPaymentEntity newAccountPayment,
			AccountPaymentEntity lastAccountPayment, Money newAmount){
		Short actionType = helper.getPaymentActionType(lastAccountPayment);
		if (isMandatory()
				&& actionType.equals(AccountConstants.ACTION_SAVINGS_DEPOSIT))
			return createDepositTrxnsForMandatoryAccountsAfterAdjust(
					newAccountPayment, lastAccountPayment, newAmount);

		if (actionType.equals(AccountConstants.ACTION_SAVINGS_DEPOSIT))
			return createDepositTrxnsForVolAccountsAfterAdjust(
					newAccountPayment, lastAccountPayment, newAmount);

		return createWithdrawalTrxnsAfterAdjust(newAccountPayment,
				lastAccountPayment, newAmount);
	}

	private Set<AccountTrxnEntity> createWithdrawalTrxnsAfterAdjust(
			AccountPaymentEntity newAccountPayment,
			AccountPaymentEntity lastAccountPayment, Money newAmount){
		MasterPersistence masterPersistence = new MasterPersistence();
		Set<AccountTrxnEntity> newTrxns = new HashSet<AccountTrxnEntity>();
		SavingsTrxnDetailEntity accountTrxn = null;
		// create transaction for withdrawal
		SavingsTrxnDetailEntity oldSavingsAccntTrxn = null;
		for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment
				.getAccountTrxns()) {
			oldSavingsAccntTrxn = (SavingsTrxnDetailEntity) oldAccntTrxn;
			break;
		}
		setSavingsBalance(getSavingsBalance().subtract(newAmount));
		accountTrxn = new SavingsTrxnDetailEntity(newAccountPayment,
				oldSavingsAccntTrxn.getCustomer(),
				(AccountActionEntity) masterPersistence.findById(
						AccountActionEntity.class,
						AccountConstants.ACTION_SAVINGS_WITHDRAWAL), newAmount,
				getSavingsBalance(), (new PersonnelPersistence()).getPersonnel(
						userContext.getId()), oldSavingsAccntTrxn.getDueDate(),
				oldSavingsAccntTrxn.getActionDate(), null, "");
		getSavingsPerformance().setTotalWithdrawals(
				getSavingsPerformance().getTotalWithdrawals().add(
						accountTrxn.getWithdrawlAmount()));
		newTrxns.add(accountTrxn);
		return newTrxns;
	}

	private Set<AccountTrxnEntity> createDepositTrxnsForMandatoryAccountsAfterAdjust(
			AccountPaymentEntity newAccountPayment,
			AccountPaymentEntity lastAccountPayment, Money newAmount){
		MasterPersistence masterPersistence = new MasterPersistence();
		Set<AccountTrxnEntity> newTrxns = new HashSet<AccountTrxnEntity>();
		SavingsTrxnDetailEntity accountTrxn = null;
		CustomerBO customer = null;
		Date oldTrxnDate = null;
		for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment
				.getAccountTrxns()) {
			customer = oldAccntTrxn.getCustomer();
			oldTrxnDate = oldAccntTrxn.getActionDate();
			break;
		}
		List<AccountActionDateEntity> accountActionList = getAccountActions(
				lastAccountPayment.getPaymentDate(), customer.getCustomerId());
		for (AccountActionDateEntity accountActionDateEntity : accountActionList) {
			SavingsScheduleEntity accountAction = (SavingsScheduleEntity) accountActionDateEntity;
			if (newAmount.getAmountDoubleValue() == 0)
				break;
			accountTrxn = null;
			if (accountAction.getDeposit().getAmountDoubleValue() <= newAmount
					.getAmountDoubleValue()) {
				setSavingsBalance(getSavingsBalance().add(
						accountAction.getDeposit()));
				accountTrxn = new SavingsTrxnDetailEntity(
						newAccountPayment,
						customer,
						(AccountActionEntity) masterPersistence
								.findById(AccountActionEntity.class,
										AccountConstants.ACTION_SAVINGS_DEPOSIT),
						accountAction.getDeposit(), getSavingsBalance(),
						(new PersonnelPersistence()).getPersonnel(
								userContext.getId()), accountAction
								.getActionDate(), oldTrxnDate, accountAction
								.getInstallmentId(), "");
				newAmount = newAmount.subtract(accountAction.getDeposit());
				accountAction.setDepositPaid(accountAction.getDepositPaid()
						.add(accountTrxn.getDepositAmount()));
				accountAction.setPaymentStatus(PaymentStatus.PAID.getValue());
			} else {
				setSavingsBalance(getSavingsBalance().add(newAmount));
				accountTrxn = new SavingsTrxnDetailEntity(
						newAccountPayment,
						customer,
						(AccountActionEntity) masterPersistence
								.findById(AccountActionEntity.class,
										AccountConstants.ACTION_SAVINGS_DEPOSIT),
						newAmount, getSavingsBalance(), (new PersonnelPersistence())
								.getPersonnel(userContext.getId()),
						accountAction.getActionDate(), oldTrxnDate,
						accountAction.getInstallmentId(), "");
				newAmount = newAmount.subtract(newAmount);
				accountAction.setDepositPaid(accountAction.getDepositPaid()
						.add(accountTrxn.getDepositAmount()));
				accountAction.setPaymentStatus(PaymentStatus.UNPAID.getValue());
			}
			accountAction
					.setPaymentDate(new java.sql.Date(new Date().getTime()));
			getSavingsPerformance().setTotalDeposits(
					getSavingsPerformance().getTotalDeposits().add(
							accountTrxn.getDepositAmount()));
			newTrxns.add(accountTrxn);
		}
		// add trxn for excess amount
		if (newAmount.getAmountDoubleValue() > 0) {
			setSavingsBalance(getSavingsBalance().add(newAmount));
			accountTrxn = new SavingsTrxnDetailEntity(newAccountPayment,
					customer, (AccountActionEntity) masterPersistence
							.findById(AccountActionEntity.class,
									AccountConstants.ACTION_SAVINGS_DEPOSIT),
					newAmount, getSavingsBalance(), (new PersonnelPersistence())
							.getPersonnel(userContext.getId()), null,
					oldTrxnDate, null, "");
			newAmount = newAmount.subtract(newAmount);
			getSavingsPerformance().setTotalDeposits(
					getSavingsPerformance().getTotalDeposits().add(
							accountTrxn.getDepositAmount()));
			newTrxns.add(accountTrxn);
		}
		return newTrxns;
	}

	private Set<AccountTrxnEntity> createDepositTrxnsForVolAccountsAfterAdjust(
			AccountPaymentEntity newAccountPayment,
			AccountPaymentEntity lastAccountPayment, Money newAmount){
		MasterPersistence masterPersistence = new MasterPersistence();
		Set<AccountTrxnEntity> newTrxns = new HashSet<AccountTrxnEntity>();
		SavingsTrxnDetailEntity accountTrxn = null;
		CustomerBO customer = null;
		Date oldTrxnDate = null;
		for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment
				.getAccountTrxns()) {
			customer = oldAccntTrxn.getCustomer();
			oldTrxnDate = oldAccntTrxn.getActionDate();
			break;
		}
		for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment
				.getAccountTrxns()) {
			if (oldAccntTrxn.getAccountActionEntity().getId().equals(
					AccountConstants.ACTION_SAVINGS_DEPOSIT)) {
				SavingsTrxnDetailEntity oldSavingsAccntTrxn = (SavingsTrxnDetailEntity) oldAccntTrxn;
				if (oldAccntTrxn.getInstallmentId() != null) {
					accountTrxn = null;
					SavingsScheduleEntity accountAction = (SavingsScheduleEntity) getAccountActionDate(
							oldSavingsAccntTrxn.getInstallmentId(),
							oldSavingsAccntTrxn.getCustomer().getCustomerId());
					if (accountAction.getDeposit().getAmountDoubleValue() <= newAmount
							.getAmountDoubleValue()) {
						setSavingsBalance(getSavingsBalance().add(
								accountAction.getDeposit()));
						accountTrxn = new SavingsTrxnDetailEntity(
								newAccountPayment,
								customer,
								(AccountActionEntity) masterPersistence
										.findById(
												AccountActionEntity.class,
												AccountConstants.ACTION_SAVINGS_DEPOSIT),
								accountAction.getDeposit(),
								getSavingsBalance(), (new PersonnelPersistence())
										.getPersonnel(userContext.getId()),
								accountAction.getActionDate(), oldTrxnDate,
								oldAccntTrxn.getInstallmentId(), "");
						newAmount = newAmount.subtract(accountAction
								.getDeposit());
						accountAction.setDepositPaid(accountAction
								.getDepositPaid().add(
										accountTrxn.getDepositAmount()));
						accountAction.setPaymentStatus(PaymentStatus.PAID
								.getValue());
					} else if (newAmount.getAmountDoubleValue() != 0) {
						setSavingsBalance(getSavingsBalance().add(newAmount));
						accountTrxn = new SavingsTrxnDetailEntity(
								newAccountPayment,
								customer,
								(AccountActionEntity) masterPersistence
										.findById(
												AccountActionEntity.class,
												AccountConstants.ACTION_SAVINGS_DEPOSIT),
								newAmount, getSavingsBalance(),
								(new PersonnelPersistence()).getPersonnel(
										userContext.getId()), accountAction
										.getActionDate(), oldTrxnDate,
								oldAccntTrxn.getInstallmentId(), "");
						newAmount = newAmount.subtract(newAmount);
						accountAction.setDepositPaid(accountAction
								.getDepositPaid().add(
										accountTrxn.getDepositAmount()));
						accountAction.setPaymentStatus(PaymentStatus.UNPAID
								.getValue());
					}
					accountAction.setPaymentDate(new java.sql.Date(new Date()
							.getTime()));
					getSavingsPerformance().setTotalDeposits(
							getSavingsPerformance().getTotalDeposits().add(
									accountTrxn.getDepositAmount()));
					break;
				}
			}
		}

		if (accountTrxn != null)
			newTrxns.add(accountTrxn);
		// Create a new transaction with remaining amount
		if (newAmount.getAmountDoubleValue() > 0) {
			setSavingsBalance(getSavingsBalance().add(newAmount));
			accountTrxn = new SavingsTrxnDetailEntity(newAccountPayment,
					customer, (AccountActionEntity) masterPersistence
							.findById(AccountActionEntity.class,
									AccountConstants.ACTION_SAVINGS_DEPOSIT),
					newAmount, getSavingsBalance(), (new PersonnelPersistence())
							.getPersonnel(userContext.getId()), null,
					oldTrxnDate, null, "");
			getSavingsPerformance().setTotalDeposits(
					getSavingsPerformance().getTotalDeposits().add(
							accountTrxn.getDepositAmount()));
			newTrxns.add(accountTrxn);
		}
		return newTrxns;
	}

	private void adjustForDeposit(AccountTrxnEntity accntTrxn) {
		SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accntTrxn;
		Short installmentId = savingsTrxn.getInstallmentId();
		setSavingsBalance(getSavingsBalance().subtract(
				savingsTrxn.getDepositAmount()));
		SavingsScheduleEntity accntActionDate = (SavingsScheduleEntity) getAccountActionDate(
				installmentId, accntTrxn.getCustomer().getCustomerId());
		if (accntActionDate != null) {
			accntActionDate.setDepositPaid(accntActionDate.getDepositPaid()
					.subtract(savingsTrxn.getDepositAmount()));
			accntActionDate.setPaymentStatus(PaymentStatus.UNPAID.getValue());
			accntActionDate.setPaymentDate(null);
		}
		getSavingsPerformance().setTotalDeposits(
				getSavingsPerformance().getTotalDeposits().subtract(
						savingsTrxn.getDepositAmount()));
	}

	private void adjustForWithdrawal(AccountTrxnEntity accntTrxn) {
		SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accntTrxn;
		setSavingsBalance(getSavingsBalance().add(
				savingsTrxn.getWithdrawlAmount()));
		getSavingsPerformance().setTotalWithdrawals(
				getSavingsPerformance().getTotalWithdrawals().subtract(
						savingsTrxn.getWithdrawlAmount()));
	}

	protected boolean isAdjustPossibleOnLastTrxn(Money amountAdjustedTo) {
		if (!(getAccountState().getId().equals(
				AccountStates.SAVINGS_ACC_APPROVED) || getAccountState()
				.getId().equals(AccountStates.SAVINGS_ACC_INACTIVE))) {
			logger
					.debug("State is not active hence adjustment is not possible");
			return false;
		}
		AccountPaymentEntity accountPayment = getLastPmnt();
		if (accountPayment != null
				&& getLastPmntAmnt() != 0
				&& (helper.getPaymentActionType(accountPayment).equals(
						AccountConstants.ACTION_SAVINGS_WITHDRAWAL) || helper
						.getPaymentActionType(accountPayment).equals(
								AccountConstants.ACTION_SAVINGS_DEPOSIT))) {
			if (accountPayment.getAmount().equals(amountAdjustedTo)) {
				logger
						.debug("Either Amount to be adjusted is same as last user payment amount, or last payment is not withdrawal or deposit, therefore adjustment is not possible.");
				return false;
			}
			return (adjustmentCheckForWithdrawal(accountPayment,
					amountAdjustedTo) && adjustmentCheckForBalance(
					accountPayment, amountAdjustedTo));
		}
		logger.debug("No last Payment found for adjustment");
		return false;
	}

	private boolean adjustmentCheckForWithdrawal(
			AccountPaymentEntity accountPayment, Money amountAdjustedTo) {
		Double maxWithdrawAmount = getSavingsOffering().getMaxAmntWithdrawl()
				.getAmountDoubleValue();
		if (helper.getPaymentActionType(accountPayment).equals(
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL)
				&& maxWithdrawAmount != null
				&& maxWithdrawAmount != 0
				&& amountAdjustedTo.getAmountDoubleValue() > maxWithdrawAmount) {
			logger.debug("Amount is more than withdrawal limit");
			return false;
		}
		return true;
	}

	private boolean adjustmentCheckForBalance(
			AccountPaymentEntity accountPayment, Money amountAdjustedTo) {
		Money balanceAfterAdjust = getSavingsBalance();
		for (AccountTrxnEntity accntTrxn : accountPayment.getAccountTrxns()) {
			SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accntTrxn;
			if (helper.getPaymentActionType(accountPayment).equals(
					AccountConstants.ACTION_SAVINGS_WITHDRAWAL)
					&& amountAdjustedTo.getAmountDoubleValue() > savingsTrxn
							.getWithdrawlAmount().getAmountDoubleValue()) {
				balanceAfterAdjust = balanceAfterAdjust
						.subtract(amountAdjustedTo.subtract(savingsTrxn
								.getWithdrawlAmount()));
				if (balanceAfterAdjust.getAmountDoubleValue() < 0) {
					logger
							.debug("After adjustment balance is becoming -ve, therefore adjustment is not possible.");
					return false;
				}
			}
		}
		return true;
	}

	public AccountNotesEntity createAccountNotes(String comment){
		AccountNotesEntity accountNotes = new AccountNotesEntity();
		accountNotes.setCommentDate(new java.sql.Date(System
				.currentTimeMillis()));
		accountNotes.setPersonnel((new PersonnelPersistence()).getPersonnel(
				userContext.getId()));
		accountNotes.setComment(comment);
		return accountNotes;
	}

	private boolean isPermissionAllowed(AccountStateEntity newSate,
			UserContext userContext, AccountStateFlagEntity flagSelected,
			boolean saveFlag) {
		short newSateId = newSate.getId().shortValue();
		short cancelFlag = flagSelected != null ? flagSelected.getId()
				.shortValue() : 0;
		if (saveFlag)
			return AuthorizationManager.getInstance().isActivityAllowed(
					userContext,
					new ActivityContext(ActivityMapper.getInstance()
							.getActivityIdForState(newSateId), this.getOffice()
							.getOfficeId(), this.getCustomer().getPersonnel()
							.getPersonnelId()));
		else
			return AuthorizationManager.getInstance().isActivityAllowed(
					userContext,
					new ActivityContext(ActivityMapper.getInstance()
							.getActivityIdForNewStateId(newSateId, cancelFlag),
							this.getOffice().getOfficeId(), this.getCustomer()
									.getPersonnel().getPersonnelId()));
	}

	private void checkPermissionForStatusChange(AccountStateEntity newState,
			UserContext userContext, AccountStateFlagEntity flagSelected)
			throws AccountException {
		if (!isPermissionAllowed(newState, userContext, flagSelected, false))
			throw new AccountException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	private void checkPermissionForSave(AccountStateEntity newState,
			UserContext userContext, AccountStateFlagEntity flagSelected)
			throws AccountException {
		if (!isPermissionAllowed(newState, userContext, flagSelected, true))
			throw new AccountException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	public Money getOverDueDepositAmount(java.sql.Date meetingDate) {
		Money overdueAmount = new Money();
		if (isMandatory()) {
			for (AccountActionDateEntity accountActionDate : getAccountActionDates()) {
				if (accountActionDate.getPaymentStatus().shortValue() == PaymentStatus.UNPAID
						.getValue()
						&& (accountActionDate.getActionDate()
								.before(meetingDate))) {
					overdueAmount = overdueAmount
							.add(((SavingsScheduleEntity) accountActionDate)
									.getTotalDepositDue());
				}
			}
		}
		return overdueAmount;
	}

	public List<SavingsRecentActivityView> getRecentAccountActivity(
			Integer count) {
		List<SavingsRecentActivityView> accountActivityList = new ArrayList<SavingsRecentActivityView>();
		int activitiesAdded = 0;
		for (SavingsActivityEntity activity : getSavingsActivityDetails()) {
			if (count == null || activitiesAdded < count.intValue()) {
				accountActivityList
						.add(createSavingsRecentActivityView(activity));
				activitiesAdded++;
			}
		}
		return accountActivityList;
	}

	private SavingsRecentActivityView createSavingsRecentActivityView(
			SavingsActivityEntity savingActivity) {
		SavingsRecentActivityView savingsRecentActivityView = new SavingsRecentActivityView();
		savingsRecentActivityView.setAccountTrxnId(savingActivity.getId());
		savingsRecentActivityView.setActionDate(savingActivity
				.getTrxnCreatedDate());
		savingsRecentActivityView.setAmount(removeSign(
				savingActivity.getAmount()).toString());
		savingsRecentActivityView.setActivity(savingActivity.getActivity()
				.getName(userContext.getLocaleId()));
		savingsRecentActivityView.setRunningBalance(String
				.valueOf(savingActivity.getBalanceAmount()
						.getAmountDoubleValue()));
		return savingsRecentActivityView;
	}

	protected Money getDueAmount(AccountActionDateEntity installment) {
		return ((SavingsScheduleEntity) installment).getTotalDepositDue();
	}

	public Money getTotalAmountDue() {
		return getTotalAmountInArrears().add(
				getTotalAmountDueForNextInstallment());
	}

	public Money getTotalAmountDueForInstallment(Short installmentId) {
		Money totalAmount = new Money();
		if (null != getAccountActionDates()
				&& getAccountActionDates().size() > 0) {
			for (AccountActionDateEntity accntActionDate : getAccountActionDates()) {
				if (accntActionDate.getInstallmentId().equals(installmentId)
						&& accntActionDate.getPaymentStatus().equals(
								PaymentStatus.UNPAID.getValue())) {
					totalAmount = totalAmount
							.add(((SavingsScheduleEntity) accntActionDate)
									.getTotalDepositDue());
				}
			}
		}

		return totalAmount;
	}

	public Money getTotalAmountDueForNextInstallment() {
		AccountActionDateEntity nextAccountAction = getDetailsOfNextInstallment();
		if (nextAccountAction != null)
			return getTotalAmountDueForInstallment(nextAccountAction
					.getInstallmentId());
		return new Money();
	}

	private List<AccountActionDateEntity> getNextInstallment() {
		List<AccountActionDateEntity> nextInstallment = new ArrayList<AccountActionDateEntity>();
		AccountActionDateEntity nextAccountAction = getDetailsOfNextInstallment();
		if (nextAccountAction != null && null != getAccountActionDates()
				&& getAccountActionDates().size() > 0) {
			for (AccountActionDateEntity accntActionDate : getAccountActionDates())
				if (accntActionDate.getInstallmentId().equals(
						nextAccountAction.getInstallmentId())
						&& accntActionDate.getPaymentStatus().equals(
								PaymentStatus.UNPAID.getValue()))
					nextInstallment.add(accntActionDate);
		}
		return nextInstallment;
	}

	public void waiveAmountDue(WaiveEnum waiveType) throws 	AccountException {
		PersonnelBO personnel = (new PersonnelPersistence()).getPersonnel(
				getUserContext().getId());
		addSavingsActivityDetails(buildSavingsActivity(
				getTotalAmountDueForNextInstallment(), getSavingsBalance(),
				AccountConstants.ACTION_WAIVEOFFDUE, personnel));
		List<AccountActionDateEntity> nextInstallments = getNextInstallment();
		for (AccountActionDateEntity accountActionDate : nextInstallments) {
			((SavingsScheduleEntity) accountActionDate).waiveDepositDue();
		}
		try {
			(new SavingsPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public void waiveAmountOverDue() throws AccountException {
		PersonnelBO personnel = (new PersonnelPersistence()).getPersonnel(
				getUserContext().getId());
		addSavingsActivityDetails(buildSavingsActivity(
				getTotalAmountInArrears(), getSavingsBalance(),
				AccountConstants.ACTION_WAIVEOFFOVERDUE, personnel));
		List<AccountActionDateEntity> installmentsInArrears = getDetailsOfInstallmentsInArrears();
		for (AccountActionDateEntity accountActionDate : installmentsInArrears) {
			((SavingsScheduleEntity) accountActionDate).waiveDepositDue();
		}
		try {
			(new SavingsPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
		
	}

	private SavingsActivityEntity buildSavingsActivity(Money amount,
			Money balanceAmount, short acccountActionId, PersonnelBO personnel){
		MasterPersistence masterPersistence = new MasterPersistence();
		AccountActionEntity accountAction = (AccountActionEntity) masterPersistence
				.findById(AccountActionEntity.class, acccountActionId);
		return new SavingsActivityEntity(personnel, accountAction, amount,
				balanceAmount);
	}

	@Override
	public void regenerateFutureInstallments(Short nextIntallmentId)
			throws AccountException {
		if (!this.getAccountState().getId().equals(
				AccountStates.SAVINGS_ACC_CANCEL)
				&& !this.getAccountState().getId().equals(
						AccountStates.SAVINGS_ACC_CLOSED)) {
			SchedulerIntf scheduler;
			List<Date> meetingDates =null;
			try {
				scheduler = SchedulerHelper
						.getScheduler(getCustomer().getCustomerMeeting()
								.getMeeting());
				meetingDates = scheduler.getAllDates();
			} catch (SchedulerException e) {
				throw new AccountException(e);
			}
			meetingDates.remove(0);
			deleteFutureInstallments();
			if (getCustomer().getCustomerLevel().getId().equals(
					CustomerConstants.CLIENT_LEVEL_ID)
					|| (getCustomer().getCustomerLevel().getId().equals(
							CustomerConstants.GROUP_LEVEL_ID) && getRecommendedAmntUnit()
							.getRecommendedAmntUnitId().shortValue() == ProductDefinitionConstants.COMPLETEGROUP)) {
				for (Date date : meetingDates) {
					AccountActionDateEntity actionDate = helper
							.createActionDateObject(this, getCustomer(),
									nextIntallmentId++, date, null,
									getRecommendedAmount());
					addAccountActionDate(actionDate);
				}
			} else {
				List<CustomerBO> children;
				try {
					children = getCustomer().getChildren(
							CustomerConstants.CLIENT_LEVEL_ID);
				} catch (PersistenceException e) {
					throw new AccountException(e);
				}
				for (Date date : meetingDates) {
					Short installmentId = new Short(nextIntallmentId++);
					for (CustomerBO customer : children) {
						AccountActionDateEntity actionDate = helper
								.createActionDateObject(this, customer,
										installmentId, date, null,
										getRecommendedAmount());
						addAccountActionDate(actionDate);
					}
				}
			}
		}

	}

	public Money getTotalPaymentDue(Integer customerId) {
		return isMandatory() ? getTotalPaymentDueForManAccount(customerId)
				: getTotalPaymentDueForVolAccount(customerId);
	}

	public List<AccountActionDateEntity> getTotalInstallmentsDue(
			Integer customerId) {
		return isMandatory() ? getInstallmentsDueForManAccount(customerId)
				: getInstallmentDueForVolAccount(customerId);
	}

	private List<AccountActionDateEntity> getInstallmentsDueForManAccount(
			Integer customerId) {
		List<AccountActionDateEntity> dueInstallements = new ArrayList<AccountActionDateEntity>();
		Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
		if (getAccountActionDates() != null
				&& getAccountActionDates().size() > 0) {
			for (AccountActionDateEntity accountAction : getAccountActionDates()) {
				if (accountAction.getActionDate().compareTo(currentDate) <= 0
						&& accountAction.getPaymentStatus().equals(
								PaymentStatus.UNPAID.getValue())
						&& accountAction.getCustomer().getCustomerId().equals(
								customerId))
					dueInstallements.add(accountAction);
			}
		}
		return dueInstallements;
	}

	private List<AccountActionDateEntity> getInstallmentDueForVolAccount(
			Integer customerId) {
		List<AccountActionDateEntity> dueInstallments = new ArrayList<AccountActionDateEntity>();
		List<AccountActionDateEntity> installments = getInstallmentsDueForManAccount(customerId);
		if (installments != null && installments.size() > 0)
			dueInstallments.add(installments.get(installments.size() - 1));
		return dueInstallments;

	}

	private Money getTotalPaymentDueForManAccount(Integer customerId) {
		List<AccountActionDateEntity> dueInstallments = getInstallmentsDueForManAccount(customerId);
		Money dueAmount = new Money();
		if (dueInstallments != null && dueInstallments.size() > 0) {
			for (AccountActionDateEntity installment : dueInstallments) {
				dueAmount = dueAmount.add(getDueAmount(installment));
			}
		}
		return dueAmount;
	}

	private Money getTotalPaymentDueForVolAccount(Integer customerId) {
		List<AccountActionDateEntity> dueInstallments = getInstallmentDueForVolAccount(customerId);
		Money dueAmount = new Money();
		if (dueInstallments != null && dueInstallments.size() > 0)
			dueAmount = getDueAmount(dueInstallments.get(0));
		return dueAmount;
	}

	public void getSavingPerformanceHistory() throws AccountException {
		String systemDate = DateHelper.getCurrentDate(Configuration
				.getInstance().getSystemConfig().getMFILocale());
		java.sql.Date currentDate = DateHelper.getLocaleDate(Configuration
				.getInstance().getSystemConfig().getMFILocale(), systemDate);
		try {
			getSavingsPerformance().addMissedDeposits(
					(new SavingsPersistence()).getMissedDeposits(getAccountId(), currentDate));
			getSavingsPerformance().addMissedDeposits(
					(new SavingsPersistence())
							.getMissedDepositsPaidAfterDueDate(getAccountId()));
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public void generateMeetingsForNextYear() throws AccountException {
		CustomerBO customerBO = getCustomer();
		if (customerBO.getCustomerMeeting() != null
				&& customerBO.getCustomerMeeting().getMeeting() != null) {
			MeetingBO depositSchedule = customerBO.getCustomerMeeting()
					.getMeeting();

			Calendar calendar = Calendar.getInstance();
			Short lastInstallmentId = getLastInstallmentId();
			AccountActionDateEntity installment = getAccountActionDate(lastInstallmentId);
			calendar.setTimeInMillis(installment.getActionDate().getTime());
			depositSchedule.setMeetingStartDate(calendar);

			depositSchedule.setMeetingEndDate(DateUtils
					.getLastDayOfNextYear(Calendar.getInstance()));
			if (customerBO.getCustomerLevel().getId().equals(
					CustomerConstants.CLIENT_LEVEL_ID)
					|| (customerBO.getCustomerLevel().getId().equals(
							CustomerConstants.GROUP_LEVEL_ID) && getRecommendedAmntUnit()
							.getRecommendedAmntUnitId().shortValue() == ProductDefinitionConstants.COMPLETEGROUP)) {
				generateDepositAccountActions(customerBO, depositSchedule,
						(short) (installment.getInstallmentId() + 1));
			} else {
				List<CustomerBO> children;
				try {
					children = getCustomer().getChildren(
							CustomerConstants.CLIENT_LEVEL_ID);
				} catch (PersistenceException e) {
					throw new AccountException(e);
				}
				for (CustomerBO customer : children) {

					generateDepositAccountActions(customer, depositSchedule,
							installment.getInstallmentId());
				}
			}
		}
	}
}
