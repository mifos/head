package org.mifos.application.rolesandpermission.business;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class TestRoleBO extends MifosTestCase {
	
	private RoleBO roleBO=null;

	public void testGetRole(){
		roleBO=getRole((short)1);
		assertNotNull(roleBO);
		assertEquals("Admin",roleBO.getName());
		assertEquals(155,roleBO.getActivities().size());
	}
	
	private RoleBO getRole(Short id){
		return (RoleBO)HibernateUtil.getSessionTL().get(RoleBO.class,id);
	}
}
