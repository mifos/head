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

import org.mifos.application.accounts.loan.struts.action.MultipleLoanAccountsCreationAction;
import org.mifos.application.configuration.struts.actionform.HiddenMandatoryConfigurationActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class HiddenMandatoryConfigurationActionTest extends
		MifosMockStrutsTestCase {

	public HiddenMandatoryConfigurationActionTest() throws SystemException, ApplicationException {
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
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
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
				.getSession().getAttribute(
						"hiddenmandatoryconfigurationactionform");
		assertEquals("1", actionForm.getHideClientBusinessWorkActivities());
		assertEquals("1", actionForm.getHideClientGovtId());
		assertEquals("0", actionForm.getHideClientMiddleName());
		assertEquals("1", actionForm.getHideClientPhone());
		assertEquals("1", actionForm.getHideClientSecondLastName());
		assertEquals("0", actionForm.getHideClientSpouseFatherMiddleName());
		assertEquals("1", actionForm.getHideClientSpouseFatherSecondLastName());
		assertEquals("1", actionForm.getHideClientTrained());
		assertEquals("1", actionForm.getHideGroupTrained());
		assertEquals("0", actionForm.getHideSystemAddress2());
		assertEquals("1", actionForm.getHideSystemAddress3());
		assertEquals("0", actionForm.getHideSystemAssignClientPostions());
		assertEquals("1", actionForm.getHideSystemCitizenShip());
		assertEquals("1", actionForm.getHideSystemCity());
		assertEquals("1", actionForm.getHideSystemCollateralTypeNotes());
		assertEquals("1", actionForm.getHideSystemCountry());
		assertEquals("1", actionForm.getHideSystemEducationLevel());
		assertEquals("1", actionForm.getHideSystemEthnicity());
		assertEquals("1", actionForm.getHideSystemExternalId());
		assertEquals("1", actionForm.getHideSystemHandicapped());
		assertEquals("1", actionForm.getHideSystemPhoto());
		assertEquals("1", actionForm.getHideSystemPostalCode());
		assertEquals("1", actionForm.getHideSystemReceiptIdDate());
		assertEquals("1", actionForm.getHideSystemState());
		assertEquals("0", actionForm.getMandatoryClientGovtId());
		assertEquals("0", actionForm.getMandatoryClientPhone());
		assertEquals("1", actionForm.getMandatoryClientSecondLastName());
		assertEquals("1", actionForm
				.getMandatoryClientSpouseFatherSecondLastName());
		assertEquals("0", actionForm.getMandatoryClientTrained());
		assertEquals("0", actionForm.getMandatoryClientTrainedOn());
		assertEquals("0", actionForm.getMandatorySystemAddress1());
		assertEquals("1", actionForm.getMandatorySystemCitizenShip());
		assertEquals("1", actionForm.getMandatorySystemEducationLevel());
		assertEquals("1", actionForm.getMandatorySystemEthnicity());
		assertEquals("1", actionForm.getMandatorySystemExternalId());
		assertEquals("1", actionForm.getMandatorySystemHandicapped());
		assertEquals("0", actionForm.getMandatorySystemPhoto());
		assertEquals("1", actionForm.getMandatoryLoanAccountPurpose());
	}
	
	public void testAddressFields() throws HibernateProcessException, PersistenceException{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("hideSystemAddress2","1");
		addRequestParameter("hideSystemAddress3","1");
		actionPerform();
		EntityMasterData.getInstance().init();
		FieldConfig fieldConfig = FieldConfig.getInstance();
		fieldConfig.init();
		
		assertTrue(fieldConfig.isFieldHidden("Client.Address2"));
		assertTrue(fieldConfig.isFieldHidden("Group.Address2"));
		assertTrue(fieldConfig.isFieldHidden("Office.Address2"));
		assertTrue(fieldConfig.isFieldHidden("Personnel.Address2"));
		assertTrue(fieldConfig.isFieldHidden("Center.Address2"));

		assertTrue(fieldConfig.isFieldHidden("Client.Address3"));
		assertTrue(fieldConfig.isFieldHidden("Group.Address3"));
		assertTrue(fieldConfig.isFieldHidden("Office.Address3"));
		assertTrue(fieldConfig.isFieldHidden("Personnel.Address3"));
		assertTrue(fieldConfig.isFieldHidden("Center.Address3"));
		
		StaticHibernateUtil.closeSession();

		setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
		addRequestParameter("method", "load");
		actionPerform();
		StaticHibernateUtil.closeSession();

		setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("hideSystemAddress2","0");
		addRequestParameter("hideSystemAddress3","1");
		actionPerform();
		EntityMasterData.getInstance().init();
		fieldConfig.init();
		
		assertFalse(fieldConfig.isFieldHidden("Client.Address2"));
		assertFalse(fieldConfig.isFieldHidden("Group.Address2"));
		assertFalse(fieldConfig.isFieldHidden("Office.Address2"));
		assertFalse(fieldConfig.isFieldHidden("Personnel.Address2"));
		assertFalse(fieldConfig.isFieldHidden("Center.Address2"));

		assertTrue(fieldConfig.isFieldHidden("Client.Address3"));
		assertTrue(fieldConfig.isFieldHidden("Group.Address3"));
		assertTrue(fieldConfig.isFieldHidden("Office.Address3"));
		assertTrue(fieldConfig.isFieldHidden("Personnel.Address3"));
		assertTrue(fieldConfig.isFieldHidden("Center.Address3"));

	}
	
	
	public void testUpdate() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("mandatoryClientGovtId", "1");
		addRequestParameter("mandatorySystemPhoto", "1");
		addRequestParameter("hideSystemAddress2","1");
		addRequestParameter("hideSystemAddress3","1");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());
		StaticHibernateUtil.closeSession();
		setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
		addRequestParameter("method", "load");
		actionPerform();

		HiddenMandatoryConfigurationActionForm actionForm = (HiddenMandatoryConfigurationActionForm) request
				.getSession().getAttribute(
						"hiddenmandatoryconfigurationactionform");
		assertEquals("0", actionForm.getHideClientBusinessWorkActivities());
		assertEquals("0", actionForm.getHideClientGovtId());
		assertEquals("0", actionForm.getHideClientMiddleName());
		assertEquals("0", actionForm.getHideClientPhone());
		assertEquals("0", actionForm.getHideClientSecondLastName());
		assertEquals("0", actionForm.getHideClientSpouseFatherMiddleName());
		assertEquals("0", actionForm.getHideClientSpouseFatherSecondLastName());
		assertEquals("0", actionForm.getHideClientTrained());
		assertEquals("0", actionForm.getHideGroupTrained());
		assertEquals("1", actionForm.getHideSystemAddress2());
		assertEquals("1", actionForm.getHideSystemAddress3());
		assertEquals("0", actionForm.getHideSystemAssignClientPostions());
		assertEquals("0", actionForm.getHideSystemCitizenShip());
		assertEquals("0", actionForm.getHideSystemCity());
		assertEquals("0", actionForm.getHideSystemCollateralTypeNotes());
		assertEquals("0", actionForm.getHideSystemCountry());
		assertEquals("0", actionForm.getHideSystemEducationLevel());
		assertEquals("0", actionForm.getHideSystemEthnicity());
		assertEquals("0", actionForm.getHideSystemExternalId());
		assertEquals("0", actionForm.getHideSystemHandicapped());
		assertEquals("0", actionForm.getHideSystemPhoto());
		assertEquals("0", actionForm.getHideSystemPostalCode());
		assertEquals("0", actionForm.getHideSystemReceiptIdDate());
		assertEquals("0", actionForm.getHideSystemState());
		assertEquals("1", actionForm.getMandatoryClientGovtId());
		assertEquals("0", actionForm.getMandatoryClientPhone());
		assertEquals("0", actionForm.getMandatoryClientSecondLastName());
		assertEquals("0", actionForm
				.getMandatoryClientSpouseFatherSecondLastName());
		assertEquals("0", actionForm.getMandatoryClientTrained());
		assertEquals("0", actionForm.getMandatoryClientTrainedOn());
		assertEquals("0", actionForm.getMandatorySystemAddress1());
		assertEquals("0", actionForm.getMandatorySystemCitizenShip());
		assertEquals("0", actionForm.getMandatorySystemEducationLevel());
		assertEquals("0", actionForm.getMandatorySystemEthnicity());
		assertEquals("0", actionForm.getMandatorySystemExternalId());
		assertEquals("0", actionForm.getMandatorySystemHandicapped());
		assertEquals("1", actionForm.getMandatorySystemPhoto());
		

		setRequestPathInfo("/hiddenmandatoryconfigurationaction.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("hideClientBusinessWorkActivities", "1");
		addRequestParameter("hideClientGovtId", "1");
		addRequestParameter("hideClientPhone", "1");
		addRequestParameter("hideClientSecondLastName", "1");
		addRequestParameter("hideClientTrained", "1");
		addRequestParameter("hideGroupAddress1", "1");
		addRequestParameter("hideGroupAddress2", "1");
		addRequestParameter("hideGroupAddress3", "1");
		addRequestParameter("hideGroupTrained", "1");
		addRequestParameter("hideSystemAddress2","0");//restore back to default state
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
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
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

}
