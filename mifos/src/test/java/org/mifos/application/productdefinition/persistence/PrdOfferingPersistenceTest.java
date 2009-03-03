package org.mifos.application.productdefinition.persistence;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.List;

import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.PrdCategoryStatus;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.application.productsmix.business.ProductMixBO;
import org.mifos.application.productsmix.business.service.ProductMixBusinessService;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class PrdOfferingPersistenceTest extends MifosIntegrationTest {

	public PrdOfferingPersistenceTest() throws SystemException, ApplicationException {
        super();
    }
    private LoanOfferingBO loanOffering;
	private LoanOfferingBO loanOffering2;

	private PrdOfferingPersistence persistence;
	
	MeetingBO meetingIntCalc;
	MeetingBO meetingIntCalc2;
	ProductMixBO prdmix;
	ProductMixBO prdmix2;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		persistence = new PrdOfferingPersistence();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(prdmix);	
		TestObjectFactory.removeObject(prdmix2);	
		TestObjectFactory.removeObject(loanOffering);
		TestObjectFactory.removeObject(loanOffering2);
		
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testretrieveLatenessForPrd() throws Exception {
		Short latenessDays = null;
		latenessDays = new LoanPrdPersistence().retrieveLatenessForPrd();
		assertNotNull(latenessDays);
		assertEquals(Short.valueOf("10"), latenessDays);
	}

	public void testGetAllPrdOffringByType() throws Exception {
		assertNotNull(new PrdOfferingPersistence().getAllPrdOffringByType(ProductType.LOAN.getValue().toString()));
	}

	public void testGetMaxPrdOfferingWithouProduct()
			throws PersistenceException {
		assertNull(new PrdOfferingPersistence().getMaxPrdOffering());
	}

	public void testGetAllowedPrdOfferingsByType() throws PersistenceException {
		SavingsOfferingBO savingsOffering = new SavingsTestHelper()
				.createSavingsOffering("Eddikhar", "Edkh");
		assertNotNull(new PrdOfferingPersistence().getAllowedPrdOfferingsByType(
				savingsOffering.getPrdOfferingId().toString(),
				ProductType.SAVINGS.getValue().toString()));
		TestObjectFactory.removeObject(savingsOffering);
	}

	public void testGetAllowedPrdOfferingsForMixProduct()
			throws PersistenceException {
		SavingsOfferingBO savingsOffering = new SavingsTestHelper()
				.createSavingsOffering("Eddikhar", "Edkh");
		assertNotNull(new PrdOfferingPersistence()
				.getAllowedPrdOfferingsForMixProduct(savingsOffering
						.getPrdOfferingId().toString(), ProductType.SAVINGS
						.getValue().toString()));
		TestObjectFactory.removeObject(savingsOffering);
	}
	
	public void testGetMaxPrdOfferingWithProduct() throws PersistenceException {
		SavingsOfferingBO savingsOffering = new SavingsTestHelper()
				.createSavingsOffering("fsaf6", "ads6");
		assertNotNull(new PrdOfferingPersistence().getMaxPrdOffering());
		TestObjectFactory.removeObject(savingsOffering);
	}

	public void testGetPrdStatus() throws PersistenceException {
		PrdStatusEntity prdStatus = new PrdOfferingPersistence()
				.getPrdStatus(PrdStatus.SAVINGS_ACTIVE);
		assertNotNull(prdStatus);
		assertEquals(ProductType.SAVINGS.getValue(), prdStatus.getPrdType()
				.getProductTypeID());
		assertEquals(Short.valueOf("1"), prdStatus.getPrdState().getId());
	}

	public void testGetPrdOfferingNameCountWithoutData()
			throws PersistenceException {
		assertEquals(Integer.valueOf("0"), new PrdOfferingPersistence()
				.getProductOfferingNameCount("Savings product"));
	}

	public void testGetPrdOfferingNameCountWithDifferentName()
			throws PersistenceException {
		SavingsOfferingBO savingsOffering = new SavingsTestHelper()
				.createSavingsOffering("fsaf6", "ads6");
		assertEquals(Integer.valueOf("0"), new PrdOfferingPersistence()
				.getProductOfferingNameCount("Savings product"));
		TestObjectFactory.removeObject(savingsOffering);

	}

	public void testGetPrdOfferingNameCountWithSameName()
			throws PersistenceException {
		SavingsOfferingBO savingsOffering = new SavingsTestHelper()
				.createSavingsOffering("Savings product", "ads6");
		assertEquals(Integer.valueOf("1"), new PrdOfferingPersistence()
				.getProductOfferingNameCount("Savings product"));
		TestObjectFactory.removeObject(savingsOffering);

	}

	public void testGetPrdOfferingShortNameCountWithoutData()
			throws PersistenceException {
		assertEquals(Integer.valueOf("0"), new PrdOfferingPersistence()
				.getProductOfferingShortNameCount("SAVP"));
	}

	public void testGetPrdOfferingShortNameCountWithDifferentName()
			throws PersistenceException {
		SavingsOfferingBO savingsOffering = new SavingsTestHelper()
				.createSavingsOffering("fsaf6", "ads6");
		assertEquals(Integer.valueOf("0"), new PrdOfferingPersistence()
				.getProductOfferingShortNameCount("SAVP"));
		TestObjectFactory.removeObject(savingsOffering);

	}

	public void testGetPrdOfferingShortNameCountWithSameName()
			throws PersistenceException {
		SavingsOfferingBO savingsOffering = new SavingsTestHelper()
				.createSavingsOffering("Savings product", "SAVP");
		assertEquals(Integer.valueOf("1"), new PrdOfferingPersistence()
				.getProductOfferingShortNameCount("SAVP"));
		TestObjectFactory.removeObject(savingsOffering);

	}

	public void testGetApplicableProductCategories()
			throws PersistenceException {
		assertEquals(1, new PrdOfferingPersistence()
				.getApplicableProductCategories(ProductType.SAVINGS,
						PrdCategoryStatus.ACTIVE).size());
	}

	public void testGetApplicablePrdStatus() throws PersistenceException {
		List<PrdStatusEntity> prdStatusList = new PrdOfferingPersistence()
				.getApplicablePrdStatus(ProductType.LOAN, (short) 1);
		HibernateUtil.closeSession();
		assertEquals(2, prdStatusList.size());
		for (PrdStatusEntity prdStatus : prdStatusList) {
			if (prdStatus.getPrdState().equals("1"))
				assertEquals("Active", prdStatus.getPrdState().getName());
			if (prdStatus.getPrdState().equals("2"))
				assertEquals("InActive", prdStatus.getPrdState().getName());
		}
	}
	public void testGetPrdOfferingMix() throws ServiceException, PersistenceException {
		createLoanProductMixed();
		createsecondLoanProductMixed();
		prdmix=createNotAllowedProductForAProductOffering(loanOffering,loanOffering);
		assertEquals(2, persistence.getPrdOfferingMix().size());
		assertTrue("Products Mix should be in alphabitical order:",
				(persistence.getPrdOfferingMix().get(0).getPrdOfferingName().compareToIgnoreCase(persistence
						.getPrdOfferingMix().get(1).getPrdOfferingName())) < 0);
		HibernateUtil.closeSession();

	}
	private void createLoanProductMixed() throws PersistenceException {

		Date startDate = new Date(System.currentTimeMillis());

		meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));

		loanOffering = TestObjectFactory.createLoanOffering("BLoan", "L",
				startDate, meetingIntCalc);
		loanOffering.updatePrdOfferingFlag();

	}
	private void createsecondLoanProductMixed() throws PersistenceException {

		Date startDate = new Date(System.currentTimeMillis());

		meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));

		loanOffering2 = TestObjectFactory.createLoanOffering("ALoan", "aL",
				startDate, meetingIntCalc);
		loanOffering2.updatePrdOfferingFlag();

	}
	private  ProductMixBO createNotAllowedProductForAProductOffering(PrdOfferingBO prdOffering,PrdOfferingBO prdOfferingNotAllowedId)
	{
		return TestObjectFactory.createNotAllowedProductForAProductOffering(prdOffering,prdOfferingNotAllowedId);
		
	}
}
