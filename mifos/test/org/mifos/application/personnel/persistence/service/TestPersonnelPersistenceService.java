package org.mifos.application.personnel.persistence.service;

import java.util.List;

import org.mifos.framework.MifosTestCase;

import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class TestPersonnelPersistenceService extends MifosTestCase {
	
	PersonnelPersistenceService persistenceService = null;
	
	public void setUp(){
		persistenceService = new PersonnelPersistenceService();
	}
	public void tearDown() {
		HibernateUtil.closeSession();

	}
	
	public void testActiveLoanOfficersInBranch() throws Exception {
		
		List<PersonnelView> personnels = persistenceService
				.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER,
						Short.valueOf("3"), Short.valueOf("3"),
						PersonnelConstants.LOAN_OFFICER);
		assertEquals(1, personnels.size());
	}

	public void testNonLoanOfficerInBranch() throws Exception {
		List<PersonnelView> personnels = persistenceService
				.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER,
						Short.valueOf("3"), Short.valueOf("2"),
						PersonnelConstants.NON_LOAN_OFFICER);
		assertEquals(1, personnels.size());
	}

}
