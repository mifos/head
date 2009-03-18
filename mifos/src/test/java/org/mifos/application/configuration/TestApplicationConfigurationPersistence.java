package org.mifos.application.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.configuration.persistence.ApplicationConfigurationPersistence;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.master.business.LookUpLabelEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.components.configuration.util.helpers.ConfigConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;



public class TestApplicationConfigurationPersistence extends MifosIntegrationTest {
	
	public TestApplicationConfigurationPersistence() throws SystemException, ApplicationException {
        super();
    }
    ApplicationConfigurationPersistence configurationPersistence ;
	
	@Override
	protected void setUp() throws Exception {
		configurationPersistence= new ApplicationConfigurationPersistence();
	}

	/* 
	 * Check that we can can retrieve LookupEntities and enforce the conventions:
	 * LookupEntity names cannot contain whitespace
	 * LookupEntities should not have more than 1 label
	 */
	public void testGetLookupEntities(){
		List<MifosLookUpEntity> entities = configurationPersistence.getLookupEntities();
		assertNotNull(entities);
		 
		// Enforce that no entity names contain whitespace
		for (MifosLookUpEntity entity : entities) {
			assertEquals(StringUtils.deleteWhitespace(entity.getEntityType()), entity.getEntityType());
			
			Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
			
			// Enforce that each entity has 0 or 1 labels and not more
			assertTrue(labels.size() <= 1);
			for (LookUpLabelEntity label : labels) {
				if (entity.getEntityType().equals("Client")) assertEquals("Client",label.getLabelText());
			}
		}

	}
	public void testGetLookupValues(){
		assertNotNull(configurationPersistence.getLookupValues());
	}
	
	/*
	 * 2007/12/24 Code in progress to test dumping of database strings to properties files.
	 */
/*
	public void testDump() {
		List<MifosLookUpEntity> entities=null;
		try
		{
		Session session = StaticHibernateUtil.getSessionTL();
		 entities = session.getNamedQuery(
				NamedQueryConstants.GET_ENTITIES).list();
		 
			for (MifosLookUpEntity entity : entities) {
				Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
				for (LookUpLabelEntity label : labels) {
					System.out.println(entity.getEntityType() + ".Label = " + label.getLabelText());
				}
			}

//			for (MifosLookUpEntity entity : entities) {
//				Set<LookUpValueEntity> values = entity.getLookUpValues();
//				List<LookUpValueEntity> valuesList = new ArrayList<LookUpValueEntity>(); 
//				valuesList.addAll(values);
//				Collections.sort(valuesList, new Comparator<LookUpValueEntity>() {
//					public int compare(LookUpValueEntity v1, LookUpValueEntity v2) {
//						return v1.getLookUpId().compareTo(v2.getLookUpId());
//					}
//				});
//				
//				int index = 0;
//				for (LookUpValueEntity lookupValue : valuesList) {
//					Set<LookUpValueLocaleEntity> localeValues = lookupValue.getLookUpValueLocales();
//					for (LookUpValueLocaleEntity locale : localeValues) {
//						if (locale.getLocaleId() == 1) {
//							String name = StringUtils.deleteWhitespace(WordUtils.capitalize(locale.getLookUpValue().toLowerCase().replaceAll("\\W"," ")));
//							//System.out.println(entity.getEntityType() + "." + index++ + "." +  name + " = " + locale.getLookUpValue());						
//							System.out.println(entity.getEntityType() + "." + lookupValue.getLookUpName() + " = " + locale.getLookUpValue());						
//						}
//					}
//				}
//			}

		} finally {
			StaticHibernateUtil.closeSession();	
		}
			
	}
*/
}

