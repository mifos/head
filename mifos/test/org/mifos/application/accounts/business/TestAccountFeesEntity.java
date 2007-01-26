package org.mifos.application.accounts.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import static org.mifos.framework.util.helpers.TestObjectFactory.*; 
import static org.mifos.application.meeting.util.helpers.MeetingType.*;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.*;


public class TestAccountFeesEntity extends MifosTestCase {
	protected AccountBO accountBO=null;
	protected CustomerBO center=null;
	protected CustomerBO group=null;
	protected AccountPersistence accountPersistence;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		accountPersistence = new AccountPersistence();
	}


	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		accountPersistence = null;
		HibernateUtil.closeSession();
	}
	
	public static void addAccountFees(AccountFeesEntity fees,AccountBO account) {
		account.addAccountFees(fees);
	}

	

	public AccountBO getLoanAccount()
	{ 
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
        		.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
        center=TestObjectFactory.createCenter("Center",meeting);
        group=TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("Loan",Short.valueOf("2"),
        		new Date(System.currentTimeMillis()),Short.valueOf("1"),300.0,1.2,Short.valueOf("3"),
        		Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("1"),
        		meeting);
        return TestObjectFactory.createLoanAccount("42423142341",group,Short.valueOf("5"),new Date(System.currentTimeMillis()),loanOffering);
   }

	
	
	public void testChangeFeesStatus(){
		accountBO=getLoanAccount();
		Set<AccountFeesEntity> accountFeesEntitySet=accountBO.getAccountFees();
		for(AccountFeesEntity accountFeesEntity: accountFeesEntitySet){
			accountFeesEntity.changeFeesStatus(AccountConstants.INACTIVE_FEES,new Date(System.currentTimeMillis()));
			assertEquals(accountFeesEntity.getFeeStatus(),AccountConstants.INACTIVE_FEES);
		}
	}
	
	
	public void testGetApplicableDatesCount() throws Exception{
		accountPersistence = new AccountPersistence();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
        center=TestObjectFactory.createCenter("Center",meeting);
        FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.LOAN, "100", RecurrenceType.WEEKLY, Short.valueOf("2"));
        AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center.getCustomerAccount(),trainingFee,new Double("100.0"));
		center.getCustomerAccount().getAccountFees().add(accountPeriodicFee);
        Date currentDate=DateUtils.getCurrentDateWithoutTimeStamp();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        calendar.add(calendar.WEEK_OF_MONTH,-1);
        Date lastAppliedFeeDate = new Date(calendar.getTimeInMillis());
        for (AccountFeesEntity accountFeesEntity : center.getCustomerAccount().getAccountFees()) {
			accountFeesEntity.setLastAppliedDate(lastAppliedFeeDate);
		}
        TestObjectFactory.updateObject(center);
        TestObjectFactory.flushandCloseSession();
        group=TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);       		
		Set<AccountFeesEntity> accountFeeSet = group.getCustomerAccount().getAccountFees();
		assertEquals(1,accountFeeSet.size());
		for (AccountFeesEntity periodicFees : center.getCustomerAccount().getAccountFees()) {
			if(periodicFees.getFees().getFeeName().equalsIgnoreCase("Training_Fee"))
				assertEquals(Integer.valueOf(0),periodicFees.getApplicableDatesCount(currentDate));
			else
				assertEquals(Integer.valueOf(1),periodicFees.getApplicableDatesCount(currentDate));
		}
	}

}
