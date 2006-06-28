package org.mifos.application.accounts.savings.util.helpers;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.scheduler.ScheduleDataIntf;
import org.mifos.framework.components.scheduler.SchedulerFactory;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.helpers.SchedulerHelper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsTestHelper {
	
	public AccountPaymentEntity createAccountPayment(Money amount, Date paymentDate, PersonnelBO createdBy){
		return createAccountPayment(null, amount, paymentDate, createdBy);
	}

	public AccountPaymentEntity createAccountPayment(Integer paymentId, Money amount, Date paymentDate, PersonnelBO createdBy){
		AccountPaymentEntity payment = new AccountPaymentEntity();
		payment.setAmount(amount);
		payment.setCreatedBy(createdBy.getPersonnelId());
		payment.setCreatedDate(new Date());
		payment.setPaymentDate(paymentDate);
		PaymentTypeEntity paymentType=new PaymentTypeEntity();
		paymentType.setId(Short.valueOf("1"));
		payment.setPaymentType(paymentType);
		payment.setPaymentId(paymentId);
		return payment;
	}
	
	public AccountPaymentEntity createAccountPaymentToPersist(Money amount, Money balance,  Date trxnDate, Short accountAction, SavingsBO savingsObj,PersonnelBO createdBy, CustomerBO customer)throws Exception{
		AccountPaymentEntity payment =createAccountPayment(balance, new Date(),createdBy);
		payment.addAcountTrxn(createAccountTrxn(null,amount, balance, trxnDate, trxnDate, null, accountAction,savingsObj, createdBy, customer));
		return payment;
	}
	
	public SavingsTrxnDetailEntity createAccountTrxn(Short installmentId, Money amount, Money balance, Date trxnDate, Date dueDate,Integer id, Short accountAction, SavingsBO savingsObj, PersonnelBO createdBy, CustomerBO customer)throws Exception{
		SavingsTrxnDetailEntity trxn = new SavingsTrxnDetailEntity();
		AccountPersistanceService accountDBService = new AccountPersistanceService();
		trxn.setAccountActionEntity(accountDBService.getAccountAction(accountAction));
		trxn.setPersonnel(createdBy);
		trxn.setActionDate(trxnDate);
		trxn.setDueDate(dueDate);
		trxn.setAmount(amount);
		trxn.setBalance(balance);
		trxn.setTrxnCreatedDate(new Timestamp(trxnDate.getTime()));
		trxn.setCreatedBy(createdBy.getPersonnelId());
		trxn.setAccountTrxnId(id);
		trxn.setAccount(savingsObj);
		trxn.setCustomer(customer);
		trxn.setInstallmentId(installmentId);
		if(accountAction.equals(AccountConstants.ACTION_SAVINGS_WITHDRAWAL ))
			trxn.setWithdrawlAmount(amount);
		else 
			trxn.setDepositAmount(amount);
		if(accountAction.equals(AccountConstants.ACTION_SAVINGS_ADJUSTMENT)){
			trxn.setWithdrawlAmount(amount);
			trxn.setDepositAmount(amount);
		}
			
		return trxn;
	}
	
	public CenterBO createCenter(){
		MeetingBO meeting = TestObjectFactory.getMeetingHelper(2,2,4);
		meeting.setMeetingStartDate(Calendar.getInstance());
		meeting.getMeetingDetails().getMeetingRecurrence().setDayNumber(new Short("1"));
		TestObjectFactory.createMeeting(meeting);
		return TestObjectFactory.createCenter("Center_Active_test", Short.valueOf("13"), "1.1", meeting,new Date(System.currentTimeMillis()));
	}
	
	public SavingsOfferingBO createSavingsOffering(){
		return createSavingsOffering("SavingPrd1", Short.valueOf("1"));
	}
	
	public SavingsOfferingBO createSavingsOffering(String offeringName, Short interestCalcType){
		MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(offeringName,Short.valueOf("2"),new Date(System.currentTimeMillis()),
				Short.valueOf("2"),300.0,Short.valueOf("1"),24.0,200.0,200.0,Short.valueOf("2"),interestCalcType,meetingIntCalc,meetingIntPost);
	}
	
	public SavingsBO createSavingsAccount(String globalAccountNum,SavingsOfferingBO savingsOffering, CustomerBO customer, short accountStateId, UserContext userContext){
		SavingsBO savings = TestObjectFactory.createSavingsAccount(globalAccountNum,customer, accountStateId ,new Date(),savingsOffering,userContext);
		savings.setUserContext(userContext);
		return savings;
	}
	
	public SavingsBO createSavingsAccount(SavingsOfferingBO savingsOffering, CustomerBO customer, Short accountState, UserContext userContext)throws Exception{
		SavingsBO savings = new SavingsBO(userContext);
		savings.setSavingsOffering(savingsOffering);
		savings.setCustomer(customer);
		savings.setRecommendedAmount(new Money(TestObjectFactory.getMFICurrency(), "500.0"));
		savings.setAccountState(new AccountStateEntity(accountState));
		savings.save();
		return savings;
	}
	
	public Date getDate(String date)throws ParseException{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		return df.parse(date);
	}
	
	public Date getDate(String date, int hr, int min)throws ParseException{
		Calendar cal = Calendar.getInstance(Configuration.getInstance().getSystemConfig().getMifosTimeZone());
		cal.setTime(getDate(date));
		cal.set(Calendar.HOUR,hr);
		cal.set(Calendar.MINUTE,min);
		return cal.getTime();
	}

	public AccountNotesEntity getAccountNotes(SavingsBO savingsBO){
		AccountNotesEntity notes = new AccountNotesEntity();
		notes.setAccount(savingsBO);
		notes.setComment("xxxxxxxxxxxx");
		return notes;
	}
	public AccountActionDateEntity createAccountActionDate(short installmentId,Date dueDate,Date paymentDate,CustomerBO customer, Money deposit, Money depositPaid, Short paymentStatus){
		AccountActionDateEntity actionDate = new AccountActionDateEntity();
		actionDate.setInstallmentId(installmentId);
		actionDate.setActionDate(new java.sql.Date(dueDate.getTime()));
		if(paymentDate!=null)
			actionDate.setPaymentDate(new java.sql.Date(paymentDate.getTime()));
		actionDate.setCustomer(customer);
		actionDate.setDeposit(deposit);
		actionDate.setDepositPaid(depositPaid);
		actionDate.setPaymentStatus(paymentStatus);
		actionDate.setPrincipal(new Money());
		actionDate.setInterest(new Money());
		actionDate.setPenalty(new Money());
		return actionDate;
	}
	
	public SchedulerIntf getScheduler(MeetingBO meeting, int dayNumber)throws Exception{		
		Short recurrenceId = meeting.getMeetingDetails().getRecurrenceType().getRecurrenceId();
		ScheduleDataIntf scheduleData=SchedulerFactory.getScheduleData(recurrenceId);
		scheduleData.setDayNumber(dayNumber);
		return SchedulerHelper.getScheduler(scheduleData , meeting);
	}

	public MeetingBO getMeeting(String frequency, Short recurAfter, Short meetingTypeId)throws Exception{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		MeetingBO meeting =  TestObjectFactory.getMeeting(frequency,String.valueOf(recurAfter),Short.valueOf(meetingTypeId));
		Calendar cal = Calendar.getInstance(Configuration.getInstance().getSystemConfig().getMifosTimeZone());
		cal.setTime(df.parse("01/01/2006"));
		meeting.setMeetingStartDate(cal);		
		return meeting;
	}
}
