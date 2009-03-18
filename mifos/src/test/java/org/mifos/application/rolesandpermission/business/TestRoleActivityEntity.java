package org.mifos.application.rolesandpermission.business;

import org.hibernate.Query;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class TestRoleActivityEntity extends MifosIntegrationTest {

	public TestRoleActivityEntity() throws SystemException, ApplicationException {
        super();
    }

    public void testGetRoleActivity(){
		RoleActivityEntity roleActivityEntity = getRoleActivity((short)1,(short)1);
		assertNull(roleActivityEntity);
		roleActivityEntity = getRoleActivity(Short.valueOf("1"),Short.valueOf("3"));
		assertNotNull(roleActivityEntity);
	}
	
	private RoleActivityEntity getRoleActivity(Short roleId,
			Short activityId) {
		Query query = StaticHibernateUtil
				.getSessionTL()
				.createQuery(
						"from org.mifos.application.rolesandpermission.business.RoleActivityEntity roleActivity where roleActivity.role=? and roleActivity.activity=?");
		query.setShort(0,roleId);
		query.setShort(1,activityId);
		RoleActivityEntity roleActivityEntity = (RoleActivityEntity) query
				.uniqueResult();
		return roleActivityEntity;
	}

}
