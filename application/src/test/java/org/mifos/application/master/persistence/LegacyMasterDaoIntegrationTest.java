/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.application.master.persistence;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.application.master.business.CustomValueDto;
import org.mifos.application.master.business.CustomValueListElementDto;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class LegacyMasterDaoIntegrationTest extends MifosIntegrationTestCase {

    @Autowired
    private LegacyMasterDao legacyMasterDao;

    @After
    public void tearDown() throws Exception {
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testEntityMasterRetrieval() throws Exception {
        CustomValueDto paymentTypes = legacyMasterDao.getCustomValueList(MasterConstants.ATTENDENCETYPES,
                "org.mifos.application.master.business.CustomerAttendanceType", "attendanceId");
        List<CustomValueListElementDto> paymentValues = paymentTypes.getCustomValueListElements();
       Assert.assertEquals(4, paymentValues.size());

    }

    @Test @Ignore
    public void testEntityMasterRetrievalForInvalidConnection() throws Exception {
        try {
            legacyMasterDao.getCustomValueList(MasterConstants.ATTENDENCETYPES,
                    "org.mifos.application.master.business.CustomerAttendanceType", "attendanceId");
            Assert.fail();
        } catch (Exception e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.flushSession();
        }
    }

    @Test
    public void testGetLookUpEntity() throws Exception {
        CustomValueDto gender = legacyMasterDao.getLookUpEntity(MasterConstants.GENDER);
        List<CustomValueListElementDto> genderValues = gender.getCustomValueListElements();
       Assert.assertEquals(2, genderValues.size());

    }

    @Test
    public void testRetrieveMasterEntities() throws Exception {
        List<ValueListElement> masterEntity = legacyMasterDao.findValueListElements(MasterConstants.LOAN_PURPOSES);
        // 131 if includes the empty lookup_name for lookup id 259, 263
       Assert.assertEquals(131, masterEntity.size());
    }

    @Test@Ignore
    public void testRetrieveMasterEntitiesForInvalidConnection() throws Exception {
        try {
            legacyMasterDao.findValueListElements(MasterConstants.LOAN_PURPOSES);
            Assert.fail();
        } catch (Exception e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.flushSession();
        }
    }

    @Test
    public void testGetMasterEntityName() throws Exception {
       Assert.assertEquals("Partial Application", legacyMasterDao.getMessageForLookupEntity(1));
    }

    @Test
    public void testRetrieveMasterDataEntity() throws Exception {
        List<AccountStateEntity> masterDataList = legacyMasterDao
                .findMasterDataEntities(AccountStateEntity.class);
       Assert.assertEquals(18, masterDataList.size());
        for (MasterDataEntity masterDataEntity : masterDataList) {
            for (LookUpValueLocaleEntity lookUpValueLocaleEntity : masterDataEntity.getLookUpValue()
                    .getLookUpValueLocales()) {
               Assert.assertEquals(Short.valueOf("1"), lookUpValueLocaleEntity.getLocaleId());
            }
        }
    }

    @Test @Ignore("Convert to unit test")
    public void testRetrieveMasterDataEntityForInvalidConnection() throws Exception {
        try {
            legacyMasterDao.findMasterDataEntities(AccountStateEntity.class);
            Assert.fail();
        } catch (Exception e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.flushSession();
        }
    }

    private boolean foundStringInCustomValueList(final String CustomValueListName,
            final String searchString) throws Exception {
        List<ValueListElement> salutations = legacyMasterDao.findValueListElements(CustomValueListName);
        boolean foundString = false;
        for (ValueListElement entity : salutations) {
            if (entity.getName().compareTo(searchString) == 0) {
                foundString = true;
            }
        }
        return foundString;
    }

    private Integer findValueListElementId(final LegacyMasterDao legacyMasterDao, final String CustomValueListName,
            final String searchString) throws Exception {
        List<ValueListElement> salutations = legacyMasterDao.findValueListElements(CustomValueListName);
        Integer elementId = null;
        for (ValueListElement entity : salutations) {
            if (entity.getName().compareTo(searchString) == 0) {
                elementId = entity.getId();
            }
        }
        return elementId;
    }

    @Test
    public void testUpdateValueListElement() throws Exception {
        // get a CustomValueListElementDto (as a BusinessActivityEntity)
        List<ValueListElement> salutations = legacyMasterDao.findValueListElements(MasterConstants.SALUTATION);
        ValueListElement first = salutations.get(0);
        Integer id = first.getId();
        String originalName = first.getName();

        // update it
        final String UPDATED_NAME = "Mister";
        first.setName(UPDATED_NAME);

        // save it
        legacyMasterDao.updateValueListElementForLocale(id, UPDATED_NAME);
        StaticHibernateUtil.flushSession();

        // get the element back
        // and verify that it has the new value
        salutations.clear();
        salutations = legacyMasterDao.findValueListElements(MasterConstants.SALUTATION);
        for (ValueListElement entity : salutations) {
            if (entity.getId() == id) {
               Assert.assertEquals(entity.getName(), UPDATED_NAME);
            }
        }
        // restore it
        legacyMasterDao.updateValueListElementForLocale(id, originalName);
        StaticHibernateUtil.flushSession();

    }
}