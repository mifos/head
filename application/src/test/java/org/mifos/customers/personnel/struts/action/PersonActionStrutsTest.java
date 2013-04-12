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

package org.mifos.customers.personnel.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.Localization;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.customers.personnel.struts.actionforms.PersonActionForm;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.screen.PersonnelInformationDto;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.audit.persistence.LegacyAuditDao;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;

public class PersonActionStrutsTest extends MifosMockStrutsTestCase {


    private String flowKey;

    private UserContext userContext;

    private OfficeBO createdBranchOffice;

    PersonnelBO personnel;

    @Autowired
    private LegacyAuditDao legacyAuditDao;

    @Before
    public void setUp() throws Exception {
        userContext = TestUtils.makeUser();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        ActivityContext ac = new ActivityContext((short) 0, userContext.getBranchId().shortValue(), userContext.getId()
                .shortValue());
        request.getSession(false).setAttribute("ActivityContext", ac);

        flowKey = createFlow(request, PersonAction.class);

        EntityMasterData.getInstance().init();
        FieldConfig fieldConfig = FieldConfig.getInstance();
        fieldConfig.init();
        getActionServlet().getServletContext().setAttribute(Constants.FIELD_CONFIGURATION,
                fieldConfig.getEntityMandatoryFieldMap());
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("input", "CreateUser");
        PersonnelBusinessService personnelBusinessService = new PersonnelBusinessService();
        SessionUtils.setAttribute(PersonnelConstants.OFFICE, personnelBusinessService
                .getOffice(TestObjectFactory.HEAD_OFFICE), request);
        SessionUtils
                .setCollectionAttribute(PersonnelConstants.ROLES_LIST, personnelBusinessService.getRoles(), request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.ROLEMASTERLIST, personnelBusinessService.getRoles(),
                request);

        personnelBusinessService = null;

    }

    @After
    public void tearDown() throws Exception {
        userContext = null;
        personnel = null;
        createdBranchOffice = null;
    }

    @Test
    public void testChooseOffice() {
        addActionAndMethod(Methods.chooseOffice.toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.chooseOffice_success.toString());
    }

