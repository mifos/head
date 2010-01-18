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

package org.mifos.application.configuration;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.configuration.persistence.ApplicationConfigurationPersistence;
import org.mifos.application.master.business.LookUpLabelEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.framework.MifosIntegrationTestCase;

public class ApplicationConfigurationPersistenceIntegrationTest extends MifosIntegrationTestCase {

    public ApplicationConfigurationPersistenceIntegrationTest() throws Exception {
        super();
    }

    ApplicationConfigurationPersistence configurationPersistence;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        configurationPersistence = new ApplicationConfigurationPersistence();
    }

    /*
     * Check that we can can retrieve LookupEntities and enforce the
     * conventions: LookupEntity names cannot contain whitespace LookupEntities
     * should not have more than 1 label
     */
    public void testGetLookupEntities() {
        List<MifosLookUpEntity> entities = configurationPersistence.getLookupEntities();
        Assert.assertNotNull(entities);

        // Enforce that no entity names contain whitespace
        for (MifosLookUpEntity entity : entities) {
           Assert.assertEquals(StringUtils.deleteWhitespace(entity.getEntityType()), entity.getEntityType());

            Set<LookUpLabelEntity> labels = entity.getLookUpLabels();

            // Enforce that each entity has 0 or 1 labels and not more
           Assert.assertTrue(labels.size() <= 1);
            for (LookUpLabelEntity label : labels) {
                if (entity.getEntityType().equals("Client"))
                   Assert.assertEquals("Client", label.getLabelText());
            }
        }

    }

    public void testGetLookupValues() {
        Assert.assertNotNull(configurationPersistence.getLookupValues());
    }

    /*
     * 2007/12/24 Code in progress to test dumping of database strings to
     * properties files.
     */
    /*
     * public void testDump() { List<MifosLookUpEntity> entities=null; try {
     * Session session = StaticHibernateUtil.getSessionTL(); entities =
     * session.getNamedQuery( NamedQueryConstants.GET_ENTITIES).list();
     * 
     * for (MifosLookUpEntity entity : entities) { Set<LookUpLabelEntity> labels
     * = entity.getLookUpLabels(); for (LookUpLabelEntity label : labels) {
     * System.out.println(entity.getEntityType() + ".Label = " +
     * label.getLabelText()); } }
     * 
     * // for (MifosLookUpEntity entity : entities) { // Set<LookUpValueEntity>
     * values = entity.getLookUpValues(); // List<LookUpValueEntity> valuesList
     * = new ArrayList<LookUpValueEntity>(); // valuesList.addAll(values); //
     * Collections.sort(valuesList, new Comparator<LookUpValueEntity>() { //
     * public int compare(LookUpValueEntity v1, LookUpValueEntity v2) { //
     * return v1.getLookUpId().compareTo(v2.getLookUpId()); // } // }); // //
     * int index = 0; // for (LookUpValueEntity lookupValue : valuesList) { //
     * Set<LookUpValueLocaleEntity> localeValues =
     * lookupValue.getLookUpValueLocales(); // for (LookUpValueLocaleEntity
     * locale : localeValues) { // if (locale.getLocaleId() == 1) { // String
     * name =
     * StringUtils.deleteWhitespace(WordUtils.capitalize(locale.getLookUpValue
     * ().toLowerCase().replaceAll("\\W"," "))); //
     * //System.out.println(entity.getEntityType() + "." + index++ + "." + name
     * + " = " + locale.getLookUpValue()); //
     * System.out.println(entity.getEntityType() + "." +
     * lookupValue.getLookUpName() + " = " + locale.getLookUpValue()); // } // }
     * // } // }
     * 
     * } finally { StaticHibernateUtil.closeSession(); }
     * 
     * }
     */
}
