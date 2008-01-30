package org.mifos.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.LookUpLabelEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.framework.ApplicationInitializer;
import org.mifos.framework.hibernate.helper.HibernateUtil;

/*
 * This is a utility class used for dumping lookup label and lookup values
 * from the database in order to create properties files and/or 
 * SQL statements for manipulating these values.  This code may be
 * used only for the initial work which is done and not be useful after 
 * that, but it is being preserved in case it may be useful again.
 */
public class DBMessageDumper {
	private static String SEPARATOR = "-";
	
	public void dumpLabels() {
		List<MifosLookUpEntity> entities=null;
		try
		{
			Session session = HibernateUtil.getSessionTL();
			entities = session.getNamedQuery(
					NamedQueryConstants.GET_ENTITIES).list();

			for (MifosLookUpEntity entity : entities) {
				Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
				for (LookUpLabelEntity label : labels) {
					System.out.println(entity.getEntityType() + ".Label = " + label.getLabelText());
				}
			}

		} finally {
			HibernateUtil.closeSession();	
		}
	}

	public void dumpLookupValues() {
		List<MifosLookUpEntity> entities=null;
		try
		{
		Session session = HibernateUtil.getSessionTL();
		 entities = session.getNamedQuery(
				NamedQueryConstants.GET_ENTITIES).list();
		 
			for (MifosLookUpEntity entity : entities) {
				Set<LookUpValueEntity> values = entity.getLookUpValues();
				List<LookUpValueEntity> valuesList = new ArrayList<LookUpValueEntity>(); 
				valuesList.addAll(values);
				Collections.sort(valuesList, new Comparator<LookUpValueEntity>() {
					public int compare(LookUpValueEntity v1, LookUpValueEntity v2) {
						return v1.getLookUpId().compareTo(v2.getLookUpId());
					}
				});
				//System.out.println("-- Entity: " + entity.getEntityType());
				System.out.println("## Entity: " + entity.getEntityType());
				int index = 0;
				for (LookUpValueEntity lookupValue : valuesList) {
					Set<LookUpValueLocaleEntity> localeValues = lookupValue.getLookUpValueLocales();
					for (LookUpValueLocaleEntity locale : localeValues) {
						if (locale.getLocaleId() == 1) {
							String camelCaseName = StringUtils.deleteWhitespace(WordUtils.capitalize(locale.getLookUpValue().toLowerCase().replaceAll("\\W"," ")));
							
							// for properties files
							System.out.println(lookupValue.getLookUpName() + " = " + locale.getLookUpValue());
							//System.out.println(entity.getEntityType() + SEPARATOR + index++ + "." +  name + " = " + locale.getLookUpValue());
							
							// for latest-data.sql
							//String lookupName = entity.getEntityType() + SEPARATOR + camelCaseName;
							
							// for downgrade
							//String lookupName = " ";
							//System.out.println("INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + 
							//		"VALUES(" + lookupValue.getLookUpId() + ", " + entity.getEntityId() + ", '" + lookupName + "');");
							//System.out.println(entity.getEntityType() + SEPARATOR + lookupValue.getLookUpName() + " = " + locale.getLookUpValue());						

							//System.out.println("UPDATE LOOKUP_VALUE SET LOOKUP_NAME = '" 
							//		+ lookupName + "' WHERE LOOKUP_ID = " + lookupValue.getLookUpId() + ";");
						}
					}
				}
			}

		} finally {
			HibernateUtil.closeSession();	
		}
			
	}

	public static void main(String[] args) {
		/*
		try {
			Class.forName(TestCaseInitializer.class.getName());
		} catch (ClassNotFoundException e) {
			throw new Error("Failed to start up", e);
		}*/
		ApplicationInitializer applicationInitializer = new ApplicationInitializer();
		applicationInitializer.init();
		
		DBMessageDumper dumper = new DBMessageDumper();
		dumper.dumpLookupValues();
	}

}
