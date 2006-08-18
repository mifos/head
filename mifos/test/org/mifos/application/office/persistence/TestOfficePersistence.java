package org.mifos.application.office.persistence;

import java.util.List;

import org.mifos.framework.MifosTestCase;

import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.HierarchyManager;

public class TestOfficePersistence extends MifosTestCase {
	private OfficePersistence officePersistence = new OfficePersistence();

	public void setUp() throws Exception {
		HierarchyManager.getInstance().init();
	}

	public void tearDown() {
		HibernateUtil.closeSession();

	}

	public void testGetActiveBranchesForHO() {
		List<OfficeView> officeList = officePersistence.getActiveOffices(Short
				.valueOf("1"));
		assertEquals(1, officeList.size());
	}

	public void testGetActiveBranchs() {
		List<OfficeView> officeList = officePersistence.getActiveOffices(Short
				.valueOf("3"));
		assertEquals(1, officeList.size());
	}

	public void testGetAllOffices() throws Exception {
		assertEquals(Integer.valueOf("3").intValue(), officePersistence
				.getAllOffices().size());
	}

	public void testGetMaxOfficeId() {
		assertEquals(3, officePersistence.getMaxOfficeId().intValue());
	}

	public void testGetChildCount() {

		assertEquals(1, officePersistence.getChildCount(Short.valueOf("1"))
				.intValue());
	}

	public void testIsOfficeNameExist() {
		assertTrue(officePersistence.isOfficeNameExist("TestAreaOffice "));
	}

	public void testIsOfficeShortNameExist() {
		assertTrue(officePersistence.isOfficeShortNameExist("MIF2"));
	}

	public void testGetCountActiveChildern() {
		assertTrue(officePersistence.hasActiveChildern(Short.valueOf("1")));
	}

	public void testGetCountActivePeronnel() {
		assertTrue(officePersistence.hasActivePeronnel(Short.valueOf("1")));
	}

	public void testGetActiveParents() {
		List<OfficeView> parents = officePersistence.getActiveParents(
				OfficeLevel.BRANCHOFFICE, Short.valueOf("1"));
		assertEquals(2, parents.size());
		for (OfficeView view : parents) {

			if (view.getLevelId().equals(OfficeLevel.HEADOFFICE))
				assertEquals("Head Office", view.getLevelName());
			else if (view.getLevelId().equals(OfficeLevel.AREAOFFICE))
				assertEquals("Area Office", view.getLevelName());
		}

	}
	public void testGetActiveLevels(){
		
		assertEquals(4,officePersistence.getActiveLevels(Short.valueOf("1")).size());
		
	}
}
