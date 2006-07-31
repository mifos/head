/**
 * 
 */
package org.mifos.application.collectionsheet.business;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mifos.framework.MifosTestCase;

import org.hibernate.Session;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.collectionsheet.persistence.service.CollectionSheetPersistenceService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.master.util.valueobjects.AccountType;
import org.mifos.application.master.util.valueobjects.InterestTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 * @author ashishsm
 * 
 */
public class TestCollSheetBO extends MifosTestCase {
	

	protected AccountBO accountBO=null;
	protected CustomerBO center=null;
	protected CustomerBO group=null;
	protected CustomerBO client=null;
	protected CollectionSheetBO collectionSheet=null;
	
	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(collectionSheet);
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	protected void setUp()throws Exception{
		HibernateUtil.getSessionTL();
	}
	
	public void testAddCollectionSheetCustomer() {
		CollSheetCustBO collectionSheetCustomer = new CollSheetCustBO();
		collectionSheetCustomer.setCustId(Integer.valueOf("1"));
		
		CollectionSheetBO collSheet = new CollectionSheetBO();
		collSheet.addCollectionSheetCustomer(collectionSheetCustomer);
		
	}
	
	public void testGetCollectionSheetCustomerForCustomerId() {
		
		CollSheetCustBO collectionSheetCustomer = new CollSheetCustBO();
		collectionSheetCustomer.setCustId(Integer.valueOf("1"));
		
		CollectionSheetBO collSheet = new CollectionSheetBO();
		assertNull(collSheet.getCollectionSheetCustomerForCustomerId(Integer
				.valueOf("1")));
		collSheet.addCollectionSheetCustomer(collectionSheetCustomer);
		
		assertEquals(collSheet.getCollectionSheetCustomerForCustomerId(
				Integer.valueOf("1")).getCustId(), Integer.valueOf("1"));
		
	}
	
	public void testAddLoanDetailsForDisbursal() {
		
		PersonnelBO personnel = new PersonnelBO();
		personnel.setPersonnelId(Short.valueOf("1"));
		
		OfficeBO office = new OfficeBO();
		office.setOfficeId(Short.valueOf("1"));
		
		CustomerLevelEntity custLevel = new CustomerLevelEntity();
		custLevel.setLevelId(Short.valueOf("1"));
		
		ClientBO client = new ClientBO();
		client.setCustomerId(Integer.valueOf("1"));
		client.setCustomerLevel(custLevel);
		client.setDisplayName("displayname");
		client.setSearchId("1.1.1");
		client.setPersonnel(personnel);
		client.setOffice(office);
		
		LoanBO loan = (LoanBO)createLoanAccount();
		loan.setLoanAmount(TestObjectFactory.getMoneyForMFICurrency(100));
		loan.setNoOfInstallments(Short.valueOf("5"));
		loan.setCustomer(client);
		InterestTypes interestType = new InterestTypes();
		interestType.setInterestTypeId(Short.valueOf(LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT));
		loan.setInterestType(interestType);
		List<LoanBO> loanWithDisbursalDate = new ArrayList<LoanBO>();
		loanWithDisbursalDate.add(loan);
		CollectionSheetBO collSheet = new CollectionSheetBO();
		collSheet.addLoanDetailsForDisbursal(loanWithDisbursalDate);
		CollSheetCustBO collectionSheetCustomer = collSheet
		.getCollectionSheetCustomerForCustomerId(client.getCustomerId());
		assertNotNull(collectionSheetCustomer);
		assertEquals(collectionSheetCustomer.getLoanDetailsForAccntId(loan.getAccountId()).getTotalNoOfInstallments(), Short
				.valueOf("5"));
	}
	
	public void testCollSheetForLoanDisbursal() throws Exception{
		
		Date startDate = new Date(System.currentTimeMillis());
		accountBO=getLoanAccount(AccountStates.LOANACC_APPROVED,startDate,1);
		LoanBO loan = (LoanBO)accountBO;
		AccountActionDateEntity firstInstallmentActionDate = loan.getAccountActionDate((short)1);
		collectionSheet = createCollectionSheet(getCurrentDate());
		generateCollectionSheetForDate(collectionSheet);
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		collectionSheet.update(Short.valueOf("2"));
		HibernateUtil.getTransaction().commit();
		HibernateUtil.closeSession();
		Money disbursedAmount = collectionSheet.getCollectionSheetCustomerForCustomerId(group.getCustomerId()).getLoanDetailsForAccntId(loan.getAccountId()).getAmntToBeDisbursed();
		assertEquals(300.00 , disbursedAmount.getAmountDoubleValue());
		
	}
	
