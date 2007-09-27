package org.mifos.application.rolesandpermission.utils;

import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.framework.exceptions.PersistenceException;

public class ActivityTestUtil {
	/**
	 * This method creates a new activity entity with a given activity id
	 * @return 
	 * @throws PersistenceException 
	 */
	public static ActivityEntity insertActivityForTest(short activityId) throws PersistenceException {
        RolesPermissionsPersistence rpp = new RolesPermissionsPersistence();
		LookUpValueEntity anLookUp = new LookUpValueEntity();
		MasterPersistence mp = new MasterPersistence();
		MifosLookUpEntity lookUpEntity = (MifosLookUpEntity) mp
				.getPersistentObject(MifosLookUpEntity.class, Short
						.valueOf((short) MifosLookUpEntity.ACTIVITY));
		anLookUp.setLookUpEntity(lookUpEntity);
		ActivityEntity parent = (ActivityEntity) mp.getPersistentObject(
				ActivityEntity.class, (short) 13);
		ActivityEntity activityEntity = new ActivityEntity(activityId,
				parent, anLookUp);
		rpp.createOrUpdate(anLookUp);
		rpp.createOrUpdate(activityEntity);
		return activityEntity;
	}
	
	/**
	 * This method delete a given activity entity
	 */
	public static void deleteActivityForTest(ActivityEntity activityEntity)
			throws PersistenceException {
		RolesPermissionsPersistence rpp = new RolesPermissionsPersistence();
		rpp.getSession().clear();
		LookUpValueEntity anLookUp = activityEntity
				.getActivityNameLookupValues();
		rpp.delete(activityEntity);
		rpp.delete(anLookUp);
	}

}
