package org.mifos.application.office.business;

import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.framework.MifosTestCase;

public class TestOfficeStatusEntity extends MifosTestCase {
	
	public void testGetOfficeStatus()throws Exception{
		
		OfficeStatusEntity officeStatus = new OfficeStatusEntity(OfficeStatus.ACTIVE);
		
		assertEquals(OfficeStatus.ACTIVE,officeStatus.getStatus());
	}

}