	public void testCollSheetForLoanDisbursalInterestDeducted() throws Exception{
		
		Date startDate = new Date(System.currentTimeMillis());
		accountBO=getLoanAccount(AccountStates.LOANACC_APPROVED,startDate,2);
		LoanBO loan = (LoanBO)accountBO;
		AccountActionDateEntity firstInstallmentActionDate = loan.getAccountActionDate((short)1);
		collectionSheet = createCollectionSheet(getCurrentDate());
		generateCollectionSheetForDate(collectionSheet);
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		collectionSheet.update(Short.valueOf("2"));
		HibernateUtil.getTransaction().commit();
		HibernateUtil.closeSession();
		CollSheetLnDetailsEntity collSheetLoanDetail =collectionSheet.getCollectionSheetCustomerForCustomerId(group.getCustomerId()).getLoanDetailsForAccntId(loan.getAccountId());
		Money disbursedAmount = collSheetLoanDetail.getAmntToBeDisbursed();
		assertEquals(300.00 ,disbursedAmount.getAmountDoubleValue());
		assertEquals(12.00 , collSheetLoanDetail.getInterestDue().getAmountDoubleValue());
		assertEquals(40.00 , collSheetLoanDetail.getFeesDue().getAmountDoubleValue());
	}
	
	public void testCollSheetForSavingsDeposit() throws Exception{
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = createSavingsAccount(SavingsConstants.SAVINGS_MANDATORY);
		SavingsBO savings = (SavingsBO)accountBO;
		AccountActionDateEntity firstInstallmentActionDate = savings.getAccountActionDate((short)3);
		collectionSheet = createCollectionSheet(firstInstallmentActionDate.getActionDate());
		generateCollectionSheetForDate(collectionSheet);
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		collectionSheet.update(Short.valueOf("2"));
		HibernateUtil.getTransaction().commit();
		HibernateUtil.closeSession();
		CollSheetSavingsDetailsEntity collSheetSavingsDetail =collectionSheet.getCollectionSheetCustomerForCustomerId(client.getCustomerId()).getSavingsDetailsForAccntId(savings.getAccountId());
		assertEquals(200.00 ,collSheetSavingsDetail.getRecommendedAmntDue().getAmountDoubleValue());
		assertEquals(400.00 ,collSheetSavingsDetail.getAmntOverDue().getAmountDoubleValue());
		
	}
	public void testCollSheetForSavingsDepositVoluntary() throws Exception{
		
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = createSavingsAccount(SavingsConstants.SAVINGS_VOLUNTARY);
		SavingsBO savings = (SavingsBO)accountBO;
		AccountActionDateEntity firstInstallmentActionDate = savings.getAccountActionDate((short)3);
		collectionSheet = createCollectionSheet(firstInstallmentActionDate.getActionDate());
		generateCollectionSheetForDate(collectionSheet);
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		collectionSheet.update(Short.valueOf("2"));
		HibernateUtil.getTransaction().commit();
		HibernateUtil.closeSession();
		CollSheetSavingsDetailsEntity collSheetSavingsDetail =collectionSheet.getCollectionSheetCustomerForCustomerId(client.getCustomerId()).getSavingsDetailsForAccntId(savings.getAccountId());
		assertEquals(200.00 ,collSheetSavingsDetail.getRecommendedAmntDue().getAmountDoubleValue());
		assertEquals(0.00 ,collSheetSavingsDetail.getAmntOverDue().getAmountDoubleValue());
	}
	
