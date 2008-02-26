package org.mifos.application.rolesandpermission.business;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ActivityEntityTest extends MifosTestCase {
	
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
