package org.mifos.framework.components.cronjob.helpers;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hibernate.HibernateException;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountCustomFieldEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.exceptions.IDGenerationException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.cronjobs.helpers.RegenerateScheduleHelper;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.helpers.SchedulerHelper;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;


public class TestRegenerateScheduleHelper extends MifosTestCase {

	private MeetingBO meeting;

	private CustomerBO center;

	private CustomerBO client;

	private CustomerBO group;

	private AccountBO accountBO;
	
	private SavingsOfferingBO savingsOffering;
	
	private LoanOfferingBO  loanOfferingBO;
	
	private CustomerBO client1;

	private CustomerBO client2;
	
	private SavingsBO savings;

	private UserContext userContext;
	
	PersonnelBO createdBy = null;
	
	RegenerateScheduleHelper regenerateScheduleHelper;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		regenerateScheduleHelper =new RegenerateScheduleHelper();
		userContext = new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		Set<Short> set = new HashSet<Short>();
		set.add(Short.valueOf("1"));
		userContext.setRoles(set);
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		userContext.setPereferedLocale(new Locale("en", "US"));
		userContext.setBranchId(new Short("1"));
		userContext.setBranchGlobalNum("0001");
		createdBy = new PersonnelPersistence()
				.getPersonnel(userContext.getId());
	}

	public void tearDown() {
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		regenerateScheduleHelper=null;
		HibernateUtil.closeSession();

	}

	public void testExcuteWithCustomerAccounts() throws NumberFormatException, SystemException, ApplicationException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE, center.getSearchId()+".1", center, new Date(System
				.currentTimeMillis()));
		CenterBO center1 = TestObjectFactory.createCenter("Center_Active_test1", Short
				.valueOf("13"), "1.5", meeting, new Date(System
				.currentTimeMillis()));
		GroupBO group1 = TestObjectFactory.createGroup("Group1", GroupConstants.ACTIVE, center1.getSearchId()+".1", center1, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("client1",ClientConstants.STATUS_ACTIVE,group.getSearchId()+".1",group,new Date(System
				.currentTimeMillis()));
		ClientBO client2 = TestObjectFactory.createClient("client2",ClientConstants.STATUS_CLOSED,group.getSearchId()+".2",group,new Date(System
				.currentTimeMillis()));
		ClientBO client3 = TestObjectFactory.createClient("client3",ClientConstants.STATUS_CANCELLED,group1.getSearchId()+".1",group1,new Date(System
				.currentTimeMillis()));
		
		center.getCustomerMeeting().getMeeting().getMeetingDetails().setRecurAfter(Short.valueOf("2"));
		center.getCustomerMeeting().setUpdatedFlag(YesNoFlag.YES.getValue());
		SchedulerIntf scheduler = SchedulerHelper.getScheduler(center.getCustomerMeeting().getMeeting());
		List<java.util.Date> meetingDates = scheduler.getAllDates();
		TestObjectFactory.updateObject(center);
		TestObjectFactory.flushandCloseSession();
		center=(CustomerBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		group=(CustomerBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		center1=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center1.getCustomerId());
		group1=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group1.getCustomerId());
		client=(CustomerBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		client2=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client2.getCustomerId());
		client3=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client3.getCustomerId());
		
		regenerateScheduleHelper.execute(System.currentTimeMillis());
		
		for(AccountActionDateEntity actionDateEntity : center.getCustomerAccount().getAccountActionDates()){
			if(actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
				assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
			else if(actionDateEntity.getInstallmentId().equals(Short.valueOf("3")))
				assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(2).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
		}
		
		for(AccountActionDateEntity actionDateEntity : group.getCustomerAccount().getAccountActionDates()){
			if(actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
				assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
			else if(actionDateEntity.getInstallmentId().equals(Short.valueOf("3")))
				assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(2).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
		}
		assertEquals(YesNoFlag.NO.getValue(),center.getCustomerMeeting().getUpdatedFlag());

		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group1);
		TestObjectFactory.cleanUp(center1);
		
	}
	
	
	public void testExecuteWithLoanAccount() throws SchedulerException, HibernateException, ServiceException{
		accountBO = getLoanAccount();
		TestObjectFactory.flushandCloseSession();
		center=(CustomerBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		group=(CustomerBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(LoanBO.class, accountBO.getAccountId());
	
		center.getCustomerMeeting().getMeeting().getMeetingDetails().setRecurAfter(Short.valueOf("2"));
		center.getCustomerMeeting().setUpdatedFlag(YesNoFlag.YES.getValue());
		
		Calendar meetingStartDate =center.getCustomerMeeting().getMeeting().getMeetingStartDate();
		AccountActionDateEntity accountActionDateEntity = center.getCustomerAccount().getDetailsOfNextInstallment();
		center.getCustomerMeeting().getMeeting().setMeetingStartDate(DateUtils.getCalendarDate(accountActionDateEntity.getActionDate().getTime()));
		
		SchedulerIntf scheduler = SchedulerHelper.getScheduler(center.getCustomerMeeting().getMeeting());
		center.getCustomerMeeting().getMeeting().setMeetingStartDate(meetingStartDate);
		
		TestObjectFactory.updateObject(center);
		TestObjectFactory.flushandCloseSession();
		regenerateScheduleHelper.execute(System.currentTimeMillis());
		
		TestObjectFactory.flushandCloseSession();
		center=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,center.getCustomerId());
		group=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());
		List<java.util.Date> meetingDates=null;
		for(AccountBO account :  center.getAccounts()){
			meetingDates = scheduler.getAllDates();
			meetingDates.remove(0);
			for(AccountActionDateEntity actionDateEntity :  account.getAccountActionDates()){
				if(actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
				else if(actionDateEntity.getInstallmentId().equals(Short.valueOf("3")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
			}
		}
		for(AccountBO account :  group.getAccounts()){
			if(account instanceof LoanBO){
				meetingDates = scheduler.getAllDates(accountBO.getApplicableIdsForFutureInstallments().size()+1);
				meetingDates.remove(0);
			}else{
				meetingDates = scheduler.getAllDates();
				meetingDates.remove(0);
			}
			for(AccountActionDateEntity actionDateEntity :  account.getAccountActionDates()){
				if(actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
				else if(actionDateEntity.getInstallmentId().equals(Short.valueOf("3")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
			}
		}
		assertEquals(YesNoFlag.NO.getValue(),center.getCustomerMeeting().getUpdatedFlag());
		
		TestObjectFactory.flushandCloseSession();
		center=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,center.getCustomerId());
		group=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());
	}
	
	public void testExecuteWithSavingsAccount() throws HibernateException, IDGenerationException, SystemException, ApplicationException{
		savings= getSavingAccount();
		TestObjectFactory.flushandCloseSession();
		savings=(SavingsBO)TestObjectFactory.getObject(SavingsBO.class,savings.getAccountId());
		center=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,center.getCustomerId());
		group=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		client1=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,client1.getCustomerId());
		client2=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,client2.getCustomerId());
		
		center.getCustomerMeeting().getMeeting().getMeetingDetails().setRecurAfter(Short.valueOf("1"));
		center.getCustomerMeeting().setUpdatedFlag(YesNoFlag.YES.getValue());
		
		Calendar meetingStartDate =center.getCustomerMeeting().getMeeting().getMeetingStartDate();
		AccountActionDateEntity accountActionDateEntity = center.getCustomerAccount().getDetailsOfNextInstallment();
		center.getCustomerMeeting().getMeeting().setMeetingStartDate(DateUtils.getCalendarDate(accountActionDateEntity.getActionDate().getTime()));
		
		SchedulerIntf scheduler = SchedulerHelper.getScheduler(center.getCustomerMeeting().getMeeting());
		center.getCustomerMeeting().getMeeting().setMeetingStartDate(meetingStartDate);
		List<java.util.Date> meetingDates = scheduler.getAllDates();
		meetingDates.remove(0);
		
		TestObjectFactory.updateObject(center);
		TestObjectFactory.flushandCloseSession();
		regenerateScheduleHelper.execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		
		savings=(SavingsBO)TestObjectFactory.getObject(SavingsBO.class,savings.getAccountId());
		center=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,center.getCustomerId());
		group=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		client1=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,client1.getCustomerId());
		client2=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,client2.getCustomerId());
		
		for(AccountBO account :  center.getAccounts()){
			for(AccountActionDateEntity actionDateEntity :  account.getAccountActionDates()){
				if(actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
				else if(actionDateEntity.getInstallmentId().equals(Short.valueOf("3")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
			}
		}
		for(AccountBO account :  group.getAccounts()){
			for(AccountActionDateEntity actionDateEntity :  account.getAccountActionDates()){
				if(actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
				else if(actionDateEntity.getInstallmentId().equals(Short.valueOf("3")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
			}
		}
		for(AccountBO account :  client1.getAccounts()){
			for(AccountActionDateEntity actionDateEntity :  account.getAccountActionDates()){
				if(actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
				else if(actionDateEntity.getInstallmentId().equals(Short.valueOf("3")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
			}
		}
		
		for(AccountBO account :  client2.getAccounts()){
			for(AccountActionDateEntity actionDateEntity :  account.getAccountActionDates()){
				if(actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
				else if(actionDateEntity.getInstallmentId().equals(Short.valueOf("3")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()),DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
			}
		}

		assertEquals(YesNoFlag.NO.getValue(),center.getCustomerMeeting().getUpdatedFlag());
		
		TestObjectFactory.flushandCloseSession();
		
		savings=(SavingsBO)TestObjectFactory.getObject(SavingsBO.class,savings.getAccountId());
		center=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,center.getCustomerId());
		group=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		client1=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,client1.getCustomerId());
		client2=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,client2.getCustomerId());
	}
	
	
	private SavingsBO getSavingAccount() throws IDGenerationException, SchedulerException, SystemException, ApplicationException{
		MeetingBO meeting = TestObjectFactory.getMeetingHelper(2,2,4);
		meeting.setMeetingStartDate(Calendar.getInstance());
		meeting.getMeetingDetails().getMeetingRecurrence().setDayNumber(new Short("1"));
		TestObjectFactory.createMeeting(meeting);
		center = TestObjectFactory.createCenter("Center_Active_test", Short.valueOf("13"), "1.1", meeting,new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group1", Short.valueOf("9"),
				center.getSearchId()+".1", center, new Date(System.currentTimeMillis()));
		client1 = TestObjectFactory.createClient("client1",
				ClientConstants.STATUS_ACTIVE, group.getSearchId()+".1", group, new Date(
						System.currentTimeMillis()));
		client2 = TestObjectFactory.createClient("client2",
				ClientConstants.STATUS_ACTIVE, group.getSearchId()+".2", group, new Date(
						System.currentTimeMillis()));
		MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		savingsOffering =  TestObjectFactory.createSavingsOffering("SavingPrd1",Short.valueOf("2"),new Date(System.currentTimeMillis()),
				Short.valueOf("1"),300.0,Short.valueOf("1"),24.0,200.0,200.0,Short.valueOf("2"),Short.valueOf("1"),meetingIntCalc,meetingIntPost);
		SavingsBO savings = new SavingsBO(userContext);
		savings.setSavingsOffering(savingsOffering);
		savings.setCustomer(group);
		savings.setAccountState(new AccountStateEntity(
				AccountStates.SAVINGS_ACC_APPROVED));
		savings.setRecommendedAmount(savingsOffering.getRecommendedAmount());

		Set<AccountCustomFieldEntity> customFields = new HashSet<AccountCustomFieldEntity>();
		AccountCustomFieldEntity field = new AccountCustomFieldEntity();
		field.setFieldId(new Short("1"));
		field.setFieldValue("13");
		customFields.add(field);
		savings.setAccountCustomFieldSet(customFields);

		savings.save();
		HibernateUtil.getTransaction().commit();

		return savings;
	}
	
	
	private AccountBO getLoanAccount() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				center.getSearchId()+".1", center, new Date(System.currentTimeMillis()));
		loanOfferingBO = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOfferingBO);
	}
	


	
}
