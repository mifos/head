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

package org.mifos.customers.personnel.business;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.exception.ConstraintViolationException;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.config.Localization;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.customers.personnel.exceptions.PersonnelException;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.personnel.util.helpers.PersonnelStatus;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.util.UserContext;

public class PersonnelBOIntegrationTest extends MifosIntegrationTestCase {

    public PersonnelBOIntegrationTest() throws Exception {
        super();
    }

    private OfficeBO office;

    private OfficeBO branchOffice;

    private OfficeBO createdBranchOffice;

    private CenterBO center;

    private GroupBO group;

    private ClientBO client;

    private MeetingBO meeting;

    Name name;

    PersonnelBO personnel;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        office = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
        branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        name = new Name("XYZ", null, null, null);
    }

    @Override
    protected void tearDown() throws Exception {
        office = null;
        branchOffice = null;
        name = null;
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        TestObjectFactory.cleanUp(personnel);
        TestObjectFactory.cleanUp(createdBranchOffice);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testCreateFailureWithNullName() throws PersistenceException {
        try {
            new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD",
                    null, null, null, null, name, null, null, null, null, null, null, null,
                    PersonnelConstants.SYSTEM_USER);
            Assert.fail();
        } catch (ValidationException e) {
           Assert.assertEquals(PersonnelConstants.ERRORMANDATORY, e.getKey());
        }
    }

    public void testCreateFailureWithDuplicateName() throws PersistenceException {
        try {

            new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD",
                    "mifos", null, null, null, name, null, null, null, null, null, null, null,
                    PersonnelConstants.SYSTEM_USER);

           Assert.assertTrue(false);
        } catch (ValidationException e) {
           Assert.assertEquals(PersonnelConstants.DUPLICATE_USER, e.getKey());
        }
    }

    public void testCreateFailureWithDuplicateGovernMentId() throws PersistenceException {
        try {
            new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD",
                    "Raj", null, null, null, name, "123", null, null, null, null, null, null,
                    PersonnelConstants.SYSTEM_USER);

           Assert.assertTrue(false);
        } catch (ValidationException e) {
           Assert.assertEquals(PersonnelConstants.DUPLICATE_GOVT_ID, e.getKey());
        }
    }

    public void testCreateFailureWithDuplicateDisplayNameAndDOB() throws Exception {
        try {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Name name1 = new Name("mifos", null, null, null);
            new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD",
                    "RAJ", null, null, null, name1, null, dateFormat.parse("1979-12-12"), null, null, null, null, null,
                    PersonnelConstants.SYSTEM_USER);

           Assert.assertTrue(false);
        } catch (ValidationException e) {
           Assert.assertEquals(PersonnelConstants.DUPLICATE_USER_NAME_OR_DOB, e.getKey());
        }
    }

    public void testGetDateSucess() throws Exception {
        Date date = new Date();
        PersonnelBO personnel = new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"), Short
                .valueOf("1"), "ABCD", "RAJ", "rajendersaini@yahoo.com", ((PersonnelBusinessService) ServiceFactory
                .getInstance().getBusinessService(BusinessServiceName.Personnel)).getRoles(), getCustomFields(), name,
                "111111", date, Integer.valueOf("1"), Integer.valueOf("1"), date, date, getAddress(),
                PersonnelConstants.SYSTEM_USER);
       Assert.assertEquals("0", personnel.getAge());

    }

    public void testGetDateFailure() throws Exception {
        Date date = new Date();
        PersonnelBO personnel = new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"), Short
                .valueOf("1"), "ABCD", "RAJ", "rajendersaini@yahoo.com", ((PersonnelBusinessService) ServiceFactory
                .getInstance().getBusinessService(BusinessServiceName.Personnel)).getRoles(), getCustomFields(), name,
                "111111", null, Integer.valueOf("1"), Integer.valueOf("1"), date, date, getAddress(),
                PersonnelConstants.SYSTEM_USER);
       Assert.assertEquals("", personnel.getAge());

    }

    public void testSaveFailure() throws Exception {
        Date date = new Date();
        PersonnelBO personnel = new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"), Short
                .valueOf("1"), "ABCD", "RAJ", "rajendersaini@yahoo.com", ((PersonnelBusinessService) ServiceFactory
                .getInstance().getBusinessService(BusinessServiceName.Personnel)).getRoles(), getCustomFields(), name,
                "111111", date, Integer.valueOf("1"), Integer.valueOf("1"), date, date, getAddress(),
                PersonnelConstants.SYSTEM_USER);
        StaticHibernateUtil.getSessionTL().close();
        try {
            personnel.save();
           Assert.assertEquals(true, false);
        } catch (PersonnelException e) {
           Assert.assertEquals(true, true);
        }
    }

    public void testCreateSucess() throws Exception {
        Date date = new Date();

        PersonnelBO personnel = new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"), Short
                .valueOf("1"), "ABCD", "RAJ", "rajendersaini@yahoo.com", ((PersonnelBusinessService) ServiceFactory
                .getInstance().getBusinessService(BusinessServiceName.Personnel)).getRoles(), getCustomFields(), name,
                "111111", date, Integer.valueOf("1"), Integer.valueOf("1"), date, date, getAddress(),
                PersonnelConstants.SYSTEM_USER);
        personnel.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.flushAndCloseSession();
        PersonnelBO personnelSaved = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                personnel.getPersonnelId());
       Assert.assertEquals("RAJ", personnelSaved.getUserName());
       Assert.assertEquals("rajendersaini@yahoo.com", personnelSaved.getEmailId());
       Assert.assertEquals("XYZ", personnelSaved.getPersonnelDetails().getName().getFirstName());
       Assert.assertEquals(generateGlobalPersonnelNum(office.getGlobalOfficeNum(), personnel.getPersonnelId()),
                personnelSaved.getGlobalPersonnelNum());
       Assert.assertEquals(PersonnelLevel.NON_LOAN_OFFICER, personnelSaved.getLevelEnum());
       Assert.assertEquals(1, personnelSaved.getLevel().getParent().getId().intValue());
        Assert.assertFalse(personnelSaved.getLevel().isInteractionFlag());
       Assert.assertEquals(null, personnelSaved.getMaxChildCount());
       Assert.assertEquals(office.getOfficeId(), personnelSaved.getOffice().getOfficeId());
        Assert.assertFalse(personnelSaved.isPasswordChanged());
       Assert.assertEquals(1, personnelSaved.getPreferredLocale().getLocaleId().intValue());
       Assert.assertEquals(1, personnelSaved.getTitle().intValue());
       Assert.assertEquals("XYZ", personnelSaved.getDisplayName());
        Assert.assertFalse(personnelSaved.isLocked());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

       Assert.assertEquals(dateFormat.parse(dateFormat.format(date)), personnelSaved.getPersonnelDetails().getDob());
       Assert.assertEquals("XYZ", personnelSaved.getPersonnelDetails().getDisplayName());
       Assert.assertEquals(personnel.getPersonnelId(), personnelSaved.getPersonnelDetails().getPersonnel().getPersonnelId());
        for (PersonnelCustomFieldEntity personnelCustomField : personnelSaved.getCustomFields()) {
           Assert.assertEquals("123456", personnelCustomField.getFieldValue());
           Assert.assertEquals(9, personnelCustomField.getFieldId().intValue());
        }

        TestObjectFactory.cleanUp(personnelSaved);

    }

    public void testUpdateFailureForBranchTransferWithActiveCustomer() throws Exception {

        createdBranchOffice = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
        StaticHibernateUtil.closeSession();
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
        createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
       Assert.assertEquals(branchOffice.getOfficeId(), personnel.getOffice().getOfficeId());
        createInitialObjects(branchOffice.getOfficeId(), personnel.getPersonnelId());
        client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class, client.getCustomerId());
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        try {
            personnel.update(PersonnelStatus.ACTIVE, PersonnelLevel.LOAN_OFFICER, createdBranchOffice, Integer
                    .valueOf("1"), Short.valueOf("1"), "ABCD", "rajendersaini@yahoo.com", null, getCustomFields(),
                    name, Integer.valueOf("1"), Integer.valueOf("1"), getAddress(), Short.valueOf("1"));
            Assert.assertFalse(true);
        } catch (PersonnelException pe) {
            personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                    personnel.getPersonnelId());
            createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                    createdBranchOffice.getOfficeId());
           Assert.assertTrue(true);
           Assert.assertEquals(pe.getKey(), PersonnelConstants.TRANSFER_NOT_POSSIBLE_EXCEPTION);
        }
    }

    public void testUpdateFailureForBranchTransferWithLoanOfficerInNonBranch() throws Exception {

        createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
       Assert.assertEquals(branchOffice.getOfficeId(), personnel.getOffice().getOfficeId());
        createInitialObjects(branchOffice.getOfficeId(), personnel.getPersonnelId());
        client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class, client.getCustomerId());
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        try {
            personnel.update(PersonnelStatus.ACTIVE, PersonnelLevel.LOAN_OFFICER, office, Integer.valueOf("1"), Short
                    .valueOf("1"), "ABCD", "rajendersaini@yahoo.com", null, getCustomFields(), name, Integer
                    .valueOf("1"), Integer.valueOf("1"), getAddress(), Short.valueOf("1"));
            Assert.assertFalse(true);
        } catch (PersonnelException pe) {
           Assert.assertTrue(true);
           Assert.assertEquals(pe.getKey(), PersonnelConstants.LO_ONLY_IN_BRANCHES);
        }
    }

    public void testUpdateFailureForUserHierarchyChangeWithLoanOfficerInNonBranch() throws Exception {

        createPersonnel(office, PersonnelLevel.NON_LOAN_OFFICER);
        try {
            personnel.update(PersonnelStatus.ACTIVE, PersonnelLevel.LOAN_OFFICER, office, Integer.valueOf("1"), Short
                    .valueOf("1"), "ABCD", "rajendersaini@yahoo.com", null, getCustomFields(), name, Integer
                    .valueOf("1"), Integer.valueOf("1"), getAddress(), Short.valueOf("1"));
            Assert.assertFalse(true);
        } catch (PersonnelException pe) {
           Assert.assertTrue(true);
           Assert.assertEquals(pe.getKey(), PersonnelConstants.LO_ONLY_IN_BRANCHES);
        }
    }

    public void testUpdateFailureForUserHierarchyChangeWithCustomersPresentForLoanOfficer() throws Exception {

        createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
        createInitialObjects(branchOffice.getOfficeId(), personnel.getPersonnelId());
        client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class, client.getCustomerId());
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        try {
            personnel.update(PersonnelStatus.ACTIVE, PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"),
                    Short.valueOf("1"), "ABCD", "rajendersaini@yahoo.com", null, getCustomFields(), name, Integer
                            .valueOf("1"), Integer.valueOf("1"), getAddress(), Short.valueOf("1"));
            Assert.assertFalse(true);
        } catch (PersonnelException pe) {
           Assert.assertTrue(true);
           Assert.assertEquals(pe.getKey(), PersonnelConstants.HIERARCHY_CHANGE_EXCEPTION);
        }
    }

    public void testUpdateFailureForStatusChangeWithCustomersPresentForLoanOfficer() throws Exception {

        createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
        createInitialObjects(branchOffice.getOfficeId(), personnel.getPersonnelId());
        client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class, client.getCustomerId());
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        try {
            personnel.update(PersonnelStatus.INACTIVE, PersonnelLevel.LOAN_OFFICER, branchOffice, Integer.valueOf("1"),
                    Short.valueOf("1"), "ABCD", "rajendersaini@yahoo.com", null, getCustomFields(), name, Integer
                            .valueOf("1"), Integer.valueOf("1"), getAddress(), Short.valueOf("1"));
            Assert.assertFalse(true);
        } catch (PersonnelException pe) {
           Assert.assertTrue(true);
           Assert.assertEquals(pe.getKey(), PersonnelConstants.STATUS_CHANGE_EXCEPTION);
        }
    }

    public void testUpdateSucess() throws Exception {

        createdBranchOffice = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
        StaticHibernateUtil.closeSession();
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
        createPersonnel(office, PersonnelLevel.NON_LOAN_OFFICER);
        Short noOfTries = personnel.getNoOfTries();
       Assert.assertEquals(office.getOfficeId(), personnel.getOffice().getOfficeId());
       Assert.assertEquals("XYZ", personnel.getPersonnelDetails().getName().getFirstName());
       Assert.assertEquals(Integer.valueOf("1"), personnel.getPersonnelDetails().getGender());
       Assert.assertEquals(Integer.valueOf("1"), personnel.getPersonnelDetails().getMaritalStatus());
       Assert.assertEquals(PersonnelLevel.NON_LOAN_OFFICER, personnel.getLevelEnum());
       Assert.assertTrue(personnel.isActive());
       Assert.assertEquals(0, personnel.getPersonnelMovements().size());
        personnel.update(PersonnelStatus.INACTIVE, PersonnelLevel.LOAN_OFFICER, createdBranchOffice, Integer
                .valueOf("2"), Short.valueOf("1"), "ABCD", "abc@yahoo.com", getNewRoles(), getCustomFields(),
                getPersonnelName(), Integer.valueOf("2"), Integer.valueOf("2"), getAddress(), Short.valueOf("1"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
       Assert.assertEquals(createdBranchOffice.getOfficeId(), personnel.getOffice().getOfficeId());
       Assert.assertEquals(getPersonnelName().getFirstName(), personnel.getPersonnelDetails().getName().getFirstName());
       Assert.assertEquals(getPersonnelName().getLastName(), personnel.getPersonnelDetails().getName().getLastName());
       Assert.assertEquals(getPersonnelName().getMiddleName(), personnel.getPersonnelDetails().getName().getMiddleName());
       Assert.assertEquals(getPersonnelName().getSecondLastName(), personnel.getPersonnelDetails().getName()
                .getSecondLastName());
       Assert.assertEquals(getPersonnelName().getDisplayName(), personnel.getDisplayName());
       Assert.assertEquals(2, personnel.getPersonnelDetails().getGender().intValue());
       Assert.assertEquals(2, personnel.getPersonnelDetails().getMaritalStatus().intValue());
       Assert.assertEquals("abc@yahoo.com", personnel.getEmailId());
        Assert.assertFalse(personnel.isPasswordChanged());
       Assert.assertEquals(2, personnel.getTitle().intValue());
       Assert.assertEquals(PersonnelLevel.LOAN_OFFICER, personnel.getLevelEnum());
        Assert.assertFalse(personnel.isActive());
       Assert.assertEquals(2, personnel.getPersonnelMovements().size());
       Assert.assertEquals(1, personnel.getPersonnelRoles().size());
       Assert.assertEquals(noOfTries, personnel.getNoOfTries());

    }

    public void testSuccessUpdateUserSettings() throws Exception {
        createdBranchOffice = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
        StaticHibernateUtil.closeSession();
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
        createPersonnel(office, PersonnelLevel.NON_LOAN_OFFICER);

        personnel.update("xyz@aditi.com", getPersonnelName(), 2, 2, getAddress(), Short.valueOf("1"), Short
                .valueOf("1"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
       Assert.assertEquals(getPersonnelName().getFirstName(), personnel.getPersonnelDetails().getName().getFirstName());
       Assert.assertEquals(getPersonnelName().getLastName(), personnel.getPersonnelDetails().getName().getLastName());
       Assert.assertEquals(getPersonnelName().getMiddleName(), personnel.getPersonnelDetails().getName().getMiddleName());
       Assert.assertEquals(getPersonnelName().getSecondLastName(), personnel.getPersonnelDetails().getName()
                .getSecondLastName());
       Assert.assertEquals("changed", personnel.getPersonnelDetails().getAddress().getCity());
       Assert.assertEquals("changed", personnel.getPersonnelDetails().getAddress().getCountry());
       Assert.assertEquals("changed", personnel.getPersonnelDetails().getAddress().getLine1());
       Assert.assertEquals("changed", personnel.getPersonnelDetails().getAddress().getLine2());
       Assert.assertEquals("changed", personnel.getPersonnelDetails().getAddress().getLine3());
       Assert.assertEquals("changed", personnel.getPersonnelDetails().getAddress().getPhoneNumber());
       Assert.assertEquals("changed", personnel.getPersonnelDetails().getAddress().getState());
       Assert.assertEquals("changed", personnel.getPersonnelDetails().getAddress().getZip());
       Assert.assertEquals(2, personnel.getPersonnelDetails().getMaritalStatus().intValue());
       Assert.assertEquals("xyz@aditi.com", personnel.getEmailId());
    }

    public void testAddNotes() throws Exception {
        createdBranchOffice = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
        StaticHibernateUtil.closeSession();
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
        createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
       Assert.assertEquals(branchOffice.getOfficeId(), personnel.getOffice().getOfficeId());
        createInitialObjects(branchOffice.getOfficeId(), personnel.getPersonnelId());
        personnel.addNotes(PersonnelConstants.SYSTEM_USER, createNotes("1.Personnel notes created"));
        personnel.addNotes(PersonnelConstants.SYSTEM_USER, createNotes("2.Personnel notes created"));
        personnel.addNotes(PersonnelConstants.SYSTEM_USER, createNotes("3.Personnel notes created"));
        personnel.addNotes(PersonnelConstants.SYSTEM_USER, createNotes("4.Personnel notes created"));
        personnel.addNotes(PersonnelConstants.SYSTEM_USER, createNotes("5.Personnel notes created"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class, client.getCustomerId());
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
       Assert.assertEquals("Size of notes should be 5", 5, personnel.getPersonnelNotes().size());
       Assert.assertEquals("Size of recent notes should be 3", 3, personnel.getRecentPersonnelNotes().size());
        for (PersonnelNotesEntity notes : personnel.getPersonnelNotes()) {
           Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), notes.getCommentDate());
           Assert.assertEquals("The most recent note should be the last one added", "5.Personnel notes created", notes
                    .getComment());
            break;
        }
    }

    public void testSuccessfullLogin() throws Exception {
        personnel = createPersonnel();
        String password = "ABCD";
        UserContext userContext = personnel.login(password);
        Assert.assertFalse(personnel.isLocked());

       Assert.assertEquals(personnel.getPersonnelId(), userContext.getId());
       Assert.assertEquals(personnel.getDisplayName(), userContext.getName());
       Assert.assertEquals(personnel.getLevelEnum(), userContext.getLevel());
       Assert.assertEquals(personnel.getLastLogin(), userContext.getLastLogin());
       Assert.assertEquals(personnel.getPasswordChanged(), userContext.getPasswordChanged());
       Assert.assertEquals(personnel.getPreferredLocale().getLocaleId(), userContext.getLocaleId());
       Assert.assertEquals(Localization.getInstance().getLocaleId(), userContext.getMfiLocaleId());
       Assert.assertEquals(personnel.getPreferredLocale().getLanguageName(), userContext.getPreferredLocale()
                .getDisplayLanguage(userContext.getPreferredLocale()));
       Assert.assertEquals(personnel.getPreferredLocale().getCountryName(), userContext.getPreferredLocale()
                .getDisplayCountry(userContext.getPreferredLocale()));
       Assert.assertEquals(personnel.getOffice().getOfficeId(), userContext.getBranchId());
       Assert.assertEquals(personnel.getOffice().getGlobalOfficeNum(), userContext.getBranchGlobalNum());
       Assert.assertEquals(0, personnel.getNoOfTries().intValue());
        Assert.assertFalse(personnel.isPasswordChanged());
        // There should be 2 roles
       Assert.assertEquals(getRoles(personnel).size(), userContext.getRoles().size());
    }

    public void testLoginForInvalidPassword() throws Exception {
        personnel = createPersonnel();
        String password = "WRONG_PASSWORD";
        try {
            personnel.login(password);
            Assert.fail();
        } catch (PersonnelException e) {
           Assert.assertEquals(1, personnel.getNoOfTries().intValue());
            Assert.assertFalse(personnel.isLocked());
           Assert.assertEquals(LoginConstants.INVALIDOLDPASSWORD, e.getKey());
        }
    }

    public void testLoginForInactivePersonnel() throws Exception {
        personnel = createPersonnel();
        personnel.update(PersonnelStatus.INACTIVE, personnel.getLevelEnum(), personnel.getOffice(), personnel
                .getTitle(), personnel.getPreferredLocale().getLocaleId(), "PASSWORD", personnel.getEmailId(), null,
                null, personnel.getPersonnelDetails().getName(), personnel.getPersonnelDetails().getMaritalStatus(),
                personnel.getPersonnelDetails().getGender(), personnel.getPersonnelDetails().getAddress(), personnel
                        .getUpdatedBy());
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
        String password = "ABCD";
        try {
            personnel.login(password);
            StaticHibernateUtil.commitTransaction();
            Assert.fail();
        } catch (PersonnelException e) {
           Assert.assertEquals(0, personnel.getNoOfTries().intValue());
            Assert.assertFalse(personnel.isLocked());
           Assert.assertEquals(LoginConstants.KEYUSERINACTIVE, e.getKey());
        }
    }

    public void testLoginFourConsecutiveWrongPasswordEntered() throws Exception {
        personnel = createPersonnel();
        try {
            loginWithWrongPassword();
            loginWithWrongPassword();
            loginWithWrongPassword();
            loginWithWrongPassword();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
            personnel.login("WRONG_PASSWORD");
            Assert.fail();
        } catch (PersonnelException e) {
           Assert.assertEquals(5, personnel.getNoOfTries().intValue());
           Assert.assertTrue(personnel.isLocked());
           Assert.assertEquals(LoginConstants.INVALIDOLDPASSWORD, e.getKey());
        }
    }

    public void testLoginFourConsecutiveWrongPasswordEnteredFifthOneCorrect() throws Exception {
        personnel = createPersonnel();
        loginWithWrongPassword();
        loginWithWrongPassword();
        loginWithWrongPassword();
        loginWithWrongPassword();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
        personnel.login("ABCD");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
        Assert.assertFalse(personnel.isLocked());
       Assert.assertEquals("No of tries should be reseted to 0", 0, personnel.getNoOfTries().intValue());
    }

    public void testLoginForLockedPersonnel() throws Exception {
        personnel = createPersonnel();
        String password = "WRONG_PASSWORD";
        try {
            loginWithWrongPassword();
            loginWithWrongPassword();
            loginWithWrongPassword();
            loginWithWrongPassword();
            loginWithWrongPassword();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
            personnel.login(password);
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                    personnel.getPersonnelId());
            Assert.fail();
        } catch (PersonnelException e) {
           Assert.assertEquals(5, personnel.getNoOfTries().intValue());
           Assert.assertTrue(personnel.isLocked());
           Assert.assertEquals(LoginConstants.KEYUSERLOCKED, e.getKey());
        }
    }

    public void testUpdatePasswordWithOldPassword() throws Exception {
        personnel = createPersonnel();
        Assert.assertNull(personnel.getLastLogin());
        personnel.updatePassword("ABCD", "NEW_PASSWORD", Short.valueOf("1"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
       Assert.assertTrue(personnel.isPasswordChanged());
        Assert.assertNotNull(personnel.getLastLogin());
    }

    public void testUpdatePassword() throws Exception {
        personnel = createPersonnel();
        Short mifosId = new Short((short) 1);
        personnel.updatePassword("testPassword", mifosId);
       Assert.assertEquals(personnel.getUpdatedBy(), mifosId);
        Assert.assertNotNull(personnel.getLastLogin());
       Assert.assertEquals(personnel.getPasswordChanged(), LoginConstants.PASSWORDCHANGEDFLAG);
    }

    public void testUpdatePasswordWithWrongOldPassword() throws Exception {
        personnel = createPersonnel();
        Assert.assertNull(personnel.getLastLogin());
        try {
            personnel.updatePassword("WRONGOLD_PASSWORD", "NEW_PASSWORD", Short.valueOf("1"));
            Assert.fail();
        } catch (PersonnelException e) {
           Assert.assertEquals(LoginConstants.INVALIDOLDPASSWORD, e.getKey());
            Assert.assertFalse(personnel.isPasswordChanged());
        }
    }

    public void testUnlockPersonnel() throws Exception {
        personnel = createPersonnel();
        loginWithWrongPassword();
        loginWithWrongPassword();
        loginWithWrongPassword();
        loginWithWrongPassword();
        loginWithWrongPassword();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
       Assert.assertTrue(personnel.isLocked());
        personnel.unlockPersonnel(Short.valueOf("1"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
        Assert.assertFalse(personnel.isLocked());
       Assert.assertEquals(0, personnel.getNoOfTries().intValue());
    }

    public void testUnlockPersonnelFailure() throws Exception {
        try {
            personnel = createPersonnel();
            loginWithWrongPassword();
            loginWithWrongPassword();
            loginWithWrongPassword();
            loginWithWrongPassword();
            loginWithWrongPassword();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
            TestObjectFactory.simulateInvalidConnection();
            personnel.unlockPersonnel(Short.valueOf("1"));
            Assert.fail();
        } catch (PersonnelException e) {
           Assert.assertEquals(CustomerConstants.UPDATE_FAILED_EXCEPTION, e.getKey());
        } finally {
            StaticHibernateUtil.closeSession();
        }

    }

    public void testReLoginAfterUnlock() throws Exception {
        personnel = createPersonnel();
        loginWithWrongPassword();
        loginWithWrongPassword();
        loginWithWrongPassword();
        loginWithWrongPassword();
        loginWithWrongPassword();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
       Assert.assertTrue(personnel.isLocked());
        personnel.unlockPersonnel(Short.valueOf("1"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
        Assert.assertFalse(personnel.isLocked());
       Assert.assertEquals(0, personnel.getNoOfTries().intValue());

        personnel.login("ABCD");
       Assert.assertEquals(0, personnel.getNoOfTries().intValue());
    }

    public void testPersonnelView() throws Exception {
        PersonnelView personnelView = new PersonnelView(Short.valueOf("1"), "Raj");
       Assert.assertEquals(Short.valueOf("1"), personnelView.getPersonnelId());
       Assert.assertEquals("Raj", personnelView.getDisplayName());
    }

    public void testGetLocaleId() throws Exception {
        Short localeId = 1;
        PersonnelBO personnel = new PersonnelBO();
        personnel.setPreferredLocale(new SupportedLocalesEntity(localeId));

       Assert.assertEquals(localeId, personnel.getLocaleId());
    }

    public void testCreateFailureWithDuplicateUserName() throws Exception {
        try {

            PersonnelBO person1 = new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"), Short
                    .valueOf("1"), "ABCD", "testUser", null, null, null, name, null, new Date(), null, 1, null, null,
                    null, PersonnelConstants.SYSTEM_USER);

            PersonnelBO person2 = new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"), Short
                    .valueOf("1"), "ABCD", "testUser", null, null, null, name, null, new Date(), null, 1, null, null,
                    null, PersonnelConstants.SYSTEM_USER);

            person1.save();
            person2.save();
            StaticHibernateUtil.rollbackTransaction();
            Assert.fail();
        } catch (PersonnelException e) {
           Assert.assertEquals(PersistenceException.class, e.getCause().getClass());
           Assert.assertEquals(ConstraintViolationException.class, e.getCause().getCause().getClass());
            StaticHibernateUtil.rollbackTransaction();
        }
    }

    private PersonnelNotesEntity createNotes(String comment) throws Exception {
        return new PersonnelNotesEntity(comment, new PersonnelPersistence()
                .getPersonnel(PersonnelConstants.SYSTEM_USER), personnel);
    }

    private String generateGlobalPersonnelNum(String officeGlobalNum, int maxPersonnelId) {
        String userId = "";
        int numberOfZeros = 5 - String.valueOf(maxPersonnelId).length();
        for (int i = 0; i < numberOfZeros; i++) {
            userId = userId + "0";
        }
        userId = userId + maxPersonnelId;
        String userGlobalNum = officeGlobalNum + "-" + userId;

        return userGlobalNum;
    }

    private void createInitialObjects(Short officeId, Short personnelId) {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());

        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting, officeId, personnelId);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
    }

    private PersonnelBO createPersonnel(OfficeBO office, PersonnelLevel personnelLevel) throws Exception {

        Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd");
        Date date = new Date();
        personnel = new PersonnelBO(personnelLevel, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD", "XYZ",
                "xyz@yahoo.com", getRoles(), getCustomFields(), name, "111111", date, Integer.valueOf("1"), Integer
                        .valueOf("1"), date, date, address, PersonnelConstants.SYSTEM_USER);
        personnel.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
        return personnel;
    }

    private List<CustomFieldView> getCustomFields() {
        List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();
        customFields.add(new CustomFieldView(Short.valueOf("9"), "123456", CustomFieldType.NUMERIC));
        return customFields;
    }

    private Address getAddress() {
        return new Address("changed", "changed", "changed", "changed", "changed", "changed", "changed", "changed");
    }

    private Name getPersonnelName() {
        return new Name("first", "middle", "secondLast", "last");
    }

    private List<RoleBO> getRoles() throws Exception {
        return ((PersonnelBusinessService) ServiceFactory.getInstance().getBusinessService(
                BusinessServiceName.Personnel)).getRoles();
    }

    private List<RoleBO> getNewRoles() throws Exception {
        List<RoleBO> roles = new ArrayList<RoleBO>();
        roles.add((RoleBO) TestObjectFactory.getObject(RoleBO.class, Short.valueOf("1")));
        return roles;
    }

    private PersonnelBO createPersonnel() throws Exception {
        createdBranchOffice = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
        StaticHibernateUtil.closeSession();
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
        createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
        return new PersonnelPersistence().getPersonnelByUserName(personnel.getUserName());
    }

    private void loginWithWrongPassword() {
        try {
            personnel.login("WRONG_PASSWORD");
            Assert.fail();
        } catch (PersonnelException expected) {
           Assert.assertEquals(LoginConstants.INVALIDOLDPASSWORD, expected.getKey());
        }
    }

    private Set<Short> getRoles(PersonnelBO personnelBO) {
        Set<Short> roles = new HashSet<Short>();
        for (PersonnelRoleEntity personnelRole : personnelBO.getPersonnelRoles()) {
            roles.add(personnelRole.getRole().getId());
        }
        return roles;
    }
}