	public void testPouplateCustomers() {
		CollectionSheetBO collSheet = new CollectionSheetBO();
		List<AccountActionDateEntity> accountActionDates = getCustomerAccntDetails();
		collSheet.pouplateCustAndCustAccntDetails(accountActionDates);
		assertEquals(collSheet.getCollectionSheetCustomers().size(), 3);
		for (AccountActionDateEntity entity : accountActionDates) {
			TestObjectFactory.cleanUp(entity.getCustomer());
		}
	}
	
	public void testPopulateLoanAccounts() {
		CollectionSheetBO collSheet = new CollectionSheetBO();
		List<AccountActionDateEntity> accountActionDates = getLnAccntDetails();
		collSheet.pouplateCustAndCustAccntDetails(accountActionDates);
		
		
		try {
			collSheet.populateLoanAccounts(accountActionDates);
		} catch (SystemException e) {
			e.printStackTrace();
		}catch(ApplicationException ae){
			ae.printStackTrace();
		}
		
		Set<CollSheetCustBO> collSheetCustBOs = collSheet
		.getCollectionSheetCustomers();
		for (CollSheetCustBO collSheetCust : collSheetCustBOs) {
			assertNotNull(collSheetCust.getCollectionSheetLoanDetails());
		}
		
		doCleanUp(accountActionDates);
	}
	
	public void testUpdateCollectiveTotals() {
		
		CollectionSheetBO collSheet = new CollectionSheetBO();
		List<AccountActionDateEntity> accountActionDates = getLnAccntDetails();
		collSheet.pouplateCustAndCustAccntDetails(accountActionDates);
		try {
			collSheet.populateLoanAccounts(accountActionDates);
		} catch (SystemException e) {
			e.printStackTrace();
		}catch(ApplicationException ae){
			ae.printStackTrace();
		}
		doCleanUp(accountActionDates);
		
	}
	
	public void testCreateSucess() throws Exception{
		collectionSheet = createCollectionSheet();
		Session session= HibernateUtil.getSessionTL();
		CollectionSheetBO collectionSheetBO = (CollectionSheetBO) session.get(CollectionSheetBO.class,collectionSheet.getCollSheetID());
		assertNotNull(collectionSheetBO);
		
	}
	
	public void testCreateFailure() throws Exception{
		CollectionSheetBO collSheet = new CollectionSheetBO();
		try
		{
			HibernateUtil.getSessionTL();
			HibernateUtil.startTransaction();
			collSheet.create();
			HibernateUtil.getTransaction().commit();
			
		}
		catch( Exception e){
		}
		finally{
			
		}
		assertNull(collSheet.getCollSheetID());
	}
	
	public void testUpdate()throws Exception{
	
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = createSavingsAccount(SavingsConstants.SAVINGS_VOLUNTARY);
		SavingsBO savings = (SavingsBO)accountBO;
		AccountActionDateEntity firstInstallmentActionDate = savings.getAccountActionDate((short)3);
		collectionSheet = createCollectionSheet(firstInstallmentActionDate.getActionDate());
		generateCollectionSheetForDate(collectionSheet);
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		collectionSheet.update(Short.valueOf("2"));
		HibernateUtil.getTransaction().commit();
		HibernateUtil.closeSession();
		assertEquals(Short.valueOf("2"),collectionSheet.getStatusFlag());
	
	}
	
	private CollectionSheetBO createCollectionSheet()throws Exception{
		
		CollectionSheetBO collSheet = new CollectionSheetBO();
		
		collSheet.setCollectionSheetCustomers(null);
		collSheet.setCollSheetDate(getCurrentDate());
		collSheet.setCreatedDate(getCurrentDate());
		collSheet.setRunDate(getCurrentDate());
		collSheet.setStatusFlag(null);
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		collSheet.create();
		HibernateUtil.getTransaction().commit();
		return collSheet;
		
	}
	
	private CollectionSheetBO createCollectionSheet(Date runDate)throws Exception{
		
		CollectionSheetBO collSheet = new CollectionSheetBO();
		collSheet.setCollectionSheetCustomers(null);
		collSheet.setCollSheetDate(runDate);
		collSheet.setCreatedDate(getCurrentDate());
		collSheet.setRunDate(runDate);
		collSheet.setStatusFlag(null);
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		collSheet.create();
		HibernateUtil.getTransaction().commit();
		return collSheet;
		
	}
	
