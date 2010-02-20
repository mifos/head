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

package org.mifos.application.configuration.struts.action;

import junit.framework.Assert;

import org.mifos.accounts.loan.struts.action.MultipleLoanAccountsCreationAction;
import org.mifos.application.configuration.struts.actionform.LabelConfigurationActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.mifosmenu.MenuRepository;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LabelConfigurationActionStrutsTest extends MifosMockStrutsTestCase {

    public LabelConfigurationActionStrutsTest() throws Exception {
        super();
    }

    private UserContext userContext;

    private String flowKey;

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        flowKey = createFlow(request, MultipleLoanAccountsCreationAction.class);
    }

    public void testUpdateWithOutData() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/labelconfigurationaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyActionErrors(new String[] { "Please specify Head office.", "Please specify Regional office.",
                "Please specify Sub regional office.", "Please specify Area office.", "Please specify Branch office.",
                "Please specify Client.", "Please specify Group.", "Please specify Center.", "Please specify Loans.",
                "Please specify Savings.", "Please specify State.", "Please specify Postal code.",
                "Please specify Ethnicity.", "Please specify Citizenship.", "Please specify Handicapped.",
                "Please specify Government ID.", "Please specify Address 1.", "Please specify Address 2.",
                "Please specify Address 3.", "Please specify Partial Application.", "Please specify Pending Approval.",
                "Please specify Approved.", "Please specify Cancel.", "Please specify Closed.",
                "Please specify On hold.", "Please specify Active.", "Please specify Inactive.",
                "Please specify Active in good standing.", "Please specify Active in bad standing.",
                "Please specify Closed-obligation met.", "Please specify Closed-rescheduled.",
                "Please specify Closed-written off.", "Please specify None.",
                "Please specify Grace on all repayments.", "Please specify Principal only grace.",
                "Please specify Interest.", "Please specify External ID.", "Please specify Bulk entry." });
        verifyInputForward();
    }

    public void testUpdate() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/labelconfigurationaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("headOffice", "Head Office-Changed");
        addRequestParameter("regionalOffice", "Regional Office-Changed");
        addRequestParameter("subRegionalOffice", "Divisional Office-Changed");
        addRequestParameter("areaOffice", "Area Office-Changed");
        addRequestParameter("branchOffice", "Branch Office-Changed");
        addRequestParameter("client", "Member-Changed");
        addRequestParameter("group", "Group-Changed");
        addRequestParameter("center", "Kendra-Changed");
        addRequestParameter("loans", "Loan-Changed");
        addRequestParameter("savings", "Margin Money-Changed");
        addRequestParameter("state", "State-Changed");
        addRequestParameter("postalCode", "Postal Code-Changed");
        addRequestParameter("ethnicity", "Caste-Changed");
        addRequestParameter("citizenship", "Religion-Changed");
        addRequestParameter("handicapped", "Handicapped-Changed");
        addRequestParameter("govtId", "Government ID-Changed");
        addRequestParameter("address1", "Address1-Changed");
        addRequestParameter("address2", "Address2-Changed");
        addRequestParameter("address3", "Village-Changed");
        addRequestParameter("partialApplication", "Partial Application-Changed");
        addRequestParameter("pendingApproval", "Application Pending Approval-Changed");
        addRequestParameter("approved", "Application Approved-Changed");
        addRequestParameter("cancel", "Cancel-Changed");
        addRequestParameter("closed", "Closed-Changed");
        addRequestParameter("onhold", "On Hold-Changed");
        addRequestParameter("active", "Active-Changed");
        addRequestParameter("inActive", "Inactive-Changed");
        addRequestParameter("activeInGoodStanding", "Active in Good Standing-Changed");
        addRequestParameter("activeInBadStanding", "Active in Bad Standing-Changed");
        addRequestParameter("closedObligationMet", "Closed- Obligation met-Changed");
        addRequestParameter("closedRescheduled", "Closed- Rescheduled-Changed");
        addRequestParameter("closedWrittenOff", "Closed- Written Off-Changed");
        addRequestParameter("none", "None-Changed");
        addRequestParameter("graceOnAllRepayments", "Grace on all repayments-Changed");
        addRequestParameter("principalOnlyGrace", "Principal only grace-Changed");
        addRequestParameter("interest", "Service Charge-Changed");
        addRequestParameter("externalId", "External Id-Changed");
        addRequestParameter("bulkEntry", "Bulk entry-Changed");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();

        Assert.assertFalse(MenuRepository.getInstance().isMenuForLocale(userContext.getPreferredLocale()));

        setRequestPathInfo("/labelconfigurationaction.do");
        addRequestParameter("method", "load");
        actionPerform();
        LabelConfigurationActionForm labelConfigurationActionForm = (LabelConfigurationActionForm) request.getSession()
                .getAttribute("labelconfigurationactionform");
       Assert.assertEquals("Head Office-Changed", labelConfigurationActionForm.getHeadOffice());
       Assert.assertEquals("Regional Office-Changed", labelConfigurationActionForm.getRegionalOffice());
       Assert.assertEquals("Divisional Office-Changed", labelConfigurationActionForm.getSubRegionalOffice());
       Assert.assertEquals("Area Office-Changed", labelConfigurationActionForm.getAreaOffice());
       Assert.assertEquals("Branch Office-Changed", labelConfigurationActionForm.getBranchOffice());
       Assert.assertEquals("Member-Changed", labelConfigurationActionForm.getClient());
       Assert.assertEquals("Group-Changed", labelConfigurationActionForm.getGroup());
       Assert.assertEquals("Kendra-Changed", labelConfigurationActionForm.getCenter());
       Assert.assertEquals("Loan-Changed", labelConfigurationActionForm.getLoans());
       Assert.assertEquals("Margin Money-Changed", labelConfigurationActionForm.getSavings());
       Assert.assertEquals("State-Changed", labelConfigurationActionForm.getState());
       Assert.assertEquals("Postal Code-Changed", labelConfigurationActionForm.getPostalCode());
       Assert.assertEquals("Caste-Changed", labelConfigurationActionForm.getEthnicity());
       Assert.assertEquals("Religion-Changed", labelConfigurationActionForm.getCitizenship());
       Assert.assertEquals("Handicapped-Changed", labelConfigurationActionForm.getHandicapped());
       Assert.assertEquals("Government ID-Changed", labelConfigurationActionForm.getGovtId());
       Assert.assertEquals("Address1-Changed", labelConfigurationActionForm.getAddress1());
       Assert.assertEquals("Address2-Changed", labelConfigurationActionForm.getAddress2());
       Assert.assertEquals("Village-Changed", labelConfigurationActionForm.getAddress3());
       Assert.assertEquals("Partial Application-Changed", labelConfigurationActionForm.getPartialApplication());
       Assert.assertEquals("Application Pending Approval-Changed", labelConfigurationActionForm.getPendingApproval());
       Assert.assertEquals("Application Approved-Changed", labelConfigurationActionForm.getApproved());
       Assert.assertEquals("Cancel-Changed", labelConfigurationActionForm.getCancel());
       Assert.assertEquals("Closed-Changed", labelConfigurationActionForm.getClosed());
       Assert.assertEquals("On Hold-Changed", labelConfigurationActionForm.getOnhold());
       Assert.assertEquals("Active-Changed", labelConfigurationActionForm.getActive());
       Assert.assertEquals("Inactive-Changed", labelConfigurationActionForm.getInActive());
       Assert.assertEquals("Active in Good Standing-Changed", labelConfigurationActionForm.getActiveInGoodStanding());
       Assert.assertEquals("Active in Bad Standing-Changed", labelConfigurationActionForm.getActiveInBadStanding());
       Assert.assertEquals("Closed- Obligation met-Changed", labelConfigurationActionForm.getClosedObligationMet());
       Assert.assertEquals("Closed- Rescheduled-Changed", labelConfigurationActionForm.getClosedRescheduled());
       Assert.assertEquals("Closed- Written Off-Changed", labelConfigurationActionForm.getClosedWrittenOff());
       Assert.assertEquals("None-Changed", labelConfigurationActionForm.getNone());
       Assert.assertEquals("Grace on all repayments-Changed", labelConfigurationActionForm.getGraceOnAllRepayments());
       Assert.assertEquals("Principal only grace-Changed", labelConfigurationActionForm.getPrincipalOnlyGrace());
       Assert.assertEquals("Service Charge-Changed", labelConfigurationActionForm.getInterest());
       Assert.assertEquals("External Id-Changed", labelConfigurationActionForm.getExternalId());
       Assert.assertEquals("Bulk entry-Changed", labelConfigurationActionForm.getBulkEntry());

        setRequestPathInfo("/labelconfigurationaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("headOffice", "Head Office");
        addRequestParameter("regionalOffice", "Regional Office");
        addRequestParameter("subRegionalOffice", "Divisional Office");
        addRequestParameter("areaOffice", "Area Office");
        addRequestParameter("branchOffice", "Branch Office");
        addRequestParameter("client", "Member");
        addRequestParameter("group", "Group");
        addRequestParameter("center", "Kendra");
        addRequestParameter("loans", "Loan");
        addRequestParameter("savings", "Savings");
        addRequestParameter("state", "State");
        addRequestParameter("postalCode", "Postal Code");
        addRequestParameter("ethnicity", "Caste");
        addRequestParameter("citizenship", "Religion");
        addRequestParameter("handicapped", "Handicapped");
        addRequestParameter("govtId", "Government ID");
        addRequestParameter("address1", "Address1");
        addRequestParameter("address2", "Address2");
        addRequestParameter("address3", "Village");
        addRequestParameter("partialApplication", "Partial Application");
        addRequestParameter("pendingApproval", "Application Pending Approval");
        addRequestParameter("approved", "Application Approved");
        addRequestParameter("cancel", "Cancel");
        addRequestParameter("closed", "Closed");
        addRequestParameter("onhold", "On Hold");
        addRequestParameter("active", "Active");
        addRequestParameter("inActive", "Inactive");
        addRequestParameter("activeInGoodStanding", "Active in Good Standing");
        addRequestParameter("activeInBadStanding", "Active in Bad Standing");
        addRequestParameter("closedObligationMet", "Closed- Obligation met");
        addRequestParameter("closedRescheduled", "Closed- Rescheduled");
        addRequestParameter("closedWrittenOff", "Closed- Written Off");
        addRequestParameter("none", "None");
        addRequestParameter("graceOnAllRepayments", "Grace on all repayments");
        addRequestParameter("principalOnlyGrace", "Principal only grace");
        addRequestParameter("interest", "Service Charge");
        addRequestParameter("externalId", "External Id");
        addRequestParameter("bulkEntry", "Bulk entry");
        actionPerform();
        verifyNoActionErrors();
    }

    public void testCancel() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/labelconfigurationaction.do");
        addRequestParameter("method", "cancel");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.cancel_success.toString());
    }

    public void testValidate() throws Exception {
        setRequestPathInfo("/labelconfigurationaction.do");
        addRequestParameter("method", "validate");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_failure.toString());
    }

    public void testValidateForUpdate() throws Exception {
        setRequestPathInfo("/labelconfigurationaction.do");
        addRequestParameter("method", "validate");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute("methodCalled", Methods.update.toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.update_failure.toString());
    }

    /**
     * FIXME: This test passes at independent (with clean database), But its
     * failing with the surefire *Test semantic.
     * 
     */
    public void xtestLoad() throws Exception {
        setRequestPathInfo("/labelconfigurationaction.do");
        addRequestParameter("method", "load");
        performNoErrors();
        verifyForward(ActionForwards.load_success.toString());
        LabelConfigurationActionForm labelConfigurationActionForm = (LabelConfigurationActionForm) request.getSession()
                .getAttribute("labelconfigurationactionform");
       Assert.assertEquals("Head Office", labelConfigurationActionForm.getHeadOffice());
       Assert.assertEquals("Regional Office", labelConfigurationActionForm.getRegionalOffice());
       Assert.assertEquals("Divisional Office", labelConfigurationActionForm.getSubRegionalOffice());
       Assert.assertEquals("Area Office", labelConfigurationActionForm.getAreaOffice());
       Assert.assertEquals("Branch Office", labelConfigurationActionForm.getBranchOffice());
       Assert.assertEquals("Member", labelConfigurationActionForm.getClient());
       Assert.assertEquals("Group", labelConfigurationActionForm.getGroup());
       Assert.assertEquals("Kendra", labelConfigurationActionForm.getCenter());
       Assert.assertEquals("Loan", labelConfigurationActionForm.getLoans());
       Assert.assertEquals("Savings", labelConfigurationActionForm.getSavings());
       Assert.assertEquals("State", labelConfigurationActionForm.getState());
       Assert.assertEquals("Postal Code", labelConfigurationActionForm.getPostalCode());
       Assert.assertEquals("Caste", labelConfigurationActionForm.getEthnicity());
       Assert.assertEquals("Religion", labelConfigurationActionForm.getCitizenship());
       Assert.assertEquals("Handicapped", labelConfigurationActionForm.getHandicapped());
       Assert.assertEquals("Government ID", labelConfigurationActionForm.getGovtId());
       Assert.assertEquals("Address1", labelConfigurationActionForm.getAddress1());
       Assert.assertEquals("Address2", labelConfigurationActionForm.getAddress2());
       Assert.assertEquals("Village", labelConfigurationActionForm.getAddress3());
       Assert.assertEquals("Partial Application", labelConfigurationActionForm.getPartialApplication());
       Assert.assertEquals("Application Pending Approval", labelConfigurationActionForm.getPendingApproval());
       Assert.assertEquals("Application Approved", labelConfigurationActionForm.getApproved());
       Assert.assertEquals("Cancel", labelConfigurationActionForm.getCancel());
       Assert.assertEquals("Closed", labelConfigurationActionForm.getClosed());
       Assert.assertEquals("On Hold", labelConfigurationActionForm.getOnhold());
       Assert.assertEquals("Active", labelConfigurationActionForm.getActive());
       Assert.assertEquals("Inactive", labelConfigurationActionForm.getInActive());
       Assert.assertEquals("Active in Good Standing", labelConfigurationActionForm.getActiveInGoodStanding());
       Assert.assertEquals("Active in Bad Standing", labelConfigurationActionForm.getActiveInBadStanding());
       Assert.assertEquals("Closed- Obligation met", labelConfigurationActionForm.getClosedObligationMet());
       Assert.assertEquals("Closed- Rescheduled", labelConfigurationActionForm.getClosedRescheduled());
       Assert.assertEquals("Closed- Written Off", labelConfigurationActionForm.getClosedWrittenOff());
       Assert.assertEquals("None", labelConfigurationActionForm.getNone());
       Assert.assertEquals("Grace on all repayments", labelConfigurationActionForm.getGraceOnAllRepayments());
       Assert.assertEquals("Principal only grace", labelConfigurationActionForm.getPrincipalOnlyGrace());
       Assert.assertEquals("Service Charge", labelConfigurationActionForm.getInterest());
       Assert.assertEquals("External Id", labelConfigurationActionForm.getExternalId());
       Assert.assertEquals("Bulk entry", labelConfigurationActionForm.getBulkEntry());
    }

}
