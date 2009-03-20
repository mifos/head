/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

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
			Session session = StaticHibernateUtil.getSessionTL();
			entities = session.getNamedQuery(
					NamedQueryConstants.GET_ENTITIES).list();

			for (MifosLookUpEntity entity : entities) {
				Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
				for (LookUpLabelEntity label : labels) {
					System.out.println(entity.getEntityType() + ".Label = " + label.getLabelText());
				}
			}

		} finally {
			StaticHibernateUtil.closeSession();	
		}
	}

	enum DumpType {
		PROPERTIES,
		LOOKUP_VALUE_LOCALE_UPGRADE,
		LOOKUP_VALUE_LOCALE_DOWNGRADE,
		LOOKUP_VALUE_UPGRADE,
		LOOKUP_VALUE_DOWNGRADE
	}
	public void dumpLookupValues(DumpType type) {
		List<MifosLookUpEntity> entities=null;
		try
		{
		Session session = StaticHibernateUtil.getSessionTL();
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
				String commentChars = null;
				if (type == DumpType.PROPERTIES) {
					commentChars = "##";
				} else {
					commentChars = "--";
				}
				System.out.println(commentChars + " Entity: " + entity.getEntityType());
				int index = 0;
				// exclude custom lookup value lists
				Set excludedEntities = new HashSet();
//				excludedEntities.add("Salutation");
//				excludedEntities.add("MaritalStatus");
//				excludedEntities.add("Ethinicity");
//				excludedEntities.add("EducationLevel");
//				excludedEntities.add("Handicapped");
//				excludedEntities.add("PersonnelTitles");
//				excludedEntities.add("CollateralTypes");
//				excludedEntities.add("LoanPurposes");
				if (!excludedEntities.contains(entity.getEntityType())) {
					for (LookUpValueEntity lookupValue : valuesList) {
						Set<LookUpValueLocaleEntity> localeValues = lookupValue.getLookUpValueLocales();
						for (LookUpValueLocaleEntity locale : localeValues) {
							if (locale.getLocaleId() == 1) {
								//String camelCaseName = StringUtils.deleteWhitespace(WordUtils.capitalize(locale.getLookUpValue().toLowerCase().replaceAll("\\W"," ")));
								//String camelCaseName = StringUtils.deleteWhitespace(WordUtils.capitalize(lookupValue.getLookUpName().toLowerCase().replaceAll("\\W"," ")));

								if (type == DumpType.PROPERTIES) {
									System.out.println(lookupValue.getLookUpName() + " = " + locale.getLookUpValue());
									//System.out.println(entity.getEntityType() + SEPARATOR +  camelCaseName + " = " + locale.getLookUpValue());
								} else if (type == DumpType.LOOKUP_VALUE_UPGRADE) {
									// for latest-data.sql
									String camelCaseName = StringUtils.deleteWhitespace(WordUtils.capitalize(locale.getLookUpValue().toLowerCase().replaceAll("\\W"," ")));
									String lookupName = entity.getEntityType() + SEPARATOR + camelCaseName;

									// for downgrade
									//String lookupName = " ";
									//System.out.println("INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + 
									//		"VALUES(" + lookupValue.getLookUpId() + ", " + entity.getEntityId() + ", '" + lookupName + "');");
									//System.out.println("INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)\n" + 
									//		"VALUES(" + locale.getLookUpValueId() + ", 1, " + lookupValue.getLookUpId() + ", NULL);");
									//System.out.println(entity.getEntityType() + SEPARATOR + lookupValue.getLookUpName() + " = " + locale.getLookUpValue());						

									if (lookupValue.getLookUpId() < 560) {
										System.out.println("UPDATE LOOKUP_VALUE SET LOOKUP_NAME = '" 
												+ lookupName + "' WHERE LOOKUP_ID = " + lookupValue.getLookUpId() + ";");
									} else {
										System.out.println("UPDATE LOOKUP_VALUE SET LOOKUP_NAME = '" 
												+ lookupName + "' WHERE LOOKUP_ID IN (SELECT LOOKUP_ID FROM LOOKUP_VALUE_LOCALE "
												+ " WHERE LOOKUP_VALUE = '" + locale.getLookUpValue() + "');");
									}
									/*
								System.out.println("UPDATE LOOKUP_VALUE_LOCALE LVL SET LOOKUP_VALUE = NULL" 
										+ " WHERE EXISTS (SELECT 1 FROM LOOKUP_VALUE LV WHERE" 
										+ " LVL.LOOKUP_ID = LV.LOOKUP_ID AND" 
										+ " LV.LOOKUP_NAME = '" + lookupValue.getLookUpName() + "');");

								System.out.println("UPDATE LOOKUP_VALUE_LOCALE LVL SET LOOKUP_VALUE = "
										+ "'" + locale.getLookUpValue() + "'"
										+ " WHERE EXISTS (SELECT 1 FROM LOOKUP_VALUE LV WHERE" 
										+ " LVL.LOOKUP_ID = LV.LOOKUP_ID AND" 
										+ " LV.LOOKUP_NAME = '" + lookupValue.getLookUpName() + "');");
									 */
									/*
								// upgrade
								System.out.println("UPDATE LOOKUP_VALUE_LOCALE LVL, LOOKUP_VALUE LV SET LVL.LOOKUP_VALUE = NULL" 
										+ " WHERE LVL.LOOKUP_ID = LV.LOOKUP_ID AND" 
										+ " LV.LOOKUP_NAME = '" + lookupValue.getLookUpName() + "';");
								// downgrade
								System.out.println("UPDATE LOOKUP_VALUE_LOCALE LVL, LOOKUP_VALUE LV SET LVL.LOOKUP_VALUE = "
										+ "'" + locale.getLookUpValue() + "'"
										+ " WHERE LVL.LOOKUP_ID = LV.LOOKUP_ID AND" 
										+ " LV.LOOKUP_NAME = '" + lookupValue.getLookUpName() + "';");
									 */
								} else {
									String lookupValueLocaleValue = null;
									if (type == DumpType.LOOKUP_VALUE_LOCALE_DOWNGRADE) {
										lookupValueLocaleValue = "'" + locale.getLookUpValue() + "'";
									} else if (type == DumpType.LOOKUP_VALUE_LOCALE_UPGRADE) {
										lookupValueLocaleValue = "NULL";
									}
									System.out.println("UPDATE LOOKUP_VALUE_LOCALE SET LOOKUP_VALUE = "
											+ lookupValueLocaleValue
											+ " WHERE LOOKUP_ID = (SELECT LOOKUP_ID FROM LOOKUP_VALUE" 
											+ " WHERE LOOKUP_NAME = '" + lookupValue.getLookUpName() + "');");
								}
							}
						}
					}
				}
			}

		} finally {
			StaticHibernateUtil.closeSession();	
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
		dumper.dumpLookupValues(DumpType.PROPERTIES);
	}

}
