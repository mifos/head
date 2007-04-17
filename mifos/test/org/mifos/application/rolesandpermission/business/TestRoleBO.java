package org.mifos.application.rolesandpermission.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.mifos.application.rolesandpermission.RoleTestUtil;
import org.mifos.application.rolesandpermission.exceptions.RolesPermissionException;
import org.mifos.application.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestRoleBO extends MifosTestCase {

	public void testGetRole() throws Exception {
		RolesPermissionsPersistence persistence = 
			new RolesPermissionsPersistence();
		RoleBO roleBO = persistence.getRole("Admin");
		assertNotNull(roleBO);
		assertEquals("Admin",roleBO.getName());
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITIES_FOR_ROLE, roleBO.getActivities().size());
	}

	public void testCreateRole() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
		RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(),"Test Role",activities);
		roleBO.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		roleBO = RolesPermissionsPersistence.getRole("Test Role");
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,roleBO.getActivities().size());
		assertEquals(roleBO.getCreatedBy(),Short.valueOf("1"));
		assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(),DateUtils.getDateWithoutTimeStamp(roleBO.getCreatedDate().getTime()));

		RolesPermissionsPersistence.delete(roleBO);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		roleBO = RolesPermissionsPersistence.getRole("Test Role");
		assertNull(roleBO);
	}

	public void testCreateFailureWhenRoleNameIsNull() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
		RoleBO roleBO = null;
		try{
			roleBO = new RoleBO(TestObjectFactory.getContext(),null,activities);
			roleBO.save();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			fail();
		}catch(RolesPermissionException e){
			assertEquals(RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED,e.getKey());
		}

		try{
			roleBO = new RoleBO(TestObjectFactory.getContext(),"",activities);
			roleBO.save();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			fail();
		}catch(RolesPermissionException e){
			assertEquals(RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED,e.getKey());
		}
	}

	public void testCreateFailureForEmptyRoleName() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
		RoleBO roleBO = null;
		try{
			roleBO = new RoleBO(TestObjectFactory.getContext(),"",activities);
			roleBO.save();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			fail();
		}catch(RolesPermissionException e){
			assertEquals(RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED,e.getKey());
		}
	}

	public void testCreateFailureForDuplicateRoleName() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
		RoleBO roleBO = null;
		try{
			roleBO = new RoleBO(TestObjectFactory.getContext(),"Admin",activities);
			roleBO.save();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			fail();
		}catch(RolesPermissionException e){
			assertEquals(RolesAndPermissionConstants.KEYROLEALREADYEXIST,e.getKey());
		}
	}

	public void testCreateFailureForNullActivities() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
		RoleBO roleBO = null;
		try{
			roleBO = new RoleBO(TestObjectFactory.getContext(),"Test Role",null);
			roleBO.save();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			fail();
		}catch(RolesPermissionException e){
			assertEquals(RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES,e.getKey());
		}
	}

	public void testCreateFailureForEmptyActivities() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
		RoleBO roleBO = null;
		try{
			roleBO = new RoleBO(TestObjectFactory.getContext(),"Test Role",new ArrayList<ActivityEntity>());
			roleBO.save();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			fail();
		}catch(RolesPermissionException e){
			assertEquals(RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES,e.getKey());
		}
	}

	public void testDeleteRole() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
		RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(),"Test Role",activities);
		roleBO.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		roleBO = RolesPermissionsPersistence.getRole("Test Role");
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,roleBO.getActivities().size());
		assertEquals(roleBO.getCreatedBy(),Short.valueOf("1"));
		assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(),DateUtils.getDateWithoutTimeStamp(roleBO.getCreatedDate().getTime()));
		roleBO.delete();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		roleBO = RolesPermissionsPersistence.getRole("Test Role");
		assertNull(roleBO);
	}


	public void testDeleteRoleFailureForRoleAssignedToPersonnel() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		RoleBO roleBO = RolesPermissionsPersistence.getRole("Admin");
		try{
			roleBO.delete();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			fail();
		}catch(RolesPermissionException e){
			assertEquals(RolesAndPermissionConstants.KEYROLEASSIGNEDTOPERSONNEL,e.getKey());
		}finally{
			roleBO = RolesPermissionsPersistence.getRole("Admin");
			assertNotNull(roleBO);
		}
	}

	public void testUpdateRemoveActivities() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
		RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(),"Test Role",activities);
		roleBO.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		roleBO = RolesPermissionsPersistence.getRole("Test Role");
		ActivityEntity activity_1=roleBO.getActivities().get(0);
		for (Iterator<ActivityEntity> iter = activities.iterator(); iter.hasNext();) {
			ActivityEntity activityEntity =  iter.next();
			if(activityEntity.getId().equals(activity_1.getId()))
				iter.remove();
		}

		ActivityEntity activity_2=roleBO.getActivities().get(1);
		for (Iterator<ActivityEntity> iter = activities.iterator(); iter.hasNext();) {
			ActivityEntity activityEntity =  iter.next();
			if(activityEntity.getId().equals(activity_2.getId()))
				iter.remove();
		}
		roleBO.update(TestObjectFactory.getUserContext().getId(),"Test Role",activities);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		roleBO = RolesPermissionsPersistence.getRole("Test Role");

		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT - 2,
			roleBO.getActivities().size());
		assertEquals(roleBO.getUpdatedBy(),Short.valueOf("1"));
		assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(),
			DateUtils.getDateWithoutTimeStamp(roleBO.getUpdatedDate().getTime()));
		RoleActivityEntity roleActivityEntity = getRoleActivity(roleBO.getId(),activity_1.getId());
		assertNull(roleActivityEntity);
		roleActivityEntity = getRoleActivity(roleBO.getId(),activity_2.getId());
		assertNull(roleActivityEntity);

		roleBO.delete();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
	}


	public void testUpdateAddingActivities() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
		ActivityEntity activity_1=activities.get(0);
		activities.remove(0);
		ActivityEntity activity_2=activities.get(1);
		activities.remove(1);
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT - 2, activities.size());
		RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(),"Test Role",activities);
		roleBO.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		roleBO = RolesPermissionsPersistence.getRole("Test Role");
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT - 2,roleBO.getActivities().size());
		RoleActivityEntity roleActivityEntity = getRoleActivity(roleBO.getId(),activity_1.getId());
		assertNull(roleActivityEntity);
		roleActivityEntity = getRoleActivity(roleBO.getId(),activity_2.getId());
		assertNull(roleActivityEntity);

		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT - 2, activities.size());
		activities.add(activity_1);
		activities.add(activity_2);
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());

		roleBO.update(TestObjectFactory.getUserContext().getId(),"Test Role",activities);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		roleBO = RolesPermissionsPersistence.getRole("Test Role");

		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,roleBO.getActivities().size());
		assertEquals(roleBO.getUpdatedBy(),Short.valueOf("1"));
		assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(),DateUtils.getDateWithoutTimeStamp(roleBO.getUpdatedDate().getTime()));
		roleActivityEntity = getRoleActivity(roleBO.getId(),activity_1.getId());
		assertNotNull(roleActivityEntity);
		roleActivityEntity = getRoleActivity(roleBO.getId(),activity_2.getId());
		assertNotNull(roleActivityEntity);

		roleBO.delete();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
	}

	public void testUpdateForChangingName() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
		RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(),"Test Role",activities);
		roleBO.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		roleBO = RolesPermissionsPersistence.getRole("Test Role");
		roleBO.update(TestObjectFactory.getUserContext().getId(),"New role",activities);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		roleBO = RolesPermissionsPersistence.getRole("New role");

		assertEquals("New role",roleBO.getName());
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,roleBO.getActivities().size());
		assertEquals(roleBO.getUpdatedBy(),Short.valueOf("1"));
		assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(),DateUtils.getDateWithoutTimeStamp(roleBO.getUpdatedDate().getTime()));

		roleBO.delete();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
	}


	public void testUpdateFailureForDuplicateName() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
		RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(),"Test Role",activities);
		roleBO.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		roleBO = RolesPermissionsPersistence.getRole("Test Role");
		try{
			roleBO.update(TestObjectFactory.getUserContext().getId(),"Admin",activities);
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			fail();
		}catch(RolesPermissionException e){
			assertEquals(RolesAndPermissionConstants.KEYROLEALREADYEXIST,e.getKey());
		}finally{
			roleBO.delete();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
		}
	}

	public void testUpdateFailureForRoleNameAsNull() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
		RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(),"Test Role",activities);
		roleBO.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		roleBO = RolesPermissionsPersistence.getRole("Test Role");
		try{
			roleBO.update(TestObjectFactory.getUserContext().getId(),null,activities);
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			fail();
		}catch(RolesPermissionException e){
			assertEquals(RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED,e.getKey());
		}finally{
			roleBO.delete();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
		}
	}

	public void testUpdateFailureForRoleNameAsEmptyString() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
		RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(),"Test Role",activities);
		roleBO.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		roleBO = RolesPermissionsPersistence.getRole("Test Role");
		try{
			roleBO.update(TestObjectFactory.getUserContext().getId(),"",activities);
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			fail();
		}catch(RolesPermissionException e){
			assertEquals(RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED,e.getKey());
		}finally{
			roleBO.delete();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
		}
	}

	public void testUpdateFailureForNullActivities() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
		RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(),"Test Role",activities);
		roleBO.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		roleBO = RolesPermissionsPersistence.getRole("Test Role");
		try{
			roleBO.update(TestObjectFactory.getUserContext().getId(),"Test Role",null);
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			fail();
		}catch(RolesPermissionException e){
			assertEquals(RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES,e.getKey());
		}finally{
			roleBO.delete();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
		}
	}


	public void testUpdateFailureForEmptyActivities() throws Exception{
		RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
		assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
		RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(),"Test Role",activities);
		roleBO.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		roleBO = RolesPermissionsPersistence.getRole("Test Role");
		try{
			roleBO.update(TestObjectFactory.getUserContext().getId(),"Test Role",new ArrayList<ActivityEntity>());
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			fail();
		}catch(RolesPermissionException e){
			assertEquals(RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES,e.getKey());
		}finally{
			roleBO.delete();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
		}
	}


	private RoleActivityEntity getRoleActivity(Short roleId,
			Short activityId) {
		Query query = HibernateUtil
				.getSessionTL()
				.createQuery(
						"from org.mifos.application.rolesandpermission.business.RoleActivityEntity roleActivity where roleActivity.role=? and roleActivity.activity=?");
		query.setShort(0,roleId);
		query.setShort(1,activityId);
		RoleActivityEntity roleActivityEntity = (RoleActivityEntity) query
				.uniqueResult();
		return roleActivityEntity;
	}

	public void testSaveRoleForInvalidConnection() throws Exception {
		try {
			RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
			List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
			assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT,activities.size());
			RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(),"Test Role",activities);
			TestObjectFactory.simulateInvalidConnection();
			roleBO.save();
			fail();
		} catch (RolesPermissionException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}

	}

}