    @Test
    public void testLoad() throws Exception {
        addActionAndMethod(Methods.load.toString());
        addRequestParameter("officeId", "1");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        OfficeBO office = (OfficeBO) SessionUtils.getAttribute(PersonnelConstants.OFFICE, request);
        Assert.assertNotNull(office);
        Assert.assertEquals(1, office.getOfficeId().intValue());
        verifyMasterData();
        PersonActionForm personActionForm = (PersonActionForm) request.getSession().getAttribute("personActionForm");
        Assert.assertNotNull(personActionForm);
//        Assert.assertEquals(1, personActionForm.getCustomFields().size());
        verifyForward(ActionForwards.load_success.toString());
        PersonActionForm actionForm = (PersonActionForm) request.getSession().getAttribute("personActionForm");
        Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
        Assert.assertEquals(currentDate, DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfJoiningMFI()));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadWithBranchOffice() throws Exception {
        addActionAndMethod(Methods.load.toString());
        addRequestParameter("officeId", "3");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        OfficeBO office = (OfficeBO) SessionUtils.getAttribute(PersonnelConstants.OFFICE, request);
        Assert.assertNotNull(office);
        Assert.assertEquals(3, office.getOfficeId().intValue());
        verifyMasterData();
        PersonActionForm personActionForm = (PersonActionForm) request.getSession().getAttribute("personActionForm");
        Assert.assertNotNull(personActionForm);
//        Assert.assertEquals(1, personActionForm.getCustomFields().size());
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.PERSONNEL_LEVEL_LIST, request));
        Assert.assertEquals(2, ((List) SessionUtils.getAttribute(PersonnelConstants.PERSONNEL_LEVEL_LIST, request))
                .size());
        verifyForward(ActionForwards.load_success.toString());
    }

    @Test
    public void testPreviewFailure() throws Exception {
        addActionAndMethod(Methods.preview.toString());
        actionPerform();
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.ERROR_FIRSTNAME));
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.ERROR_LASTNAME));
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.ERROR_GENDER));
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.ERROR_LEVEL));
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.ERROR_USER_NAME));
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.PASSWORD));
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.ERROR_DOB));
        verifyInputForward();
    }

    @Test
    @Ignore
    public void testPreviewFailureWrongPasswordLength() throws Exception {
        addActionAndMethod(Methods.preview.toString());
        setRequestData();
        addRequestParameter("userPassword", "XXX");
        actionPerform();
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.ERROR_PASSWORD_LENGTH));
        verifyInputForward();
    }

    @Test
    public void testPreviewFailureWrongPasswordAndReaptPassword() throws Exception {
        addActionAndMethod(Methods.preview.toString());
        setRequestData();
        addRequestParameter("userPassword", "XXXXXX");
        addRequestParameter("passwordRepeat", "XXXXXZ");
        actionPerform();
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.PASSWORD));
        verifyInputForward();
    }

    @Test
    public void testPreviewSucess() throws Exception {
        addActionAndMethod(Methods.preview.toString());
        addRequestParameter("userPassword", "XXXXXXXX");
        addRequestParameter("passwordRepeat", "XXXXXXXX");

        setRequestData();
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.preview_success.toString());
    }

    @Test
    public void testPreviousSucess() throws Exception {
        addActionAndMethod(Methods.previous.toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.previous_success.toString());
    }

    @Test
    public void testGetSucess() throws Exception {
        addActionAndMethod(Methods.get.toString());
        addRequestParameter("globalPersonnelNum", "1");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyMasterData();
        verifyForward(ActionForwards.get_success.toString());
    }

    @Test
    public void testSearchSucess() throws Exception {
        addActionAndMethod(Methods.search.toString());
        addRequestParameter("searchString", "Mi");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        QueryResult queryResult = (QueryResult) SessionUtils.getAttribute(Constants.SEARCH_RESULTS, request);
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(1, queryResult.getSize());
        Assert.assertEquals(1, queryResult.get(0, 10).size());
        verifyForward(ActionForwards.search_success.toString());
    }

    @Test
    public void testSearchwithNoinput() throws Exception {
        cleanRequest();
        addActionAndMethod(Methods.search.toString());
        addRequestParameter("input", "");
        actionPerform();
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.NO_SEARCH_STRING));
        verifyInputForward();
    }

    @Test
    public void testLoadSearchSucess() throws Exception {
        addActionAndMethod(Methods.search.toString());
        addRequestParameter("searchString", "Mi");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.search_success.toString());
    }

    private void addActionAndMethod(String method) {
        setRequestPathInfo("/PersonAction.do");
        addRequestParameter("method", method);

    }

    private void cleanRequest() throws PageExpiredException {
        SessionUtils.removeAttribute(PersonnelConstants.OFFICE, request);
        SessionUtils.removeAttribute(PersonnelConstants.ROLES_LIST, request);
        SessionUtils.removeAttribute(PersonnelConstants.ROLEMASTERLIST, request);

    }

    @SuppressWarnings("unchecked")
    private void verifyMasterData() throws Exception {
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.TITLE_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.PERSONNEL_LEVEL_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.GENDER_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.MARITAL_STATUS_LIST, request));
        List languages = (List) SessionUtils.getAttribute(PersonnelConstants.LANGUAGE_LIST, request);
        Assert.assertNotNull(languages);
        Assert.assertEquals(Localization.getInstance().getLocaleForUI().size(), languages.size());
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.ROLES_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
    }

    @Test
    public void testManage() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createPersonnelAndSetInSession(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        addActionAndMethod(Methods.manage.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        StaticHibernateUtil.flushAndClearSession();
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.TITLE_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.PERSONNEL_LEVEL_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.GENDER_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.MARITAL_STATUS_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.LANGUAGE_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.ROLES_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
    }

    @Test
    public void testPreviewManage() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createPersonnelAndSetInSession(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        addActionAndMethod(Methods.manage.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        StaticHibernateUtil.flushAndClearSession();
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.TITLE_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.PERSONNEL_LEVEL_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.GENDER_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.MARITAL_STATUS_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.LANGUAGE_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.ROLES_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
        addActionAndMethod(Methods.previewManage.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("userPassword", "abcdef");
        addRequestParameter("passwordRepeat", "abcdef");
        addRequestParameter("personnelRoles", "1");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.previewManage_success.toString());
    }

    @Test
    public void testManagePreviewFailure() throws Exception {
        addActionAndMethod(Methods.previewManage.toString());
        actionPerform();
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.ERROR_FIRSTNAME));
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.ERROR_LASTNAME));
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.ERROR_GENDER));
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.ERROR_LEVEL));
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.ERROR_USER_NAME));
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.PASSWORD));
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.ERROR_DOB));
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.OFFICE));
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.STATUS));
        verifyInputForward();
    }

    @Test
    public void testManagePreviewFailureWrongPasswordLength() throws Exception {
        addActionAndMethod(Methods.preview.toString());
        setRequestData();
        addRequestParameter("userPassword", "XXX");
        actionPerform();
        Assert.assertEquals(2, getErrorSize("password"));
        verifyInputForward();
    }

    @Test
    public void testManagePreviewFailureLoginNameWithSpace() throws Exception {
        addActionAndMethod(Methods.preview.toString());
        setRequestData();
        addRequestParameter("loginName", "XYZ PQR");
        actionPerform();
        Assert.assertEquals(1, getErrorSize(PersonnelConstants.INVALID_USER_NAME));
        verifyInputForward();
    }

    @Test
    public void testManagePreviewFailureWrongPasswordAndReaptPassword() throws Exception {
        addActionAndMethod(Methods.preview.toString());
        setRequestData();
        addRequestParameter("userPassword", "XXXXXX");
        addRequestParameter("passwordRepeat", "XXXXXZ");
        actionPerform();
        Assert.assertEquals(1, getErrorSize("password"));
        verifyInputForward();
    }

    @Test
    public void testLoadUnLockUser() throws Exception {
        addActionAndMethod(Methods.loadUnLockUser.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.loadUnLockUser_success.toString());
        Assert.assertEquals(LoginConstants.MAXTRIES, SessionUtils.getAttribute(PersonnelConstants.LOGIN_ATTEMPTS_COUNT,
                request));
    }

    @Test
    public void testUnLockUser() throws Exception {
        createPersonnelAndSetInSession(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        addActionAndMethod(Methods.unLockUserAccount.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.unLockUserAccount_success.toString());
        Assert.assertFalse(personnel.isLocked());
        Assert.assertEquals(0, personnel.getNoOfTries().intValue());
    }

    private void createPersonnelAndSetInSession(OfficeBO office, PersonnelLevel personnelLevel) throws Exception {
        List<CustomFieldDto> customFieldDto = new ArrayList<CustomFieldDto>();
        customFieldDto.add(new CustomFieldDto(Short.valueOf("9"), "123456", CustomFieldType.NUMERIC.getValue()));
        Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd");
        Name name = new Name("XYZ", null, null, "Last Name");
        Date date = new Date();
        personnel = new PersonnelBO(personnelLevel, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD", "XYZ",
                "xyz@yahoo.com", null, customFieldDto, name, "111111", date, Integer.valueOf("1"),
                Integer.valueOf("1"), date, date, address, userContext.getId(), new Date(), new HashSet());
        IntegrationTestObjectMother.createPersonnel(personnel);
        personnel = IntegrationTestObjectMother.findPersonnelById(personnel.getPersonnelId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, personnel, request);
    }

    @Override
    public OfficeBO getBranchOffice() {
        return TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
    }

    private void setRequestData() throws InvalidDateException {
        addRequestParameter("firstName", "Jim");
        addRequestParameter("lastName", "khan");
        addRequestParameter("gender", "1");
        addRequestParameter("level", "1");
        addRequestParameter("title", "1");
        addRequestParameter("emailId", "1@1.com");
        addRequestDateParameter("dob", "20/03/76");
        addRequestParameter("loginName", "tarzen");
        addRequestParameter("personnelRoles", "1");
        addRequestParameter("preferredLocale", "1");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadChangeLog() throws Exception {
        addActionAndMethod(Methods.get.toString());
        addRequestParameter("globalPersonnelNum", "1");
        actionPerform();
        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        // personnel = (PersonnelBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        // Changed the PersonnelBO to PersonnelInformationDto as the former is no longer stored in session using
        // business key
        PersonnelInformationDto personnel = (PersonnelInformationDto) SessionUtils.getAttribute(
                "personnelInformationDto", request);
        AuditLog auditLog = new AuditLog(personnel.getPersonnelId().intValue(), EntityType.PERSONNEL.getValue(),
                "Mifos", new java.sql.Date(System.currentTimeMillis()), Short.valueOf("3"));
        Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
        AuditLogRecord auditLogRecord = new AuditLogRecord("ColumnName_1", "test_1", "new_test_1", auditLog);
        auditLogRecords.add(auditLogRecord);
        auditLog.addAuditLogRecords(auditLogRecords);

        legacyAuditDao.save(auditLog);

        setRequestPathInfo("/PersonAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("method", "loadChangeLog");
        addRequestParameter("entityType", "Personnel");
        addRequestParameter("entityId", personnel.getPersonnelId().toString());
        actionPerform();
        Assert.assertEquals(1, ((List) request.getSession().getAttribute(AuditConstants.AUDITLOGRECORDS)).size());
        verifyForward("viewPersonnelChangeLog");
        personnel = null;

    }

    @Test
    public void testCancelChangeLog() {
        setRequestPathInfo("/PersonAction.do");
        addRequestParameter("method", "cancelChangeLog");
        addRequestParameter("entityType", "Personnel");
        actionPerform();
        verifyForward("cancelPersonnelChangeLog");
        personnel = null;
    }
}
