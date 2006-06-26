package org.mifos.application.personnel.persistence;

import java.util.List;

import junit.framework.TestCase;

import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class TestPersonnelPersistence extends TestCase  {

	PersonnelPersistence persistence;
	
	public void setUp(){
		persistence = new PersonnelPersistence();
	}
	public void tearDown() {
		HibernateUtil.closeSession();

	}
	
	public void testActiveLoanOfficersInBranch()throws Exception{
		persistence = new PersonnelPersistence();
		List<PersonnelView> personnels = persistence.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER ,Short.valueOf("3"),Short.valueOf("3"),PersonnelConstants.LOAN_OFFICER);
		assertEquals(1 , personnels.size());
	}
	
	public void testNonLoanOfficerInBranch()throws Exception{
		persistence = new PersonnelPersistence();
		List<PersonnelView> personnels = persistence.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER ,Short.valueOf("3"),Short.valueOf("2"),PersonnelConstants.NON_LOAN_OFFICER);
		assertEquals(1 , personnels.size());
	}

}
