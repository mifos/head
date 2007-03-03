package org.mifos.application.productdefinition.persistence;

import java.sql.Date;
import java.util.List;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanPrdPersistenceTest extends MifosTestCase {

	private LoanOfferingBO loanOffering;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(loanOffering);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testretrieveLatenessForPrd() throws Exception {
		Short latenessDays = null;
		latenessDays = new LoanPrdPersistence().retrieveLatenessForPrd();
		assertNotNull(latenessDays);
		assertEquals(Short.valueOf("10"), latenessDays);
	}

	public void testGetLoanOffering() throws PersistenceException {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		HibernateUtil.closeSession();

		loanOffering = new LoanPrdPersistence().getLoanOffering(loanOffering
				.getPrdOfferingId());
		assertNotNull(loanOffering);
		assertEquals("Loan Offering", loanOffering.getPrdOfferingName());
		assertEquals("Loan", loanOffering.getPrdOfferingShortName());
	}

	public void testGetLoanOfferingWithLocaleId() throws PersistenceException {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		HibernateUtil.closeSession();

		short localeId = 1;
		loanOffering = new LoanPrdPersistence().getLoanOffering(loanOffering
				.getPrdOfferingId(), localeId);
		assertNotNull(loanOffering);
		assertEquals("Loan Offering", loanOffering.getPrdOfferingName());
		assertEquals("Loan", loanOffering.getPrdOfferingShortName());

		assertEquals("Other", loanOffering.getPrdCategory()
				.getProductCategoryName());
		assertEquals(ApplicableTo.GROUPS, 
				loanOffering.getPrdApplicableMasterEnum());
		assertEquals("Active", loanOffering.getPrdStatus().getPrdState()
				.getName());
		assertEquals("Grace on all repayments", loanOffering
				.getGracePeriodType().getName());
		assertEquals("Flat", loanOffering.getInterestTypes().getName());
	}

	public void testGetAllLoanOfferings() throws PersistenceException {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		LoanOfferingBO loanOffering1 = createLoanOfferingBO("Loan Offering1",
				"Loa1");
		HibernateUtil.closeSession();

		List<LoanOfferingBO> loanOfferings = new LoanPrdPersistence()
				.getAllLoanOfferings((short) 1);
		assertNotNull(loanOfferings);
		assertEquals(2, loanOfferings.size());
		for (LoanOfferingBO loanOfferingBO : loanOfferings) {
			assertNotNull(loanOfferingBO.getPrdOfferingName());
			assertNotNull(loanOfferingBO.getPrdOfferingId());
			assertNotNull(loanOfferingBO.getPrdStatus().getPrdState().getName());
		}
		HibernateUtil.closeSession();
		TestObjectFactory.removeObject(loanOffering1);
	}

	private LoanOfferingBO createLoanOfferingBO(String prdOfferingName,
			String shortName) {
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		return TestObjectFactory.createLoanOffering(prdOfferingName, shortName,
				ApplicableTo.GROUPS, startDate, 
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, 
				InterestType.FLAT, true, false,
				frequency);
	}

}
