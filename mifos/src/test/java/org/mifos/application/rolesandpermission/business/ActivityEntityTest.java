package org.mifos.application.rolesandpermission.business;

import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ActivityEntityTest extends MifosIntegrationTest {
	
	public ActivityEntityTest() throws SystemException, ApplicationException {
        super();
    }

    private ActivityEntity activityEntity=null;

	public void testGetActivity(){
		activityEntity=getActivityEntity(Short.valueOf("1"));
		assertNull(activityEntity.getActivityName());
		assertNull(activityEntity.getDescription());
		activityEntity.setLocaleId(Short.valueOf("1"));
		assertEquals("Organization Management",activityEntity.getActivityName());
		assertEquals("Organization Management",activityEntity.getDescription());
	}

	private ActivityEntity getActivityEntity(Short id) {
		return (ActivityEntity)TestObjectFactory.getObject(ActivityEntity.class,id);
	}

}
