package org.mifos.framework.security.activity;

import java.io.IOException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.RoleActivityEntity;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;

public class ActivityGenerator {

	private int lookUpId;
	private ActivityEntity activityEntity;

	public int getLookUpId() {
		return lookUpId;
	}

	public void upgradeUsingHQL(Session session, short parentActivity,
			String lookUpDescription) throws IOException, HibernateException,
			PersistenceException, ServiceException {

		Transaction tx = session.beginTransaction();
		try {
			insertLookUpValue(session);
			insertLookUpValueLocale(session, lookUpId, lookUpDescription);
			insertActivity(session, parentActivity, lookUpId);
			insertRolesActivity(session);
			tx.commit();
		}
		catch (Exception ex) {
			tx.rollback();
		}

	}

	private void insertRolesActivity(Session session) {
		
		RoleBO role = (RoleBO)session.load(RoleBO.class, (short)RolesAndPermissionConstants.ADMIN_ROLE);
		
	    RoleActivityEntity roleActivityEntity = new RoleActivityEntity(role, activityEntity);
		session.save(roleActivityEntity);
	}

	private void insertActivity(Session session, short parentActivity,
			int lookUpId) throws ServiceException {
		ActivityEntity parentActivityEntity;
		if(parentActivity != 0)
			parentActivityEntity = (ActivityEntity) session.load(
				ActivityEntity.class, parentActivity);
		else
			parentActivityEntity = null;
		LookUpValueEntity lookupValueEntity = (LookUpValueEntity) session.load(
				LookUpValueEntity.class, lookUpId);
		activityEntity = new ActivityEntity(
				(short) calculateDynamicActivityId(), parentActivityEntity,
				lookupValueEntity);
		session.save(activityEntity);
	}

	//	public ActivityEntity getActivity() {
	//		return activityEntity;
	//	}

	private void insertLookUpValueLocale(Session session, int lookUpId,
			String lookUpDescription) {
		LookUpValueLocaleEntity lookUpValueLocaleEntity = new LookUpValueLocaleEntity();
		lookUpValueLocaleEntity.setLookUpId(new Integer(lookUpId));
		lookUpValueLocaleEntity
				.setLocaleId(DatabaseVersionPersistence.ENGLISH_LOCALE);
		lookUpValueLocaleEntity.setLookUpValue(lookUpDescription);
		session.save(lookUpValueLocaleEntity);
	}

	private void insertLookUpValue(Session session) throws PersistenceException {

		//		MifosLookUpEntity lookUpEntity = new MifosLookUpEntity();
		//		lookUpEntity.setEntityId((short) MifosLookUpEntity.ACTIVITY);

		LookUpValueEntity anLookUp = new LookUpValueEntity();
		anLookUp.setLookUpEntity((MifosLookUpEntity) session.load(
				MifosLookUpEntity.class, (short) MifosLookUpEntity.ACTIVITY));
		session.save(anLookUp);
		lookUpId = anLookUp.getLookUpId().intValue();
	}

	public LookUpValueLocaleEntity getLookUpValueLocaleEntity(Session session,
			int lookUpId) {
		Query query = session.createQuery(
				"from LookUpValueLocaleEntity u where u.lookUpId = :anId")
				.setParameter("anId", new Integer(lookUpId));
		List list = query.list();
		if (list == null || list.isEmpty())
			return null;
		else return (LookUpValueLocaleEntity) list.get(0);
	}

	public ActivityEntity getActivityEntity(Session session, int lookUpId) {
		Query query = session
				.createQuery(
						"select u from ActivityEntity u where u.activityNameLookupValues = :anId")
				.setParameter("anId", new Integer(lookUpId));
		List list = query.list();
		if (list == null || list.isEmpty())
			return null;
		else return (ActivityEntity) list.get(0);
	}

	public static int calculateDynamicActivityId() throws ServiceException {
		int activityId = 0;
		for (ActivityEntity activity : new RolesPermissionsBusinessService()
				.getActivities()) {
			if (activity.getId().intValue() < activityId)
				activityId = activity.getId();
		}
		int newActivityId = activityId - 1;
		return newActivityId;
	}


}
