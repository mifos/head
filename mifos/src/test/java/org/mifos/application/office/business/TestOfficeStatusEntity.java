package org.mifos.application.office.business;

import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class TestOfficeStatusEntity extends MifosTestCase {
	
	public TestOfficeStatusEntity() throws SystemException, ApplicationException {
        super();
    }

    public void testGetOfficeStatus()throws Exception{
		
		OfficeStatusEntity officeStatus = new OfficeStatusEntity(OfficeStatus.ACTIVE);
		
		assertEquals(OfficeStatus.ACTIVE,officeStatus.getStatus());
	}

}
