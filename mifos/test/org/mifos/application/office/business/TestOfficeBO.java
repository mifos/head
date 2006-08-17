package org.mifos.application.office.business;

import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OperationMode;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.office.util.valueobjects.ParentOffice;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestOfficeBO extends MifosTestCase {

	UserContext userContext = null;

	@Override
	protected void setUp() throws Exception {
		
		userContext = TestObjectFactory.getUserContext();
	}

	public void testCreateFailureDuplicateName() throws Exception {
		// check officeName
		try {

			new OfficeBO(userContext, OfficeLevel.AREAOFFICE, TestObjectFactory
					.getOffice(Short.valueOf("1")), null, "TestAreaOffice ",
					"ABCD", null, OperationMode.REMOTE_SERVER);
		} catch (OfficeException e) {
			assertEquals(OfficeConstants.OFFICENAMEEXIST, e.getKey());

		}

	}

	public void testCreateFailureDuplicateShortName() throws Exception {
		// check short name
		try {

			new OfficeBO(userContext, OfficeLevel.AREAOFFICE, TestObjectFactory
					.getOffice(Short.valueOf("1")), null, "abcd", "mif2", null,
					OperationMode.REMOTE_SERVER);
		} catch (OfficeException e) {
			assertEquals(OfficeConstants.OFFICESHORTNAMEEXIST, e.getKey());

		}

	}
	public void testCreateSucess() throws Exception {
		// check short name
		 OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));
		 OfficeBO officeBO=	new OfficeBO(userContext, OfficeLevel.AREAOFFICE,parent , null, "abcd", "abcd", null,
					OperationMode.REMOTE_SERVER);
		 officeBO.save();
		 
		 TestObjectFactory.flushandCloseSession();
		 officeBO = TestObjectFactory.getOffice(officeBO.getOfficeId());
		 assertEquals("1.1.2", officeBO.getSearchId());
		 assertEquals("abcd",officeBO.getOfficeName());
		 assertEquals("abcd",officeBO.getShortName());
		 assertEquals(OperationMode.REMOTE_SERVER,officeBO.getMode());
		 TestObjectFactory.cleanUp(officeBO);
		 
	}
}
