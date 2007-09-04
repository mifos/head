package org.mifos.framework.security.activity;

import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.framework.hibernate.helper.HibernateUtil;
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
		lookUpEntity.setEntityId((short)MifosLookUpEntity.ACTIVITY);

		
		short parentId = 13;
		
		int maxLookUpId = findMaxLookUpId(session,lookUpEntity);
		activityGenerator.upgradeUsingHQL(session,parentId, "abcd");
		int lookUpId = activityGenerator.getLookUpId();
		assertEquals(maxLookUpId, lookUpId-1);
		assertEquals("abcd", activityGenerator.getLookUpValueLocaleEntity(session, lookUpId).getLookUpValue());
		assertEquals(ActivityGenerator.calculateDynamicActivityId(), (int)activityGenerator.getActivityEntity(session,lookUpId).getId()-1);
		Query query = session.createQuery("from RoleActivityEntity r where r.activity = :activity and r.role = :role");
		query.setParameter("activity", activityGenerator.getActivityEntity(session,lookUpId));
		RoleBO roleBo = (RoleBO)session.load(RoleBO.class, (short)RolesAndPermissionConstants.ADMIN_ROLE);
		query.setParameter("role", roleBo);
		assertEquals(1, query.list().size());
		
	}
	
	
	private int findMaxLookUpId(Session session, MifosLookUpEntity lookUpEntity){
	
			Query queryForMaxLookUpId = session
					.createQuery("select max(u.lookUpId) from LookUpValueEntity u");
			List list = queryForMaxLookUpId.list();
			if (list==null || list.isEmpty())
				return 0;
			else return ((Integer) list.get(0)).intValue();
		}

}
