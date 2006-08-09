/**
 * 
 */
package org.mifos.application.accounts.business.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.struts.taglib.tiles.GetAttributeTag;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.business.TransactionHistoryView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanFeeScheduleEntity;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.ApplicableCharge;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerFeeScheduleEntity;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountService extends MifosTestCase {
	
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
	}

	public void testSuccessRemoveFees() {
		AccountBusinessService accountBusinessService = new AccountBusinessService();
		try {
			accountBO=getLoanAccount();
			Set<AccountFeesEntity> accountFeesEntitySet = accountBO
					.getAccountFees();
			UserContext uc = TestObjectFactory.getUserContext();
			Iterator itr = accountFeesEntitySet.iterator();
			while (itr.hasNext()) {
				AccountFeesEntity accountFeesEntity = (AccountFeesEntity) itr
						.next();
				accountBusinessService.removeFees(accountBO.getAccountId(),
						accountFeesEntity.getFees().getFeeId(), uc.getId());
				assertTrue(true);
			}
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	public void testFailureRemoveFees() {
		AccountBusinessService accountBusinessService = new AccountBusinessService();
		try {
			accountBO=getLoanAccount();
			UserContext uc = TestObjectFactory.getUserContext();
			Set<AccountFeesEntity> accountFeesEntitySet = accountBO
					.getAccountFees();
			Iterator itr = accountFeesEntitySet.iterator();
			while (itr.hasNext()) {
				AccountFeesEntity accountFeesEntity = (AccountFeesEntity) itr
						.next();
				accountBusinessService.removeFees(Integer.valueOf("-1"),
						accountFeesEntity.getFees().getFeeId(), uc.getId());
				assertTrue(false);
			}
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	public void testGetTrxnHistory() throws Exception {
		AccountBusinessService accountBusinessService = new AccountBusinessService();
		Date currentDate = new Date(System.currentTimeMillis());
		accountBO=getLoanAccount();
		LoanBO loan = (LoanBO) accountBO;

		UserContext uc = TestObjectFactory.getUserContext();
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		PaymentData accountPaymentDataView = TestObjectFactory
				.getLoanAccountPaymentData(accntActionDates, TestObjectFactory
						.getMoneyForMFICurrency(0), null, loan.getPersonnel(),
						"receiptNum", Short.valueOf("1"), currentDate,
						currentDate);
		
		loan.applyPayment(accountPaymentDataView);
		TestObjectFactory.flushandCloseSession();
		loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan
				.getAccountId());
		loan.setUserContext(uc);

		List<TransactionHistoryView> trxnHistlist = accountBusinessService
				.getTrxnHistory(loan, uc);
		assertNotNull("Account TrxnHistoryView list object should not be null",
				trxnHistlist);
		assertTrue(
				"Account TrxnHistoryView list object Size should be greater than zero",
				trxnHistlist.size() > 0);
		TestObjectFactory.flushandCloseSession();
		accountBO = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan
				.getAccountId());

	}

	public void testGetAccountAction() throws Exception {
		AccountBusinessService service = (AccountBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Accounts);
		AccountActionEntity accountaction = service.getAccountAction(
				AccountConstants.ACTION_SAVINGS_DEPOSIT, Short.valueOf("1"));
		assertNotNull(accountaction);
		assertEquals(Short.valueOf("1"), accountaction.getLocaleId());
	}
	
	public void testGetAppllicableFees() throws Exception {
		AccountBusinessService accountBusinessService = new AccountBusinessService();
		accountBO=getLoanAccount();
		TestObjectFactory.flushandCloseSession();
		center=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class, center.getCustomerId());
		group=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class, group.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class, accountBO
				.getAccountId());
		UserContext uc = TestObjectFactory.getUserContext();
		List<ApplicableCharge> applicableChargeList=accountBusinessService.getAppllicableFees(accountBO.getAccountId(),uc);
		assertNull(applicableChargeList);
	}
	
	public void testGetAppllicableFeesForInstallmentStartingOnCurrentDate() throws Exception {
		AccountBusinessService accountBusinessService = new AccountBusinessService();
		accountBO=getLoanAccountWithAllTypesOfFees();
		TestObjectFactory.flushandCloseSession();
		center=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class, center.getCustomerId());
		group=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class, group.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class, accountBO
				.getAccountId());
		UserContext uc = TestObjectFactory.getUserContext();
		List<ApplicableCharge> applicableChargeList=accountBusinessService.getAppllicableFees(accountBO.getAccountId(),uc);
		assertEquals(4,applicableChargeList.size());
		for(ApplicableCharge applicableCharge :  applicableChargeList){
			if(applicableCharge.getFeeName().equalsIgnoreCase("Upfront Fee")){
				assertEquals(new Money("20.0").toString(),applicableCharge.getAmountOrRate());
				assertNotNull(applicableCharge.getFormula());
				assertNull(applicableCharge.getPeriodicity());
			}else if(applicableCharge.getFeeName().equalsIgnoreCase("Periodic Fee")){
				assertEquals(new Money("200.0").toString(),applicableCharge.getAmountOrRate());
				assertNull(applicableCharge.getFormula());
				assertNotNull(applicableCharge.getPeriodicity());
			}else if(applicableCharge.getFeeName().equalsIgnoreCase("Misc Fee")){
				assertNull(applicableCharge.getAmountOrRate());
				assertNull(applicableCharge.getFormula());
				assertNull(applicableCharge.getPeriodicity());
			}
		}
	}
	
	
	public void testGetAppllicableFeesForInstallmentStartingAfterCurrentDate() throws Exception {
		AccountBusinessService accountBusinessService = new AccountBusinessService();
		accountBO=getLoanAccountWithAllTypesOfFees();
		incrementInstallmentDate(accountBO,1,Short.valueOf("1"));
		accountBO.setAccountState(new AccountStateEntity(AccountState.LOANACC_DBTOLOANOFFICER.getValue()));
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();
		center=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class, center.getCustomerId());
		group=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class, group.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class, accountBO
				.getAccountId());
		UserContext uc = TestObjectFactory.getUserContext();
		List<ApplicableCharge> applicableChargeList=accountBusinessService.getAppllicableFees(accountBO.getAccountId(),uc);
		assertEquals(6,applicableChargeList.size());
		for(ApplicableCharge applicableCharge :  applicableChargeList){
			if(applicableCharge.getFeeName().equalsIgnoreCase("Upfront Fee")){
				assertEquals(new Money("20.0").toString(),applicableCharge.getAmountOrRate());
				assertNotNull(applicableCharge.getFormula());
				assertNull(applicableCharge.getPeriodicity());
			}else if(applicableCharge.getFeeName().equalsIgnoreCase("Periodic Fee")){
				assertEquals(new Money("200.0").toString(),applicableCharge.getAmountOrRate());
				assertNull(applicableCharge.getFormula());
				assertNotNull(applicableCharge.getPeriodicity());
			}else if(applicableCharge.getFeeName().equalsIgnoreCase("Misc Fee")){
				assertNull(applicableCharge.getAmountOrRate());
				assertNull(applicableCharge.getFormula());
				assertNull(applicableCharge.getPeriodicity());
			}
		}
	}
	
	
	public void testGetAppllicableFeesForMeetingStartingOnCurrentDate() throws Exception {
		AccountBusinessService accountBusinessService = new AccountBusinessService();
		CustomerAccountBO customerAccountBO=getCustomerAccountWithAllTypesOfFees();
		TestObjectFactory.flushandCloseSession();
		center=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class, center.getCustomerId());
		UserContext uc = TestObjectFactory.getUserContext();
		List<ApplicableCharge> applicableChargeList=accountBusinessService.getAppllicableFees(customerAccountBO.getAccountId(),uc);
		assertEquals(4,applicableChargeList.size());
		for(ApplicableCharge applicableCharge :  applicableChargeList){
			if(applicableCharge.getFeeName().equalsIgnoreCase("Upfront Fee")){
				assertEquals(new Money("20.0").toString(),applicableCharge.getAmountOrRate());
				assertNotNull(applicableCharge.getFormula());
				assertNull(applicableCharge.getPeriodicity());
			}else if(applicableCharge.getFeeName().equalsIgnoreCase("Misc Fee")){
				assertNull(applicableCharge.getAmountOrRate());
				assertNull(applicableCharge.getFormula());
				assertNull(applicableCharge.getPeriodicity());
			}else if(applicableCharge.getFeeName().equalsIgnoreCase("Periodic Fee")){
				assertEquals(new Money("200.0").toString(),applicableCharge.getAmountOrRate());
				assertNull(applicableCharge.getFormula());
				assertNotNull(applicableCharge.getPeriodicity());
			}else if(applicableCharge.getFeeName().equalsIgnoreCase("Mainatnence Fee")){
				assertFalse(true);
			}
		}
	}
	
	private AccountBO getLoanAccount()
	{ 
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1,1,4,2));
        center=TestObjectFactory.createCenter("Center",Short.valueOf("13"),"1.1",meeting,new Date(System.currentTimeMillis()));
        group=TestObjectFactory.createGroup("Group",Short.valueOf("9"),"1.1.1",center,new Date(System.currentTimeMillis()));
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("Loan",Short.valueOf("2"),
        		new Date(System.currentTimeMillis()),Short.valueOf("1"),300.0,1.2,Short.valueOf("3"),
        		Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("1"),
        		meeting);
        return TestObjectFactory.createLoanAccount("42423142341",group,Short.valueOf("5"),new Date(System.currentTimeMillis()),loanOffering);
   }
	
	private AccountBO getLoanAccountWithAllTypesOfFees(){
        accountBO = getLoanAccount();
          
        LoanScheduleEntity loanScheduleEntity=(LoanScheduleEntity)accountBO.getAccountActionDate(Short.valueOf("1"));
        
  		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee("Upfront Fee",
					FeeCategory.LOAN, Double.valueOf("20"),FeeFormula.AMOUNT, FeePayment.UPFRONT);
		AccountFeesEntity accountUpfrontFee = new AccountFeesEntity(accountBO,upfrontFee,new Money("20.0"),
				FeeStatus.ACTIVE.getValue(), null,loanScheduleEntity.getActionDate());
		accountBO.addAccountFees(accountUpfrontFee);
		AccountFeesActionDetailEntity accountUpfrontFeesaction = new LoanFeeScheduleEntity(
				loanScheduleEntity, upfrontFee, accountUpfrontFee,
				new Money("20.0"));
		loanScheduleEntity.addAccountFeesAction(accountUpfrontFeesaction);
		TestObjectFactory.updateObject(accountBO);
		
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		loanScheduleEntity=(LoanScheduleEntity)accountBO.getAccountActionDate(Short.valueOf("1"));
		FeeBO timeOfDisbursmentFees = TestObjectFactory.createOneTimeAmountFee("Disbursment Fee",
				FeeCategory.LOAN, "30", FeePayment.TIME_OF_DISBURSMENT);
		AccountFeesEntity accountDisbursmentFee = new AccountFeesEntity(accountBO,timeOfDisbursmentFees,new Money("30.0"),
				FeeStatus.ACTIVE.getValue(), null,loanScheduleEntity.getActionDate());
		accountBO.addAccountFees(accountDisbursmentFee);
		AccountFeesActionDetailEntity accountDisbursmentFeesaction = new LoanFeeScheduleEntity(
				loanScheduleEntity, timeOfDisbursmentFees, accountDisbursmentFee,
				new Money("30.0"));
		loanScheduleEntity.addAccountFeesAction(accountDisbursmentFeesaction);
		TestObjectFactory.updateObject(accountBO);
		
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		loanScheduleEntity=(LoanScheduleEntity)accountBO.getAccountActionDate(Short.valueOf("1"));
		FeeBO firstLoanRepaymentFee  = TestObjectFactory.createOneTimeAmountFee("First Loan Repayment Fee",
				FeeCategory.LOAN, "40", FeePayment.TIME_OF_FIRSTLOANREPAYMENT);
		AccountFeesEntity accountFirstLoanRepaymentFee = new AccountFeesEntity(accountBO,firstLoanRepaymentFee,new Money("40.0"),
				FeeStatus.ACTIVE.getValue(), null,loanScheduleEntity.getActionDate());
		accountBO.addAccountFees(accountFirstLoanRepaymentFee);
		AccountFeesActionDetailEntity accountTimeOfFirstLoanRepaymentFeesaction = new LoanFeeScheduleEntity(
				loanScheduleEntity, firstLoanRepaymentFee, accountFirstLoanRepaymentFee,
				new Money("40.0"));
		loanScheduleEntity.addAccountFeesAction(accountTimeOfFirstLoanRepaymentFeesaction);
		TestObjectFactory.updateObject(accountBO);
		
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee",
				FeeCategory.LOAN, "200", MeetingFrequency.WEEKLY, Short
						.valueOf("1"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(accountBO,periodicFee,new Money("200.0"),
				FeeStatus.INACTIVE.getValue(), null,null);
		accountBO.addAccountFees(accountPeriodicFee);
		TestObjectFactory.updateObject(accountBO);
		
		return accountBO;
	}
	
	
	private CustomerAccountBO getCustomerAccountWithAllTypesOfFees(){
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1,1,4,2));
		center=TestObjectFactory.createCenter("Center",Short.valueOf("13"),"1.1",meeting,new Date(System.currentTimeMillis()));
        CustomerAccountBO customerAccountBO =center.getCustomerAccount();
		
        CustomerScheduleEntity customerScheduleEntity=(CustomerScheduleEntity)customerAccountBO.getAccountActionDate(Short.valueOf("1"));
        
  		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee("Upfront Fee",
					FeeCategory.CENTER, Double.valueOf("20"),FeeFormula.AMOUNT, FeePayment.UPFRONT);
		AccountFeesEntity accountUpfrontFee = new AccountFeesEntity(customerAccountBO,upfrontFee,new Money("20.0"),
				FeeStatus.ACTIVE.getValue(), null,customerScheduleEntity.getActionDate());
		customerAccountBO.addAccountFees(accountUpfrontFee);
		AccountFeesActionDetailEntity accountUpfrontFeesaction = new CustomerFeeScheduleEntity(
				customerScheduleEntity, upfrontFee, accountUpfrontFee,
				new Money("20.0"));
		customerScheduleEntity.addAccountFeesAction(accountUpfrontFeesaction);
		TestObjectFactory.updateObject(center);
		
        customerAccountBO =center.getCustomerAccount();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee",
				FeeCategory.ALLCUSTOMERS, "200", MeetingFrequency.WEEKLY, Short
						.valueOf("1"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(customerAccountBO,periodicFee,new Money("200.0"),
				FeeStatus.INACTIVE.getValue(), null,null);
		customerAccountBO.addAccountFees(accountPeriodicFee);
		TestObjectFactory.updateObject(center);
		
		return customerAccountBO;
	}

	
	
	private void incrementInstallmentDate(AccountBO accountBO, int numberOfDays,
			Short installmentId) {
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			if (accountActionDateEntity.getInstallmentId()
					.equals(installmentId)) {
				Calendar dateCalendar = new GregorianCalendar();
				dateCalendar.setTimeInMillis(accountActionDateEntity
						.getActionDate().getTime());
				int year = dateCalendar.get(Calendar.YEAR);
				int month = dateCalendar.get(Calendar.MONTH);
				int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
				dateCalendar = new GregorianCalendar(year, month, day
						+ numberOfDays);
				accountActionDateEntity.setActionDate(new java.sql.Date(
						dateCalendar.getTimeInMillis()));
				break;
			}
		}
	}
}
