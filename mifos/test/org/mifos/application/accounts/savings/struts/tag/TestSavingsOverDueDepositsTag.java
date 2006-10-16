package org.mifos.application.accounts.savings.struts.tag;

import java.sql.Date;
import java.util.Locale;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.struts.tags.DateHelper;
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
		assertTrue(new SavingsOverDueDepositsTag().buildDateUI(new Locale("en","US"),date).toString().contains(DateHelper.getUserLocaleDate(new Locale("en","US"), date
				.toString())));
		
	}
	public void testBuildAmountUI(){
		assertTrue(new SavingsOverDueDepositsTag().buildAmountUI(new Money("1000")).toString().contains("1000"));
		
	}
	
	public void testBuildDepositDueUIRow(){
		Date date = new Date(System.currentTimeMillis());
		
		String outString =new SavingsOverDueDepositsTag().buildDepositDueUIRow(new Locale("en","US"),date,new Money("1000")).toString();
		assertTrue(outString.contains(DateHelper.getUserLocaleDate(new Locale("en","US"), date
				.toString())));
		
		assertTrue(outString.contains("1000"));
	}
	
	public void testbuildUI()throws Exception{
		createInitialObjects() ;
		assertNotNull( new SavingsOverDueDepositsTag().buildUI(savings.getDetailsOfInstallmentsInArrears(),new Locale("en","US")));
	}
	private void createInitialObjects() throws Exception{
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("9"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
		
		SavingsTestHelper helper = new SavingsTestHelper();
		savingsOffering = helper.createSavingsOffering("2333dsf", "2132");
		savings = helper.createSavingsAccount(savingsOffering, group,
				AccountStates.SAVINGS_ACC_APPROVED, TestObjectFactory.getUserContext());

	}
}
