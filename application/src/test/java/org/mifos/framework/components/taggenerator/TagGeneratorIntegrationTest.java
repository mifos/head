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

package org.mifos.framework.components.taggenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

public class TagGeneratorIntegrationTest extends MifosIntegrationTestCase {

    private CustomerBO group;

    private CustomerBO center;

    private SavingsBO savings;

    private SavingsOfferingBO savingsOffering;

    private UserContext userContext;

    private PersonnelBO personnel;

    private OfficeBO branchOffice;

    Object randomNum = null;

    @Before
    public void setUp() throws Exception {
        userContext = TestUtils.makeUser();

        randomNum = new Random().nextLong();
    }

    @After
    public void tearDown() throws Exception {
        branchOffice = null;
        savings = null;
        group = null;
        center = null;
        personnel = null;
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testSavingsAccountLinkWithoutSelfLink() throws Exception {
        createInitialObjectsForSavings();
        String createdLink = TagGenerator.createHeaderLinks(savings, false, randomNum);
        Assert.assertEquals(true, createdLink.contains("clientsAndAccounts"));
        Assert.assertEquals(true, createdLink.contains("TestBranchOffice"));
        Assert.assertEquals(true, createdLink.contains("viewCenterDetails"));
        Assert.assertEquals(true, createdLink.contains("Center_Active_test"));
        Assert.assertEquals(true, createdLink.contains("viewGroupDetails"));
        Assert.assertEquals(true, createdLink.contains("Group_Active_test"));
        Assert.assertEquals(true, createdLink.contains("prd1"));
    }

    @Test
    public void testSavingsAccountLinkWithSelfLink() throws Exception {
        createInitialObjectsForSavings();
        String createdLink = TagGenerator.createHeaderLinks(savings, true, randomNum);
        Assert.assertEquals(true, createdLink.contains("clientsAndAccounts"));
        Assert.assertEquals(true, createdLink.contains("TestBranchOffice"));
        Assert.assertEquals(true, createdLink.contains("viewCenterDetails"));
        Assert.assertEquals(true, createdLink.contains("Center_Active_test"));
        Assert.assertEquals(true, createdLink.contains("viewGroupDetails"));
        Assert.assertEquals(true, createdLink.contains("Group_Active_test"));
        Assert.assertEquals(true, createdLink.contains("viewSavingsAccountDetails"));
    }

    @Test
    public void testPersonnelLinkWithoutSelfLink() throws Exception {
        branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
        String createdLink = TagGenerator.createHeaderLinks(personnel, false, randomNum);
        Assert.assertEquals(false, createdLink.contains("PersonAction"));
        Assert.assertEquals(true, createdLink.contains("TestBranchOffice"));
    }

    @Test
    public void testPersonnelLinkWithSelfLink() throws Exception {
        branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
        String createdLink = TagGenerator.createHeaderLinks(personnel, true, randomNum);
        Assert.assertEquals(true, createdLink.contains("PersonAction"));
        Assert.assertEquals(true, createdLink.contains("TestBranchOffice"));
    }

    @Test
    public void testTagGeneratorFactory() throws Exception {
        createInitialObjectsForSavings();
        TagGenerator tagGenerator = TagGeneratorFactory.getInstance().getGenerator(center);
        if (tagGenerator instanceof CustomerTagGenerator) {
            Assert.assertTrue(true);
        }

        tagGenerator = TagGeneratorFactory.getInstance().getGenerator(group);
        if (tagGenerator instanceof CustomerTagGenerator) {
            Assert.assertTrue(true);
        }

        tagGenerator = TagGeneratorFactory.getInstance().getGenerator(savings);
        if (tagGenerator instanceof AccountTagGenerator) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void testTagGeneratorFactoryPageExpired() throws Exception {
        try {
            TagGeneratorFactory.getInstance().getGenerator(null);
            Assert.fail();
        } catch (PageExpiredException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testTagGeneratorFactoryForPersonnel() throws Exception {
        branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
        TagGenerator tagGenerator = TagGeneratorFactory.getInstance().getGenerator(personnel);
        tagGenerator = TagGeneratorFactory.getInstance().getGenerator(personnel);
        if (tagGenerator instanceof PersonnelTagGenerator) {
            Assert.assertTrue(true);
        }
    }

    private void createInitialObjectsForSavings() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE,
                center);
        SavingsTestHelper helper = new SavingsTestHelper();
        savingsOffering = helper.createSavingsOffering("prd1", "cdfg");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
    }

    private PersonnelBO createPersonnel(OfficeBO office, PersonnelLevel personnelLevel) throws Exception {
        List<CustomFieldDto> customFieldDto = new ArrayList<CustomFieldDto>();
        customFieldDto.add(new CustomFieldDto(Short.valueOf("9"), "123456", CustomFieldType.NUMERIC.getValue()));
        Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd");
        Name name = new Name("XYZ", null, null, null);
        java.util.Date date = new java.util.Date();
        personnel = new PersonnelBO(personnelLevel, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD", "XYZ",
                "xyz@yahoo.com", null, customFieldDto, name, "111111", date, Integer.valueOf("1"),
                Integer.valueOf("1"), date, date, address, userContext.getId(), new Date(), new HashSet());

        IntegrationTestObjectMother.createPersonnel(personnel);
        return IntegrationTestObjectMother.findPersonnelById(personnel.getPersonnelId());
    }
}
