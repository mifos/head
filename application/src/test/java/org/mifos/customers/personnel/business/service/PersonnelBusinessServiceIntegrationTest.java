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

package org.mifos.customers.personnel.business.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.login.util.helpers.LoginConstants;

public class PersonnelBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    public PersonnelBusinessServiceIntegrationTest() throws Exception {
        super();
    }

    private OfficeBO office;

    private PersonnelBO personnel;

    private PersonnelBO personnelBO;

    private PersonnelBusinessService personnelBusinessService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        personnelBusinessService = new PersonnelBusinessService();
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(personnelBO);
        TestObjectFactory.cleanUp(personnel);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testSuccessfullGetPersonnel() throws Exception {
        personnel = createPersonnel();
        String oldUserName = personnel.getUserName();
        personnel = personnelBusinessService.getPersonnel(personnel.getUserName());
       Assert.assertEquals(oldUserName, personnel.getUserName());
    }

    public void testFailureGetPersonnel() throws Exception {
        personnel = createPersonnel();
        try {
            personnel = personnelBusinessService.getPersonnel("WRONG_USERNAME");
        } catch (ServiceException pe) {
           Assert.assertTrue(true);
           Assert.assertEquals(LoginConstants.KEYINVALIDUSER, pe.getKey());
        }

    }

    public void testFailureGetOffice() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            personnelBusinessService.getOffice(TestObjectFactory.HEAD_OFFICE);
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }

    }

    public void testSearchFailureWithNoConn() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            personnelBusinessService.getPersonnel("WRONG_USERNAME");
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }

    }

    public void testSearch() throws Exception {
        personnel = createPersonnel();
        QueryResult queryResult = personnelBusinessService.search(personnel.getUserName(), Short.valueOf("1"), Short
                .valueOf("1"));

        Assert.assertNotNull(queryResult);
       Assert.assertEquals(1, queryResult.getSize());
       Assert.assertEquals(1, queryResult.get(0, 10).size());
    }

    public void testSearchFailure() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            personnelBusinessService.search("Raj", Short.valueOf("1"), Short.valueOf("1"));
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }

    }

    public void testGetActiveLoanOfficersUnderOffice() throws Exception {
        office = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        personnel = createPersonnel(office, PersonnelLevel.NON_LOAN_OFFICER);
        List<PersonnelBO> loanOfficers = personnelBusinessService
                .getActiveLoanOfficersUnderOffice(office.getOfficeId());
        Assert.assertNotNull(loanOfficers);
       Assert.assertEquals(1, loanOfficers.size());

    }

    public void testGetActiveLoanOfficersUnderOfficeFailure() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            personnelBusinessService.getActiveLoanOfficersUnderOffice(Short.valueOf("3"));
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }

    }

    public void testGetRolesFailure() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            personnelBusinessService.getRoles();
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }

    }

    public void testGetPersonnelByGlobalPersonnelNumFailure() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            personnelBusinessService.getPersonnelByGlobalPersonnelNum("12345678");
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }

    }

    public void testGetPersonnelFailure() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            personnelBusinessService.getPersonnel("12345678");
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }

    }

    public void testGetSupportedLocale() throws Exception {
        // asserting only on not null as suppored locales can be added by user
        Assert.assertNotNull(personnelBusinessService.getSupportedLocales());
    }

    public void testGetAllPersonnel() throws Exception {
        List<PersonnelBO> personnel = personnelBusinessService.getAllPersonnel();
        Assert.assertNotNull(personnel);
       Assert.assertEquals(3, personnel.size());
    }

    private PersonnelBO createPersonnel(OfficeBO office, PersonnelLevel personnelLevel) throws Exception {
        List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
        customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456", CustomFieldType.NUMERIC));
        Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd");
        Date date = new Date();
        personnel = new PersonnelBO(personnelLevel, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD", "XYZ",
                "xyz@yahoo.com", null, customFieldView, new Name("XYZ", null, null, null), "111111", date, Integer
                        .valueOf("1"), Integer.valueOf("1"), date, date, address, PersonnelConstants.SYSTEM_USER);
        personnel.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
        return personnel;
    }

    private PersonnelBO createPersonnel() throws Exception {
        office = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
        return createPersonnel(office, PersonnelLevel.LOAN_OFFICER);
    }
}
