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

package org.mifos.customers.center.struts.action;

import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.builders.MifosUserBuilder;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.center.struts.actionforms.CenterCustActionForm;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.domain.builders.FeeBuilder;
import org.mifos.domain.builders.MeetingBuilder;
import org.mifos.dto.domain.ApplicableAccountFeeDto;
import org.mifos.dto.domain.CenterInformationDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
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
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

public class CenterActionStrutsTest extends MifosMockStrutsTestCase {


    private CenterBO center;
    private GroupBO group;
    private String flowKey;
    private final SavingsTestHelper helper = new SavingsTestHelper();
    private SavingsOfferingBO savingsOffering;
    private SavingsBO savingsBO;
    private static final String dateFormat = "dd/MM/yyyy";

    @Override
    protected void setStrutsConfig() throws IOException {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/customer-struts-config.xml");
    }

    @Before
    public void setUp() throws Exception {

        UserContext userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());

        flowKey = createFlow(request, CenterCustAction.class);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

        EntityMasterData.getInstance().init();
        FieldConfig fieldConfig = FieldConfig.getInstance();
        fieldConfig.init();
        getActionServlet().getServletContext().setAttribute(Constants.FIELD_CONFIGURATION,
                fieldConfig.getEntityMandatoryFieldMap());
    }

    @After
    public void tearDown() throws Exception {
        savingsBO = null;
        group = null;
        center = null;
    }

    @Test
    public void testLoad() throws Exception {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.LOAN_OFFICER_LIST, request));
