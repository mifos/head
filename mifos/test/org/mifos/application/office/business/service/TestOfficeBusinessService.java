package org.mifos.application.office.business.service;

import java.util.List;

import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.framework.MifosTestCase;

public class TestOfficeBusinessService extends MifosTestCase {
	private OfficeBusinessService officeBusinessService = new OfficeBusinessService();
	public void testGetActiveParents(){
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
	public void testGetActiveLevels(){
		
		assertEquals(4,officeBusinessService.getConfiguredLevels(Short.valueOf("1")).size());
		
	}
	public void testGetOffice()throws Exception{
		assertNotNull(officeBusinessService.getOffice(Short.valueOf("1")));
	}
	public void testGetStatusList(){
		assertEquals(2,officeBusinessService.getStatusList(Short.valueOf("1")).size());
	}

	public void testGetBranchOffices() {
		assertEquals(1,officeBusinessService.getBranchOffices().size());
	}
	
	public void testGetOfficesTillBranchOffice() {
		assertEquals(2,officeBusinessService.getOfficesTillBranchOffice().size());
	}
}
