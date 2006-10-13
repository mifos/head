package org.mifos.application.office.persistence;

import java.util.List;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestOfficePersistence extends MifosTestCase {
	private OfficePersistence officePersistence = new OfficePersistence();

	@Override
	public void setUp() throws Exception {
	}

	@Override
	public void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetActiveBranchesForHO() throws Exception {
		List<OfficeView> officeList = officePersistence.getActiveOffices(Short
				.valueOf("1"));
		assertEquals(1, officeList.size());
	}

	public void testGetActiveBranchs() throws Exception {
		List<OfficeView> officeList = officePersistence.getActiveOffices(Short
				.valueOf("3"));
		assertEquals(1, officeList.size());
	}

	public void testGetAllOffices() throws Exception {
		assertEquals(Integer.valueOf("3").intValue(), officePersistence
				.getAllOffices().size());
	}

	public void testGetMaxOfficeId() throws Exception {
		assertEquals(3, officePersistence.getMaxOfficeId().intValue());
	}

	public void testGetChildCount() throws Exception{

		assertEquals(1, officePersistence.getChildCount(Short.valueOf("1"))
				.intValue());
	}

	public void testIsOfficeNameExist() throws Exception {
		assertTrue(officePersistence.isOfficeNameExist("TestAreaOffice "));
	}

	public void testIsOfficeShortNameExist() throws Exception {
		assertTrue(officePersistence.isOfficeShortNameExist("MIF2"));
	}

	public void testGetCountActiveChildern() throws Exception{
		assertTrue(officePersistence.hasActiveChildern(Short.valueOf("1")));
	}

	public void testGetCountActivePeronnel() throws Exception {
		assertTrue(officePersistence.hasActivePeronnel(Short.valueOf("1")));
	}

	public void testGetActiveParents() throws Exception{
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

	public void testGetActiveLevels() throws Exception{

		assertEquals(4, officePersistence.getActiveLevels(Short.valueOf("1"))
				.size());

	}

	public void testGetActiveLevelsFailure() throws Exception {

		assertEquals(null, officePersistence.getActiveLevels(Short
				.valueOf("-1")));

	}

	public void testGetStatusList() throws Exception {
		assertEquals(2, officePersistence.getStatusList(Short.valueOf("1"))
				.size());
	}

	public void testGetStatusListFailure()throws Exception{
		assertEquals(null, officePersistence.getStatusList(Short.valueOf("-1")));
	}

	public void testGetChildern() throws Exception{
		assertEquals(1, officePersistence.getChildern(Short.valueOf("1"))
				.size());
	}

	public void testGetChildern_failure() throws Exception {
		assertEquals(null, officePersistence.getChildern(Short.valueOf("-1")));
	}

	public void testGetSearchId() throws Exception {
		assertEquals("1.1", officePersistence.getSearchId(Short.valueOf("1")));
	}

	public void testIsBranchInactive()throws Exception {
		assertFalse(officePersistence.isBranchInactive(Short.valueOf("3")));
	}

	public void testGetBranchOffices() throws Exception {
		assertEquals(1, officePersistence.getBranchOffices().size());
	}

	public void testGetOfficesTillBranchOffice() throws Exception {
		assertEquals(2, officePersistence.getOfficesTillBranchOffice().size());
	}

	public void testGetOfficesTillBranchOfficeActive()throws Exception {
		assertEquals(2, officePersistence.getOfficesTillBranchOffice("1.1")
				.size());
	}

	public void testGetBranchParents() throws Exception {
		List<OfficeBO> officeList = officePersistence.getBranchParents("1.1");
		assertEquals(1, officeList.size());
		assertEquals(1, ((OfficeBO) officeList.get(0)).getChildren().size());
		officeList = null;
	}

	public void testGetChildOffices() throws Exception {
		OfficeBO headOffice = TestObjectFactory.getOffice(Short.valueOf("1"));
		List<OfficeView> officeList = officePersistence
				.getChildOffices(headOffice.getSearchId());
		assertEquals(3, officeList.size());
		officeList = null;
		headOffice = null;
	}
	public void testGetBranchesUnderUser()throws Exception{
		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));
		OfficeBO branchOffice = TestObjectFactory.createOffice(
				OfficeLevel.BRANCHOFFICE, parent, "abcd", "abcd");

		List<OfficeBO> officeList =officePersistence.getActiveBranchesUnderUser("1.1");
		assertNotNull(officeList);
		assertEquals(2,officeList.size());
		assertEquals(branchOffice.getOfficeName(),officeList.get(0).getOfficeName());
		assertEquals("TestBranchOffice",officeList.get(1).getOfficeName());
		TestObjectFactory.cleanUp(branchOffice);

		
		
	}
	
}
