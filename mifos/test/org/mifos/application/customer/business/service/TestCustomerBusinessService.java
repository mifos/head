/**
 * 
 */
package org.mifos.application.customer.business.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.CustomerActivityEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerRecentActivityView;
import org.mifos.application.customer.util.helpers.LoanCycleCounter;
import org.mifos.application.master.util.valueobjects.YesNoMaster;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 * This class hosts the test cases for CustomerBusinessService.
 * @author ashishsm
 *
 */
public class TestCustomerBusinessService extends TestCase {
	private CenterBO center =null;
	private GroupBO group = null;
	private LoanBO loan = null;
	private LoanOfferingBO loanOffering = null;
	private CustomerBusinessService service;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		service = (CustomerBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.Customer);
	}
	
	public void tearDown(){
		TestObjectFactory.cleanUp(loan);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(group);
	}
	
	
	protected LoanBO getLoanAccount(){ 
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1,1,4,2));
        center=TestObjectFactory.createCenter("Center",Short.valueOf("13"),"1.1",meeting,new Date(System.currentTimeMillis()));
        group=TestObjectFactory.createGroup("Group",Short.valueOf("9"),"1.1.1",center,new Date(System.currentTimeMillis()));
        loanOffering = TestObjectFactory.createLoanOffering("Loan",Short.valueOf("2"),
        		new Date(System.currentTimeMillis()),Short.valueOf("1"),300.0,1.2,Short.valueOf("3"),
        		Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("1"),
        		meeting);
        return TestObjectFactory.createLoanAccount("42423142341",group,Short.valueOf("5"),new Date(System.currentTimeMillis()),loanOffering);
	}
	
	public void testFetchLoanCycleCounter()throws Exception{
		loan = getLoanAccount();
		YesNoMaster yesNo = new YesNoMaster();
		yesNo.setYesNoMasterID(Short.valueOf("1"));
		loanOffering.setLoanCounter(yesNo);
		TestObjectFactory.updateObject(loanOffering);
		TestObjectFactory.flushandCloseSession();
		List<LoanCycleCounter> loanCycleCounters = ((CustomerBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer)).fetchLoanCycleCounter(group.getCustomerId());
		for(LoanCycleCounter loanCycleCounter : loanCycleCounters){
			assertEquals(loanCycleCounter.getOfferingName(), "Loan");
			assertEquals(loanCycleCounter.getCounter(), 1);
			break;
		}
	}
	
	public void testGetAllActivityView() throws Exception{
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1,1,4,2));
		center=TestObjectFactory.createCenter("Center",Short.valueOf("13"),"1.1",meeting,new Date(System.currentTimeMillis()));
		List<CustomerRecentActivityView> customerActivityViewList=service.getAllActivityView(center.getGlobalCustNum());
		assertEquals(0,customerActivityViewList.size());
		UserContext uc=TestObjectFactory.getUserContext();
		center.getCustomerAccount().setUserContext(uc);
		center.getCustomerAccount().waiveAmountDue();
		TestObjectFactory.flushandCloseSession();
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		Set<CustomerActivityEntity> customerActivityDetails=center.getCustomerAccount().getCustomerActivitDetails();
		assertEquals(1,customerActivityDetails.size());
		for(CustomerActivityEntity customerActivityEntity : customerActivityDetails){
			assertEquals(new Money("100"),customerActivityEntity.getAmount());
		}
	}
	
	
	public void testGetRecentActivityView() throws Exception{
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1,1,4,2));
		center=TestObjectFactory.createCenter("Center",Short.valueOf("13"),"1.1",meeting,new Date(System.currentTimeMillis()));
		List<CustomerRecentActivityView> customerActivityViewList=service.getAllActivityView(center.getGlobalCustNum());
		assertEquals(0,customerActivityViewList.size());
		UserContext uc=TestObjectFactory.getUserContext();
		center.getCustomerAccount().setUserContext(uc);
		center.getCustomerAccount().waiveAmountDue();
		TestObjectFactory.flushandCloseSession();
		
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		for(AccountActionDateEntity accountActionDateEntity : center.getCustomerAccount().getAccountActionDates()){
			if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))){
				Set<AccountFeesActionDetailEntity> accountFeesActionDetails=accountActionDateEntity.getAccountFeesActionDetails();
				for(AccountFeesActionDetailEntity accountFeesActionDetailEntity :  accountFeesActionDetails){
					accountFeesActionDetailEntity.setFeeAmount(new Money("100"));
				}
			}
		}
		TestObjectFactory.updateObject(center);
		center.getCustomerAccount().setUserContext(uc);
		center.getCustomerAccount().waiveAmountDue();
		TestObjectFactory.flushandCloseSession();

		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		for(AccountActionDateEntity accountActionDateEntity : center.getCustomerAccount().getAccountActionDates()){
			if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))){
				Set<AccountFeesActionDetailEntity> accountFeesActionDetails=accountActionDateEntity.getAccountFeesActionDetails();
				for(AccountFeesActionDetailEntity accountFeesActionDetailEntity :  accountFeesActionDetails){
					accountFeesActionDetailEntity.setFeeAmount(new Money("100"));
				}
			}
		}
		TestObjectFactory.updateObject(center);
		center.getCustomerAccount().setUserContext(uc);
		center.getCustomerAccount().waiveAmountDue();
		TestObjectFactory.flushandCloseSession();
		
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		for(AccountActionDateEntity accountActionDateEntity : center.getCustomerAccount().getAccountActionDates()){
			if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("3"))){
				Set<AccountFeesActionDetailEntity> accountFeesActionDetails=accountActionDateEntity.getAccountFeesActionDetails();
				for(AccountFeesActionDetailEntity accountFeesActionDetailEntity :  accountFeesActionDetails){
					accountFeesActionDetailEntity.setFeeAmount(new Money("20"));
				}
			}
		}
		TestObjectFactory.updateObject(center);
		center.getCustomerAccount().setUserContext(uc);
		center.getCustomerAccount().waiveAmountDue();
		TestObjectFactory.flushandCloseSession();
		
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		Set<CustomerActivityEntity> customerActivityDetails=center.getCustomerAccount().getCustomerActivitDetails();
		assertEquals(3,customerActivityDetails.size());
		for(CustomerActivityEntity customerActivityEntity : customerActivityDetails){
			assertEquals(new Money("100"),customerActivityEntity.getAmount());
		}
	}
	
	
	public void testFindBySystemId() throws PersistenceException, ServiceException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short.valueOf("13"), "1.1", meeting,new Date(System.currentTimeMillis()));
		group=TestObjectFactory.createGroup("Group_Active_test",Short.valueOf("9"),"1.1.1",center,new Date(System.currentTimeMillis()));
		GroupBO groupBO = (GroupBO)service.findBySystemId("Group_Active_test");
		assertEquals(groupBO.getGlobalCustNum(),group.getGlobalCustNum());
	}

	public void testgetBySystemId() throws PersistenceException, ServiceException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short.valueOf("13"), "1.1", meeting,new Date(System.currentTimeMillis()));
		group=TestObjectFactory.createGroup("Group_Active_test",Short.valueOf("9"),"1.1.1",center,new Date(System.currentTimeMillis()));
		GroupBO groupBO = (GroupBO)service.getBySystemId("Group_Active_test",group.getCustomerLevel().getLevelId());
		assertEquals(groupBO.getGlobalCustNum(),group.getGlobalCustNum());
	}

}
