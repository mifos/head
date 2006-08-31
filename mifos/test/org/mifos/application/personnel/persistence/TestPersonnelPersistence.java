package org.mifos.application.personnel.persistence;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class TestPersonnelPersistence extends MifosTestCase {

	PersonnelPersistence persistence;

	public void setUp() {
		persistence = new PersonnelPersistence();
	}

	public void tearDown() {
		HibernateUtil.closeSession();

	}

	public void testActiveLoanOfficersInBranch() throws Exception {
		List<PersonnelView> personnels = persistence
				.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER,
						Short.valueOf("3"), Short.valueOf("3"),
						PersonnelConstants.LOAN_OFFICER);
		assertEquals(1, personnels.size());
	}

	public void testNonLoanOfficerInBranch() throws Exception {
		List<PersonnelView> personnels = persistence
				.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER,
						Short.valueOf("3"), Short.valueOf("2"),
						PersonnelConstants.NON_LOAN_OFFICER);
		assertEquals(1, personnels.size());
	}

	public void testIsUserExistSucess() {
		assertTrue(persistence.isUserExist("mifos"));
	}

	public void testIsUserExistFailure() {
		assertFalse(persistence.isUserExist("XXX"));
	}

	public void testIsUserExistWithGovernmentIdSucess() {
		assertTrue(persistence.isUserExistWithGovernmentId("123"));
	}

	public void testIsUserExistWithGovernmentIdFailure() {
		assertFalse(persistence.isUserExistWithGovernmentId("XXX"));
	}

	public void testIsUserExistWithDobAndDisplayNameSucess() throws Exception {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		assertTrue(persistence.isUserExist("mifos", dateFormat
				.parse("1979-12-12")));
	}

	public void testIsUserExistWithDobAndDisplayNameFailure() {
		assertFalse(persistence.isUserExist("mifos", new GregorianCalendar(
				1989, 12, 12, 0, 0, 0).getTime()));
	}
}
