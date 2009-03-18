package org.mifos.application.accounts.business;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;


public class TestAccountFeesEntity extends MifosIntegrationTest {
	public TestAccountFeesEntity() throws SystemException, ApplicationException {
        super();
    }


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
		try {
			TestObjectFactory.cleanUp(accountBO);
			TestObjectFactory.cleanUp(group);
			TestObjectFactory.cleanUp(center);
			accountPersistence = null;
		} catch (Exception e) {
			// TODO Whoops, cleanup didnt work, reset db
			TestDatabase.resetMySQLDatabase();
		}
		StaticHibernateUtil.closeSession();
		super.tearDown();
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
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
        	"Loan", ApplicableTo.GROUPS,
        	new Date(System.currentTimeMillis()),
        	PrdStatus.LOAN_ACTIVE,300.0,1.2, 3,
        	InterestType.FLAT, meeting);
        return TestObjectFactory.createLoanAccount("42423142341",group,
        	AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
        	new Date(System.currentTimeMillis()),loanOffering);
   }

	
	
	public void testChangeFeesStatus(){
		accountBO=getLoanAccount();
		Set<AccountFeesEntity> accountFeesEntitySet=accountBO.getAccountFees();
		for(AccountFeesEntity accountFeesEntity: accountFeesEntitySet){
			accountFeesEntity.changeFeesStatus(FeeStatus.INACTIVE,
				new Date(System.currentTimeMillis()));
			assertEquals(accountFeesEntity.getFeeStatus(),FeeStatus.INACTIVE.getValue());
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
