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

package org.mifos.customers.personnel.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.UserContext;

public class PersonnelSettingsActionStrutsTest extends MifosMockStrutsTestCase {
    public PersonnelSettingsActionStrutsTest() throws Exception {
        super();
    }

    private String flowKey;

    private UserContext userContext;

    PersonnelBO personnel;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestUtils.makeUser();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        ActivityContext ac = new ActivityContext((short) 0, userContext.getBranchId().shortValue(), userContext.getId()
                .shortValue());
        request.getSession(false).setAttribute("ActivityContext", ac);
        flowKey = createFlow(request, PersonnelSettingsAction.class);
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

    @Override
    protected void tearDown() throws Exception {
        userContext = null;
        TestObjectFactory.cleanUp(personnel);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGet() throws Exception {
        createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.get.toString());
        addRequestParameter("globalPersonnelNum", personnel.getGlobalPersonnelNum());
        performNoErrors();
        verifyMasterData();
        verifyForward(ActionForwards.get_success.toString());
    }

    public void testManage() throws Exception {
        createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.get.toString());
        addRequestParameter("globalPersonnelNum", personnel.getGlobalPersonnelNum());
        actionPerform();

        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.manage.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        performNoErrors();
        verifyForward(ActionForwards.manage_success.toString());
    }

    public void testFailurePreview() throws Exception {
        createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter("middleName", personnel.getPersonnelDetails().getName().getMiddleName());
        addRequestParameter("secondLastName", personnel.getPersonnelDetails().getName().getSecondLastName());
        addRequestParameter("maritalStatus", personnel.getPersonnelDetails().getMaritalStatus().toString());
        addRequestParameter("emailId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
       Assert.assertEquals(1, getErrorSize("firstName"));
       Assert.assertEquals(1, getErrorSize("lastName"));
       Assert.assertEquals(1, getErrorSize("gender"));
       Assert.assertEquals(1, getErrorSize(PersonnelConstants.ERROR_VALID_EMAIL));
        verifyInputForward();
    }

    public void testFailurePreviewNoFirstName() throws Exception {
        createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter("middleName", personnel.getPersonnelDetails().getName().getMiddleName());
        addRequestParameter("secondLastName", personnel.getPersonnelDetails().getName().getSecondLastName());
        addRequestParameter("lastName", personnel.getPersonnelDetails().getName().getLastName());
        addRequestParameter("gender", personnel.getPersonnelDetails().getGender().toString());
        addRequestParameter("maritalStatus", personnel.getPersonnelDetails().getMaritalStatus().toString());
        addRequestParameter("emailId", personnel.getEmailId());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
       Assert.assertEquals(1, getErrorSize("firstName"));
        verifyInputForward();
    }

    public void testFailurePreviewFirstNameLengthExceedsLimit() throws Exception {
        createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter("firstName", "Testing for firstName length exceeding by 100 characters"
                + "Testing for firstName length exceeding by 100 characters"
                + "Testing for firstName length exceeding by 100 characters"
                + "Testing for firstName length exceeding by 100 characters"
                + "Testing for firstName length exceeding by 100 characters "
                + "Testing for firstName length exceeding by 100 characters "
                + "Testing for firstName length exceeding by 100 characters");
        addRequestParameter("middleName", personnel.getPersonnelDetails().getName().getMiddleName());
        addRequestParameter("secondLastName", personnel.getPersonnelDetails().getName().getSecondLastName());
        addRequestParameter("lastName", personnel.getPersonnelDetails().getName().getLastName());
        addRequestParameter("gender", personnel.getPersonnelDetails().getGender().toString());
        addRequestParameter("maritalStatus", personnel.getPersonnelDetails().getMaritalStatus().toString());
        addRequestParameter("emailId", personnel.getEmailId());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
       Assert.assertEquals(1, getErrorSize("firstName"));
        verifyInputForward();
    }

    public void testFailurePreviewLastNameLengthExceedsLimit() throws Exception {
        createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter("lastName", "Testing for lastName length exceeding by 100 characters"
                + "Testing for lastName length exceeding by 100 characters"
                + "Testing for lastName length exceeding by 100 characters"
                + "Testing for lastName length exceeding by 100 characters"
                + "Testing for lastName length exceeding by 100 characters "
                + "Testing for lastName length exceeding by 100 characters "
                + "Testing for lastName length exceeding by 100 characters");
        addRequestParameter("middleName", personnel.getPersonnelDetails().getName().getMiddleName());
        addRequestParameter("secondLastName", personnel.getPersonnelDetails().getName().getSecondLastName());
        addRequestParameter("firstName", personnel.getPersonnelDetails().getName().getFirstName());
        addRequestParameter("gender", personnel.getPersonnelDetails().getGender().toString());
        addRequestParameter("maritalStatus", personnel.getPersonnelDetails().getMaritalStatus().toString());
        addRequestParameter("emailId", personnel.getEmailId());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
       Assert.assertEquals(1, getErrorSize("lastName"));
        verifyInputForward();
    }

    public void testFailurePreviewNoLastName() throws Exception {
        createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter("firstName", personnel.getPersonnelDetails().getName().getFirstName());
        addRequestParameter("middleName", personnel.getPersonnelDetails().getName().getMiddleName());
        addRequestParameter("secondLastName", personnel.getPersonnelDetails().getName().getSecondLastName());
        addRequestParameter("gender", personnel.getPersonnelDetails().getGender().toString());
        addRequestParameter("maritalStatus", personnel.getPersonnelDetails().getMaritalStatus().toString());
        addRequestParameter("emailId", personnel.getEmailId());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
       Assert.assertEquals(1, getErrorSize("lastName"));
        verifyInputForward();
    }

    public void testFailurePreviewNoGenderSelected() throws Exception {
        createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter("firstName", personnel.getPersonnelDetails().getName().getFirstName());
        addRequestParameter("middleName", personnel.getPersonnelDetails().getName().getMiddleName());
        addRequestParameter("secondLastName", personnel.getPersonnelDetails().getName().getSecondLastName());
        addRequestParameter("lastName", personnel.getPersonnelDetails().getName().getLastName());
        addRequestParameter("maritalStatus", personnel.getPersonnelDetails().getMaritalStatus().toString());
        addRequestParameter("emailId", personnel.getEmailId());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
       Assert.assertEquals(1, getErrorSize("gender"));
        verifyInputForward();
    }

    public void testFailurePreviewDisplayLengthExceedsMaxLimit() throws Exception {
        createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        userContext.setId(personnel.getPersonnelId());
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.get.toString());
        addRequestParameter("globalPersonnelNum", personnel.getGlobalPersonnelNum());
        actionPerform();

        userContext.setId(PersonnelConstants.SYSTEM_USER);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.manage.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter("firstName",
                "Testing for displayName length exceeding by 200 characters.It should be less than 200");
        addRequestParameter("middleName", "new middle name");
        addRequestParameter("secondLastName", "new second Last name");
        addRequestParameter("lastName",
                "Testing for displayName length exceeding by 200 characters.It should be less than 200");
        addRequestParameter("gender", "2");
        addRequestParameter("maritalStatus", "2");
        addRequestParameter("emailId", "XYZ@aditi.com");
        addRequestParameter("preferredLocale", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

       Assert.assertEquals(1, getErrorSize("displayName"));
        verifyInputForward();
    }

    public void testSuccessPreview() throws Exception {
        createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter("firstName", personnel.getPersonnelDetails().getName().getFirstName());
        addRequestParameter("middleName", personnel.getPersonnelDetails().getName().getMiddleName());
        addRequestParameter("secondLastName", personnel.getPersonnelDetails().getName().getSecondLastName());
        addRequestParameter("lastName", personnel.getPersonnelDetails().getName().getLastName());
        addRequestParameter("gender", personnel.getPersonnelDetails().getGender().toString());
        addRequestParameter("maritalStatus", personnel.getPersonnelDetails().getMaritalStatus().toString());
        addRequestParameter("emailId", personnel.getEmailId());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        performNoErrors();
        verifyForward(ActionForwards.preview_success.toString());
    }

    public void testPrevious() throws Exception {
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.previous.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        performNoErrors();
        verifyForward(ActionForwards.previous_success.toString());
    }

    public void testSuccessUpdate() throws Exception {
        createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        userContext.setId(personnel.getPersonnelId());
        request.getSession().setAttribute(Constants.USER_CONTEXT_KEY, userContext);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.get.toString());
        addRequestParameter("globalPersonnelNum", personnel.getGlobalPersonnelNum());
        actionPerform();

        userContext.setId(PersonnelConstants.SYSTEM_USER);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.manage.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter("firstName", "new first name");
        addRequestParameter("middleName", "new middle name");
        addRequestParameter("secondLastName", "new second Last name");
        addRequestParameter("lastName", "new last name");
        addRequestParameter("gender", "2");
        addRequestParameter("maritalStatus", "2");
        addRequestParameter("emailId", "XYZ@aditi.com");
        addRequestParameter("preferredLocale", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.update.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        performNoErrors();
        verifyForward(ActionForwards.updateSettings_success.toString());

        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
       Assert.assertEquals("new first name", personnel.getPersonnelDetails().getName().getFirstName());
       Assert.assertEquals("new middle name", personnel.getPersonnelDetails().getName().getMiddleName());
       Assert.assertEquals("new second Last name", personnel.getPersonnelDetails().getName().getSecondLastName());
       Assert.assertEquals("new last name", personnel.getPersonnelDetails().getName().getLastName());
       Assert.assertEquals("XYZ@aditi.com", personnel.getEmailId());
       Assert.assertEquals(2, personnel.getPersonnelDetails().getGender().intValue());
       Assert.assertEquals(1, personnel.getPreferredLocale().getLocaleId().intValue());
       Assert.assertEquals(2, personnel.getPersonnelDetails().getMaritalStatus().intValue());
    }

    public void testLoadChangePassword() throws Exception {
        createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.loadChangePassword.toString());
        performNoErrors();
        verifyForward(ActionForwards.loadChangePassword_success.toString());
    }

    public void testGet_batchJobNotRunning() throws Exception {
       Assert.assertEquals(false, MifosTask.isBatchJobRunning());
        createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.get.toString());
        addRequestParameter("globalPersonnelNum", personnel.getGlobalPersonnelNum());
        performNoErrors();
        verifyMasterData();
        verifyForward(ActionForwards.get_success.toString());
    }

    public void testGet_batchJobRunning() throws Exception {
        MifosTask.batchJobStarted();
       Assert.assertEquals(true, MifosTask.isBatchJobRunning());
        createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        setRequestPathInfo("/yourSettings.do");
        addRequestParameter("method", Methods.get.toString());
        addRequestParameter("globalPersonnelNum", personnel.getGlobalPersonnelNum());
        actionPerform();
        verifyForward(ActionForwards.load_main_page.toString());
        MifosTask.batchJobFinished();
    }

    private void verifyMasterData() throws Exception {
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.GENDER_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.MARITAL_STATUS_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(PersonnelConstants.LANGUAGE_LIST, request));
    }

    private PersonnelBO createPersonnel(OfficeBO office, PersonnelLevel personnelLevel) throws Exception {
        List<CustomFieldDto> customFieldDto = new ArrayList<CustomFieldDto>();
        customFieldDto.add(new CustomFieldDto(Short.valueOf("9"), "123456", CustomFieldType.NUMERIC));
        Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd");
        Date date = new Date();
        personnel = new PersonnelBO(personnelLevel, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD", "XYZ",
                "xyz@yahoo.com", getRoles(), customFieldDto, new Name("XYZ", null, null, "ABC"), "111111", date,
                Integer.valueOf("1"), Integer.valueOf("1"), date, date, address, userContext.getId());
        personnel.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
        return personnel;
    }

    public List<RoleBO> getRoles() throws Exception {
        return ((PersonnelBusinessService) ServiceFactory.getInstance().getBusinessService(
                BusinessServiceName.Personnel)).getRoles();
    }

    private OfficeBO getBranchOffice() {
        return TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);

    }
}