//        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));

        CenterCustActionForm actionForm = (CenterCustActionForm) request.getSession().getAttribute(
                "centerCustActionForm");

        String currentDate = DateUtils.getCurrentDate(TestUtils.ukLocale());
        SimpleDateFormat retrievedFormat = new SimpleDateFormat(dateFormat);
        SimpleDateFormat localFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, TestUtils
                .ukLocale());
        Date curDate = localFormat.parse(currentDate);
        Date retrievedDate = retrievedFormat.parse(actionForm.getMfiJoiningDate());
        Assert.assertEquals(curDate, retrievedDate);
    }

    @Test
    public void testFailurePreviewWithAllValuesNull() throws Exception {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertEquals("Center Name", 1, getErrorSize(CustomerConstants.NAME));
        Assert.assertEquals("Loan Officer", 1, getErrorSize(CustomerConstants.LOAN_OFFICER));
        Assert.assertEquals("Meeting", 1, getErrorSize(CustomerConstants.MEETING));
        verifyInputForward();
    }

    @Test
    public void testFailurePreviewWithNameNotNull() throws Exception {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("officeId", "3");
        addRequestParameter("displayName", "center");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertEquals("Center Name", 0, getErrorSize(CustomerConstants.NAME));
        Assert.assertEquals("Loan Officer", 1, getErrorSize(CustomerConstants.LOAN_OFFICER));
        Assert.assertEquals("Meeting", 1, getErrorSize(CustomerConstants.MEETING));
        verifyInputForward();
    }

    @Test
    public void testFailurePreviewWithLoanOfficerNotNull() throws Exception {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("officeId", "3");
        addRequestParameter("displayName", "center");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertEquals("Center Name", 0, getErrorSize(CustomerConstants.NAME));
        Assert.assertEquals("Loan Officer", 0, getErrorSize(CustomerConstants.LOAN_OFFICER));
        Assert.assertEquals("Meeting", 1, getErrorSize(CustomerConstants.MEETING));
        verifyInputForward();
    }

    @Test
    public void testFailurePreviewWithMeetingNull() throws Exception {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        actionPerform();

        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("officeId", "3");
        addRequestParameter("displayName", "center");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        Assert.assertEquals("Meeting", 1, getErrorSize(CustomerConstants.MEETING));
        verifyInputForward();
    }

    @Test
    public void testFailurePreview_WithDuplicateFee() throws Exception {
        List<FeeDto> feesToRemove = getFees(RecurrenceType.MONTHLY);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        actionPerform();

        List<ApplicableAccountFeeDto> feeList = retrieveAdditionalFeesFromSession();
        ApplicableAccountFeeDto fee = feeList.get(0);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId().toString());
        addRequestParameter("selectedFee[0].amount", "100");
        addRequestParameter("selectedFee[1].feeId", fee.getFeeId().toString());
        addRequestParameter("selectedFee[1].amount", "150");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        Assert.assertEquals("Fee", 1, getErrorSize(CustomerConstants.FEE));
    }

    @Test
    public void testFailurePreview_WithFee_WithoutFeeAmount() throws Exception {
        List<FeeDto> feesToRemove = getFees(RecurrenceType.MONTHLY);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        List<ApplicableAccountFeeDto> feeList = retrieveAdditionalFeesFromSession();
        ApplicableAccountFeeDto fee = feeList.get(0);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId().toString());
        addRequestParameter("selectedFee[0].amount", "");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        Assert.assertEquals("Fee", 1, getErrorSize(CustomerConstants.FEE));
    }

    @Test
    public void testFailurePreview_FeeFrequencyMismatch() throws Exception {
        List<FeeDto> feesToRemove = getFees(RecurrenceType.WEEKLY);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        actionPerform();
        List<ApplicableAccountFeeDto> feeList = retrieveAdditionalFeesFromSession();
        ApplicableAccountFeeDto fee = feeList.get(0);
        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, getMeeting(), request);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId().toString());
        addRequestParameter("selectedFee[0].amount", "200");
        actionPerform();
        Assert.assertEquals("Fee", 1, getErrorSize(CustomerConstants.ERRORS_FEE_FREQUENCY_MISMATCH));
    }

    @Test
    public void testSuccessfulPreview() throws Exception {
        List<FeeDto> feesToRemove = getFees(RecurrenceType.MONTHLY);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        actionPerform();

        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, new MeetingBO(RecurrenceType.MONTHLY, Short
                .valueOf("2"), new Date(), MeetingType.CUSTOMER_MEETING), request);
        List<ApplicableAccountFeeDto> feeList = retrieveAdditionalFeesFromSession();
        ApplicableAccountFeeDto fee = feeList.get(0);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("displayName", "center");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));

        addRequestParameter("selectedFee[0].feeId", fee.getFeeId().toString());
        addRequestParameter("selectedFee[0].amount", fee.getAmount());
        actionPerform();

        Assert.assertEquals(0, getErrorSize());

        verifyForward(ActionForwards.preview_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    @Test
    public void testSuccessfulPrevious() throws Exception {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "previous");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.previous_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    @Test
    public void testFailureFeeFrequencyMismatch() throws Exception {
        List<FeeDto> feesToRemove = getFees(RecurrenceType.MONTHLY);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        actionPerform();

        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, new MeetingBO(RecurrenceType.MONTHLY, Short
                .valueOf("3"), new Date(), MeetingType.CUSTOMER_MEETING), request);
        List<ApplicableAccountFeeDto> feeList = retrieveAdditionalFeesFromSession();
        ApplicableAccountFeeDto fee = feeList.get(0);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("displayName", "center");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));

        addRequestParameter("selectedFee[0].feeId", fee.getFeeId().toString());
        addRequestParameter("selectedFee[0].amount", fee.getAmount());
        actionPerform();

        Assert.assertEquals("Fee", 1, getErrorSize(CustomerConstants.ERRORS_FEE_FREQUENCY_MISMATCH));
    }
    
    @Test
    public void testSuccessfulCreate() throws Exception {

        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();

        AmountFeeBO monthlyPeriodicFeeForFirstClients = new FeeBuilder().appliesToCenterOnly()
                                                                        .withFeeAmount("200.0")
                                                                        .withName("PeriodicAmountFee")
                                                                        .withSameRecurrenceAs(weeklyMeeting)
                                                                        .with(sampleBranchOffice())
                                                                        .build();
        IntegrationTestObjectMother.saveFee(monthlyPeriodicFeeForFirstClients);

        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        actionPerform();
        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, weeklyMeeting, request);

        List<ApplicableAccountFeeDto> feeList = retrieveAdditionalFeesFromSession();

        ApplicableAccountFeeDto fee = feeList.get(0);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("displayName", "center");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));

        addRequestParameter("selectedFee[0].feeId", fee.getFeeId().toString());
        addRequestParameter("selectedFee[0].amount", fee.getAmount());
        actionPerform();
        verifyForward(ActionForwards.preview_success.toString());
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.create_success.toString());
        CenterCustActionForm actionForm = (CenterCustActionForm) request.getSession().getAttribute("centerCustActionForm");
        center = TestObjectFactory.getCenter(actionForm.getCustomerIdAsInt());
    }

    @SuppressWarnings("unchecked")
    private List<ApplicableAccountFeeDto> retrieveAdditionalFeesFromSession() throws PageExpiredException {
        return (List<ApplicableAccountFeeDto>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request);
    }

    @Test
    public void testManage() throws Exception {
        createAndSetCenterInSession();
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "manage");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.LOAN_OFFICER_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CLIENT_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.POSITIONS, request));

        CenterCustActionForm actionForm = (CenterCustActionForm) request.getSession().getAttribute(
                "centerCustActionForm");
        Assert.assertEquals(center.getPersonnel().getPersonnelId(), actionForm.getLoanOfficerIdValue());
    }

    @Test
    public void testFailureEditPreviewWithLoanOfficerNull() throws Exception {
        createAndSetCenterInSession();
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "manage");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter("loanOfficerId", "");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertEquals("Loan Officer", 1, getErrorSize(CustomerConstants.LOAN_OFFICER));
        verifyInputForward();
    }


    @Test
    public void testSuccessfulEditPreview() throws Exception {
        createAndSetCenterInSession();
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "manage");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter("displayName", "center");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter("mfiJoiningDateDD", "01");
        addRequestParameter("mfiJoiningDateMM", "01");
        addRequestParameter("mfiJoiningDateYY", "01");

        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        Assert.assertEquals(0, getErrorSize());

        verifyForward(ActionForwards.editpreview_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    private void createAndSetCenterInSession() throws Exception {
        String name = "manage_center";
        center = TestObjectFactory.createWeeklyFeeCenter(name, getMeeting());
        StaticHibernateUtil.flushSession();
        center = TestObjectFactory.getCenter(Integer.valueOf(center.getCustomerId()).intValue());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, center, request);
    }

    @Test
    public void testSuccessfulEditPrevious() throws Exception {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "editPrevious");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.editprevious_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    @Test
    public void testGet() throws Exception {
        setNonLoanOfficerMifosUser();

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        savingsBO = getSavingsAccount("fsaf6", "ads6", center);
        StaticHibernateUtil.flushSession();
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("globalCustNum", center.getGlobalCustNum());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.get_success.toString());
        CustomerBO centerBO = (CenterBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        Assert.assertNotNull(center);
        Assert.assertEquals(center.getCustomerId(), centerBO.getCustomerId());

        CenterInformationDto centerInformation = (CenterInformationDto) SessionUtils.getAttribute(
                "centerInformationDto", request);
        List<CustomerDetailDto> children = centerInformation.getGroupsOtherThanClosedAndCancelled();

        Assert.assertNotNull(children);
        Assert.assertEquals(1, children.size());

        Assert.assertEquals("Size of the active accounts should be 1", 1, centerInformation.getSavingsAccountsInUse()
                .size());
        StaticHibernateUtil.flushSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        savingsBO = TestObjectFactory.getObject(SavingsBO.class, savingsBO.getAccountId());
    }

    @Test
    public void testLoadSearch() throws Exception {
        addActionAndMethod(Methods.loadSearch.toString());
        addRequestParameter("input", "search");
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.loadSearch_success.toString());
    }

    @Test
    public void testSearch() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("SearchCenter", meeting);
        addActionAndMethod(Methods.search.toString());
        addRequestParameter("searchString", "Sear");
        addRequestParameter("input", "search");

        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.search_success.toString());
        QueryResult queryResult = (QueryResult) SessionUtils.getAttribute(Constants.SEARCH_RESULTS, request);
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(1, queryResult.getSize());
        Assert.assertEquals(1, queryResult.get(0, 10).size());
    }

    private void addActionAndMethod(String method) {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", method);

    }

    private SavingsBO getSavingsAccount(String offeringName, String shortName, CustomerBO customer) throws Exception {
        savingsOffering = helper.createSavingsOffering(offeringName, shortName);
        return TestObjectFactory.createSavingsAccount("000100000000017", customer,
                AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, new Date(System.currentTimeMillis()), savingsOffering);
    }

    private MeetingBO getMeeting() throws Exception {
        MeetingBO meeting = new MeetingBO(Short.valueOf("2"), Short.valueOf("2"), new Date(),
                MeetingType.CUSTOMER_MEETING, "MeetingPlace");
        return meeting;
    }

    private List<FeeDto> getFees(RecurrenceType frequency) throws Exception {
        List<FeeDto> fees = new ArrayList<FeeDto>();
        AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory.createPeriodicAmountFee("PeriodicAmountFee",
                FeeCategory.CENTER, "200", frequency, Short.valueOf("2"));
        fees.add(new FeeDto(TestObjectFactory.getContext(), fee1));
        StaticHibernateUtil.flushSession();
        return fees;
    }

    private void setNonLoanOfficerMifosUser() {
        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().nonLoanOfficer().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
