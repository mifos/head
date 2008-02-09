package org.mifos.application.accounts.savings.struts.tag;

import java.sql.Date;
import java.util.Locale;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsOverDueDepositsTag extends MifosTestCase {
	
	CenterBO center;
	GroupBO group;
	SavingsOfferingBO savingsOffering;
	SavingsBO savings;
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
	}	
	public void testBuildDateUI(){
		Date date = new Date(System.currentTimeMillis());
		assertTrue(new SavingsOverDueDepositsTag().buildDateUI(new Locale("en","GB"),date).toString().contains(DateUtils.getUserLocaleDate(new Locale("en","GB"), date
		.toString())));
		
	}
	public void testBuildAmountUI(){
		assertTrue(new SavingsOverDueDepositsTag().buildAmountUI(new Money("1000")).toString().contains("1000"));
		
	}
	
	public void testBuildDepositDueUIRow(){
		Date date = new Date(System.currentTimeMillis());
		
		String outString =new SavingsOverDueDepositsTag().buildDepositDueUIRow(new Locale("en","GB"),date,new Money("1000")).toString();
		assertTrue(outString.contains(DateUtils.getUserLocaleDate(new Locale("en","GB"), date
		.toString())));
		
		assertTrue(outString.contains("1000"));
	}
	
	public void testbuildUI()throws Exception{
		createInitialObjects() ;
		assertNotNull( new SavingsOverDueDepositsTag().buildUI(savings.getDetailsOfInstallmentsInArrears(),new Locale("en","GB")));
	}

	private void createInitialObjects() throws Exception{
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
		
		SavingsTestHelper helper = new SavingsTestHelper();
		savingsOffering = helper.createSavingsOffering("2333dsf", "2132");
		savings = helper.createSavingsAccount(savingsOffering, group,
				AccountState.SAVINGS_ACTIVE, 
				TestUtils.makeUser());

	}
}
