package org.mifos.application.office.business.service;

import java.util.List;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestOfficeBusinessService extends MifosTestCase {
	private OfficeBusinessService officeBusinessService = new OfficeBusinessService();

	public void testGetActiveParents() throws NumberFormatException,
			ServiceException {
		List<OfficeView> parents = officeBusinessService.getActiveParents(
				OfficeLevel.BRANCHOFFICE, Short.valueOf("1"));
		assertEquals(2, parents.size());
		for (OfficeView view : parents) {
			if (view.getLevelId().equals(OfficeLevel.HEADOFFICE))
				assertEquals("Head Office", view.getLevelName());
			else if (view.getLevelId().equals(OfficeLevel.AREAOFFICE))
				assertEquals("Area Office", view.getLevelName());
		}
	}

	public void testGetActiveLevels() throws NumberFormatException,
			ServiceException {

		assertEquals(4, officeBusinessService.getConfiguredLevels(
				Short.valueOf("1")).size());

	}

	public void testGetOffice() throws Exception {
		assertNotNull(officeBusinessService.getOffice(Short.valueOf("1")));
	}

	public void testGetStatusList() throws NumberFormatException,
			ServiceException {
		assertEquals(2, officeBusinessService.getStatusList(Short.valueOf("1"))
				.size());
	}

	public void testGetBranchOffices() throws ServiceException {
		assertEquals(1, officeBusinessService.getBranchOffices().size());
	}

	public void testGetOfficesTillBranchOffice() throws ServiceException {
		assertEquals(2, officeBusinessService.getOfficesTillBranchOffice()
				.size());
	}

	public void testGetChildOffices() throws ServiceException {
		OfficeBO headOffice = TestObjectFactory.getOffice(Short.valueOf("1"));
		List<OfficeView> officeList = officeBusinessService
				.getChildOffices(headOffice.getSearchId());
		assertEquals(3, officeList.size());
		officeList = null;
		headOffice = null;
	}
}
