package org.mifos.framework.security.activity;

import java.io.IOException;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
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

public class ActivityGenerator {

	private int lookUpId;
	private ActivityEntity activityEntity;

	public int getLookUpId() {
		return lookUpId;
	}

	public void upgradeUsingHQL(short parentActivity,
			String lookUpDescription) throws IOException, HibernateException,
			PersistenceException, ServiceException, ActivityGeneratorException {

		Transaction tx = HibernateUtil.startTransaction();
			insertLookUpValue();
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

	private void insertLookUpValue() throws PersistenceException {

		LookUpValueEntity anLookUp = new LookUpValueEntity();
		MasterPersistence mp = new MasterPersistence();
		MifosLookUpEntity lookUpEntity = (MifosLookUpEntity) mp
				.getPersistentObject(MifosLookUpEntity.class, Short
						.valueOf((short) MifosLookUpEntity.ACTIVITY));
		anLookUp.setLookUpEntity(lookUpEntity);
		mp.createOrUpdate(anLookUp);
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
