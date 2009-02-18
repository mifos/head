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

package org.mifos.framework.security.activity;

import java.io.IOException;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.RoleActivityEntity;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.application.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.security.activity.DynamicLookUpValueCreationTypes;
import org.mifos.framework.util.helpers.StringUtils;


public class ActivityGenerator {

	private int lookUpId;
	private ActivityEntity activityEntity;

	public int getLookUpId() {
		return lookUpId;
	}

	public void upgradeUsingHQL(DynamicLookUpValueCreationTypes type, short parentActivity,
			String lookUpDescription) throws IOException, HibernateException,
			PersistenceException, ServiceException, ActivityGeneratorException {

		Transaction tx = HibernateUtil.startTransaction();
			insertLookUpValue(type, lookUpDescription);
			insertLookUpValueLocale(lookUpId, lookUpDescription);
			insertActivity(parentActivity, lookUpId);
			insertRolesActivity();
			tx.commit();
	}

	private void insertRolesActivity() throws PersistenceException {
		RolesPermissionsPersistence rpp = new RolesPermissionsPersistence();
		RoleBO role = (RoleBO) rpp.getPersistentObject(RoleBO.class,
				(short) RolesAndPermissionConstants.ADMIN_ROLE);

		RoleActivityEntity roleActivityEntity = new RoleActivityEntity(role,
				activityEntity);
		rpp.createOrUpdate(roleActivityEntity);
	}

	private void insertActivity(short parentActivity, int lookUpId)
			throws ServiceException, ActivityGeneratorException,
			PersistenceException {
		ActivityEntity parentActivityEntity;
		RolesPermissionsPersistence rpp = new RolesPermissionsPersistence();
		if (parentActivity != 0)
			parentActivityEntity = (ActivityEntity) rpp.getPersistentObject(
					ActivityEntity.class, parentActivity);
		else parentActivityEntity = null;
		LookUpValueEntity lookupValueEntity = (LookUpValueEntity) rpp
				.getPersistentObject(LookUpValueEntity.class, lookUpId);
		activityEntity = new ActivityEntity(
				(short) calculateDynamicActivityId(), parentActivityEntity,
				lookupValueEntity);
		rpp.createOrUpdate(activityEntity);
	}


	private void insertLookUpValueLocale(int lookUpId, String lookUpDescription)
			throws PersistenceException {
		MasterPersistence mp = new MasterPersistence();
		LookUpValueLocaleEntity lookUpValueLocaleEntity = new LookUpValueLocaleEntity();
		lookUpValueLocaleEntity.setLookUpId(new Integer(lookUpId));
		lookUpValueLocaleEntity
				.setLocaleId(DatabaseVersionPersistence.ENGLISH_LOCALE);
		lookUpValueLocaleEntity.setLookUpValue(lookUpDescription);
		mp.createOrUpdate(lookUpValueLocaleEntity);
	
	}

	private void insertLookUpValue(DynamicLookUpValueCreationTypes type, String lookUpDescription) throws PersistenceException {

		LookUpValueEntity anLookUp = new LookUpValueEntity();
		MasterPersistence mp = new MasterPersistence();
		MifosLookUpEntity lookUpEntity = (MifosLookUpEntity) mp
				.getPersistentObject(MifosLookUpEntity.class, Short
						.valueOf((short) MifosLookUpEntity.ACTIVITY));
		String lookupName = StringUtils.generateLookupName(type.name(), lookUpDescription);
		anLookUp.setLookUpName(lookupName);
		anLookUp.setLookUpEntity(lookUpEntity);
		mp.createOrUpdate(anLookUp);
		MessageLookup.getInstance().updateLookupValueInCache(lookupName, lookUpDescription);
		lookUpId = anLookUp.getLookUpId().intValue();
	}

	public LookUpValueLocaleEntity getLookUpValueLocaleEntity(short localId, int lookUpId)
			throws PersistenceException {
		MasterPersistence mp = new MasterPersistence();
		return mp.retrieveOneLookUpValueLocaleEntity(localId, lookUpId);
	}

	public ActivityEntity getActivityEntity(int lookUpId)
			throws PersistenceException {
		RolesPermissionsPersistence rpp = new RolesPermissionsPersistence();
		return rpp.retrieveOneActivityEntity(lookUpId);
	}

	public static int calculateDynamicActivityId() throws ServiceException,
			ActivityGeneratorException {
		int activityId = 0;
		for (ActivityEntity activity : new RolesPermissionsBusinessService()
				.getActivities()) {
			if (activity.getId().intValue() < activityId)
				activityId = activity.getId();
		}
		if (activityId <= Short.MIN_VALUE)
			throw new ActivityGeneratorException();
		int newActivityId = activityId - 1;

		return newActivityId;
	}

	public static void reparentActivityUsingHibernate(short activityId,
			Short newParent) throws PersistenceException {
		RolesPermissionsPersistence rpp = new RolesPermissionsPersistence();
		ActivityEntity parent = (ActivityEntity) rpp.getPersistentObject(
				ActivityEntity.class, newParent);
		ActivityEntity activity = (ActivityEntity) rpp.getPersistentObject(
				ActivityEntity.class, activityId);
		activity.setParent(parent);
		rpp.createOrUpdate(activity);
	}

	public static void changeActivityMessage(short activityId, short localeId, String newMessage) throws PersistenceException {
		RolesPermissionsPersistence rpp = new RolesPermissionsPersistence();
		MasterPersistence mp = new MasterPersistence();
		ActivityEntity activityEntity = (ActivityEntity) rpp
				.getPersistentObject(ActivityEntity.class, Short
						.valueOf(activityId));
		Integer lookUpId = activityEntity.getActivityNameLookupValues()
				.getLookUpId();
		LookUpValueLocaleEntity lookUpValueLocaleEntity = mp
				.retrieveOneLookUpValueLocaleEntity(localeId, lookUpId);
		lookUpValueLocaleEntity.setLookUpValue(newMessage);
		mp.createOrUpdate(lookUpValueLocaleEntity);
	}


}
