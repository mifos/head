package org.mifos.application.accounts;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.Date;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccount extends MifosTestCase {
	protected LoanBO accountBO=null;
	protected CustomerBO center=null;
	protected CustomerBO group=null;
	protected AccountPersistence accountPersistence;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		accountBO = createLoanAccount();
		accountPersistence = new AccountPersistence();
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			TestObjectFactory.cleanUp(accountBO);
			TestObjectFactory.cleanUp(group);
			TestObjectFactory.cleanUp(center);
			accountPersistence = null;
		}
		catch (Exception e) {
			// throwing here tends to mask failures
			e.printStackTrace();
		}

		super.tearDown();
	}

	public LoanBO createLoanAccount()
	{ 
        MeetingBO meeting = TestObjectFactory.createMeeting(
        	TestObjectFactory.getNewMeetingForToday(
        		WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
        center=TestObjectFactory.createCenter("Center",meeting);
        group=TestObjectFactory.createGroupUnderCenter(
        	"Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
        	"Loan", ApplicableTo.GROUPS,
        	new Date(System.currentTimeMillis()),
        	PrdStatus.LOAN_ACTIVE,
        	300.0,1.2,(short)3,
        	InterestType.FLAT, true, true, meeting);
        return TestObjectFactory.createLoanAccount(
        	"42423142341",group, AccountState.LOANACC_ACTIVEINGOODSTANDING,
        	new Date(System.currentTimeMillis()),loanOffering);
   }

}
