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

import org.mifos.application.accounts.loan.struts.action.MultipleLoanAccountsCreationAction;
import org.mifos.application.configuration.struts.actionform.HiddenMandatoryConfigurationActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class HiddenMandatoryConfigurationActionStrutsTest extends MifosMockStrutsTestCase {

    public HiddenMandatoryConfigurationActionStrutsTest() throws Exception {
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

    public void testLoad() throws Exception {
        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "load");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());
        HiddenMandatoryConfigurationActionForm actionForm = (HiddenMandatoryConfigurationActionForm) request
                .getSession().getAttribute("hiddenmandatoryconfigurationactionform");
       Assert.assertEquals("1", actionForm.getHideClientBusinessWorkActivities());
       Assert.assertEquals("1", actionForm.getHideClientGovtId());
       Assert.assertEquals("0", actionForm.getHideClientMiddleName());
       Assert.assertEquals("1", actionForm.getHideClientPhone());
       Assert.assertEquals("1", actionForm.getHideClientSecondLastName());
       Assert.assertEquals("0", actionForm.getHideClientSpouseFatherMiddleName());
       Assert.assertEquals("1", actionForm.getHideClientSpouseFatherSecondLastName());
       Assert.assertEquals("1", actionForm.getHideClientTrained());
       Assert.assertEquals("1", actionForm.getHideGroupTrained());
       Assert.assertEquals("0", actionForm.getHideSystemAddress2());
       Assert.assertEquals("1", actionForm.getHideSystemAddress3());
       Assert.assertEquals("0", actionForm.getHideSystemAssignClientPostions());
       Assert.assertEquals("1", actionForm.getHideSystemCitizenShip());
       Assert.assertEquals("1", actionForm.getHideSystemCity());
       Assert.assertEquals("1", actionForm.getHideSystemCollateralTypeNotes());
       Assert.assertEquals("1", actionForm.getHideSystemCountry());
       Assert.assertEquals("1", actionForm.getHideSystemEducationLevel());
       Assert.assertEquals("1", actionForm.getHideSystemEthnicity());
       Assert.assertEquals("1", actionForm.getHideSystemExternalId());
       Assert.assertEquals("1", actionForm.getHideSystemHandicapped());
       Assert.assertEquals("1", actionForm.getHideSystemPhoto());
       Assert.assertEquals("1", actionForm.getHideSystemPostalCode());
       Assert.assertEquals("1", actionForm.getHideSystemReceiptIdDate());
       Assert.assertEquals("1", actionForm.getHideSystemState());
       Assert.assertEquals("0", actionForm.getMandatoryClientGovtId());
       Assert.assertEquals("0", actionForm.getMandatoryClientPhone());
       Assert.assertEquals("1", actionForm.getMandatoryClientSecondLastName());
       Assert.assertEquals("1", actionForm.getMandatoryClientSpouseFatherSecondLastName());
       Assert.assertEquals("0", actionForm.getMandatoryClientTrained());
       Assert.assertEquals("0", actionForm.getMandatoryClientTrainedOn());
       Assert.assertEquals("0", actionForm.getMandatorySystemAddress1());
       Assert.assertEquals("1", actionForm.getMandatorySystemCitizenShip());
       Assert.assertEquals("1", actionForm.getMandatorySystemEducationLevel());
       Assert.assertEquals("1", actionForm.getMandatorySystemEthnicity());
       Assert.assertEquals("1", actionForm.getMandatorySystemExternalId());
       Assert.assertEquals("1", actionForm.getMandatorySystemHandicapped());
       Assert.assertEquals("0", actionForm.getMandatorySystemPhoto());
       Assert.assertEquals("1", actionForm.getMandatoryLoanAccountPurpose());
    }

    public void testAddressFields() throws HibernateProcessException, PersistenceException {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("hideSystemAddress2", "1");
        addRequestParameter("hideSystemAddress3", "1");
        actionPerform();
        EntityMasterData.getInstance().init();
        FieldConfig fieldConfig = FieldConfig.getInstance();
        fieldConfig.init();

       Assert.assertTrue(fieldConfig.isFieldHidden("Client.Address2"));
       Assert.assertTrue(fieldConfig.isFieldHidden("Group.Address2"));
       Assert.assertTrue(fieldConfig.isFieldHidden("Office.Address2"));
       Assert.assertTrue(fieldConfig.isFieldHidden("Personnel.Address2"));
       Assert.assertTrue(fieldConfig.isFieldHidden("Center.Address2"));

       Assert.assertTrue(fieldConfig.isFieldHidden("Client.Address3"));
       Assert.assertTrue(fieldConfig.isFieldHidden("Group.Address3"));
       Assert.assertTrue(fieldConfig.isFieldHidden("Office.Address3"));
       Assert.assertTrue(fieldConfig.isFieldHidden("Personnel.Address3"));
       Assert.assertTrue(fieldConfig.isFieldHidden("Center.Address3"));

        StaticHibernateUtil.closeSession();

        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "load");
        actionPerform();
        StaticHibernateUtil.closeSession();

        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("hideSystemAddress2", "0");
        addRequestParameter("hideSystemAddress3", "1");
        actionPerform();
        EntityMasterData.getInstance().init();
        fieldConfig.init();

        Assert.assertFalse(fieldConfig.isFieldHidden("Client.Address2"));
        Assert.assertFalse(fieldConfig.isFieldHidden("Group.Address2"));
        Assert.assertFalse(fieldConfig.isFieldHidden("Office.Address2"));
        Assert.assertFalse(fieldConfig.isFieldHidden("Personnel.Address2"));
        Assert.assertFalse(fieldConfig.isFieldHidden("Center.Address2"));

       Assert.assertTrue(fieldConfig.isFieldHidden("Client.Address3"));
       Assert.assertTrue(fieldConfig.isFieldHidden("Group.Address3"));
       Assert.assertTrue(fieldConfig.isFieldHidden("Office.Address3"));
       Assert.assertTrue(fieldConfig.isFieldHidden("Personnel.Address3"));
       Assert.assertTrue(fieldConfig.isFieldHidden("Center.Address3"));

    }

    public void testCityField() throws HibernateProcessException, PersistenceException {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("hideSystemCity", "0");
        actionPerform();
        EntityMasterData.getInstance().init();
        FieldConfig fieldConfig = FieldConfig.getInstance();
        fieldConfig.init();

        Assert.assertFalse(fieldConfig.isFieldHidden("Client.City"));
        Assert.assertFalse(fieldConfig.isFieldHidden("Group.City"));
        Assert.assertFalse(fieldConfig.isFieldHidden("Office.City"));
        Assert.assertFalse(fieldConfig.isFieldHidden("Center.City"));

        StaticHibernateUtil.closeSession();

        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "load");
        actionPerform();
        StaticHibernateUtil.closeSession();

        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("hideSystemCity", "1");
        actionPerform();
        EntityMasterData.getInstance().init();
        fieldConfig.init();

        Assert.assertTrue(fieldConfig.isFieldHidden("Client.City"));
        Assert.assertTrue(fieldConfig.isFieldHidden("Group.City"));
        Assert.assertTrue(fieldConfig.isFieldHidden("Office.City"));
        Assert.assertTrue(fieldConfig.isFieldHidden("Center.City"));
    }

    public void testUpdate() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("mandatoryClientGovtId", "1");
        addRequestParameter("mandatorySystemPhoto", "1");
        addRequestParameter("hideSystemAddress2", "1");
        addRequestParameter("hideSystemAddress3", "1");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.update_success.toString());
        StaticHibernateUtil.closeSession();
        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "load");
        actionPerform();

        HiddenMandatoryConfigurationActionForm actionForm = (HiddenMandatoryConfigurationActionForm) request
                .getSession().getAttribute("hiddenmandatoryconfigurationactionform");
       Assert.assertEquals("0", actionForm.getHideClientBusinessWorkActivities());
       Assert.assertEquals("0", actionForm.getHideClientGovtId());
       Assert.assertEquals("0", actionForm.getHideClientMiddleName());
       Assert.assertEquals("0", actionForm.getHideClientPhone());
       Assert.assertEquals("0", actionForm.getHideClientSecondLastName());
       Assert.assertEquals("0", actionForm.getHideClientSpouseFatherMiddleName());
       Assert.assertEquals("0", actionForm.getHideClientSpouseFatherSecondLastName());
       Assert.assertEquals("0", actionForm.getHideClientTrained());
       Assert.assertEquals("0", actionForm.getHideGroupTrained());
       Assert.assertEquals("1", actionForm.getHideSystemAddress2());
       Assert.assertEquals("1", actionForm.getHideSystemAddress3());
       Assert.assertEquals("0", actionForm.getHideSystemAssignClientPostions());
       Assert.assertEquals("0", actionForm.getHideSystemCitizenShip());
       Assert.assertEquals("0", actionForm.getHideSystemCity());
       Assert.assertEquals("0", actionForm.getHideSystemCollateralTypeNotes());
       Assert.assertEquals("0", actionForm.getHideSystemCountry());
       Assert.assertEquals("0", actionForm.getHideSystemEducationLevel());
       Assert.assertEquals("0", actionForm.getHideSystemEthnicity());
       Assert.assertEquals("0", actionForm.getHideSystemExternalId());
       Assert.assertEquals("0", actionForm.getHideSystemHandicapped());
       Assert.assertEquals("0", actionForm.getHideSystemPhoto());
       Assert.assertEquals("0", actionForm.getHideSystemPostalCode());
       Assert.assertEquals("0", actionForm.getHideSystemReceiptIdDate());
       Assert.assertEquals("0", actionForm.getHideSystemState());
       Assert.assertEquals("1", actionForm.getMandatoryClientGovtId());
       Assert.assertEquals("0", actionForm.getMandatoryClientPhone());
       Assert.assertEquals("0", actionForm.getMandatoryClientSecondLastName());
       Assert.assertEquals("0", actionForm.getMandatoryClientSpouseFatherSecondLastName());
       Assert.assertEquals("0", actionForm.getMandatoryClientTrained());
       Assert.assertEquals("0", actionForm.getMandatoryClientTrainedOn());
       Assert.assertEquals("0", actionForm.getMandatorySystemAddress1());
       Assert.assertEquals("0", actionForm.getMandatorySystemCitizenShip());
       Assert.assertEquals("0", actionForm.getMandatorySystemEducationLevel());
       Assert.assertEquals("0", actionForm.getMandatorySystemEthnicity());
       Assert.assertEquals("0", actionForm.getMandatorySystemExternalId());
       Assert.assertEquals("0", actionForm.getMandatorySystemHandicapped());
       Assert.assertEquals("1", actionForm.getMandatorySystemPhoto());

        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("hideClientBusinessWorkActivities", "1");
        addRequestParameter("hideClientGovtId", "1");
        addRequestParameter("hideClientPhone", "1");
        addRequestParameter("hideClientSecondLastName", "1");
        addRequestParameter("hideClientTrained", "1");
        addRequestParameter("hideGroupAddress1", "1");
        addRequestParameter("hideGroupAddress2", "1");
        addRequestParameter("hideGroupAddress3", "1");
        addRequestParameter("hideGroupTrained", "1");
        addRequestParameter("hideSystemAddress2", "0");// restore back to
        // default state
        addRequestParameter("hideSystemAddress3", "1");
        addRequestParameter("hideSystemCitizenShip", "1");
        addRequestParameter("hideSystemCity", "1");
        addRequestParameter("hideSystemCollateralTypeNotes", "1");
        addRequestParameter("hideSystemCountry", "1");
        addRequestParameter("hideSystemEducationLevel", "1");
        addRequestParameter("hideSystemEthnicity", "1");
        addRequestParameter("hideSystemExternalId", "1");
        addRequestParameter("hideSystemHandicapped", "1");
        addRequestParameter("hideSystemPhoto", "1");
        addRequestParameter("hideSystemPostalCode", "1");
        addRequestParameter("hideSystemReceiptIdDate", "1");
        addRequestParameter("hideSystemState", "1");
        addRequestParameter("mandatoryClientSecondLastName", "1");
        addRequestParameter("mandatoryGroupAddress1", "1");
        addRequestParameter("mandatorySystemCitizenShip", "1");
        addRequestParameter("mandatorySystemEducationLevel", "1");
        addRequestParameter("mandatorySystemEthnicity", "1");
        addRequestParameter("mandatorySystemExternalId", "1");
        addRequestParameter("mandatorySystemHandicapped", "1");
        addRequestParameter("hideClientSpouseFatherSecondLastName", "1");
        addRequestParameter("mandatoryClientSpouseFatherSecondLastName", "1");
        addRequestParameter("mandatoryClientGovtId", "0");
        addRequestParameter("mandatorySystemPhoto", "0");
        addRequestParameter("mandatoryLoanAccountPurpose", "1");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testCancel() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "cancel");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.cancel_success.toString());
    }

    public void testValidate() throws Exception {
        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "validate");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_failure.toString());
    }

    public void testValidateForUpdate() throws Exception {
        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "validate");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute("methodCalled", Methods.update.toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.update_failure.toString());
    }

    public void testSourceOfFundField() throws HibernateProcessException, PersistenceException {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("mandatoryLoanSourceOfFund", "0");
        actionPerform();
        EntityMasterData.getInstance().init();
        FieldConfig fieldConfig = FieldConfig.getInstance();
        fieldConfig.init();

        Assert.assertFalse(fieldConfig.isFieldManadatory("Loan.SourceOfFund"));

        StaticHibernateUtil.closeSession();

        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "load");
        actionPerform();
        StaticHibernateUtil.closeSession();

        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("mandatoryLoanSourceOfFund", "1");
        actionPerform();
        EntityMasterData.getInstance().init();
        fieldConfig.init();

        Assert.assertTrue(fieldConfig.isFieldManadatory("Loan.SourceOfFund"));
    }

    public void testSpouseFatherNameFields() throws HibernateProcessException, PersistenceException {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("hideClientSpouseFatherMiddleName", "1");
        addRequestParameter("mandatoryClientSpouseFatherSecondLastName", "1");
        actionPerform();
        EntityMasterData.getInstance().init();
        FieldConfig fieldConfig = FieldConfig.getInstance();
        fieldConfig.init();

        Assert.assertTrue(fieldConfig.isFieldHidden("Client.SpouseFatherMiddleName"));
        Assert.assertFalse(fieldConfig.isFieldHidden("Client.MiddleName"));

        Assert.assertTrue(fieldConfig.isFieldManadatory("Client.SpouseFatherSecondLastName"));
        Assert.assertFalse(fieldConfig.isFieldManadatory("Client.SecondLastName"));

        StaticHibernateUtil.closeSession();

        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "load");
        actionPerform();
        StaticHibernateUtil.closeSession();

        setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("hideClientSpouseFatherMiddleName", "0");
        addRequestParameter("mandatoryClientSpouseFatherSecondLastName", "0");
        actionPerform();
        EntityMasterData.getInstance().init();
        fieldConfig.init();

        Assert.assertFalse(fieldConfig.isFieldHidden("Client.SpouseFatherMiddleName"));
        Assert.assertFalse(fieldConfig.isFieldHidden("Client.MiddleName"));

        Assert.assertFalse(fieldConfig.isFieldManadatory("Client.SpouseFatherSecondLastName"));
        Assert.assertFalse(fieldConfig.isFieldManadatory("Client.SecondLastName"));
    }
}
