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

package org.mifos.application.personnel.business;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.personnel.exceptions.PersonnelException;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.personnel.util.helpers.PersonnelStatus;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.config.Localization;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class PersonnelBOIntegrationTest extends MifosIntegrationTest {

    public PersonnelBOIntegrationTest() throws SystemException, ApplicationException {
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
            fail();
        } catch (ValidationException e) {
            assertEquals(PersonnelConstants.ERRORMANDATORY, e.getKey());
        }
    }

    public void testCreateFailureWithDuplicateName() throws PersistenceException {
        try {

            new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD",
                    "mifos", null, null, null, name, null, null, null, null, null, null, null,
                    PersonnelConstants.SYSTEM_USER);

            assertTrue(false);
        } catch (ValidationException e) {
            assertEquals(PersonnelConstants.DUPLICATE_USER, e.getKey());
        }
    }

    public void testCreateFailureWithDuplicateGovernMentId() throws PersistenceException {
        try {
            new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD",
                    "Raj", null, null, null, name, "123", null, null, null, null, null, null,
                    PersonnelConstants.SYSTEM_USER);

            assertTrue(false);
        } catch (ValidationException e) {
            assertEquals(PersonnelConstants.DUPLICATE_GOVT_ID, e.getKey());
        }
    }

    public void testCreateFailureWithDuplicateDisplayNameAndDOB() throws Exception {
        try {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Name name1 = new Name("mifos", null, null, null);
            new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD",
                    "RAJ", null, null, null, name1, null, dateFormat.parse("1979-12-12"), null, null, null, null, null,
                    PersonnelConstants.SYSTEM_USER);

            assertTrue(false);
        } catch (ValidationException e) {
            assertEquals(PersonnelConstants.DUPLICATE_USER_NAME_OR_DOB, e.getKey());
        }
    }

    public void testGetDateSucess() throws Exception {
        Date date = new Date();
        PersonnelBO personnel = new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"), Short
                .valueOf("1"), "ABCD", "RAJ", "rajendersaini@yahoo.com", ((PersonnelBusinessService) ServiceFactory
                .getInstance().getBusinessService(BusinessServiceName.Personnel)).getRoles(), getCustomFields(), name,
                "111111", date, Integer.valueOf("1"), Integer.valueOf("1"), date, date, getAddress(),
                PersonnelConstants.SYSTEM_USER);
        assertEquals("0", personnel.getAge());

    }

    public void testGetDateFailure() throws Exception {
        Date date = new Date();
        PersonnelBO personnel = new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"), Short
                .valueOf("1"), "ABCD", "RAJ", "rajendersaini@yahoo.com", ((PersonnelBusinessService) ServiceFactory
                .getInstance().getBusinessService(BusinessServiceName.Personnel)).getRoles(), getCustomFields(), name,
                "111111", null, Integer.valueOf("1"), Integer.valueOf("1"), date, date, getAddress(),
                PersonnelConstants.SYSTEM_USER);
        assertEquals("", personnel.getAge());

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
            assertEquals(true, false);
        } catch (PersonnelException e) {
            assertEquals(true, true);
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
        assertEquals("RAJ", personnelSaved.getUserName());
        assertEquals("rajendersaini@yahoo.com", personnelSaved.getEmailId());
        assertEquals("XYZ", personnelSaved.getPersonnelDetails().getName().getFirstName());
        assertEquals(generateGlobalPersonnelNum(office.getGlobalOfficeNum(), personnel.getPersonnelId()),
                personnelSaved.getGlobalPersonnelNum());
        assertEquals(PersonnelLevel.NON_LOAN_OFFICER, personnelSaved.getLevelEnum());
        assertEquals(1, personnelSaved.getLevel().getParent().getId().intValue());
        assertFalse(personnelSaved.getLevel().isInteractionFlag());
        assertEquals(null, personnelSaved.getMaxChildCount());
        assertEquals(office.getOfficeId(), personnelSaved.getOffice().getOfficeId());
        assertFalse(personnelSaved.isPasswordChanged());
        assertEquals(1, personnelSaved.getPreferredLocale().getLocaleId().intValue());
        assertEquals(1, personnelSaved.getTitle().intValue());
        assertEquals("XYZ", personnelSaved.getDisplayName());
        assertFalse(personnelSaved.isLocked());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        assertEquals(dateFormat.parse(dateFormat.format(date)), personnelSaved.getPersonnelDetails().getDob());
        assertEquals("XYZ", personnelSaved.getPersonnelDetails().getDisplayName());
        assertEquals(personnel.getPersonnelId(), personnelSaved.getPersonnelDetails().getPersonnel().getPersonnelId());
        for (PersonnelCustomFieldEntity personnelCustomField : personnelSaved.getCustomFields()) {
            assertEquals("123456", personnelCustomField.getFieldValue());
            assertEquals(9, personnelCustomField.getFieldId().intValue());
        }

        TestObjectFactory.cleanUp(personnelSaved);

    }

    public void testUpdateFailureForBranchTransferWithActiveCustomer() throws Exception {

        createdBranchOffice = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
        StaticHibernateUtil.closeSession();
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
        createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
        assertEquals(branchOffice.getOfficeId(), personnel.getOffice().getOfficeId());
        createInitialObjects(branchOffice.getOfficeId(), personnel.getPersonnelId());
        client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class, client.getCustomerId());
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        try {
            personnel.update(PersonnelStatus.ACTIVE, PersonnelLevel.LOAN_OFFICER, createdBranchOffice, Integer
                    .valueOf("1"), Short.valueOf("1"), "ABCD", "rajendersaini@yahoo.com", null, getCustomFields(),
                    name, Integer.valueOf("1"), Integer.valueOf("1"), getAddress(), Short.valueOf("1"));
            assertFalse(true);
        } catch (PersonnelException pe) {
            personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                    personnel.getPersonnelId());
            createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                    createdBranchOffice.getOfficeId());
            assertTrue(true);
            assertEquals(pe.getKey(), PersonnelConstants.TRANSFER_NOT_POSSIBLE_EXCEPTION);
        }
    }

    public void testUpdateFailureForBranchTransferWithLoanOfficerInNonBranch() throws Exception {

        createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
        assertEquals(branchOffice.getOfficeId(), personnel.getOffice().getOfficeId());
        createInitialObjects(branchOffice.getOfficeId(), personnel.getPersonnelId());
        client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class, client.getCustomerId());
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        try {
            personnel.update(PersonnelStatus.ACTIVE, PersonnelLevel.LOAN_OFFICER, office, Integer.valueOf("1"), Short
                    .valueOf("1"), "ABCD", "rajendersaini@yahoo.com", null, getCustomFields(), name, Integer
                    .valueOf("1"), Integer.valueOf("1"), getAddress(), Short.valueOf("1"));
            assertFalse(true);
        } catch (PersonnelException pe) {
            assertTrue(true);
            assertEquals(pe.getKey(), PersonnelConstants.LO_ONLY_IN_BRANCHES);
        }
    }

    public void testUpdateFailureForUserHierarchyChangeWithLoanOfficerInNonBranch() throws Exception {

        createPersonnel(office, PersonnelLevel.NON_LOAN_OFFICER);
        try {
            personnel.update(PersonnelStatus.ACTIVE, PersonnelLevel.LOAN_OFFICER, office, Integer.valueOf("1"), Short
                    .valueOf("1"), "ABCD", "rajendersaini@yahoo.com", null, getCustomFields(), name, Integer
                    .valueOf("1"), Integer.valueOf("1"), getAddress(), Short.valueOf("1"));
            assertFalse(true);
        } catch (PersonnelException pe) {
            assertTrue(true);
            assertEquals(pe.getKey(), PersonnelConstants.LO_ONLY_IN_BRANCHES);
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
            assertFalse(true);
        } catch (PersonnelException pe) {
            assertTrue(true);
            assertEquals(pe.getKey(), PersonnelConstants.HIERARCHY_CHANGE_EXCEPTION);
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
            assertFalse(true);
        } catch (PersonnelException pe) {
            assertTrue(true);
            assertEquals(pe.getKey(), PersonnelConstants.STATUS_CHANGE_EXCEPTION);
        }
    }

    public void testUpdateSucess() throws Exception {

        createdBranchOffice = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
        StaticHibernateUtil.closeSession();
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
        createPersonnel(office, PersonnelLevel.NON_LOAN_OFFICER);
        Short noOfTries = personnel.getNoOfTries();
        assertEquals(office.getOfficeId(), personnel.getOffice().getOfficeId());
        assertEquals("XYZ", personnel.getPersonnelDetails().getName().getFirstName());
        assertEquals(Integer.valueOf("1"), personnel.getPersonnelDetails().getGender());
        assertEquals(Integer.valueOf("1"), personnel.getPersonnelDetails().getMaritalStatus());
        assertEquals(PersonnelLevel.NON_LOAN_OFFICER, personnel.getLevelEnum());
        assertTrue(personnel.isActive());
        assertEquals(0, personnel.getPersonnelMovements().size());
        personnel.update(PersonnelStatus.INACTIVE, PersonnelLevel.LOAN_OFFICER, createdBranchOffice, Integer
                .valueOf("2"), Short.valueOf("1"), "ABCD", "abc@yahoo.com", getNewRoles(), getCustomFields(),
                getPersonnelName(), Integer.valueOf("2"), Integer.valueOf("2"), getAddress(), Short.valueOf("1"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
        assertEquals(createdBranchOffice.getOfficeId(), personnel.getOffice().getOfficeId());
        assertEquals(getPersonnelName().getFirstName(), personnel.getPersonnelDetails().getName().getFirstName());
        assertEquals(getPersonnelName().getLastName(), personnel.getPersonnelDetails().getName().getLastName());
        assertEquals(getPersonnelName().getMiddleName(), personnel.getPersonnelDetails().getName().getMiddleName());
        assertEquals(getPersonnelName().getSecondLastName(), personnel.getPersonnelDetails().getName()
                .getSecondLastName());
        assertEquals(getPersonnelName().getDisplayName(), personnel.getDisplayName());
        assertEquals(2, personnel.getPersonnelDetails().getGender().intValue());
        assertEquals(2, personnel.getPersonnelDetails().getMaritalStatus().intValue());
        assertEquals("abc@yahoo.com", personnel.getEmailId());
        assertFalse(personnel.isPasswordChanged());
        assertEquals(2, personnel.getTitle().intValue());
        assertEquals(PersonnelLevel.LOAN_OFFICER, personnel.getLevelEnum());
        assertFalse(personnel.isActive());
        assertEquals(2, personnel.getPersonnelMovements().size());
        assertEquals(1, personnel.getPersonnelRoles().size());
        assertEquals(noOfTries, personnel.getNoOfTries());

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
        assertEquals(getPersonnelName().getFirstName(), personnel.getPersonnelDetails().getName().getFirstName());
        assertEquals(getPersonnelName().getLastName(), personnel.getPersonnelDetails().getName().getLastName());
        assertEquals(getPersonnelName().getMiddleName(), personnel.getPersonnelDetails().getName().getMiddleName());
        assertEquals(getPersonnelName().getSecondLastName(), personnel.getPersonnelDetails().getName()
                .getSecondLastName());
        assertEquals("changed", personnel.getPersonnelDetails().getAddress().getCity());
        assertEquals("changed", personnel.getPersonnelDetails().getAddress().getCountry());
        assertEquals("changed", personnel.getPersonnelDetails().getAddress().getLine1());
        assertEquals("changed", personnel.getPersonnelDetails().getAddress().getLine2());
        assertEquals("changed", personnel.getPersonnelDetails().getAddress().getLine3());
        assertEquals("changed", personnel.getPersonnelDetails().getAddress().getPhoneNumber());
        assertEquals("changed", personnel.getPersonnelDetails().getAddress().getState());
        assertEquals("changed", personnel.getPersonnelDetails().getAddress().getZip());
        assertEquals(2, personnel.getPersonnelDetails().getMaritalStatus().intValue());
        assertEquals("xyz@aditi.com", personnel.getEmailId());
    }

    public void testAddNotes() throws Exception {
        createdBranchOffice = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
        StaticHibernateUtil.closeSession();
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
        createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
        assertEquals(branchOffice.getOfficeId(), personnel.getOffice().getOfficeId());
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
        assertEquals("Size of notes should be 5", 5, personnel.getPersonnelNotes().size());
        assertEquals("Size of recent notes should be 3", 3, personnel.getRecentPersonnelNotes().size());
        for (PersonnelNotesEntity notes : personnel.getPersonnelNotes()) {
            assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), notes.getCommentDate());
            assertEquals("The most recent note should be the last one added", "5.Personnel notes created", notes
                    .getComment());
            break;
        }
    }

    public void testSuccessfullLogin() throws Exception {
        personnel = createPersonnel();
        String password = "ABCD";
        UserContext userContext = personnel.login(password);
        assertFalse(personnel.isLocked());

        assertEquals(personnel.getPersonnelId(), userContext.getId());
        assertEquals(personnel.getDisplayName(), userContext.getName());
        assertEquals(personnel.getLevelEnum(), userContext.getLevel());
        assertEquals(personnel.getLastLogin(), userContext.getLastLogin());
        assertEquals(personnel.getPasswordChanged(), userContext.getPasswordChanged());
        assertEquals(personnel.getPreferredLocale().getLocaleId(), userContext.getLocaleId());
        assertEquals(Localization.getInstance().getLocaleId(), userContext.getMfiLocaleId());
        assertEquals(personnel.getPreferredLocale().getLanguageName(), userContext.getPreferredLocale()
                .getDisplayLanguage(userContext.getPreferredLocale()));
        assertEquals(personnel.getPreferredLocale().getCountryName(), userContext.getPreferredLocale()
                .getDisplayCountry(userContext.getPreferredLocale()));
        assertEquals(personnel.getOffice().getOfficeId(), userContext.getBranchId());
        assertEquals(personnel.getOffice().getGlobalOfficeNum(), userContext.getBranchGlobalNum());
        assertEquals(0, personnel.getNoOfTries().intValue());
        assertFalse(personnel.isPasswordChanged());
        // There should be 2 roles
        assertEquals(getRoles(personnel).size(), userContext.getRoles().size());
    }

    public void testLoginForInvalidPassword() throws Exception {
        personnel = createPersonnel();
        String password = "WRONG_PASSWORD";
        try {
            personnel.login(password);
            fail();
        } catch (PersonnelException e) {
            assertEquals(1, personnel.getNoOfTries().intValue());
            assertFalse(personnel.isLocked());
            assertEquals(LoginConstants.INVALIDOLDPASSWORD, e.getKey());
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
            fail();
        } catch (PersonnelException e) {
            assertEquals(0, personnel.getNoOfTries().intValue());
            assertFalse(personnel.isLocked());
            assertEquals(LoginConstants.KEYUSERINACTIVE, e.getKey());
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
            fail();
        } catch (PersonnelException e) {
            assertEquals(5, personnel.getNoOfTries().intValue());
            assertTrue(personnel.isLocked());
            assertEquals(LoginConstants.INVALIDOLDPASSWORD, e.getKey());
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
        assertFalse(personnel.isLocked());
        assertEquals("No of tries should be reseted to 0", 0, personnel.getNoOfTries().intValue());
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
            fail();
        } catch (PersonnelException e) {
            assertEquals(5, personnel.getNoOfTries().intValue());
            assertTrue(personnel.isLocked());
            assertEquals(LoginConstants.KEYUSERLOCKED, e.getKey());
        }
    }

    public void testUpdatePasswordWithOldPassword() throws Exception {
        personnel = createPersonnel();
        assertNull(personnel.getLastLogin());
        personnel.updatePassword("ABCD", "NEW_PASSWORD", Short.valueOf("1"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
        assertTrue(personnel.isPasswordChanged());
        assertNotNull(personnel.getLastLogin());
    }

    public void testUpdatePassword() throws Exception {
        personnel = createPersonnel();
        Short mifosId = new Short((short) 1);
        personnel.updatePassword("testPassword", mifosId);
        assertEquals(personnel.getUpdatedBy(), mifosId);
        assertNotNull(personnel.getLastLogin());
        assertEquals(personnel.getPasswordChanged(), LoginConstants.PASSWORDCHANGEDFLAG);
    }

    public void testUpdatePasswordWithWrongOldPassword() throws Exception {
        personnel = createPersonnel();
        assertNull(personnel.getLastLogin());
        try {
            personnel.updatePassword("WRONGOLD_PASSWORD", "NEW_PASSWORD", Short.valueOf("1"));
            fail();
        } catch (PersonnelException e) {
            assertEquals(LoginConstants.INVALIDOLDPASSWORD, e.getKey());
            assertFalse(personnel.isPasswordChanged());
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
        assertTrue(personnel.isLocked());
        personnel.unlockPersonnel(Short.valueOf("1"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
        assertFalse(personnel.isLocked());
        assertEquals(0, personnel.getNoOfTries().intValue());
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
            fail();
        } catch (PersonnelException e) {
            assertEquals(CustomerConstants.UPDATE_FAILED_EXCEPTION, e.getKey());
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
        assertTrue(personnel.isLocked());
        personnel.unlockPersonnel(Short.valueOf("1"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
        assertFalse(personnel.isLocked());
        assertEquals(0, personnel.getNoOfTries().intValue());

        personnel.login("ABCD");
        assertEquals(0, personnel.getNoOfTries().intValue());
    }

    public void testPersonnelView() throws Exception {
        PersonnelView personnelView = new PersonnelView(Short.valueOf("1"), "Raj");
        assertEquals(Short.valueOf("1"), personnelView.getPersonnelId());
        assertEquals("Raj", personnelView.getDisplayName());
    }

    public void testGetLocaleId() throws Exception {
        Short localeId = 1;
        PersonnelBO personnel = new PersonnelBO();
        personnel.setPreferredLocale(new SupportedLocalesEntity(localeId));

        assertEquals(localeId, personnel.getLocaleId());
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
            fail();
        } catch (PersonnelException e) {
            assertEquals(PersistenceException.class, e.getCause().getClass());
            assertEquals(ConstraintViolationException.class, e.getCause().getCause().getClass());
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

        center = TestObjectFactory.createCenter("Center", meeting, officeId, personnelId);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
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
            fail();
        } catch (PersonnelException expected) {
            assertEquals(LoginConstants.INVALIDOLDPASSWORD, expected.getKey());
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
