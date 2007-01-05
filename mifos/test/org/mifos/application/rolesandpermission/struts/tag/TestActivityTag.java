package org.mifos.application.rolesandpermission.struts.tag;



import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.framework.MifosTestCase;

public class TestActivityTag extends MifosTestCase {
	

	
	public void testPopulateLocaleID()throws Exception{
		List<ActivityEntity> activities = getActivities();
		new ActivityTag().populateLocaleID(activities,Short.valueOf("1"));
		assertEquals(Short.valueOf("1"),activities.get(0).getLocaleId());
		
	}
	
	public void testConvertToIdSet()throws Exception{
		
		Set<Short> activities =new ActivityTag().convertToIdSet(getActivities());
		assertNotNull(activities);
		assertEquals(178,activities.size());
		
	}
	
	public void testGetActivities() throws Exception{
		
		List<ActivityEntity> activities = 	new ActivityTag().getActivities(getActivities(),getUiActivities());
		assertNotNull(activities);
		assertEquals(2,activities.size());
	}
	private List<ActivityEntity> getActivities() throws Exception{
           return new RolesPermissionsPersistence().getActivities();		
	}
	private Map<String,String> getUiActivities(){
		Map<String,String> uiActivities = new HashMap<String,String>();
		uiActivities.put("1","checkbox");
		uiActivities.put("2","3");
		uiActivities.put("3","4");
		return uiActivities;
	}
}
