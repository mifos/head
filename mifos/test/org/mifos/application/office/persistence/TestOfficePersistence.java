package org.mifos.application.office.persistence;

import java.util.List;

import junit.framework.TestCase;

import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.HierarchyManager;

public class TestOfficePersistence extends TestCase {
	private OfficePersistence officePersistence = new OfficePersistence();
	public void setUp() throws Exception {
		HierarchyManager.getInstance().init();
	}
	public void tearDown() {
		HibernateUtil.closeSession();

	}
	public void  testGetActiveBranchesForHO(){
		List <OfficeView> officeList = officePersistence.getActiveOffices(Short.valueOf("1"));
		assertEquals(1 ,officeList.size()) ;
	}
	
	public void  testGetActiveBranchs(){
		List <OfficeView> officeList = officePersistence.getActiveOffices(Short.valueOf("3"));
		assertEquals(1 ,officeList.size()) ;
	}
	
	public void testGetAllOffices()throws Exception{
		assertEquals(Integer.valueOf("3").intValue(),officePersistence.getAllOffices().size());
	}
}