	private Date getCurrentDate(){
		return new Date(System.currentTimeMillis());
	}
	
	private List<AccountActionDateEntity> getAccountActionDates() {
		
		
		MeetingBO meeting = TestObjectFactory.getMeetingHelper(1, 1, 4, 2);
		TestObjectFactory.createMeeting(meeting);
		CustomerBO center = TestObjectFactory.createCenter("ashCenter", Short
				.valueOf("1"), "1.1", meeting, new Date(System
						.currentTimeMillis()));
		
		CustomerBO group = TestObjectFactory.createGroup("ashGrp", Short
				.valueOf("1"), "1.1.1", center, new Date(System
						.currentTimeMillis()));
		
		CustomerBO client = TestObjectFactory.createClient("ash", Short
				.valueOf("1"), "1.1.1.1", group, new Date(System
						.currentTimeMillis()));
		
		
		
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		
		AccountActionDateEntity accntActionDateClient = new AccountActionDateEntity();
		accntActionDateClient.setCustomer(client);
		
		AccountActionDateEntity accntActionDateGrp = new AccountActionDateEntity();
		accntActionDateGrp.setCustomer(group);
		
		AccountActionDateEntity accntActionDateCenter = new AccountActionDateEntity();
		accntActionDateCenter.setCustomer(center);
		
		accntActionDates.add(accntActionDateCenter);
		accntActionDates.add(accntActionDateGrp);
		accntActionDates.add(accntActionDateClient);
		
		return accntActionDates;
	}
	
	private   List<AccountActionDateEntity> getCustomerAccntDetails() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center1", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		List<AccountActionDateEntity> accntActionDates = getAccountActionDates();
		for (AccountActionDateEntity accntActionDate : accntActionDates) {
			AccountBO accountBO = center.getCustomerAccount();
			accntActionDate.setAccount(accountBO);
			AccountFeesActionDetailEntity accntFeesActionDetailEntity = new AccountFeesActionDetailEntity();
			accntFeesActionDetailEntity.setFeeAmount(TestObjectFactory.getMoneyForMFICurrency(5));
			accntFeesActionDetailEntity.setFeeAmountPaid(TestObjectFactory.getMoneyForMFICurrency(3));
			
			accntActionDate.addAccountFeesAction(accntFeesActionDetailEntity);
			
			accntActionDate.setPenalty(TestObjectFactory.getMoneyForMFICurrency(10));
			accntActionDate.setMiscPenalty(TestObjectFactory.getMoneyForMFICurrency(3));
			accntActionDate.setMiscPenaltyPaid(TestObjectFactory.getMoneyForMFICurrency(0));
			accntActionDate.setPenaltyPaid(TestObjectFactory.getMoneyForMFICurrency(5));
			accntActionDate.setMiscFee(TestObjectFactory.getMoneyForMFICurrency(5));
			accntActionDate.setMiscFeePaid(TestObjectFactory.getMoneyForMFICurrency(5));
		}
		
