package org.mifos.application.accounts.savings.util.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_SECOND_MONTH;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.accounts.savings.business.TestSavingsBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestGeneralLedgerCode;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsTestHelper {

	public AccountPaymentEntity createAccountPayment(AccountBO account,
			Money amount, Date paymentDate, PersonnelBO createdBy) {
		return createAccountPayment(account, null, amount, paymentDate,
				createdBy);
	}

	public AccountPaymentEntity createAccountPayment(AccountBO account,
			Integer paymentId, Money amount, Date paymentDate,
			PersonnelBO createdBy) {
		AccountPaymentEntity payment = new AccountPaymentEntity(account,
				amount, null, null, new PaymentTypeEntity(Short.valueOf("1")), new Date(System.currentTimeMillis()));
		payment.setCreatedBy(createdBy.getPersonnelId());
		payment.setCreatedDate(new Date());
		return payment;
	}

	public AccountPaymentEntity createAccountPaymentToPersist(
			AccountBO account, Money amount, Money balance, Date trxnDate,
			Short accountAction, SavingsBO savingsObj, PersonnelBO createdBy,
			CustomerBO customer) throws Exception {
		AccountPaymentEntity payment = createAccountPayment(account, amount,
				new Date(), createdBy);
		payment.addAccountTrxn(createAccountTrxn(payment, null, amount, balance,
				trxnDate, trxnDate, null, accountAction, savingsObj, createdBy,
				customer));
		return payment;
	}

	public SavingsTrxnDetailEntity createAccountTrxn(
			AccountPaymentEntity paymentEntity, Short installmentId,
			Money amount, Money balance, Date trxnDate, Date dueDate,
			Integer id, Short accountAction, SavingsBO savingsObj,
			PersonnelBO createdBy, CustomerBO customer) throws Exception {
		SavingsTrxnDetailEntity trxn = new SavingsTrxnDetailEntity(
				paymentEntity, customer,
				(AccountActionEntity) new MasterPersistence()
						.getPersistentObject(AccountActionEntity.class,
								accountAction), amount, balance, createdBy,
				dueDate, trxnDate, installmentId, "");
		return trxn;
	}

	public SavingsTrxnDetailEntity createAccountTrxn(
			AccountPaymentEntity paymentEntity, Money amount, Money balance,
			Date trxnDate, Date dueDate, Short accountAction,
			SavingsBO savingsObj, PersonnelBO createdBy, CustomerBO customer,
			String comments, AccountTrxnEntity relatedTrxn) throws Exception {
		SavingsTrxnDetailEntity trxn = new SavingsTrxnDetailEntity(
				paymentEntity, (AccountActionEntity) new MasterPersistence()
						.getPersistentObject(AccountActionEntity.class,
								accountAction), amount, balance, createdBy,
				customer, dueDate, trxnDate, comments, relatedTrxn);
		return trxn;
	}

	public SavingsTrxnDetailEntity createAccountTrxn(
			AccountPaymentEntity paymentEntity, Short installmentId,
			Money amount, Money balance, Date trxnDate, Date dueDate,
			Integer id, Short accountAction, SavingsBO savingsObj,
			PersonnelBO createdBy, CustomerBO customer, String comments)
			throws Exception {
		SavingsTrxnDetailEntity trxn = new SavingsTrxnDetailEntity(
				paymentEntity, customer,
				(AccountActionEntity) new MasterPersistence()
						.getPersistentObject(AccountActionEntity.class,
								accountAction), amount, balance, createdBy,
				dueDate, trxnDate, installmentId, comments);
		return trxn;
	}

	public CenterBO createCenter() {
		MeetingBO meeting = TestObjectFactory.getNewMeetingForToday(MONTHLY, EVERY_SECOND_MONTH, CUSTOMER_MEETING);
		meeting.setMeetingStartDate(new Date());
		meeting.getMeetingDetails().getMeetingRecurrence().setDayNumber(
				new Short("1"));
		TestObjectFactory.createMeeting(meeting);
		return TestObjectFactory.createCenter("Center_Active_test", meeting);
	}

	public SavingsOfferingBO createSavingsOffering(String offeringName,
			String shortName) {
		return createSavingsOffering(offeringName, shortName, 
			TestGeneralLedgerCode.ASSETS,
			TestGeneralLedgerCode.CASH_AND_BANK_BALANCES
			);
	}

	public SavingsOfferingBO createSavingsOffering(String offeringName,
			String shortName, Short depGLCode, Short intGLCode) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		return TestObjectFactory.createSavingsProduct(
				offeringName, shortName,
				ApplicableTo.GROUPS, 
				new Date(System.currentTimeMillis()), 
				PrdStatus.SAVINGS_ACTIVE, 
				300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 24.0, 200.0,
				200.0, SavingsType.VOLUNTARY, 
				InterestCalcType.MINIMUM_BALANCE, 
				meetingIntCalc, meetingIntPost, depGLCode, intGLCode);
	}

	public SavingsOfferingBO createSavingsOffering(String offeringName,
			InterestCalcType interestCalcType, SavingsType savingsType) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		return TestObjectFactory.createSavingsProduct(
			offeringName, ApplicableTo.GROUPS, 
			new Date(System.currentTimeMillis()), 
			PrdStatus.SAVINGS_ACTIVE, 
			300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 
			24.0, 200.0, 200.0, 
			savingsType, interestCalcType, 
			meetingIntCalc, meetingIntPost);
	}

	public SavingsBO createSavingsAccount(String globalAccountNum,
			SavingsOfferingBO savingsOffering, CustomerBO customer,
			short accountStateId, UserContext userContext) throws Exception {
		return createSavingsAccount(globalAccountNum, savingsOffering, 
			customer, AccountState.fromShort(accountStateId), userContext);
	}

	public SavingsBO createSavingsAccount(String globalAccountNum, 
		SavingsOfferingBO savingsOffering, CustomerBO customer, 
		AccountState state, UserContext userContext) throws Exception {
		SavingsBO savings = TestObjectFactory.createSavingsAccount(
				globalAccountNum, customer, state, new Date(),
				savingsOffering, userContext);
		savings.setUserContext(userContext);
		return savings;
	}

	public SavingsBO createSavingsAccount(SavingsOfferingBO savingsOffering,
			CustomerBO customer, AccountState accountState, UserContext userContext)
			throws Exception {
		SavingsBO savings = new SavingsBO(userContext, savingsOffering,
				customer, accountState, new Money("500.0"), null, new CustomerPersistence());
		savings.save();
		return savings;
	}

	public Date getDate(String date) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		return df.parse(date);
	}

	public Date getDate(String date, int hr, int min) throws ParseException {
		Calendar cal = Calendar.getInstance(Configuration.getInstance()
				.getSystemConfig().getMifosTimeZone());
		cal.setTime(getDate(date));
		cal.set(Calendar.HOUR, hr);
		cal.set(Calendar.MINUTE, min);
		return cal.getTime();
	}

	public AccountNotesEntity getAccountNotes(SavingsBO savingsBO) {
		AccountNotesEntity notes = new AccountNotesEntity(new java.sql.Date(
				System.currentTimeMillis()), "xxxxxxxxxxxx", savingsBO
				.getPersonnel(),savingsBO);
		return notes;
	}

	public AccountActionDateEntity createAccountActionDate(AccountBO account,
			short installmentId, Date dueDate, Date paymentDate,
			CustomerBO customer, Money deposit, Money depositPaid,
			PaymentStatus paymentStatus) {
		SavingsScheduleEntity actionDate = new SavingsScheduleEntity(account,
				customer, installmentId, new java.sql.Date(dueDate.getTime()),
				paymentStatus, deposit);
		TestSavingsBO.setDepositPaid(actionDate,depositPaid);
		if (paymentDate != null)
			TestSavingsBO.setPaymentDate(actionDate,new java.sql.Date(paymentDate.getTime()));
		return actionDate;
	}

	public MeetingBO getMeeting(RecurrenceType frequency, short recurAfter,
			MeetingType meetingType) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		MeetingBO meeting = TestObjectFactory.getNewMeetingForToday(frequency, recurAfter, meetingType);
		Calendar cal = Calendar.getInstance(Configuration.getInstance()
				.getSystemConfig().getMifosTimeZone());
		cal.setTime(df.parse("01/01/2006"));
		meeting.setMeetingStartDate(cal.getTime());
		return meeting;
	}

	/*
	public MeetingBO getMeeting(RecurrenceType recurrenceType, Short dayNumber,
			Short weekDay, Short dayRank, Short recurAfter,
			MeetingType meetingType) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		MeetingBO meeting = TestObjectFactory.getMeeting(recurrenceType,
				dayNumber, weekDay, dayRank, recurAfter, meetingType);
		Calendar cal = Calendar.getInstance(Configuration.getInstance()
				.getSystemConfig().getMifosTimeZone());
		cal.setTime(df.parse("01/01/2006"));
		meeting.setMeetingStartDate(cal);
		return meeting;
	}
	*/
}
