/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class OfficePersistenceIntegrationTest extends MifosIntegrationTestCase {

    private OfficePersistence officePersistence;

    @Before
    public void setUp() throws Exception {
        officePersistence = new OfficePersistence();
        initializeStatisticsService();
    }

    @After
    public void tearDown() throws Exception {
        StaticHibernateUtil.flushSession();
    }

    private OfficePersistence getOfficePersistence() {
        return this.officePersistence;
    }

    @Test
    public void testGetActiveBranchesForHO() throws Exception {
        List<OfficeDetailsDto> officeList = getOfficePersistence().getActiveOffices(Short.valueOf("1"));
        Assert.assertEquals(1, officeList.size());
    }

    @Test
    public void testGetActiveBranchs() throws Exception {
        List<OfficeDetailsDto> officeList = getOfficePersistence().getActiveOffices(Short.valueOf("3"));
        Assert.assertEquals(1, officeList.size());
    }

    @Test
    public void testGetAllOffices() throws Exception {
        Assert.assertEquals(Integer.valueOf("3").intValue(), getOfficePersistence().getAllOffices().size());
    }

    @Test
    public void testGetMaxOfficeId() throws Exception {
        Assert.assertEquals(3, getOfficePersistence().getMaxOfficeId().intValue());
    }

    @Test
    public void testGetChildCount() throws Exception {

        Assert.assertEquals(1, getOfficePersistence().getChildCount(Short.valueOf("1")).intValue());
    }

    @Test
    public void testIsOfficeNameExist() throws Exception {
        Assert.assertTrue(getOfficePersistence().isOfficeNameExist("TestAreaOffice "));
    }

    @Test
    public void testIsOfficeShortNameExist() throws Exception {
        Assert.assertTrue(getOfficePersistence().isOfficeShortNameExist("MIF2"));
    }

    @Test
    public void testGetChildern() throws Exception {
        Assert.assertEquals(1, getOfficePersistence().getChildern(Short.valueOf("1")).size());
    }

    @Test
    public void testGetChildern_failure() throws Exception {
        Assert.assertEquals(null, getOfficePersistence().getChildern(Short.valueOf("-1")));
    }

    @Test
    public void testGetSearchId() throws Exception {
        Assert.assertEquals("1.1.", getOfficePersistence().getSearchId(Short.valueOf("1")));
    }

    @Test
    public void testIsBranchInactive() throws Exception {
        Assert.assertFalse(getOfficePersistence().isBranchInactive(Short.valueOf("3")));
    }

    @Test
    public void testGetBranchOffices() throws Exception {
        Assert.assertEquals(1, getOfficePersistence().getBranchOffices().size());
    }

    @Test
    public void testGetOfficesTillBranchOffice() throws Exception {
        Assert.assertEquals(2, getOfficePersistence().getOfficesTillBranchOffice().size());
    }

    @Test
    public void testGetOfficesTillBranchOfficeActive() throws Exception {
        Assert.assertEquals(2, getOfficePersistence().getOfficesTillBranchOffice("1.1").size());
    }

    @Test
    public void testGetBranchParents() throws Exception {
        List<OfficeBO> officeList = getOfficePersistence().getBranchParents("1.1");
        Assert.assertEquals(1, officeList.size());
        Assert.assertEquals(1, officeList.get(0).getChildren().size());
        officeList = null;
    }

    @Test
    public void testGetChildOffices() throws Exception {
        OfficeBO headOffice = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
        List<OfficeDetailsDto> officeList = getOfficePersistence().getChildOffices(headOffice.getSearchId());
        Assert.assertEquals(3, officeList.size());
        officeList = null;
        headOffice = null;
    }

    @Test
    public void testGetBranchesUnderUser() throws Exception {
        OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
        OfficeBO branchOffice = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, parent, "abcd", "abcd");

        List<OfficeBO> officeList = getOfficePersistence().getActiveBranchesUnderUser("1.1");
        Assert.assertNotNull(officeList);
        Assert.assertEquals(2, officeList.size());
        Assert.assertEquals(branchOffice.getOfficeName(), officeList.get(0).getOfficeName());
        Assert.assertEquals("TestBranchOffice", officeList.get(1).getOfficeName());
    }

    @Test
    public void testGetAllofficesForCustomFIeld() throws Exception {
        List<OfficeBO> officeList = getOfficePersistence().getAllofficesForCustomFIeld();
        Assert.assertNotNull(officeList);
        Assert.assertEquals(3, officeList.size());
    }

}
