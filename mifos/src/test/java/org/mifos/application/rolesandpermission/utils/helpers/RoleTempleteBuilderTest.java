package org.mifos.application.rolesandpermission.utils.helpers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.application.rolesandpermission.util.helpers.RoleTempleteBuilder;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.security.util.ActivityChangeEvent;
import org.mifos.framework.security.util.RoleChangeEvent;

public class RoleTempleteBuilderTest extends MifosTestCase {

	public void testLocaleId() {
		RoleTempleteBuilder roleTempleteBuilder = new RoleTempleteBuilder();
		roleTempleteBuilder.setLocaleId(Short.valueOf("1"));
		assertEquals(Short.valueOf("1"), roleTempleteBuilder.getLocaleId());
	}

	public void testSetCurrentActivites() {
		Set<Short> activities = new HashSet<Short>();
		activities.add(Short.valueOf("1"));
		RoleTempleteBuilder roleTempleteBuilder = new RoleTempleteBuilder();
		roleTempleteBuilder.setCurrentActivites(activities);
		activities = roleTempleteBuilder.getCurrentActivites();
		assertEquals(1, activities.size());
	}

	public void testGetRolesTemplete() throws Exception {
		List<ActivityEntity> activities = new RolesPermissionsBusinessService()
				.getActivities();
		for (ActivityEntity activityEntity : activities)
			activityEntity.setLocaleId(Short.valueOf("1"));
		StringBuilder stringBuilder = new RoleTempleteBuilder()
				.getRolesTemplete(activities);
		assertNotNull(stringBuilder);
		assertTrue(stringBuilder.toString().contains("Can create new role"));
		assertTrue(stringBuilder.toString().contains("Can modify a role"));
		assertTrue(stringBuilder.toString().contains("Can delete a role"));
	}

	public void testActivityChangeEvent() {
		ActivityChangeEvent activityChangeEvent = new ActivityChangeEvent(
				"event", "stringObject");
		assertEquals("value of event", "event", activityChangeEvent
				.getEventType());
		assertEquals("value of object", "stringObject", activityChangeEvent
				.getObject());
	}

	public void testRoleChangeEvent() {
		RoleChangeEvent roleChangeEvent = new RoleChangeEvent("event",
				"stringObject");
		assertEquals("value of event", "event", roleChangeEvent.getEventType());
		assertEquals("value of object", "stringObject", roleChangeEvent
				.getObject());
	}
}
