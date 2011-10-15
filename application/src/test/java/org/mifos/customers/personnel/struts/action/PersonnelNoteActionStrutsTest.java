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
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.builders.MifosUserBuilder;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.MifosUser;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.UserContext;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

public class PersonnelNoteActionStrutsTest extends MifosMockStrutsTestCase {



    private String flowKey;

    private UserContext userContext;

    private OfficeBO createdBranchOffice;

    PersonnelBO personnel;

    @Before
    public void setUp() throws Exception {
        userContext = TestUtils.makeUser();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        ActivityContext ac = new ActivityContext((short) 0, userContext.getBranchId().shortValue(), userContext.getId()
                .shortValue());
        request.getSession(false).setAttribute("ActivityContext", ac);
        flowKey = createFlow(request, PersonnelNoteAction.class);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
    }

    @After
    public void tearDown() throws Exception {
        userContext = null;
        personnel = null;
        createdBranchOffice = null;
    }

    @Test
    public void testSuccessLoadPersonnelNote() throws Exception {
        createPersonnelAndSetInSession(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        setRequestPathInfo("/personnelNoteAction.do");
        addRequestParameter("method", Methods.load.toString());
        addRequestParameter("personnelId", personnel.getPersonnelId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());
    }

    @Test
    public void testFailurePreviewWithNotesValueNull() throws Exception {
        setRequestPathInfo("/personnelNoteAction.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
       Assert.assertEquals(1, getErrorSize());
       Assert.assertEquals("Notes", 1, getErrorSize(PersonnelConstants.ERROR_MANDATORY_TEXT_AREA));
        verifyInputForward();
    }

    @Test
    public void testFailurePreviewWithNotesValueExceedingMaxLength() throws Exception {
        setRequestPathInfo("/personnelNoteAction.do");
        addRequestParameter("comment", "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters "
                + "Testing for comment length exceeding by 500 characters "
                + "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
       Assert.assertEquals(1, getErrorSize());
       Assert.assertEquals("Notes", 1, getErrorSize(PersonnelConstants.MAXIMUM_LENGTH));
        verifyInputForward();
    }

    @Test
    public void testSuccessPreviewPersonnelNote() throws Exception {
        setRequestPathInfo("/personnelNoteAction.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter("comment", "Test");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.preview_success.toString());
    }

    @Test
    public void testSuccessPreviousPersonnelNote() {
        setRequestPathInfo("/personnelNoteAction.do");
        addRequestParameter("method", Methods.previous.toString());
        addRequestParameter("comment", "Test");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(ActionForwards.previous_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    @Test
    public void testSuccessCancelPersonnelNote() {
        setRequestPathInfo("/personnelNoteAction.do");
        addRequestParameter("method", Methods.cancel.toString());
        addRequestParameter("comment", "Test");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(ActionForwards.cancel_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    @Test
    public void testSuccessCreatePersonnelNote() throws Exception {

        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().nonLoanOfficer().withAdminRole().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        createPersonnelAndSetInSession(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        setRequestPathInfo("/personnelNoteAction.do");
        addRequestParameter("method", Methods.create.toString());
        addRequestParameter("personnelId", personnel.getPersonnelId().toString());
        addRequestParameter("comment", "Test");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(ActionForwards.create_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    @Test
    public void testSuccessSearch() throws Exception {
        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().nonLoanOfficer().withAdminRole().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        createPersonnelAndSetInSession(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
        setRequestPathInfo("/personnelNoteAction.do");
        addRequestParameter("method", Methods.load.toString());
        addRequestParameter("personnelId", personnel.getPersonnelId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        setRequestPathInfo("/personnelNoteAction.do");
        addRequestParameter("method", Methods.preview.toString());
        addRequestParameter("comment", "Notes created");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        setRequestPathInfo("/personnelNoteAction.do");
        addRequestParameter("method", Methods.create.toString());
        addRequestParameter("comment", "Notes created");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        StaticHibernateUtil.flushSession();

        setRequestPathInfo("/PersonAction.do");
        addRequestParameter("method", Methods.get.toString());

        addRequestParameter("globalPersonnelNum", personnel.getGlobalPersonnelNum());
        StaticHibernateUtil.flushAndClearSession();
        actionPerform();

        setRequestPathInfo("/personnelNoteAction.do");
        addRequestParameter("method", Methods.search.toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(ActionForwards.search_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();

       Assert.assertEquals("Size of the search result should be 1", 1, ((QueryResult) SessionUtils.getAttribute(
                Constants.SEARCH_RESULTS, request)).getSize());
        StaticHibernateUtil.flushSession();

        personnel = (PersonnelBO) TestObjectFactory.getObject(PersonnelBO.class, personnel.getPersonnelId());

    }

    private void createPersonnelAndSetInSession(OfficeBO office, PersonnelLevel personnelLevel) throws Exception {
        List<CustomFieldDto> customFieldDto = new ArrayList<CustomFieldDto>();
        customFieldDto.add(new CustomFieldDto(Short.valueOf("9"), "123456", CustomFieldType.NUMERIC.getValue()));
        Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd");
        Name name = new Name("XYZ", null, null, "Last Name");
        Date date = new Date();
        personnel = new PersonnelBO(personnelLevel, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD", "XYZ",
                "xyz@yahoo.com", null, customFieldDto, name, "111111", date, Integer.valueOf("1"), Integer
                        .valueOf("1"), date, date, address, userContext.getId());
        IntegrationTestObjectMother.createPersonnel(personnel);
        personnel = IntegrationTestObjectMother.findPersonnelById(personnel.getPersonnelId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, personnel, request);
    }

    @Override
    public OfficeBO getBranchOffice() {
        return TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
    }
}