		return accntActionDates;
	}
	
	private List<AccountActionDateEntity> getLnAccntDetails() {
		List<AccountActionDateEntity> accntActionDates = getAccountActionDates();
		for (AccountActionDateEntity accntActionDate : accntActionDates) {
			
			AccountType accntType = new AccountType();
			accntType.setAccountTypeId(Short.valueOf(AccountTypes.LOANACCOUNT));
			
			LoanBO loan= createLoanAccount(accntActionDate.getCustomer());
			accntActionDate.setAccount(loan);
			AccountFeesActionDetailEntity accntFeesActionDetailEntity = new AccountFeesActionDetailEntity();
			accntFeesActionDetailEntity.setFeeAmount(TestObjectFactory.getMoneyForMFICurrency(5));
			accntFeesActionDetailEntity.setFeeAmountPaid(TestObjectFactory.getMoneyForMFICurrency(3));
			
			accntActionDate.addAccountFeesAction(accntFeesActionDetailEntity);
			
			accntActionDate.setPenalty(TestObjectFactory.getMoneyForMFICurrency(10));
			accntActionDate.setMiscPenalty(TestObjectFactory.getMoneyForMFICurrency(3));
			accntActionDate.setPenaltyPaid(TestObjectFactory.getMoneyForMFICurrency(5));
			accntActionDate.setMiscFee(TestObjectFactory.getMoneyForMFICurrency(5));
			accntActionDate.setMiscFeePaid(TestObjectFactory.getMoneyForMFICurrency(4));
			accntActionDate.setPrincipal(TestObjectFactory.getMoneyForMFICurrency(10));
			accntActionDate.setPrincipalPaid(TestObjectFactory.getMoneyForMFICurrency(5));
			accntActionDate.setInterest(TestObjectFactory.getMoneyForMFICurrency(2));
			accntActionDate.setInterestPaid(TestObjectFactory.getMoneyForMFICurrency(1));
			accntActionDate.setInstallmentId(Short.valueOf("2"));
		}
		return accntActionDates;
	}
	private LoanBO createLoanAccount(CustomerBO customerBO){
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
				.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), customerBO.getCustomerMeeting().getMeeting());
		return  TestObjectFactory.createLoanAccount(
				"42423142341", customerBO, Short.valueOf("5"), new Date(System
						.currentTimeMillis()), loanOffering);
		
		
	}
	
	
	private void doCleanUp(List<AccountActionDateEntity> accountActionDates ){
		for (AccountActionDateEntity entity : accountActionDates) {
			TestObjectFactory.cleanUp(entity.getAccount());
			TestObjectFactory.cleanUp(entity.getCustomer());
		}
		
	}
	private AccountBO getLoanAccount(Short accountSate,Date startDate,int disbursalType) {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1,1,4,2));
        center=TestObjectFactory.createCenter("Center",Short.valueOf("13"),"1.1",meeting,new Date(System.currentTimeMillis()));
        group=TestObjectFactory.createGroup("Group",Short.valueOf("9"),"1.1.1",center,new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccountWithDisbursement("99999999999", group,
				accountSate, startDate, loanOffering,disbursalType);

	}
	/**
	 *It queries the database for valid customers which have the meeting date tomorrow and populates its 
	 * corresponding fields with the data.
	 * @param currentDate
	 */
	private void generateCollectionSheetForDate(CollectionSheetBO collectionSheet )throws SystemException,ApplicationException{
		
			CollectionSheetPersistenceService collSheetPerService = (CollectionSheetPersistenceService)ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.CollectionSheet);
			try{
				List<AccountActionDateEntity> accountActionDates = collSheetPerService.getCustFromAccountActionsDate(collectionSheet.getCollSheetDate());
				MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("After retrieving account action date objects for next meeting date. ");
				if(null != accountActionDates && accountActionDates.size() > 0){
					collectionSheet.populateAccountActionDates(accountActionDates);
					
				}
				System.out.println("Coll sheet date: "+collectionSheet.getCollSheetDate());
				List<LoanBO> loanBOs = collSheetPerService.getLnAccntsWithDisbursalDate(collectionSheet.getCollSheetDate());
				MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("After retrieving loan accounts due for disbursal");
				if(null != loanBOs && loanBOs.size()>0){
					collectionSheet.addLoanDetailsForDisbursal(loanBOs);
					MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("After processing loan accounts which had disbursal due.");
				}
				
				if(null != collectionSheet.getCollectionSheetCustomers() && collectionSheet.getCollectionSheetCustomers().size()>0){
					collectionSheet.updateCollectiveTotals();
					MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("After updating collective totals");
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
	}
	
	private SavingsBO createSavingsAccount(short savingsType){
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsOffering("SavingPrd1",(short)2,new Date(System.currentTimeMillis()),
				(short)2,300.0,(short)1,1.2,200.0,200.0,savingsType,(short)1,meetingIntCalc,meetingIntPost);
		center = TestObjectFactory.createCenter("Center", (short)13,
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", (short)9,
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client", (short)3,
				"1.1.1.1", group, new Date(System.currentTimeMillis()));
		return TestObjectFactory.createSavingsAccount("43245434",
				client, (short)16, new Date(System.currentTimeMillis()), savingsOffering);

	}
	
	private AccountBO createLoanAccount(){
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		accountBO = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		return accountBO;
	}
}
