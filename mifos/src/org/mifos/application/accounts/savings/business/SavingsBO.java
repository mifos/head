package org.mifos.application.accounts.savings.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountCustomFieldEntity;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.application.accounts.util.helpers.AccountPaymentData;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.ChildrenStateType;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.application.productdefinition.business.RecommendedAmntUnitEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.business.SavingsTypeEntity;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class SavingsBO extends AccountBO {

	private Money recommendedAmount;

	private Money savingsBalance;

	private SavingsOfferingBO savingsOffering;

	private SavingsPerformanceEntity savingsPerformance;

	private Date activationDate;

	private RecommendedAmntUnitEntity recommendedAmntUnit;

	private SavingsTypeEntity savingsType;

	private Money interestToBePosted;

	private Date lastIntCalcDate;

	private Date lastIntPostDate;

	private Date nextIntCalcDate;

	private Date nextIntPostDate;

	private Date interIntCalcDate;

	private Money minAmntForInt;

	private Double interestRate;

	private InterestCalcTypeEntity interestCalcType;

	private MeetingBO timePerForInstcalc;

	private Set<SavingsActivityEntity> savingsActivityDetails;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	private SavingsHelper helper = new SavingsHelper();

	public SavingsBO(UserContext userContext,
			SavingsOfferingBO savingsOffering, CustomerBO customer,
			AccountState accountState, Money recommendedAmount,
			List<CustomFieldView> customFields) throws AccountException {
		super(userContext, customer, AccountTypes.SAVINGSACCOUNT, accountState);
		savingsActivityDetails = new HashSet<SavingsActivityEntity>();
		this.setSavingsOffering(savingsOffering);

		setSavingsPerformance(createSavingsPerformance());
		setSavingsBalance(new Money());
		this.setSavingsType(savingsOffering.getSavingsType());
		this.setRecommendedAmntUnit(savingsOffering.getRecommendedAmntUnit());
		addcustomFields(customFields);
		this.recommendedAmount = recommendedAmount;
		this.setSavingsOfferingDetails();
		// generated the deposit action dates only if savings account is being
		// saved in approved state
		if (isActive())
			setValuesForActiveState();

	}

	protected SavingsBO() {
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

	void setSavingsBalance(Money savingsBalance) {
		this.savingsBalance = savingsBalance;
	}

	public SavingsOfferingBO getSavingsOffering() {
		return savingsOffering;
	}

	void setSavingsOffering(SavingsOfferingBO savingsOffering) {
		this.savingsOffering = savingsOffering;
	}

	public SavingsPerformanceEntity getSavingsPerformance() {
		return savingsPerformance;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public RecommendedAmntUnitEntity getRecommendedAmntUnit() {
		return recommendedAmntUnit;
	}

	void setRecommendedAmntUnit(
			RecommendedAmntUnitEntity recommendedAmntUnit) {
		this.recommendedAmntUnit = recommendedAmntUnit;
	}

	public SavingsTypeEntity getSavingsType() {
		return savingsType;
	}

	void setSavingsType(SavingsTypeEntity savingsType) {
		this.savingsType = savingsType;
	}

	public Money getInterestToBePosted() {
		return interestToBePosted;
	}

	void setInterestToBePosted(Money interestToBePosted) {
		this.interestToBePosted = interestToBePosted;
	}

	public Date getInterIntCalcDate() {
		return interIntCalcDate;
	}

	void setInterIntCalcDate(Date interIntCalcDate) {
		this.interIntCalcDate = interIntCalcDate;
	}

	public Date getLastIntCalcDate() {
		return lastIntCalcDate;
	}

	void setLastIntCalcDate(Date lastIntCalcDate) {
		this.lastIntCalcDate = lastIntCalcDate;
	}

	public Date getLastIntPostDate() {
		return lastIntPostDate;
	}

	void setLastIntPostDate(Date lastIntPostDate) {
		this.lastIntPostDate = lastIntPostDate;
	}

	public Date getNextIntCalcDate() {
		return nextIntCalcDate;
	}

	void setNextIntCalcDate(Date nextIntCalcDate) {
		this.nextIntCalcDate = nextIntCalcDate;
	}

	public Date getNextIntPostDate() {
		return nextIntPostDate;
	}

	void setNextIntPostDate(Date nextIntPostDate) {
		this.nextIntPostDate = nextIntPostDate;
	}

	public InterestCalcTypeEntity getInterestCalcType() {
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

	private void setInterestCalcType(InterestCalcTypeEntity interestCalcType) {
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
		savingsActivityDetails.add(savingsActivity);
	}

	@Override
	public AccountTypes getType() {
		return AccountTypes.SAVINGSACCOUNT;
	}

	@Override
	public boolean isOpen() {
		return !(getAccountState().getId().equals(
				AccountState.SAVINGS_ACC_CANCEL.getValue()) || getAccountState()
				.getId().equals(AccountState.SAVINGS_ACC_CLOSED.getValue()));
	}

	private void setSavingsOfferingDetails() throws AccountException {
		setMinAmntForInt(getSavingsOffering().getMinAmntForInt());
		setInterestRate(getSavingsOffering().getInterestRate());
		setInterestCalcType(getSavingsOffering().getInterestCalcType());
		try {
			setTimePerForInstcalc(getMeeting(getSavingsOffering()
					.getTimePerForInstcalc().getMeeting()));
		} catch (ProductDefinitionException e) {
			throw new AccountException(e);
		} catch (MeetingException me) {
			throw new AccountException(me);
		}
		// setFreqOfPostIntcalc(getMeeting(getSavingsOffering().getFreqOfPostIntcalc().getMeeting()));
	}

	private MeetingBO getMeeting(MeetingBO offeringMeeting)
			throws MeetingException {
		RecurrenceType recurrenceType =
			offeringMeeting.getMeetingDetails().getRecurrenceTypeEnum();
		MeetingType meetingType = MeetingType.fromInt(offeringMeeting
				.getMeetingType().getMeetingTypeId());
		return new MeetingBO(recurrenceType, offeringMeeting
				.getMeetingDetails().getRecurAfter(), helper
				.getFiscalStartDate(), meetingType);
	}

	private void setSavingsPerformance(
			SavingsPerformanceEntity savingsPerformance) {
		this.savingsPerformance = savingsPerformance;
	}

	private SavingsPerformanceEntity createSavingsPerformance() {
		SavingsPerformanceEntity savingsPerformance = new SavingsPerformanceEntity(this);
		logger
				.info("In SavingsBO::createSavingsPerformance(), SavingsPerformanceEntity created successfully ");
		return savingsPerformance;
	}

	public void save() throws AccountException {
		logger.info("In SavingsBO::save(), Before Saving , accountId: "
				+ getAccountId());

		try {
			this
					.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
							this.getAccountState(), this.getAccountState(),
							(new PersonnelPersistence())
									.getPersonnel(userContext.getId()), this));

			(new SavingsPersistence()).createOrUpdate(this);

			this.globalAccountNum = generateId(userContext.getBranchGlobalNum());

			(new SavingsPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
		logger.info("In SavingsBO::save(), Successfully saved , accountId: "
				+ getAccountId());
	}

	public void update(Money recommendedAmount,
			List<CustomFieldView> customFields) throws AccountException {
		logger.debug("In SavingsBO::update(), accountId: " + getAccountId());
		this.setUpdatedBy(userContext.getId());
		this.setUpdatedDate(new Date());
		if (isDepositScheduleBeRegenerated()) {
			if (this.recommendedAmount != null
					&& recommendedAmount != null
					&& this.recommendedAmount.getAmountDoubleValue() != recommendedAmount
							.getAmountDoubleValue()) {
				for (AccountActionDateEntity accountDate : this
						.getAccountActionDates()) {
					if (accountDate.getActionDate().compareTo(
							helper.getCurrentDate()) >= 0)
						((SavingsScheduleEntity) accountDate)
								.setDeposit(recommendedAmount);
				}
			}
		}
		this.recommendedAmount = recommendedAmount;
		if (this.getAccountCustomFields() != null && customFields != null) {
			for (CustomFieldView view : customFields) {
				boolean fieldPresent=false;
				for (AccountCustomFieldEntity customFieldEntity : this
						.getAccountCustomFields()) {
					if (customFieldEntity.getFieldId()
							.equals(view.getFieldId())){
						fieldPresent=true;
						customFieldEntity.setFieldValue(view.getFieldValue());
					}
				}
				if(!fieldPresent)
					this.getAccountCustomFields().add(
							new AccountCustomFieldEntity(this, view.getFieldId(),
									view.getFieldValue()));
			}
		}

		try {
			(new SavingsPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
		logger
				.info("In SavingsBO::update(), successfully updated , accountId: "
						+ getAccountId());
	}

	public boolean isMandatory() {
		logger.debug("In SavingsBO::isMandatory(), savingTypeId: "
				+ getSavingsType().getId());
		return getSavingsType().getId()
				.equals(SavingsType.MANDATORY.getValue());
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
			throws AccountException {
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
			Date currentIntPostingDate = getNextIntPostDate();
			try {
				setNextIntPostDate(helper.getNextScheduleDate(
						getActivationDate(), getLastIntPostDate(),
						getMeeting(getSavingsOffering().getFreqOfPostIntcalc()
								.getMeeting())));
			} catch (ProductDefinitionException e) {
				throw new AccountException(e);
			} catch (MeetingException me) {
				throw new AccountException(me);
			}
			PaymentTypeEntity paymentType = null;
			try {
				paymentType = (PaymentTypeEntity) HibernateUtil.openSession()
						.get(PaymentTypeEntity.class,
								SavingsConstants.DEFAULT_PAYMENT_TYPE);
			} catch (HibernateException e) {
				throw new AccountException(e);
			} catch (HibernateProcessException e) {
				throw new AccountException(e);
			}
			makeEntriesForInterestPosting(interestPosted, paymentType,
					getCustomer(), currentIntPostingDate, null);
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
		} catch (MeetingException me) {
			throw new AccountException(me);
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
			throws AccountException {
		if (fromDate == null)
			fromDate = getFromDate();

		SavingsTrxnDetailEntity trxn = null;
		try {
			if (getActivationDate().equals(fromDate)) {
				trxn = (new SavingsPersistence())
						.retrieveFirstTransaction(getAccountId());
				if (trxn != null)
					fromDate = trxn.getActionDate();
			} else {
				trxn = (new SavingsPersistence()).retrieveLastTransaction(
						getAccountId(), fromDate);
				if (trxn == null) {
					trxn = (new SavingsPersistence())
							.retrieveFirstTransaction(getAccountId());
					if (trxn != null)
						fromDate = trxn.getActionDate();
				}
			}
		} catch (PersistenceException pe) {
			throw new AccountException(pe);
		}
		Money principal = null;
		Money interestAmount = new Money();
		if (trxn != null) {
			trxn = (trxn.getAccountPayment().getAmount().getAmountDoubleValue() > 0) ? getLastTrxnForPayment(trxn
					.getAccountPayment())
					: getLastTrxnForAdjustedPayment(trxn.getAccountPayment());
			if (getInterestCalcType().getId().equals(
					InterestCalcType.MINIMUM_BALANCE.getValue()))
				principal = getMinimumBalance(fromDate, toDate, trxn,
						adjustedTrxn);
			else if (getInterestCalcType().getId().equals(
					InterestCalcType.AVERAGE_BALANCE.getValue()))
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
		try {
			PersonnelBO loggedInUser = (new PersonnelPersistence())
					.getPersonnel(userContext.getId());
			AccountStateEntity accountState = this.getAccountState();

			this.setAccountState((new SavingsPersistence())
					.getAccountStatusObject(AccountStates.SAVINGS_ACC_CLOSED));

			setInterestToBePosted(payment.getAmount().subtract(
					getSavingsBalance()));
			MasterPersistence masterPersistence = new MasterPersistence();
			if (getInterestToBePosted() != null
					&& getInterestToBePosted().getAmountDoubleValue() > 0)
				makeEntriesForInterestPosting(getInterestToBePosted(), payment
						.getPaymentType(), customer, payment.getPaymentDate(),
						loggedInUser);

			if (payment.getAmount().getAmountDoubleValue() > 0) {

				payment
						.addAcountTrxn(helper
								.createAccountPaymentTrxn(
										payment,
										new Money(),
										(AccountActionEntity) masterPersistence
												.getPersistentObject(
														AccountActionEntity.class,
														AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()),
										customer, loggedInUser));

				payment.setCreatedDate(helper.getCurrentDate());
				this.addAccountPayment(payment);
				addSavingsActivityDetails(buildSavingsActivity(payment
						.getAmount(), new Money(),
						AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), payment
								.getPaymentDate(), loggedInUser));
				getSavingsPerformance().setTotalWithdrawals(
						getSavingsPerformance().getTotalWithdrawals().add(
								payment.getAmount()));
				buildFinancialEntries(payment.getAccountTrxns());
			}
			this.addAccountNotes(notes);
			this.setLastIntCalcDate(helper.getCurrentDate());
			this.setLastIntPostDate(helper.getCurrentDate());
			this.setInterIntCalcDate(null);
			this.setSavingsBalance(new Money());
			this.setInterestToBePosted(new Money());
			this.setClosedDate(new Date(System.currentTimeMillis()));
			this
					.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
							accountState, this.getAccountState(), loggedInUser,
							this));
			this.update();
			logger
					.debug("In SavingsBO::close(), account closed successfully ; accountId: "
							+ getAccountId());

		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	private void makeEntriesForInterestPosting(Money interestAmt,
			PaymentTypeEntity paymentType, CustomerBO customer,
			Date postingDate, PersonnelBO loggedInUser) throws AccountException {
		AccountPaymentEntity interestPayment = helper.createAccountPayment(
				this, interestAmt, paymentType, loggedInUser);
		MasterPersistence masterPersistence = new MasterPersistence();
		try {
			interestPayment
					.addAcountTrxn(helper
							.createAccountPaymentTrxn(
									interestPayment,
									interestAmt,
									(AccountActionEntity) masterPersistence
											.getPersistentObject(
													AccountActionEntity.class,
													AccountActionTypes.SAVINGS_INTEREST_POSTING.getValue()),
									customer, loggedInUser));
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
		this.addAccountPayment(interestPayment);
		if (userContext == null)
			addSavingsActivityDetails(buildSavingsActivity(interestAmt,
					getSavingsBalance().add(interestAmt),
					AccountActionTypes.SAVINGS_INTEREST_POSTING.getValue(),
					postingDate, null));
		else
			addSavingsActivityDetails(buildSavingsActivity(interestAmt,
					getSavingsBalance().add(interestAmt),
					AccountActionTypes.SAVINGS_INTEREST_POSTING.getValue(),
					postingDate, loggedInUser));
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

				totalBalance = totalBalance
						.add(new Money(String.valueOf(initialBalance
								.getAmountDoubleValue()
								* days)));
				initialBalance = lastTrxnAmt;
				if (adjustedTrxn != null
						&& getLastPmnt().getPaymentId().equals(
								savingsTrxn.getAccountPayment().getPaymentId()))
					initialBalance = adjustedTrxn.getBalance();
				noOfDays += days;
			}
		}
		int days = helper.calculateDays(fromDate, toDate);
		totalBalance = totalBalance.add(new Money(String.valueOf(initialBalance
				.getAmountDoubleValue()
				* days)));
		noOfDays += days;
		return (noOfDays == 0 ? initialBalance : new Money(String
				.valueOf(totalBalance.getAmountDoubleValue() / noOfDays)));
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
						AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
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
			double interestRate, Date fromDate, Date toDate)
			throws AccountException {
		int days = helper.calculateDays(fromDate, toDate);
		return getInterest(principal, interestRate, days, SavingsConstants.DAYS);
	}

	private Money getInterest(Money principal, double interestRate,
			int duration, String durationType) {
		double intRate = interestRate;
		if (durationType.equals(SavingsConstants.DAYS)) {
			intRate = (intRate / (AccountConstants.INTEREST_DAYS)) * duration;
		} else {
			intRate = (intRate / 12) * duration;
		}
		Money interestAmount = principal.multiply(
				new Double(1 + (intRate / 100.0))).subtract(principal);
		return interestAmount;
	}

	public void generateAndUpdateDepositActionsForClient(ClientBO client)
			throws AccountException {
		if (client.getCustomerMeeting().getMeeting() != null) {
			if (!(getCustomer().getCustomerLevel().getId().shortValue() == CustomerConstants.GROUP_LEVEL_ID && getRecommendedAmntUnit()
					.getId().equals(
							RecommendedAmountUnit.COMPLETE_GROUP.getValue()))) {
				generateDepositAccountActions(client, client
						.getCustomerMeeting().getMeeting());
				this.update();
			}
		}
	}

	private void generateDepositAccountActions() throws AccountException {
		logger.debug("In SavingsBO::generateDepositAccountActions()");
		// deposit happens on each meeting date of the customer. If for
		// center/group with individual deposits, insert row for every client
		if (getCustomer().getCustomerMeeting() != null
				&& getCustomer().getCustomerMeeting().getMeeting() != null) {
			MeetingBO depositSchedule = getCustomer().getCustomerMeeting()
					.getMeeting();

			if (getCustomer().getCustomerLevel().getId().equals(
					CustomerConstants.CLIENT_LEVEL_ID)
					|| (getCustomer().getCustomerLevel().getId().equals(
							CustomerConstants.GROUP_LEVEL_ID) && getRecommendedAmntUnit()
							.getId().equals(
									RecommendedAmountUnit.COMPLETE_GROUP
											.getValue()))) {
				generateDepositAccountActions(getCustomer(), depositSchedule);
			} else {
				List<CustomerBO> children;
				try {
					children = getCustomer().getChildren(CustomerLevel.CLIENT,
							ChildrenStateType.ACTIVE_AND_ONHOLD);
				} catch (CustomerException ce) {
					throw new AccountException(ce);
				}
				for (CustomerBO customer : children) {
					generateDepositAccountActions(customer, depositSchedule);
				}
			}
		}
	}

	private void generateDepositAccountActions(CustomerBO customer,
			MeetingBO meeting) throws AccountException {
		Date oldMeetingStartDate = meeting.getStartDate();
		meeting.setStartDate(DateUtils.getCurrentDateWithoutTimeStamp());
		
		List<Date> depositDates;
		try {
			depositDates = meeting.getAllDates((short) 10);
		} catch (MeetingException e) {
			throw new AccountException(e);
		}
		meeting.setStartDate(oldMeetingStartDate);
		short installmentNumber = 1;
		for (Date date : depositDates) {
			AccountActionDateEntity actionDate = helper.createActionDateObject(
					this, customer, installmentNumber++, date, userContext
							.getId(), getRecommendedAmount());
			addAccountActionDate(actionDate);
			logger
					.debug("In SavingsBO::generateDepositAccountActions(), Successfully added account action on date: "
							+ date);
		}
	}

	private void generateDepositAccountActions(CustomerBO customer,
			MeetingBO meeting, AccountActionDateEntity lastInstallment)
			throws AccountException {
		List<Date> depositDates;
		try {
			depositDates = meeting.getAllDates((short) 11);
			if (depositDates.get(0).compareTo(lastInstallment.getActionDate()) == 0) {
				depositDates.remove(0);
			} else {
				depositDates.remove(10);
			}
		} catch (MeetingException me) {
			throw new AccountException(me);
		}
		short installmentNumber = lastInstallment.getInstallmentId();
		for (Date dt : depositDates) {
			AccountActionDateEntity actionDate = helper.createActionDateObject(
					this, customer, ++installmentNumber, dt, (short)1, getRecommendedAmount());

			addAccountActionDate(actionDate);
			logger
					.debug("In SavingsBO::generateDepositAccountActions(), Successfully added account action on date: "
							+ dt);
		}
	}

	@Override
	protected AccountPaymentEntity makePayment(PaymentData paymentData)
			throws AccountException {
		MasterPersistence masterPersistence = new MasterPersistence();
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
			try {
				this
						.setAccountState((new SavingsPersistence())
								.getAccountStatusObject(AccountStates.SAVINGS_ACC_APPROVED));
			} catch (PersistenceException pe) {
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
					AccountActionTypes.SAVINGS_DEPOSIT.getValue(), transactionDate,
					paymentData.getPersonnel()));
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
				if (getSavingsType().getId().equals(
						SavingsType.VOLUNTARY.getValue())
						&& depositAmount.getAmountDoubleValue() > 0.0)
					paymentStatus = PaymentStatus.PAID;
				savingsBalance = savingsBalance.add(depositAmount);

				savingsPerformance.setPaymentDetails(depositAmount);
				accountAction.setPaymentDetails(depositAmount, paymentStatus,
						new java.sql.Date(transactionDate.getTime()));

				SavingsTrxnDetailEntity accountTrxn;
				try {
					accountTrxn = new SavingsTrxnDetailEntity(
							accountPayment,
							customer,
							(AccountActionEntity) masterPersistence
									.getPersistentObject(
											AccountActionEntity.class,
											AccountActionTypes.SAVINGS_DEPOSIT.getValue()),
							depositAmount, getSavingsBalance(), paymentData
									.getPersonnel(), accountAction
									.getActionDate(), paymentData
									.getTransactionDate(), accountAction
									.getInstallmentId(), "");
				} catch (PersistenceException e) {
					throw new AccountException(e);
				}

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
				getSavingsBalance(), AccountActionTypes.SAVINGS_DEPOSIT.getValue(),
				transactionDate, paymentData.getPersonnel()));
		return accountPayment;
	}

	private SavingsTrxnDetailEntity buildUnscheduledDeposit(
			AccountPaymentEntity accountPayment, Money depositAmount,
			PersonnelBO personnel, CustomerBO customer, Date transactionDate)
			throws AccountException {
		MasterPersistence masterPersistence = new MasterPersistence();
		SavingsTrxnDetailEntity accountTrxn;
		savingsBalance = savingsBalance.add(depositAmount);
		savingsPerformance.setPaymentDetails(depositAmount);
		try {
			accountTrxn = new SavingsTrxnDetailEntity(accountPayment, customer,
					(AccountActionEntity) masterPersistence
							.getPersistentObject(AccountActionEntity.class,
									AccountActionTypes.SAVINGS_DEPOSIT.getValue()),
					depositAmount, getSavingsBalance(), personnel,
					transactionDate, transactionDate,

					null, "");
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
		return accountTrxn;
	}

	public void withdraw(PaymentData accountPaymentData)
			throws AccountException {
		MasterPersistence masterPersistence = new MasterPersistence();
		Money totalAmount = accountPaymentData.getTotalAmount();
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
		SavingsTrxnDetailEntity accountTrxnBO;
		try {
			accountTrxnBO = new SavingsTrxnDetailEntity(
					accountPayment,
					customer,
					(AccountActionEntity) masterPersistence
							.getPersistentObject(AccountActionEntity.class,
									AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()),
					totalAmount, getSavingsBalance(), accountPaymentData
							.getPersonnel(), accountPaymentData
							.getTransactionDate(), accountPaymentData
							.getTransactionDate(), null, "");
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
		accountPayment.addAcountTrxn(accountTrxnBO);
		addAccountPayment(accountPayment);
		addSavingsActivityDetails(buildSavingsActivity(totalAmount,
				getSavingsBalance(),
				AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), accountPaymentData
						.getTransactionDate(), accountPaymentData
						.getPersonnel()));
		buildFinancialEntries(accountPayment.getAccountTrxns());
		if (this.getAccountState().getId().equals(
				AccountStates.SAVINGS_ACC_INACTIVE))
			try {
				this
						.setAccountState((new SavingsPersistence())
								.getAccountStatusObject(AccountStates.SAVINGS_ACC_APPROVED));
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}
		try {
			(new SavingsPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	private void setValuesForActiveState() throws AccountException {
		this.setActivationDate(new Date(new java.util.Date().getTime()));
		this.generateDepositAccountActions();
		try {
			this.setNextIntCalcDate(helper.getNextScheduleDate(
					getActivationDate(), null, getTimePerForInstcalc()));
			this.setNextIntPostDate(helper.getNextScheduleDate(
					getActivationDate(), null, getMeeting(getSavingsOffering()
							.getFreqOfPostIntcalc().getMeeting())));
		} catch (ProductDefinitionException e) {
			throw new AccountException(e);
		} catch (MeetingException me) {
			throw new AccountException(me);
		}
	}

	@Override
	protected void activationDateHelper(Short newStatusId)
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

	public void adjustLastUserAction(Money amountAdjustedTo,
			String adjustmentComment) throws AccountException {
		logger
				.debug("In SavingsBO::generateDepositAccountActions(), accountId: "
						+ getAccountId());
		if (!isAdjustPossibleOnLastTrxn(amountAdjustedTo)) {
			throw new AccountException(AccountExceptionConstants.CANNOTADJUST);
		}
		Date trxnDate = getTrxnDate(getLastPmnt());
		Money oldInterest = calculateInterestForAdjustment(trxnDate, null);
		adjustExistingPayment(amountAdjustedTo, adjustmentComment);
		AccountPaymentEntity newPayment = createAdjustmentPayment(
				amountAdjustedTo, adjustmentComment);
		adjustInterest(oldInterest, trxnDate, newPayment);
		if (this.getAccountState().getId().equals(
				AccountState.SAVINGS_ACC_INACTIVE.getValue())) {
			try {
				this
						.setAccountState((new SavingsPersistence())
								.getAccountStatusObject(AccountState.SAVINGS_ACC_APPROVED
										.getValue()));
			} catch (PersistenceException pe) {
				throw new AccountException(pe);
			}
		}
		this.update();
	}

	protected void adjustInterest(Money oldInterest, Date trxnDate,
			AccountPaymentEntity newPayment) throws AccountException {
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
		if (accountAction.equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue())) {
			for (AccountTrxnEntity accountTrxn : payment.getAccountTrxns())
				return (SavingsTrxnDetailEntity) accountTrxn;
		} else if (accountAction
				.equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
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
		if (accountAction.equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue())) {
			for (AccountTrxnEntity accountTrxn : payment.getAccountTrxns()) {
				if (accountTrxn.getAccountActionEntity().getId().equals(
						AccountActionTypes.SAVINGS_ADJUSTMENT.getValue()))
					return (SavingsTrxnDetailEntity) accountTrxn;
			}
		} else if (accountAction
				.equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
			SavingsTrxnDetailEntity lastTrxn = null;
			for (AccountTrxnEntity trxn : payment.getAccountTrxns()) {
				SavingsTrxnDetailEntity accountTrxn = (SavingsTrxnDetailEntity) trxn;
				if (lastTrxn == null
						|| (accountTrxn
								.getAccountActionEntity()
								.getId()
								.equals(
										AccountActionTypes.SAVINGS_ADJUSTMENT.getValue()) && accountTrxn
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
			} catch (MeetingException me) {
				throw new AccountException(me);
			}
			if (fromDate != null)
				fromDate = new Timestamp(fromDate.getTime());
		} while (fromDate != null && trxnDate.compareTo(fromDate) < 0);
		return (fromDate == null) ? getActivationDate() : fromDate;
	}

	protected Money calculateInterestForAdjustment(Date trxnDate,
			SavingsTrxnDetailEntity adjustedTrxn) throws AccountException {
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
			} catch (MeetingException me) {
				throw new AccountException(me);
			}
		}
		return oldInterest;
	}

	protected void adjustExistingPayment(Money amountAdjustedTo,
			String adjustmentComment) throws AccountException {
		AccountPaymentEntity lastPayment = getLastPmnt();
		Short actionType = helper.getPaymentActionType(lastPayment);
		try {
			PersonnelBO personnel = (new PersonnelPersistence())
					.getPersonnel(userContext.getId());
			for (AccountTrxnEntity accntTrxn : lastPayment.getAccountTrxns()) {
				if (actionType.equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue()))
					adjustForDeposit(accntTrxn);
				else if (actionType
						.equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()))
					adjustForWithdrawal(accntTrxn);
			}
			addSavingsActivityDetails(buildSavingsActivity(lastPayment
					.getAmount(), getSavingsBalance(),
					AccountActionTypes.SAVINGS_ADJUSTMENT.getValue(), new Date(),
					personnel));
			logger
					.debug("transaction count before adding reversal transactions: "
							+ lastPayment.getAccountTrxns().size());
			List<AccountTrxnEntity> newlyAddedTrxns = reversalAdjustment(
					adjustmentComment, lastPayment);
			buildFinancialEntries(new HashSet<AccountTrxnEntity>(
					newlyAddedTrxns));
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	protected AccountPaymentEntity createAdjustmentPayment(
			Money amountAdjustedTo, String adjustmentComment)
			throws AccountException {
		try {
			AccountPaymentEntity lastPayment = getLastPmnt();
			AccountPaymentEntity newAccountPayment = null;
			PersonnelBO personnel = (new PersonnelPersistence())
					.getPersonnel(userContext.getId());
			if (amountAdjustedTo.getAmountDoubleValue() > 0) {
				newAccountPayment = helper.createAccountPayment(this,
						amountAdjustedTo, lastPayment.getPaymentType(),
						(new PersonnelPersistence()).getPersonnel(userContext
								.getId()));
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

				addSavingsActivityDetails(buildSavingsActivity(
						amountAdjustedTo, getSavingsBalance(), helper
								.getPaymentActionType(lastPayment),
						getTrxnDate(lastPayment), personnel));
			}
			return newAccountPayment;
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
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
			AccountPaymentEntity lastAccountPayment, Money newAmount)
			throws AccountException {
		Short actionType = helper.getPaymentActionType(lastAccountPayment);
		if (isMandatory()
				&& actionType.equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue()))
			return createDepositTrxnsForMandatoryAccountsAfterAdjust(
					newAccountPayment, lastAccountPayment, newAmount);

		if (actionType.equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue()))
			return createDepositTrxnsForVolAccountsAfterAdjust(
					newAccountPayment, lastAccountPayment, newAmount);

		return createWithdrawalTrxnsAfterAdjust(newAccountPayment,
				lastAccountPayment, newAmount);
	}

	private Set<AccountTrxnEntity> createWithdrawalTrxnsAfterAdjust(
			AccountPaymentEntity newAccountPayment,
			AccountPaymentEntity lastAccountPayment, Money newAmount)
			throws AccountException {
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
		try {
			accountTrxn = new SavingsTrxnDetailEntity(
					newAccountPayment,
					oldSavingsAccntTrxn.getCustomer(),
					(AccountActionEntity) masterPersistence
							.getPersistentObject(AccountActionEntity.class,
									AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()),
					newAmount, getSavingsBalance(),
					(new PersonnelPersistence()).getPersonnel(userContext
							.getId()), oldSavingsAccntTrxn.getDueDate(),
					oldSavingsAccntTrxn.getActionDate(), null, "");
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
		getSavingsPerformance().setTotalWithdrawals(
				getSavingsPerformance().getTotalWithdrawals().add(
						accountTrxn.getWithdrawlAmount()));
		newTrxns.add(accountTrxn);
		return newTrxns;
	}

	private Set<AccountTrxnEntity> createDepositTrxnsForMandatoryAccountsAfterAdjust(
			AccountPaymentEntity newAccountPayment,
			AccountPaymentEntity lastAccountPayment, Money newAmount)
			throws AccountException {
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
				try {
					accountTrxn = new SavingsTrxnDetailEntity(
							newAccountPayment,
							customer,
							(AccountActionEntity) masterPersistence
									.getPersistentObject(
											AccountActionEntity.class,
											AccountActionTypes.SAVINGS_DEPOSIT.getValue()),
							accountAction.getDeposit(), getSavingsBalance(),
							(new PersonnelPersistence())
									.getPersonnel(userContext.getId()),
							accountAction.getActionDate(), oldTrxnDate,
							accountAction.getInstallmentId(), "");
				} catch (PersistenceException e) {
					throw new AccountException(e);
				}
				newAmount = newAmount.subtract(accountAction.getDeposit());
				accountAction.setDepositPaid(accountAction.getDepositPaid()
						.add(accountTrxn.getDepositAmount()));
				accountAction.setPaymentStatus(PaymentStatus.PAID);
			} else {
				setSavingsBalance(getSavingsBalance().add(newAmount));
				try {
					accountTrxn = new SavingsTrxnDetailEntity(
							newAccountPayment,
							customer,
							(AccountActionEntity) masterPersistence
									.getPersistentObject(
											AccountActionEntity.class,
											AccountActionTypes.SAVINGS_DEPOSIT.getValue()),
							newAmount, getSavingsBalance(),
							(new PersonnelPersistence())
									.getPersonnel(userContext.getId()),
							accountAction.getActionDate(), oldTrxnDate,
							accountAction.getInstallmentId(), "");
				} catch (PersistenceException e) {
					throw new AccountException(e);
				}
				newAmount = newAmount.subtract(newAmount);
				accountAction.setDepositPaid(accountAction.getDepositPaid()
						.add(accountTrxn.getDepositAmount()));
				accountAction.setPaymentStatus(PaymentStatus.UNPAID);
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
			try {
				accountTrxn = new SavingsTrxnDetailEntity(
						newAccountPayment,
						customer,
						(AccountActionEntity) masterPersistence
								.getPersistentObject(AccountActionEntity.class,
										AccountActionTypes.SAVINGS_DEPOSIT.getValue()),
						newAmount, getSavingsBalance(),
						(new PersonnelPersistence()).getPersonnel(userContext
								.getId()), null, oldTrxnDate, null, "");
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}
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
			AccountPaymentEntity lastAccountPayment, Money newAmount)
			throws AccountException {
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
					AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
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
						try {
							accountTrxn = new SavingsTrxnDetailEntity(
									newAccountPayment,
									customer,
									(AccountActionEntity) masterPersistence
											.getPersistentObject(
													AccountActionEntity.class,
													AccountActionTypes.SAVINGS_DEPOSIT.getValue()),
									accountAction.getDeposit(),
									getSavingsBalance(),
									(new PersonnelPersistence())
											.getPersonnel(userContext.getId()),
									accountAction.getActionDate(), oldTrxnDate,
									oldAccntTrxn.getInstallmentId(), "");
						} catch (PersistenceException e) {
							throw new AccountException(e);
						}
						newAmount = newAmount.subtract(accountAction
								.getDeposit());
						accountAction.setDepositPaid(accountAction
								.getDepositPaid().add(
										accountTrxn.getDepositAmount()));
						accountAction.setPaymentStatus(PaymentStatus.PAID);
					} else if (newAmount.getAmountDoubleValue() != 0) {
						setSavingsBalance(getSavingsBalance().add(newAmount));
						try {
							accountTrxn = new SavingsTrxnDetailEntity(
									newAccountPayment,
									customer,
									(AccountActionEntity) masterPersistence
											.getPersistentObject(
													AccountActionEntity.class,
													AccountActionTypes.SAVINGS_DEPOSIT.getValue()),
									newAmount, getSavingsBalance(),
									(new PersonnelPersistence())
											.getPersonnel(userContext.getId()),
									accountAction.getActionDate(), oldTrxnDate,
									oldAccntTrxn.getInstallmentId(), "");
						} catch (PersistenceException e) {
							throw new AccountException(e);
						}
						newAmount = newAmount.subtract(newAmount);
						accountAction.setDepositPaid(accountAction
								.getDepositPaid().add(
										accountTrxn.getDepositAmount()));
						accountAction.setPaymentStatus(PaymentStatus.UNPAID);
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
			try {
				accountTrxn = new SavingsTrxnDetailEntity(
						newAccountPayment,
						customer,
						(AccountActionEntity) masterPersistence
								.getPersistentObject(AccountActionEntity.class,
										AccountActionTypes.SAVINGS_DEPOSIT.getValue()),
						newAmount, getSavingsBalance(),
						(new PersonnelPersistence()).getPersonnel(userContext
								.getId()), null, oldTrxnDate, null, "");
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}
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
		SavingsScheduleEntity accntActionDate = (SavingsScheduleEntity) 
			getAccountActionDate(
				installmentId, accntTrxn.getCustomer().getCustomerId());
		if (accntActionDate != null) {
			accntActionDate.setDepositPaid(accntActionDate.getDepositPaid()
					.subtract(savingsTrxn.getDepositAmount()));
			accntActionDate.setPaymentStatus(PaymentStatus.UNPAID);
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
						AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()) || helper
						.getPaymentActionType(accountPayment).equals(
								AccountActionTypes.SAVINGS_DEPOSIT.getValue()))) {
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
				AccountActionTypes.SAVINGS_WITHDRAWAL.getValue())
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
					AccountActionTypes.SAVINGS_WITHDRAWAL.getValue())
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

	public AccountNotesEntity createAccountNotes(String comment)
			throws AccountException {
		try {
			AccountNotesEntity accountNotes = new AccountNotesEntity(
					new java.sql.Date(System.currentTimeMillis()), comment,
					(new PersonnelPersistence()).getPersonnel(userContext
							.getId()), this);
			return accountNotes;

		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
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

	@Override
	protected Money getDueAmount(AccountActionDateEntity installment) {
		return ((SavingsScheduleEntity) installment).getTotalDepositDue();
	}

	@Override
	public Money getTotalAmountDue() {
		return getTotalAmountInArrears().add(
				getTotalAmountDueForNextInstallment());
	}

	@Override
	public Money getTotalAmountInArrears(){
		List<AccountActionDateEntity> installmentsInArrears = getDetailsOfInstallmentsInArrears();
		Money totalAmount = new Money();
		if (installmentsInArrears != null && installmentsInArrears.size() > 0){
			for (AccountActionDateEntity accountAction : installmentsInArrears){
				if(!(accountAction.getCustomer().getCustomerLevel().getId().equals(CustomerLevel.CLIENT.getValue()) 
						&& 	accountAction.getCustomer().getStatus().equals(CustomerStatus.CLIENT_CLOSED)))
					totalAmount = totalAmount.add(getDueAmount(accountAction));
			}
		}
		return totalAmount;
	}
	
	public Money getTotalAmountDueForNextInstallment(){
		AccountActionDateEntity nextAccountAction = getDetailsOfNextInstallment();
		Money totalAmount = new Money();
		if (nextAccountAction != null){
			if (null != getAccountActionDates()
					&& getAccountActionDates().size() > 0) {
				for (AccountActionDateEntity accntActionDate : getAccountActionDates()) {
					if (accntActionDate.getInstallmentId().equals(nextAccountAction.getInstallmentId())
							&& accntActionDate.getPaymentStatus().equals(
									PaymentStatus.UNPAID.getValue())) {
						if(!(accntActionDate.getCustomer().getCustomerLevel().getId().equals(CustomerLevel.CLIENT.getValue()) 
								&& 	accntActionDate.getCustomer().getStatus().equals(CustomerStatus.CLIENT_CLOSED)))
							totalAmount = totalAmount.add(((SavingsScheduleEntity) accntActionDate)
											.getTotalDepositDue());
					}
				}
			}
		}
		return totalAmount;
	}
	
	/*private Money getTotalAmountDueForInstallment(Short installmentId) {
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
	}*/

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

	@Override
	public void waiveAmountDue(WaiveEnum waiveType) throws AccountException {
		try {
			PersonnelBO personnel = (new PersonnelPersistence())
					.getPersonnel(getUserContext().getId());
			addSavingsActivityDetails(buildSavingsActivity(
					getTotalAmountDueForNextInstallment(), getSavingsBalance(),
					AccountActionTypes.WAIVEOFFDUE.getValue(), new Date(), personnel));
			List<AccountActionDateEntity> nextInstallments = getNextInstallment();
			for (AccountActionDateEntity accountActionDate : nextInstallments) {
				((SavingsScheduleEntity) accountActionDate).waiveDepositDue();
			}

			(new SavingsPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public void waiveAmountOverDue() throws AccountException {
		try {
			PersonnelBO personnel = (new PersonnelPersistence())
					.getPersonnel(getUserContext().getId());
			addSavingsActivityDetails(buildSavingsActivity(
					getTotalAmountInArrears(), getSavingsBalance(),
					AccountActionTypes.WAIVEOFFOVERDUE.getValue(), new Date(),
					personnel));
			List<AccountActionDateEntity> installmentsInArrears = getDetailsOfInstallmentsInArrears();
			for (AccountActionDateEntity accountActionDate : installmentsInArrears) {
				((SavingsScheduleEntity) accountActionDate).waiveDepositDue();
			}

			(new SavingsPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}

	}

	private SavingsActivityEntity buildSavingsActivity(Money amount,
			Money balanceAmount, short acccountActionId, Date trxnDate,
			PersonnelBO personnel) throws AccountException {
		MasterPersistence masterPersistence = new MasterPersistence();
		AccountActionEntity accountAction;
		try {
			accountAction = (AccountActionEntity) masterPersistence
					.getPersistentObject(AccountActionEntity.class,
							acccountActionId);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
		return new SavingsActivityEntity(personnel, accountAction, amount,
				balanceAmount, trxnDate, this);
	}

	@Override
	protected void regenerateFutureInstallments(Short nextInstallmentId)
			throws AccountException {
		if (!this.getAccountState().getId().equals(
				AccountStates.SAVINGS_ACC_CANCEL)
				&& !this.getAccountState().getId().equals(
						AccountStates.SAVINGS_ACC_CLOSED)) {
			List<Date> meetingDates = null;
			int installmentSize = getLastInstallmentId();
			int totalInstallmentDatesToBeChanged = installmentSize
					- nextInstallmentId + 1;
			try {
				meetingDates = getCustomer().getCustomerMeeting().getMeeting()
						.getAllDates(totalInstallmentDatesToBeChanged + 1);
				if (meetingDates.get(0).compareTo(
						DateUtils.getCurrentDateWithoutTimeStamp()) == 0) {
					meetingDates.remove(0);
				} else {
					meetingDates.remove(totalInstallmentDatesToBeChanged);
				}
			} catch (MeetingException me) {
				throw new AccountException(me);
			}
			if (getCustomer().getCustomerLevel().getId().equals(
					CustomerConstants.CLIENT_LEVEL_ID)
					|| (getCustomer().getCustomerLevel().getId().equals(
							CustomerConstants.GROUP_LEVEL_ID) && getRecommendedAmntUnit()
							.getId().equals(
									RecommendedAmountUnit.COMPLETE_GROUP
											.getValue()))) {
				for (int count = 0; count < meetingDates.size(); count++) {
					short installmentId = (short) (nextInstallmentId.intValue() + count);
					AccountActionDateEntity accountActionDate = getAccountActionDate(installmentId);
					if (accountActionDate != null)
						((SavingsScheduleEntity)accountActionDate).setActionDate(new java.sql.Date(
								meetingDates.get(count).getTime()));
				}
			} else {
				List<CustomerBO> children;
				try {
					children = getCustomer().getChildren(CustomerLevel.CLIENT,
							ChildrenStateType.OTHER_THAN_CLOSED);
				} catch (CustomerException ce) {
					throw new AccountException(ce);
				}
				for (int count = 0; count < meetingDates.size(); count++) {
					short installmentId = (short) (nextInstallmentId.intValue() + count);
					for (CustomerBO customer : children) {
						AccountActionDateEntity accountActionDate = getAccountActionDate(
								installmentId, customer.getCustomerId());
						if (accountActionDate != null)
							((SavingsScheduleEntity)accountActionDate).setActionDate(new java.sql.Date(
									meetingDates.get(count).getTime()));
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
		String systemDate = DateUtils.getCurrentDate(Configuration
		.getInstance().getSystemConfig().getMFILocale());
		java.sql.Date currentDate = DateUtils.getLocaleDate(Configuration
		.getInstance().getSystemConfig().getMFILocale(), systemDate);
		try {
			getSavingsPerformance().addMissedDeposits(
					(new SavingsPersistence()).getMissedDeposits(
							getAccountId(), currentDate));
			getSavingsPerformance().addMissedDeposits(
					(new SavingsPersistence())
							.getMissedDepositsPaidAfterDueDate(getAccountId()));
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public void generateNextSetOfMeetingDates() throws AccountException {
		CustomerBO customerBO = getCustomer();
		if (customerBO.getCustomerMeeting() != null
				&& customerBO.getCustomerMeeting().getMeeting() != null) {
			MeetingBO depositSchedule = customerBO.getCustomerMeeting()
					.getMeeting();
			Date oldMeetingDate = depositSchedule.getStartDate();
			Calendar calendar = Calendar.getInstance();
			Short lastInstallmentId = getLastInstallmentId();
			AccountActionDateEntity lastInstallment = getAccountActionDate(lastInstallmentId);
			calendar.setTimeInMillis(lastInstallment.getActionDate().getTime());
			depositSchedule.setMeetingStartDate(calendar);
			if (customerBO.getCustomerLevel().getId().equals(
					CustomerConstants.CLIENT_LEVEL_ID)
					|| (customerBO.getCustomerLevel().getId().equals(
							CustomerConstants.GROUP_LEVEL_ID) && getRecommendedAmntUnit()
							.getId().equals(
									RecommendedAmountUnit.COMPLETE_GROUP
											.getValue()))) {
				generateDepositAccountActions(customerBO, depositSchedule,
						lastInstallment);
			} else {
				List<CustomerBO> children;
				try {
					children = getCustomer().getChildren(CustomerLevel.CLIENT,
							ChildrenStateType.OTHER_THAN_CLOSED);
				} catch (CustomerException ce) {
					throw new AccountException(ce);
				}
				for (CustomerBO customer : children) {
					generateDepositAccountActions(customer, depositSchedule,
							lastInstallment);
				}
			}
			depositSchedule.setStartDate(oldMeetingDate);
		}
	}

	@Override
	public Date getNextMeetingDate() {
		AccountActionDateEntity nextAccountAction = getDetailsOfNextInstallment();

		return nextAccountAction != null ? nextAccountAction.getActionDate()
				: null;
	}
	
	@Override
	public boolean isTrxnDateValid(Date trxnDate) throws AccountException {
		if (Configuration.getInstance().getAccountConfig(
				getOffice().getOfficeId()).isBackDatedTxnAllowed()) {
			Date meetingDate = null;
			trxnDate = DateUtils.getDateWithoutTimeStamp(trxnDate.getTime());
			try {
				meetingDate = new CustomerPersistence()
						.getLastMeetingDateForCustomer(getCustomer()
								.getCustomerId());
				if (meetingDate != null) {
					meetingDate = DateUtils.getDateWithoutTimeStamp(meetingDate.getTime());					
					return trxnDate.compareTo(meetingDate) >= 0 ? true : false;
				} else{
					Date activationDate = DateUtils.getDateWithoutTimeStamp(getActivationDate().getTime());
					return trxnDate.compareTo(activationDate) >= 0 && trxnDate.compareTo(DateUtils.getCurrentDateWithoutTimeStamp())<=0? true : false;
				}
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}			
		}else
			return trxnDate.equals(DateUtils.getCurrentDateWithoutTimeStamp());
	}
}
