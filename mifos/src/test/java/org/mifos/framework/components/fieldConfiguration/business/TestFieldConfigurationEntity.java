package org.mifos.framework.components.fieldConfiguration.business;

import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class TestFieldConfigurationEntity extends MifosIntegrationTest{

	public TestFieldConfigurationEntity() throws SystemException, ApplicationException {
        super();
    }

    public void testGetFieldConfigurationEntity(){
		FieldConfigurationEntity fieldConfigurationEntity=(FieldConfigurationEntity)HibernateUtil.getSessionTL().get(FieldConfigurationEntity.class,Integer.valueOf("1"));
		assertEquals(fieldConfigurationEntity.getFieldName(),"SecondLastName");
	}
}
