package org.mifos.application.office.persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeTemplate;
import org.mifos.application.office.business.OfficeTemplateImpl;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestOfficePersistence extends MifosIntegrationTest {
	public TestOfficePersistence() throws SystemException, ApplicationException {
        super();
    }

    private OfficePersistence officePersistence;

	@Override
	public void setUp() throws Exception {
        officePersistence = new OfficePersistence();
        initializeStatisticsService();
        super.setUp();
    }

	@Override
	public void tearDown() throws Exception {
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

    private OfficePersistence getOfficePersistence() {
        return this.officePersistence;
    }

    public void testCreateOffice() throws Exception {
        long transactionCount = getStatisticsService().getSuccessfulTransactionCount();
        try {
            UserContext userContext = TestUtils.makeUser();
            OfficeTemplate template =
                    OfficeTemplateImpl.createNonUniqueOfficeTemplate(OfficeLevel.BRANCHOFFICE);
            OfficeBO office = getOfficePersistence().createOffice(userContext, template);

            assertNotNull(office.getOfficeId());
            assertTrue(office.isActive());
        }
        finally {
            StaticHibernateUtil.rollbackTransaction();
        }
        assertTrue(transactionCount == getStatisticsService().getSuccessfulTransactionCount());
    }

    public void testCreateOfficeValidationFailure() throws PersistenceException, OfficeException {
        UserContext userContext = TestUtils.makeUser();
        OfficeTemplateImpl template =
                OfficeTemplateImpl.createNonUniqueOfficeTemplate(OfficeLevel.BRANCHOFFICE);
        template.setParentOfficeId(new Short((short) -1));
        try {
            OfficeBO office = getOfficePersistence().createOffice(userContext, template);
            fail("Office " + office.getOfficeName()
            		+ "should not have been successfully created");
        } catch (ValidationException e) {
            // This is what we're expecting here.
            assertTrue(e.getMessage().equals(OfficeConstants.PARENTOFFICE));
        }
        finally {
            StaticHibernateUtil.rollbackTransaction();
        }
    }

    public void testGetActiveBranchesForHO() throws Exception {
		List<OfficeView> officeList = getOfficePersistence().getActiveOffices(Short
				.valueOf("1"));
		assertEquals(1, officeList.size());
	}

	public void testGetActiveBranchs() throws Exception {
		List<OfficeView> officeList = getOfficePersistence().getActiveOffices(Short
				.valueOf("3"));
		assertEquals(1, officeList.size());
	}

	public void testGetAllOffices() throws Exception {
		assertEquals(Integer.valueOf("3").intValue(), getOfficePersistence()
				.getAllOffices().size());
	}

	public void testGetMaxOfficeId() throws Exception {
		assertEquals(3, getOfficePersistence().getMaxOfficeId().intValue());
	}

	public void testGetChildCount() throws Exception{

		assertEquals(1, getOfficePersistence().getChildCount(Short.valueOf("1"))
				.intValue());
	}

	public void testIsOfficeNameExist() throws Exception {
		assertTrue(getOfficePersistence().isOfficeNameExist("TestAreaOffice "));
	}

	public void testIsOfficeShortNameExist() throws Exception {
		assertTrue(getOfficePersistence().isOfficeShortNameExist("MIF2"));
	}

	public void testGetCountActiveChildern() throws Exception{
		assertTrue(getOfficePersistence().hasActiveChildern(Short.valueOf("1")));
	}

	public void testGetCountActivePeronnel() throws Exception {
		assertTrue(getOfficePersistence().hasActivePeronnel(Short.valueOf("1")));
	}

	public void testGetActiveParents() throws Exception{
		List<OfficeView> parents = getOfficePersistence().getActiveParents(
				OfficeLevel.BRANCHOFFICE, Short.valueOf("1"));
		assertEquals(2, parents.size());
		for (OfficeView view : parents) {

			if (view.getLevelId().equals(OfficeLevel.HEADOFFICE.getValue()))
				assertEquals("Head Office", view.getLevelName());
			else if (view.getLevelId().equals(OfficeLevel.AREAOFFICE.getValue()))
				assertEquals("Area Office", view.getLevelName());
		}

	}

	/*
	 * Check that we get the appropriate unordered list of levels back.
	 */
	public void testGetActiveLevels() throws Exception{

		List<OfficeView> officeLevels = getOfficePersistence().getActiveLevels(MasterDataEntity.CUSTOMIZATION_LOCALE_ID); 
		assertEquals(4, officeLevels.size());
		
		Set<String> levels = new HashSet();
		levels.add("Regional Office");
		levels.add("Divisional Office");
		levels.add("Area Office");
		levels.add("Branch Office");

		for (OfficeView level : officeLevels) {
			assertTrue(levels.contains(level.getLevelName()));
		}
	}

	public void testGetStatusList() throws Exception {
		List<OfficeView> officeLevels = getOfficePersistence().getStatusList(MasterDataEntity.CUSTOMIZATION_LOCALE_ID); 
		assertEquals(2, officeLevels.size());
		
		Set<String> levels = new HashSet();
		levels.add("Active");
		levels.add("Inactive");

		for (OfficeView level : officeLevels) {
			assertTrue(levels.contains(level.getLevelName()));
		}
	}

	public void testGetChildern() throws Exception{
		assertEquals(1, getOfficePersistence().getChildern(Short.valueOf("1"))
				.size());
	}

	public void testGetChildern_failure() throws Exception {
		assertEquals(null, getOfficePersistence().getChildern(Short.valueOf("-1")));
	}

	public void testGetSearchId() throws Exception {
		assertEquals("1.1", getOfficePersistence().getSearchId(Short.valueOf("1")));
	}

	public void testIsBranchInactive()throws Exception {
		assertFalse(getOfficePersistence().isBranchInactive(Short.valueOf("3")));
	}

	public void testGetBranchOffices() throws Exception {
		assertEquals(1, getOfficePersistence().getBranchOffices().size());
	}

	public void testGetOfficesTillBranchOffice() throws Exception {
		assertEquals(2, getOfficePersistence().getOfficesTillBranchOffice().size());
	}

	public void testGetOfficesTillBranchOfficeActive()throws Exception {
		assertEquals(2, getOfficePersistence().getOfficesTillBranchOffice("1.1")
				.size());
	}

	public void testGetBranchParents() throws Exception {
		List<OfficeBO> officeList = getOfficePersistence().getBranchParents("1.1");
		assertEquals(1, officeList.size());
		assertEquals(1, officeList.get(0).getChildren().size());
		officeList = null;
	}

	public void testGetChildOffices() throws Exception {
		OfficeBO headOffice = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		List<OfficeView> officeList = getOfficePersistence()
				.getChildOffices(headOffice.getSearchId());
		assertEquals(3, officeList.size());
		officeList = null;
		headOffice = null;
	}

	public void testGetBranchesUnderUser()throws Exception{
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO branchOffice = TestObjectFactory.createOffice(
				OfficeLevel.BRANCHOFFICE, parent, "abcd", "abcd");

		List<OfficeBO> officeList = getOfficePersistence().getActiveBranchesUnderUser("1.1");
		assertNotNull(officeList);
		assertEquals(2,officeList.size());
		assertEquals(branchOffice.getOfficeName(),officeList.get(0).getOfficeName());
		assertEquals("TestBranchOffice",officeList.get(1).getOfficeName());
		TestObjectFactory.cleanUp(branchOffice);
	}
	
	public void testGetAllofficesForCustomFIeld() throws Exception {
		List<OfficeBO> officeList = getOfficePersistence().getAllofficesForCustomFIeld();
		assertNotNull(officeList);
		assertEquals(3, officeList.size());
	}
	
}
