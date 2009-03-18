package org.mifos.application.productdefinition.persistence;

import java.sql.Date;
import java.util.List;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanPrdPersistenceTest extends MifosIntegrationTest {

	public LoanPrdPersistenceTest() throws SystemException, ApplicationException {
        super();
    }

    private LoanOfferingBO loanOffering1;
	private LoanOfferingBO loanOffering2;
	private LoanOfferingBO loanOffering3;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(loanOffering1);
		TestObjectFactory.removeObject(loanOffering2);
		TestObjectFactory.removeObject(loanOffering3);
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

	public void testretrieveLatenessForPrd() throws Exception {
		Short latenessDays = null;
		latenessDays = new LoanPrdPersistence().retrieveLatenessForPrd();
		assertNotNull(latenessDays);
		assertEquals(Short.valueOf("10"), latenessDays);
	}

	
	public void testGetAllActiveLoanOfferings() throws PersistenceException {
		List <LoanOfferingBO> loanOfferingList = new LoanPrdPersistence().getAllActiveLoanOfferings(Short.valueOf("1"));
		assertNotNull(loanOfferingList);
	}
		
	public void testGetLoanOfferingsNotMixed() throws PersistenceException {
		List <LoanOfferingBO> loanOfferingList = new LoanPrdPersistence().getLoanOfferingsNotMixed(Short.valueOf("1"));
		assertNotNull(loanOfferingList);
	}
	public void testGetLoanOffering() throws PersistenceException {
		loanOffering1 = createLoanOfferingBO("Loan Offering", "Loan");
		StaticHibernateUtil.closeSession();

		loanOffering1 = new LoanPrdPersistence().getLoanOffering(loanOffering1
				.getPrdOfferingId());
		assertNotNull(loanOffering1);
		assertEquals("Loan Offering", loanOffering1.getPrdOfferingName());
		assertEquals("Loan", loanOffering1.getPrdOfferingShortName());
	}

	public void testGetLoanOfferingWithLocaleId() throws PersistenceException {
		loanOffering1 = createLoanOfferingBO("Loan Offering", "Loan");
		StaticHibernateUtil.closeSession();

		short localeId = 1;
		loanOffering1 = new LoanPrdPersistence().getLoanOffering(loanOffering1
				.getPrdOfferingId(), localeId);
		assertNotNull(loanOffering1);
		assertEquals("Loan Offering", loanOffering1.getPrdOfferingName());
		assertEquals("Loan", loanOffering1.getPrdOfferingShortName());

		assertEquals("Other", loanOffering1.getPrdCategory()
				.getProductCategoryName());
		assertEquals(ApplicableTo.GROUPS, 
				loanOffering1.getPrdApplicableMasterEnum());
		assertEquals("Active", loanOffering1.getPrdStatus().getPrdState()
				.getName());
		assertEquals("Grace on all repayments", loanOffering1
				.getGracePeriodType().getName());
		assertEquals("Flat", loanOffering1.getInterestTypes().getName());
	}

	public void testGetAllLoanOfferingsShouldReturnLoanOfferingListSortedByName() throws PersistenceException {
		String[] loanPrdNamesSortedByName = new String[] {"firstLoanOffering", "secondLoanOffering", "thirdLoanOffering"};
		loanOffering1 = createLoanOfferingBO(loanPrdNamesSortedByName[1], "Loa1");
		loanOffering2 = createLoanOfferingBO(loanPrdNamesSortedByName[2],
						"Loa2");
		loanOffering3 = createLoanOfferingBO(loanPrdNamesSortedByName[0],
				"Loa3");
		StaticHibernateUtil.closeSession();

		List<LoanOfferingBO> loanOfferings = new LoanPrdPersistence()
				.getAllLoanOfferings((short) 1);
		assertNotNull(loanOfferings);
		assertEquals(3, loanOfferings.size());
		int i = 0;
		for (LoanOfferingBO loanOfferingBO : loanOfferings) {
			assertEquals(loanPrdNamesSortedByName[i++], loanOfferingBO.getPrdOfferingName());
			assertNotNull(loanOfferingBO.getPrdOfferingId());
			assertNotNull(loanOfferingBO.getPrdStatus().getPrdState().getName());
		}
		StaticHibernateUtil.closeSession();
	}

	private LoanOfferingBO createLoanOfferingBO(String prdOfferingName,
			String shortName) {
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		return TestObjectFactory.createLoanOffering(prdOfferingName, shortName,
				ApplicableTo.GROUPS, startDate, 
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, 
				InterestType.FLAT, frequency);
	}

}
