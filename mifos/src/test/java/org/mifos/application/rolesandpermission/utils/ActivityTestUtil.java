/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
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
