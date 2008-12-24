package org.mifos.framework.components.fieldConfiguration.business;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;



public class TestFieldConfigurationEntity extends MifosTestCase{

	public void testGetFieldConfigurationEntity(){
		FieldConfigurationEntity fieldConfigurationEntity=(FieldConfigurationEntity)HibernateUtil.getSessionTL().get(FieldConfigurationEntity.class,Integer.valueOf("1"));
		assertEquals(fieldConfigurationEntity.getFieldName(),"SecondLastName");
	}
}
