/**

 * TestFeeAction.java    version: xxx



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */

package org.mifos.application.fees.struts.action;

import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.helpers.FeesConstants;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestFeeAction extends MifosMockStrutsTestCase {

	private final static Short FORMULA_ID = 1;

	private final static String GLOCDE_ID = "7";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/framework/util/helpers/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		UserContext userContext = TestObjectFactory.getUserContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testLoad() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());

		HttpSession session = request.getSession();
		assertEquals("The size of master data for categories",
				((List<LookUpMaster>) session
						.getAttribute(FeesConstants.CATEGORYLIST)).size(), 5);
		assertEquals(
				"The size of master data for loan time of charges for one time fees  : ",
				((List<LookUpMaster>) session
						.getAttribute(FeesConstants.LOANTIMEOFCHARGES)).size(),
				3);
		assertEquals(
				"The size of master data for customer  time of charges for one time fees master : ",
				((List<LookUpMaster>) session
						.getAttribute(FeesConstants.CUSTOMERTIMEOFCHARGES))
						.size(), 1);
		assertEquals("The size of master data for loan formula : ",
				((List<LookUpMaster>) session
						.getAttribute(FeesConstants.FORMULALIST)).size(), 3);
		assertEquals("The size of master data for GLCodes of fees : ",
				((List<GLCodeEntity>) session
						.getAttribute(FeesConstants.GLCODE_LIST)).size(), 7);

	}

	public void testFailurePreview() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyActionErrors(new String[] { "Please specify Fee Name .",
				"Please select customers/products to which Fees Applies.",
				"Please select Frequency.", "Please select GL Code.",
				"errors.enter" });
		verifyInputForward();
	}

	public void testFailurePreviewWithCategory() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType.categoryId", FeeCategory.LOAN
				.getValue());
		addRequestParameter("feeFrequency.feeFrequencyType.feeFrequencyTypeId",
				FeeFrequencyType.ONETIME.getValue().toString());
		addRequestParameter("amount", "");
		addRequestParameter("rate", "");
		actionPerform();
		verifyActionErrors(new String[] { "Please specify Fee Name .",
				"Please select one time along with payment type.",
				"Please select GL Code.", "errors.amountOrRate" });
		verifyInputForward();
	}

	public void testFailurePreviewWithOnlyRateEntered() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType.categoryId", FeeCategory.LOAN
				.getValue());
		addRequestParameter("feeFrequency.feeFrequencyType.feeFrequencyTypeId",
				FeeFrequencyType.ONETIME.getValue().toString());
		addRequestParameter("amount", "");
		addRequestParameter("rate", "13");
		actionPerform();
		verifyActionErrors(new String[] { "Please specify Fee Name .",
				"Please select one time along with payment type.",
				"Please select GL Code.", "errors.rateAndFormulaId" });
		verifyInputForward();
	}

	public void testSuccessfulPreview() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType.categoryId", FeeCategory.ALLCUSTOMERS
				.getValue());
		addRequestParameter("amount", "100");
		addRequestParameter("feeName", "Customer One time");
		addRequestParameter("feeFrequency.feeFrequencyType.feeFrequencyTypeId",
				FeeFrequencyType.ONETIME.getValue().toString());
		addRequestParameter("customerCharge", FeePayment.UPFRONT.getValue()
				.toString());
		addRequestParameter("glCodeEntity.glcodeId", GLOCDE_ID);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.preview_success.toString());

		HttpSession session = request.getSession();
		FeesBO fees = (FeesBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, session);
		assertEquals(Short.valueOf(FeeCategory.ALLCUSTOMERS.getValue()), fees
				.getCategoryType().getCategoryId());
		assertEquals("Customer One time", fees.getFeeName());
		assertFalse(fees.isRateFee());
	}

	public void testSuccessfulCreateOneTimeFee() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "load");
		actionPerform();
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType.categoryId", FeeCategory.ALLCUSTOMERS
				.getValue());
		addRequestParameter("amount", "100");
		addRequestParameter("feeName", "Customer One time");
		addRequestParameter("feeFrequency.feeFrequencyType.feeFrequencyTypeId",
				FeeFrequencyType.ONETIME.getValue().toString());
		addRequestParameter("customerCharge", FeePayment.UPFRONT.getValue()
				.toString());
		addRequestParameter("glCodeEntity.glcodeId", GLOCDE_ID);
		actionPerform();
		verifyNoActionErrors();
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "create");
		addRequestParameter("org.apache.struts.taglib.html.TOKEN",
				(String) request.getSession().getAttribute(
						"org.apache.struts.action.TOKEN"));
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.create_success.toString());

		HttpSession session = request.getSession();
		FeesBO fee = (FeesBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY,
				session);
		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("Customer One time", fee.getFeeName());
		assertEquals(Short.valueOf(FeeCategory.ALLCUSTOMERS.getValue()), fee
				.getCategoryType().getCategoryId());
		assertEquals("100.0", fee.getFeeAmount().toString());
		assertNull(fee.getRate());
		assertTrue(fee.isOneTime());
		assertFalse(fee.isAdminFee());
		assertTrue(fee.isActive());
	}

	public void testSuccessfulCreateOneTimeAdminFee() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "load");
		actionPerform();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType.categoryId", FeeCategory.ALLCUSTOMERS
				.getValue());
		addRequestParameter("amount", "100");
		addRequestParameter("adminCheck", "1");
		addRequestParameter("feeName", "Customer One time Admin Fee");
		addRequestParameter("feeFrequency.feeFrequencyType.feeFrequencyTypeId",
				FeeFrequencyType.ONETIME.getValue().toString());
		addRequestParameter("customerCharge", FeePayment.UPFRONT.getValue()
				.toString());
		addRequestParameter("glCodeEntity.glcodeId", GLOCDE_ID);
		actionPerform();
		verifyNoActionErrors();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "create");
		addRequestParameter("org.apache.struts.taglib.html.TOKEN",
				(String) request.getSession().getAttribute(
						"org.apache.struts.action.TOKEN"));
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.create_success.toString());

		HttpSession session = request.getSession();
		FeesBO fee = (FeesBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY,
				session);
		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("Customer One time Admin Fee", fee.getFeeName());
		assertEquals(Short.valueOf(FeeCategory.ALLCUSTOMERS.getValue()), fee
				.getCategoryType().getCategoryId());
		assertEquals("100.0", fee.getFeeAmount().toString());
		assertNull(fee.getRate());
		assertTrue(fee.isOneTime());
		assertTrue(fee.isAdminFee());
		assertTrue(fee.isActive());
	}

	public void testSuccessfulCreatePeriodicFee() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "load");
		actionPerform();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType.categoryId", FeeCategory.ALLCUSTOMERS
				.getValue());
		addRequestParameter("amount", "100");
		addRequestParameter("adminCheck", "1");
		addRequestParameter("feeName", "Customer Periodic Fee");
		addRequestParameter("feeFrequency.feeFrequencyType.feeFrequencyTypeId",
				FeeFrequencyType.PERIODIC.getValue().toString());
		addRequestParameter(
				"feeFrequency.feeMeetingFrequency.meetingDetails.recurrenceType.recurrenceId",
				MeetingFrequency.WEEKLY.getValue().toString());
		addRequestParameter("weekRecurAfter", "2");
		addRequestParameter("glCodeEntity.glcodeId", GLOCDE_ID);
		actionPerform();
		verifyNoActionErrors();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "create");
		addRequestParameter("org.apache.struts.taglib.html.TOKEN",
				(String) request.getSession().getAttribute(
						"org.apache.struts.action.TOKEN"));
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.create_success.toString());

		HttpSession session = request.getSession();
		FeesBO fee = (FeesBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY,
				session);
		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("Customer Periodic Fee", fee.getFeeName());
		assertEquals(Short.valueOf(FeeCategory.ALLCUSTOMERS.getValue()), fee
				.getCategoryType().getCategoryId());
		assertEquals("100.0", fee.getFeeAmount().toString());
		assertNull(fee.getRate());
		assertTrue(fee.isPeriodic());
		assertTrue(fee.isAdminFee());
		assertTrue(fee.isActive());
	}

	public void testSuccessfulCreatePeriodicFeeWithFormula() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "load");
		actionPerform();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType.categoryId", FeeCategory.LOAN
				.getValue());
		addRequestParameter("rate", "23");
		addRequestParameter("amount", "");
		addRequestParameter("feeFormula.feeFormulaId", FORMULA_ID.toString());
		addRequestParameter("feeName", "Loan Periodic Fee");
		addRequestParameter("adminCheck", "0");
		addRequestParameter("feeFrequency.feeFrequencyType.feeFrequencyTypeId",
				FeeFrequencyType.PERIODIC.getValue().toString());
		addRequestParameter(
				"feeFrequency.feeMeetingFrequency.meetingDetails.recurrenceType.recurrenceId",
				MeetingFrequency.WEEKLY.getValue().toString());
		addRequestParameter("weekRecurAfter", "2");
		addRequestParameter("glCodeEntity.glcodeId", GLOCDE_ID);
		actionPerform();
		verifyNoActionErrors();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "create");
		addRequestParameter("org.apache.struts.taglib.html.TOKEN",
				(String) request.getSession().getAttribute(
						"org.apache.struts.action.TOKEN"));
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.create_success.toString());

		HttpSession session = request.getSession();
		FeesBO fee = (FeesBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY,
				session);
		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("Loan Periodic Fee", fee.getFeeName());
		assertEquals(Short.valueOf(FeeCategory.LOAN.getValue()), fee
				.getCategoryType().getCategoryId());
		assertEquals(23.0, fee.getRate());
		assertTrue(fee.isRateFee());
		assertEquals(fee.getFeeFormula().getFeeFormulaId(), FORMULA_ID);
		assertTrue(fee.isPeriodic());
		assertTrue(fee.isActive());
	}

	public void testManage() {
		FeesBO fee = TestObjectFactory.createOneTimeFees("One Time Fee", 100.0,
				Short.valueOf("1"), 1);
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("feeId", fee.getFeeId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());

		HttpSession session = request.getSession();
		fee = (FeesBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY,
				request.getSession());
		assertEquals("One Time Fee", fee.getFeeName());
		assertEquals(Short.valueOf(FeeCategory.ALLCUSTOMERS.getValue()), fee
				.getCategoryType().getCategoryId());
		assertEquals(FeeFrequencyType.ONETIME.getValue(), fee.getFeeFrequency()
				.getFeeFrequencyType().getFeeFrequencyTypeId());
		assertEquals("The size of master data for status",
				((List<LookUpMaster>) session
						.getAttribute(FeesConstants.STATUSLIST)).size(), 2);
		assertEquals("The size of master data for loan formula : ",
				((List<LookUpMaster>) session
						.getAttribute(FeesConstants.FORMULALIST)).size(), 3);

	}

	public void testFailureEditPreviewForAmount() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("amount", "");
		actionPerform();
		verifyActionErrors(new String[] { "errors.enter", "errors.select" });
		verifyInputForward();
	}

	public void testFailureEditPreviewForRate() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("rate", "");
		actionPerform();
		verifyActionErrors(new String[] { "errors.rateAndFormulaId",
				"errors.select" });
		verifyInputForward();
	}

	public void testFailureEditPreviewWithAmount() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("amount", "100.0");
		actionPerform();
		verifyActionErrors(new String[] { "errors.select" });
		verifyInputForward();
	}

	public void testSuccessfulEditPreview() {
		FeesBO fee = TestObjectFactory.createOneTimeFees("One Time Fee", 100.0,
				Short.valueOf("1"), 1);
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("feeId", fee.getFeeId().toString());
		actionPerform();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("amount", "100.0");
		addRequestParameter("feeStatus.statusId", FeeStatus.INACTIVE.getValue()
				.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.editpreview_success.toString());

		HttpSession session = request.getSession();
		fee = (FeesBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY,
				request.getSession());
		assertFalse(fee.isActive());
		assertEquals("100.0", fee.getFeeAmount().toString());
	}

	public void testSuccessfulUpdate() {
		FeesBO fee = TestObjectFactory.createOneTimeFees("One Time Fee", 100.0,
				Short.valueOf("1"), 1);
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("feeId", fee.getFeeId().toString());
		actionPerform();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("amount", "100.0");
		addRequestParameter("feeStatus.statusId", FeeStatus.INACTIVE.getValue()
				.toString());
		actionPerform();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "update");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.update_success.toString());

		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertFalse(fee.isActive());
		assertEquals("100.0", fee.getFeeAmount().toString());
	}

}
