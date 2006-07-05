package org.mifos.application.accounts.financial.business.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.mifos.framework.MifosTestCase;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.persistence.service.SavingsPersistenceService;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestFinancialBusinessService extends MifosTestCase {	
	protected LoanBO loan = null;
	protected SavingsBO savings;
	protected SavingsOfferingBO savingsOffering;
	protected CustomerBO center=null;
	private CustomerBO group=null;
	private UserContext userContext;
	
	protected void setUp() throws Exception {
		super.setUp();
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
	}

	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(loan);
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoanAdjustmentAccountingEntries() throws Exception {
		Date currentDate = new Date(System.currentTimeMillis());
		loan = getLoanAccount();
		loan.setUserContext(TestObjectFactory.getUserContext());
		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity();
		accountPaymentEntity.setPaymentDetails(TestObjectFactory.getMoneyForMFICurrency(630), "1111", currentDate, Short.valueOf("1"));
		FinancialBusinessService financialBusinessService = (FinancialBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Financial);
		AccountTrxnEntity accountTrxnEntity = getAccountTrxnObj();
		accountTrxnEntity.setAccount(loan);
		accountPaymentEntity.addAcountTrxn(accountTrxnEntity);
		loan.addAccountPayment(accountPaymentEntity);
		
		financialBusinessService.buildAccountingEntries(accountTrxnEntity);
		
		TestObjectFactory.updateObject(loan);
		assertEquals(accountTrxnEntity.getFinancialTransactions().size(),10);
		
		int countNegativeFinTrxn = 0;
		for (FinancialTransactionBO finTrxn : accountTrxnEntity.getFinancialTransactions()) {
			if(finTrxn.getPostedAmount().getAmountDoubleValue() < 0)
				countNegativeFinTrxn++;
			else
				assertEquals("Positive finTrxn values",finTrxn.getPostedAmount().getAmountDoubleValue(),200.0);
		}
		assertEquals("Negative finTrxn values count",countNegativeFinTrxn,9);
		assertEquals("Positive finTrxn values count",accountTrxnEntity.getFinancialTransactions().size()-countNegativeFinTrxn,1);
	}
	
	private AccountTrxnEntity getAccountTrxnObj() throws Exception {
		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
		.getInstance().getPersistenceService(PersistenceServiceName.MasterDataService);
		Date currentDate = new Date(System.currentTimeMillis());
		
		AccountActionDateEntity accountAction = loan.getAccountActionDate(Short.valueOf("1"));
		
		LoanTrxnDetailEntity accountTrxnEntity = new LoanTrxnDetailEntity();
		accountTrxnEntity.setActionDate(currentDate);
		accountTrxnEntity.setDueDate(accountAction.getActionDate());
		accountTrxnEntity.setPersonnel(TestObjectFactory.getPersonnel(TestObjectFactory.getUserContext().getId()));
		accountTrxnEntity.setAccountActionEntity((AccountActionEntity) masterPersistenceService.findById(AccountActionEntity.class,AccountConstants.ACTION_LOAN_ADJUSTMENT));
		accountTrxnEntity.setComments("test for loan adjustment");
		accountTrxnEntity.setCustomer(group);
		accountTrxnEntity.setTrxnCreatedDate(new Timestamp(System.currentTimeMillis()));
		accountTrxnEntity.setInstallmentId(Short.valueOf("1"));
		accountTrxnEntity.setAmount(TestObjectFactory.getMoneyForMFICurrency(630));
		accountTrxnEntity.setMiscFeeAmount(TestObjectFactory.getMoneyForMFICurrency(10));
		accountTrxnEntity.setMiscPenaltyAmount(TestObjectFactory.getMoneyForMFICurrency(20));
		accountTrxnEntity.setPrincipalAmount(TestObjectFactory.getMoneyForMFICurrency(200));
		accountTrxnEntity.setInterestAmount(TestObjectFactory.getMoneyForMFICurrency(300));
		
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountAction.getAccountFeesActionDetails()) {
			accountFeesActionDetailEntity.setFeeAmountPaid(TestObjectFactory.getMoneyForMFICurrency(100));
			FeesTrxnDetailEntity feeTrxn = new FeesTrxnDetailEntity();
			feeTrxn.makePayment(accountFeesActionDetailEntity);
			accountTrxnEntity.addFeesTrxnDetail(feeTrxn);
		}

		return accountTrxnEntity;
	}
	
	private LoanBO getLoanAccount()
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
	
	public void testSavingsAdjustmentDepositAccountingEntries() throws Exception {
		createInitialObjectsForSavings();
		SavingsTestHelper helper = new SavingsTestHelper();
		SavingsPersistenceService savingsService = new SavingsPersistenceService();
		
		PersonnelBO createdBy = new PersonnelPersistence().getPersonnel(userContext.getId());
		Money depositAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),"1000.0");
		Money balanceAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),"5000.0");
		java.util.Date trxnDate=helper.getDate("20/05/2006");

		
		AccountPaymentEntity  payment = helper.createAccountPaymentToPersist(depositAmount,balanceAmount,trxnDate,AccountConstants.ACTION_SAVINGS_DEPOSIT,savings, createdBy,group);
		savings.addAccountPayment(payment);
		savings.setSavingsBalance(balanceAmount);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		
		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		payment = savings.getLastPmnt();
		balanceAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),"4000.0");
		AccountTrxnEntity accountTrxn = helper.createAccountTrxn(null, depositAmount.negate(), balanceAmount,trxnDate, trxnDate,null, AccountConstants.ACTION_SAVINGS_ADJUSTMENT,savings,createdBy,group);
		payment.addAcountTrxn(accountTrxn);
		savings.setSavingsBalance(balanceAmount);
		
		GLCodeEntity glCodeEntity =(GLCodeEntity)HibernateUtil.getSessionTL().get(GLCodeEntity.class,Short.valueOf("31"));
		savings.getSavingsOffering().setDepositGLCode(glCodeEntity);
		FinancialBusinessService financialBusinessService = (FinancialBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Financial);
		financialBusinessService.buildAccountingEntries(accountTrxn);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		
		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		payment = savings.getLastPmnt();
		assertEquals(Integer.valueOf(2).intValue(),payment.getAccountTrxns().size());
		for(AccountTrxnEntity trxn: payment.getAccountTrxns()){
			if(trxn.getAccountActionEntity().getId().equals(AccountConstants.ACTION_SAVINGS_ADJUSTMENT)){
				assertTrue(true);
				assertEquals(Integer.valueOf(2).intValue(),trxn.getFinancialTransactions().size());
				int countNegativeFinTrxn = 0;
				for (FinancialTransactionBO finTrxn : trxn.getFinancialTransactions()) {
					if(finTrxn.getPostedAmount().getAmountDoubleValue() < 0){
						countNegativeFinTrxn++;
						assertEquals(1000.0,finTrxn.getPostedAmount().negate().getAmountDoubleValue());
					}
					else
						assertTrue(false);
				}
				
				assertEquals("Negative finTrxn values count",2,countNegativeFinTrxn);
				assertEquals("Positive finTrxn values count",0,accountTrxn.getFinancialTransactions().size()-countNegativeFinTrxn);
			}
		}
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}
	
	
	public void testSavingsAdjustmentWithdrawalAccountingEntries() throws Exception {
		createInitialObjectsForSavings();
		SavingsTestHelper helper = new SavingsTestHelper();
		SavingsPersistenceService savingsService = new SavingsPersistenceService();
		
		PersonnelBO createdBy = new PersonnelPersistence().getPersonnel(userContext.getId());
		Money withdrawalAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),"1000.0");
		Money balanceAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),"5000.0");
		java.util.Date trxnDate=helper.getDate("20/05/2006");

		
		AccountPaymentEntity  payment = helper.createAccountPaymentToPersist(withdrawalAmount,balanceAmount,trxnDate,AccountConstants.ACTION_SAVINGS_WITHDRAWAL,savings, createdBy,group);
		savings.addAccountPayment(payment);
		savings.setSavingsBalance(balanceAmount);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		
		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		payment = savings.getLastPmnt();
		balanceAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),"6000.0");
		AccountTrxnEntity accountTrxn = helper.createAccountTrxn(null, withdrawalAmount, balanceAmount,trxnDate, trxnDate,null, AccountConstants.ACTION_SAVINGS_ADJUSTMENT,savings,createdBy,group);
		accountTrxn.setComments("correction entry");
		payment.addAcountTrxn(accountTrxn);
		savings.setSavingsBalance(balanceAmount);
		
		GLCodeEntity glCodeEntity =(GLCodeEntity)HibernateUtil.getSessionTL().get(GLCodeEntity.class,Short.valueOf("31"));
		savings.getSavingsOffering().setDepositGLCode(glCodeEntity);
		FinancialBusinessService financialBusinessService = (FinancialBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Financial);
		financialBusinessService.buildAccountingEntries(accountTrxn);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		
		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		payment = savings.getLastPmnt();
		assertEquals(Integer.valueOf(2).intValue(),payment.getAccountTrxns().size());
		for(AccountTrxnEntity trxn: payment.getAccountTrxns()){
			if(trxn.getAccountActionEntity().getId().equals(AccountConstants.ACTION_SAVINGS_ADJUSTMENT)){
				assertTrue(true);
				assertEquals(Integer.valueOf(2).intValue(),trxn.getFinancialTransactions().size());
				int countNegativeFinTrxn = 0;
				for (FinancialTransactionBO finTrxn : trxn.getFinancialTransactions()) {
					if(finTrxn.getPostedAmount().getAmountDoubleValue() < 0){
						countNegativeFinTrxn++;
					}
					else
						assertEquals(1000.0,finTrxn.getPostedAmount().getAmountDoubleValue());
					assertEquals("correction entry",finTrxn.getNotes());
				}
				
				assertEquals("Negative finTrxn values count",0,countNegativeFinTrxn);
				assertEquals("Positive finTrxn values count",2,accountTrxn.getFinancialTransactions().size()-countNegativeFinTrxn);
				
			}
		}
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testWithdrawalEntriesOnSavingsCloseAccount()throws Exception{
		createInitialObjectsForSavings();
		SavingsTestHelper helper = new SavingsTestHelper();
		PersonnelBO createdBy = new PersonnelPersistence().getPersonnel(userContext.getId());
		Money withdrawalAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),"1000.7");
		java.util.Date trxnDate=helper.getDate("20/05/2006");
			
		GLCodeEntity glCodeEntity =(GLCodeEntity)HibernateUtil.getSessionTL().get(GLCodeEntity.class,Short.valueOf("31"));
		savings.getSavingsOffering().setDepositGLCode(glCodeEntity);
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(withdrawalAmount,new Money(),trxnDate, AccountConstants.ACTION_SAVINGS_WITHDRAWAL,savings,createdBy,group);
		
		assertEquals(Integer.valueOf(1).intValue(),payment.getAccountTrxns().size());
		FinancialBusinessService financialBusinessService = (FinancialBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Financial);
		savings.addAccountPayment(payment);
		SavingsTrxnDetailEntity accountTrxn = null;
		for(AccountTrxnEntity trxn: payment.getAccountTrxns())
			accountTrxn = (SavingsTrxnDetailEntity)trxn;
		savings.setAccountState(new SavingsPersistence().getAccountStatusObject(
				AccountStates.SAVINGS_ACC_CLOSED));
		financialBusinessService.buildAccountingEntries(accountTrxn);
		Set<FinancialTransactionBO> financialTrxns = accountTrxn.getFinancialTransactions();
		assertEquals(Integer.valueOf(4).intValue(),financialTrxns.size());

		int withdrawalTrxns = 0;
		int roundingTrxns = 0;
		for(FinancialTransactionBO finTrxn: financialTrxns){
			if(finTrxn.getFinancialAction().getId().equals(FinancialActionConstants.ROUNDING))
				roundingTrxns++;
			else
				withdrawalTrxns++;
		}
		assertEquals(Integer.valueOf(2).intValue(),roundingTrxns);
		assertEquals(Integer.valueOf(2).intValue(),withdrawalTrxns);
		assertEquals(new Money (Configuration.getInstance().getSystemConfig().getCurrency(),"1001"),accountTrxn.getWithdrawlAmount());
		HibernateUtil.closeSession();
		
		savings = new SavingsPersistence().findById(savings.getAccountId());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}
	
	private void createInitialObjectsForSavings(){
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short.valueOf("13"), "1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short.valueOf("9"), "1.1.1", center, new Date(System.currentTimeMillis()));
		SavingsTestHelper helper = new SavingsTestHelper();
		savingsOffering=helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",savingsOffering, group,AccountStates.SAVINGS_ACC_APPROVED, userContext);
	}
	
	public void testLoanWriteOffAccountingEntries() throws Exception {
		Date currentDate = new Date(System.currentTimeMillis());
		loan = getLoanAccount();
		loan.setUserContext(TestObjectFactory.getUserContext());
		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity();
		accountPaymentEntity.setPaymentDetails(TestObjectFactory.getMoneyForMFICurrency(630), null, null, Short.valueOf("1"));
		FinancialBusinessService financialBusinessService = (FinancialBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Financial);
		AccountActionDateEntity accountActionDateEntity = loan.getAccountActionDate(Short.valueOf("1"));
		LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity();
		accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);
		loanTrxnDetailEntity.setAccount(loan);
		loanTrxnDetailEntity.setLoanTrxnDetailsForWriteOff(accountActionDateEntity,loan.getUserContext().getId());
		loan.addAccountPayment(accountPaymentEntity);
		
		financialBusinessService.buildAccountingEntries(loanTrxnDetailEntity);
		TestObjectFactory.updateObject(loan);
		Set<FinancialTransactionBO> finTrxnSet = loanTrxnDetailEntity.getFinancialTransactions();
		assertEquals(finTrxnSet.size(),2);
		int countNegativeFinTrxn = 0;
		for (FinancialTransactionBO finTrxn : finTrxnSet) {
			if(finTrxn.getPostedAmount().getAmountDoubleValue() < 0) {
				assertEquals("Negative finTrxn values",finTrxn.getPostedAmount().negate().getAmountDoubleValue(),100.0);
				countNegativeFinTrxn++;
			}
			else
				assertEquals("Positive finTrxn values",finTrxn.getPostedAmount().getAmountDoubleValue(),100.0);
		}
		assertEquals("Negative finTrxn values count",countNegativeFinTrxn,1);
		assertEquals("Positive finTrxn values count",finTrxnSet.size()-countNegativeFinTrxn,1);
	}
}
