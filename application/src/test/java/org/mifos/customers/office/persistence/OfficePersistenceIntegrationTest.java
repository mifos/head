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

package org.mifos.customers.office.persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeTemplate;
import org.mifos.customers.office.business.OfficeTemplateImpl;
import org.mifos.customers.office.business.OfficeView;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class OfficePersistenceIntegrationTest extends MifosIntegrationTestCase {
    public OfficePersistenceIntegrationTest() throws Exception {
        super();
    }

    private OfficePersistence officePersistence;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        officePersistence = new OfficePersistence();
        initializeStatisticsService();
    }

    @Override
    public void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    private OfficePersistence getOfficePersistence() {
        return this.officePersistence;
    }

    public void testCreateOffice() throws Exception {
        long transactionCount = getStatisticsService().getSuccessfulTransactionCount();
        try {
            UserContext userContext = TestUtils.makeUser();
            OfficeTemplate template = OfficeTemplateImpl.createNonUniqueOfficeTemplate(OfficeLevel.BRANCHOFFICE);
            OfficeBO office = getOfficePersistence().createOffice(userContext, template);

            Assert.assertNotNull(office.getOfficeId());
           Assert.assertTrue(office.isActive());
        } finally {
            StaticHibernateUtil.rollbackTransaction();
        }
       Assert.assertTrue(transactionCount == getStatisticsService().getSuccessfulTransactionCount());
    }

    public void testCreateOfficeValidationFailure() throws PersistenceException, OfficeException {
        UserContext userContext = TestUtils.makeUser();
        OfficeTemplateImpl template = OfficeTemplateImpl.createNonUniqueOfficeTemplate(OfficeLevel.BRANCHOFFICE);
        template.setParentOfficeId(new Short((short) -1));
        try {
            OfficeBO office = getOfficePersistence().createOffice(userContext, template);
            Assert.fail("Office " + office.getOfficeName() + "should not have been successfully created");
        } catch (ValidationException e) {
            // This is what we're expecting here.
           Assert.assertTrue(e.getMessage().equals(OfficeConstants.PARENTOFFICE));
        } finally {
            StaticHibernateUtil.rollbackTransaction();
        }
    }

    public void testGetActiveBranchesForHO() throws Exception {
        List<OfficeView> officeList = getOfficePersistence().getActiveOffices(Short.valueOf("1"));
       Assert.assertEquals(1, officeList.size());
    }

    public void testGetActiveBranchs() throws Exception {
        List<OfficeView> officeList = getOfficePersistence().getActiveOffices(Short.valueOf("3"));
       Assert.assertEquals(1, officeList.size());
    }

    public void testGetAllOffices() throws Exception {
       Assert.assertEquals(Integer.valueOf("3").intValue(), getOfficePersistence().getAllOffices().size());
    }

    public void testGetMaxOfficeId() throws Exception {
       Assert.assertEquals(3, getOfficePersistence().getMaxOfficeId().intValue());
    }

    public void testGetChildCount() throws Exception {

       Assert.assertEquals(1, getOfficePersistence().getChildCount(Short.valueOf("1")).intValue());
    }

    public void testIsOfficeNameExist() throws Exception {
       Assert.assertTrue(getOfficePersistence().isOfficeNameExist("TestAreaOffice "));
    }

    public void testIsOfficeShortNameExist() throws Exception {
       Assert.assertTrue(getOfficePersistence().isOfficeShortNameExist("MIF2"));
    }

    public void testGetCountActiveChildern() throws Exception {
       Assert.assertTrue(getOfficePersistence().hasActiveChildern(Short.valueOf("1")));
    }

    public void testGetCountActivePeronnel() throws Exception {
       Assert.assertTrue(getOfficePersistence().hasActivePeronnel(Short.valueOf("1")));
    }

    public void testGetActiveParents() throws Exception {
        List<OfficeView> parents = getOfficePersistence()
                .getActiveParents(OfficeLevel.BRANCHOFFICE, Short.valueOf("1"));
       Assert.assertEquals(2, parents.size());
        for (OfficeView view : parents) {

            if (view.getLevelId().equals(OfficeLevel.HEADOFFICE.getValue())) {
                Assert.assertEquals("Head Office", view.getLevelName());
            } else if (view.getLevelId().equals(OfficeLevel.AREAOFFICE.getValue())) {
                Assert.assertEquals("Area Office", view.getLevelName());
            }
        }

    }

    /*
     * Check that we get the appropriate unordered list of levels back.
     */
    public void testGetActiveLevels() throws Exception {

        List<OfficeView> officeLevels = getOfficePersistence()
                .getActiveLevels(MasterDataEntity.CUSTOMIZATION_LOCALE_ID);
       Assert.assertEquals(4, officeLevels.size());

        Set<String> levels = new HashSet();
        levels.add("Regional Office");
        levels.add("Divisional Office");
        levels.add("Area Office");
        levels.add("Branch Office");

        for (OfficeView level : officeLevels) {
           Assert.assertTrue(levels.contains(level.getLevelName()));
        }
    }

    public void testGetStatusList() throws Exception {
        List<OfficeView> officeLevels = getOfficePersistence().getStatusList(MasterDataEntity.CUSTOMIZATION_LOCALE_ID);
       Assert.assertEquals(2, officeLevels.size());

        Set<String> levels = new HashSet();
        levels.add("Active");
        levels.add("Inactive");

        for (OfficeView level : officeLevels) {
           Assert.assertTrue(levels.contains(level.getLevelName()));
        }
    }

    public void testGetChildern() throws Exception {
       Assert.assertEquals(1, getOfficePersistence().getChildern(Short.valueOf("1")).size());
    }

    public void testGetChildern_failure() throws Exception {
       Assert.assertEquals(null, getOfficePersistence().getChildern(Short.valueOf("-1")));
    }

    public void testGetSearchId() throws Exception {
       Assert.assertEquals("1.1.", getOfficePersistence().getSearchId(Short.valueOf("1")));
    }

    public void testIsBranchInactive() throws Exception {
        Assert.assertFalse(getOfficePersistence().isBranchInactive(Short.valueOf("3")));
    }

    public void testGetBranchOffices() throws Exception {
       Assert.assertEquals(1, getOfficePersistence().getBranchOffices().size());
    }

    public void testGetOfficesTillBranchOffice() throws Exception {
       Assert.assertEquals(2, getOfficePersistence().getOfficesTillBranchOffice().size());
    }

    public void testGetOfficesTillBranchOfficeActive() throws Exception {
       Assert.assertEquals(2, getOfficePersistence().getOfficesTillBranchOffice("1.1").size());
    }

    public void testGetBranchParents() throws Exception {
        List<OfficeBO> officeList = getOfficePersistence().getBranchParents("1.1");
       Assert.assertEquals(1, officeList.size());
       Assert.assertEquals(1, officeList.get(0).getChildren().size());
        officeList = null;
    }

    public void testGetChildOffices() throws Exception {
        OfficeBO headOffice = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
        List<OfficeView> officeList = getOfficePersistence().getChildOffices(headOffice.getSearchId());
       Assert.assertEquals(3, officeList.size());
        officeList = null;
        headOffice = null;
    }

    public void testGetBranchesUnderUser() throws Exception {
        OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
        OfficeBO branchOffice = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, parent, "abcd", "abcd");

        List<OfficeBO> officeList = getOfficePersistence().getActiveBranchesUnderUser("1.1");
        Assert.assertNotNull(officeList);
       Assert.assertEquals(2, officeList.size());
       Assert.assertEquals(branchOffice.getOfficeName(), officeList.get(0).getOfficeName());
       Assert.assertEquals("TestBranchOffice", officeList.get(1).getOfficeName());
        TestObjectFactory.cleanUp(branchOffice);
    }

    public void testGetAllofficesForCustomFIeld() throws Exception {
        List<OfficeBO> officeList = getOfficePersistence().getAllofficesForCustomFIeld();
        Assert.assertNotNull(officeList);
       Assert.assertEquals(3, officeList.size());
    }

}
