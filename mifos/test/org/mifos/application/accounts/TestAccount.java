package org.mifos.application.accounts;

import java.util.Date;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
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
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1,1,4,2));
        center=TestObjectFactory.createCenter("Center",meeting);
        group=TestObjectFactory.createGroupUnderCenter(
        	"Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("Loan",Short.valueOf("2"),
        		new Date(System.currentTimeMillis()),Short.valueOf("1"),300.0,1.2,Short.valueOf("3"),
        		Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("1"),
        		meeting);
        return TestObjectFactory.createLoanAccount("42423142341",group,Short.valueOf("5"),new Date(System.currentTimeMillis()),loanOffering);
   }

}
