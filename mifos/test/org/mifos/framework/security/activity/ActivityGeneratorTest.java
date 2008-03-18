package org.mifos.framework.security.activity;

import junit.framework.TestCase;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.application.rolesandpermission.utils.ActivityTestUtil;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.TestCaseInitializer;

public class ActivityGeneratorTest extends TestCase {

	public void testShouldInsertSuccessActivity() throws Exception {

		DatabaseSetup.configureLogging();
		DatabaseSetup.initializeHibernate();
		Class.forName(TestCaseInitializer.class.getName());


		Session session = HibernateUtil.getSessionTL();

		ActivityGenerator activityGenerator = new ActivityGenerator();
		MifosLookUpEntity lookUpEntity = new MifosLookUpEntity();
		lookUpEntity.setEntityId((short) MifosLookUpEntity.ACTIVITY);


		short parentId = 13;

		activityGenerator.upgradeUsingHQL(DynamicLookUpValueCreationTypes.BirtReport, parentId, "abcd");
		int lookUpId = activityGenerator.getLookUpId();
		assertEquals("abcd", activityGenerator.getLookUpValueLocaleEntity(
				DatabaseVersionPersistence.ENGLISH_LOCALE, lookUpId)
				.getLookUpValue());
		assertEquals(ActivityGenerator.calculateDynamicActivityId(),
				(int) activityGenerator.getActivityEntity(lookUpId)
						.getId() - 1);
		Query query = session
				.createQuery("from RoleActivityEntity r where r.activity = :activity and r.role = :role");
		query.setParameter("activity", activityGenerator.getActivityEntity(
				 lookUpId));
		RoleBO roleBo = (RoleBO) session.load(RoleBO.class,
				(short) RolesAndPermissionConstants.ADMIN_ROLE);
		query.setParameter("role", roleBo);
		assertEquals(1, query.list().size());

	}

	public void testShouldSuccessWhenChangeActivityParent()
			throws PersistenceException {
		RolesPermissionsPersistence rpp = new RolesPermissionsPersistence();
		ActivityEntity activity = (ActivityEntity) rpp.getPersistentObject(
				ActivityEntity.class, Short.valueOf((short) 2));
		assertEquals(1, activity.getParent().getId().shortValue());
		ActivityGenerator.reparentActivityUsingHibernate((short) 2, (short) 13);
		activity = (ActivityEntity) rpp.getPersistentObject(
				ActivityEntity.class, Short.valueOf((short) 2));
		assertEquals(13, activity.getParent().getId().shortValue());
		ActivityGenerator.reparentActivityUsingHibernate((short) 2, (short) 1);
	}

	public void testShouldSuccessWhenChangeActivityMessage() throws Exception {
		RolesPermissionsPersistence rpp = new RolesPermissionsPersistence();
		MasterPersistence mp = new MasterPersistence();
		ActivityEntity activityEntity = (ActivityEntity) rpp
				.getPersistentObject(ActivityEntity.class, Short
						.valueOf((short) 3));
		Integer lookUpId = activityEntity.getActivityNameLookupValues()
				.getLookUpId();
		assertEquals(373, lookUpId.intValue());

		short localeId = DatabaseVersionPersistence.ENGLISH_LOCALE;
		LookUpValueLocaleEntity lookUpValueLocaleEntity = mp
				.retrieveOneLookUpValueLocaleEntity(localeId, lookUpId
						.intValue());
		assertEquals("Can create funds", lookUpValueLocaleEntity
				.getLookUpValue());

		ActivityGenerator.changeActivityMessage((short) 3, localeId, "wahaha");
		lookUpValueLocaleEntity = mp.retrieveOneLookUpValueLocaleEntity(
				localeId, lookUpId.intValue());

		assertEquals("wahaha", lookUpValueLocaleEntity.getLookUpValue());
		ActivityGenerator.changeActivityMessage((short) 3, localeId, "Can create funds");

	}
	
	public void testShouldGenerateMinActivityIdWhenCalculate() throws Exception {
		short minActivityId = -32767;
		ActivityEntity activity = ActivityTestUtil.insertActivityForTest(minActivityId);
		assertEquals(minActivityId - 1, ActivityGenerator
				.calculateDynamicActivityId());
		ActivityTestUtil.deleteActivityForTest(activity);
	}
	
}
