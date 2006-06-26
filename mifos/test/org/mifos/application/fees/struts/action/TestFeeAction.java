/**

 * TestFeeAction.java    version: xxx



 * Copyright © 2005-2006 Grameen Foundation USA

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
import org.mifos.application.fees.util.helpers.FeesConstants;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

import servletunit.struts.MockStrutsTestCase;

public class TestFeeAction extends MockStrutsTestCase {

	public TestFeeAction() {
		super();
	}

	public TestFeeAction(String names) {
		super(names);
	}

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
		verifyForward(FeesConstants.LOADSUCCESS);

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
		addRequestParameter("categoryType.categoryId", FeesConstants.LOAN);
		addRequestParameter("feeFrequency.feeFrequencyType.feeFrequencyTypeId",
				FeesConstants.ONETIME.toString());
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
		addRequestParameter("categoryType.categoryId", FeesConstants.LOAN);
		addRequestParameter("feeFrequency.feeFrequencyType.feeFrequencyTypeId",
				FeesConstants.ONETIME.toString());
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
		addRequestParameter("categoryType.categoryId",
				FeesConstants.ALLCUSTOMERS);
		addRequestParameter("amount", "100");
		addRequestParameter("feeName", "Customer One time");
		addRequestParameter("feeFrequency.feeFrequencyType.feeFrequencyTypeId",
				FeesConstants.ONETIME.toString());
		addRequestParameter("customerCharge", FeesConstants.UPFRONT.toString());
		addRequestParameter("glCodeEntity.glcodeId", "1");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(FeesConstants.PREVIEWSUCCESS);

		HttpSession session = request.getSession();
		FeesBO fees = (FeesBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, session);
		assertEquals("The category entered :", fees.getCategoryType()
				.getCategoryId(), Short.valueOf(FeesConstants.ALLCUSTOMERS));
		assertEquals("The amount entered :", fees.getRateOrAmount(), Double
				.valueOf("100"));
		assertEquals("The fee name entered :", fees.getFeeName(),
				"Customer One time");
	}

	public void testSuccessfulCreateOneTimeFee() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType.categoryId",
				FeesConstants.ALLCUSTOMERS);
		addRequestParameter("amount", "100");
		addRequestParameter("feeName", "Customer One time");
		addRequestParameter("feeFrequency.feeFrequencyType.feeFrequencyTypeId",
				FeesConstants.ONETIME.toString());
		addRequestParameter("customerCharge", FeesConstants.UPFRONT.toString());
		addRequestParameter("glCodeEntity.glcodeId", "1");
		actionPerform();
		verifyNoActionErrors();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(FeesConstants.CREATESUCCESS);

		HttpSession session = request.getSession();
		FeesBO fees = (FeesBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, session);
		fees = (FeesBO) TestObjectFactory.getObject(FeesBO.class, fees
				.getFeeId());
		assertEquals("The fee name entered :", fees.getFeeName(),
				"Customer One time");
		assertEquals("The category entered :", fees.getCategoryType()
				.getCategoryId(), Short.valueOf(FeesConstants.ALLCUSTOMERS));
		assertEquals("The amount entered :", fees.getRateOrAmount(), Double
				.valueOf("100"));
		assertEquals("The fee amount entered :", fees.getFeeAmount().toString(), "100.0");
		assertEquals("The rate entered :", fees.getRate(), null);
		assertEquals("The frequency of the fees :", fees.getFeeFrequency()
				.getFeeFrequencyType().getFeeFrequencyTypeId(),
				FeesConstants.ONETIME);
		assertEquals("The size of the fee level is :", fees.getFeeLevels()
				.size(), 0);

	}

	public void testSuccessfulCreateOneTimeAdminFee() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType.categoryId",
				FeesConstants.ALLCUSTOMERS);
		addRequestParameter("amount", "100");
		addRequestParameter("adminCheck", "Yes");
		addRequestParameter("feeName", "Customer One time Admin Fee");
		addRequestParameter("feeFrequency.feeFrequencyType.feeFrequencyTypeId",
				FeesConstants.ONETIME.toString());
		addRequestParameter("customerCharge", FeesConstants.UPFRONT.toString());
		addRequestParameter("glCodeEntity.glcodeId", "1");
		actionPerform();
		verifyNoActionErrors();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(FeesConstants.CREATESUCCESS);

		HttpSession session = request.getSession();
		FeesBO fees = (FeesBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, session);
		fees = (FeesBO) TestObjectFactory.getObject(FeesBO.class, fees
				.getFeeId());
		assertEquals("The fee name entered :", fees.getFeeName(),
				"Customer One time Admin Fee");
		assertEquals("The category entered :", fees.getCategoryType()
				.getCategoryId(), Short.valueOf(FeesConstants.ALLCUSTOMERS));
		assertEquals("The amount entered :", fees.getRateOrAmount(), Double
				.valueOf("100"));
		assertEquals("The fee amount entered :", fees.getFeeAmount().toString(), "100.0");
		assertEquals("The rate entered :", fees.getRate(), null);
		assertEquals("The frequency of the fees :", fees.getFeeFrequency()
				.getFeeFrequencyType().getFeeFrequencyTypeId(),
				FeesConstants.ONETIME);
		assertEquals("The size of the fee level is :", fees.getFeeLevels()
				.size(), 3);

	}

	public void testSuccessfulCreatePeriodicFee() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType.categoryId",
				FeesConstants.ALLCUSTOMERS);
		addRequestParameter("amount", "100");
		addRequestParameter("adminCheck", "Yes");
		addRequestParameter("feeName", "Customer Periodic Fee");
		addRequestParameter("feeFrequency.feeFrequencyType.feeFrequencyTypeId",
				FeesConstants.PERIODIC.toString());
		addRequestParameter(
				"feeFrequency.feeMeetingFrequency.meetingDetails.recurrenceType.recurrenceId",
				FeesConstants.WEEKLY.toString());
		addRequestParameter("weekRecurAfter", "2");
		addRequestParameter("glCodeEntity.glcodeId", "1");
		actionPerform();
		verifyNoActionErrors();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(FeesConstants.CREATESUCCESS);

		HttpSession session = request.getSession();
		FeesBO fees = (FeesBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, session);
		fees = (FeesBO) TestObjectFactory.getObject(FeesBO.class, fees
				.getFeeId());
		assertEquals("The fee name entered :", fees.getFeeName(),
				"Customer Periodic Fee");
		assertEquals("The category entered :", fees.getCategoryType()
				.getCategoryId(), Short.valueOf(FeesConstants.ALLCUSTOMERS));
		assertEquals("The amount entered :", fees.getRateOrAmount(), Double
				.valueOf("100"));
		assertEquals("The fees amount entered :", fees.getFeeAmount().toString(), "100.0");
		assertEquals("The rate entered :", fees.getRate(), null);
		assertEquals("The frequency of the fees :", fees.getFeeFrequency()
				.getFeeFrequencyType().getFeeFrequencyTypeId(),
				FeesConstants.PERIODIC);
		assertEquals("The size of the fee level is :", fees.getFeeLevels()
				.size(), 3);

	}

	public void testSuccessfulCreatePeriodicFeeWithFormula() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType.categoryId", FeesConstants.LOAN);
		addRequestParameter("rate", "23");
		addRequestParameter("amount", "");
		addRequestParameter("feeFormula.feeFormulaId", "1");
		addRequestParameter("feeName", "Loan Periodic Fee");
		addRequestParameter("adminCheck", "No");
		addRequestParameter("feeFrequency.feeFrequencyType.feeFrequencyTypeId",
				FeesConstants.PERIODIC.toString());
		addRequestParameter(
				"feeFrequency.feeMeetingFrequency.meetingDetails.recurrenceType.recurrenceId",
				FeesConstants.WEEKLY.toString());
		addRequestParameter("weekRecurAfter", "2");
		addRequestParameter("glCodeEntity.glcodeId", "1");
		actionPerform();
		verifyNoActionErrors();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(FeesConstants.CREATESUCCESS);

		HttpSession session = request.getSession();
		FeesBO fees = (FeesBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, session);
		fees = (FeesBO) TestObjectFactory.getObject(FeesBO.class, fees
				.getFeeId());
		assertEquals("The fee name entered :", fees.getFeeName(),
				"Loan Periodic Fee");
		assertEquals("The category entered :", fees.getCategoryType()
				.getCategoryId(), Short.valueOf(FeesConstants.LOAN));
		assertEquals("The rate entered :", fees.getRateOrAmount(), Double
				.valueOf("23"));
		assertEquals("The rate entered :", fees.getRate(), Double
				.valueOf("23"));
		assertEquals("The rate flag should be 1:", fees.getRateFlatFlag(),
				Short.valueOf("1"));
		assertEquals("The formula id should be 1:", fees.getFeeFormula()
				.getFeeFormulaId(), Short.valueOf("1"));
		assertEquals("The frequency of the fees :", fees.getFeeFrequency()
				.getFeeFrequencyType().getFeeFrequencyTypeId(),
				FeesConstants.PERIODIC);
	}
	
	public void testManage() {
		FeesBO fees = TestObjectFactory.createOneTimeFees("One Time Fee",100.0, Short.valueOf("1"), 1);
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("feeId", fees.getFeeId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(FeesConstants.MANAGESUCCESS);

		HttpSession session = request.getSession();
		fees = (FeesBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request.getSession());
		assertEquals("The fee name entered :", fees.getFeeName(),"One Time Fee");
		assertEquals("The category entered :", fees.getCategoryType().getCategoryId(), Short.valueOf(FeesConstants.ALLCUSTOMERS));
		assertEquals("The amount entered :", fees.getRateOrAmount(), Double.valueOf("100"));
		assertEquals("The frequency of the fees :", fees.getFeeFrequency().getFeeFrequencyType().getFeeFrequencyTypeId(),
		FeesConstants.ONETIME);
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
		verifyActionErrors(new String[] { "errors.enter","errors.select"});
		verifyInputForward();
	}
	
	public void testFailureEditPreviewForRate() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("rate", "");
		actionPerform();
		verifyActionErrors(new String[] { "errors.rateAndFormulaId","errors.select"});
		verifyInputForward();
	}
	
	public void testFailureEditPreviewWithAmount() {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("amount", "100.0");
		actionPerform();
		verifyActionErrors(new String[] { "errors.select"});
		verifyInputForward();
	}
	
	public void testSuccessfulEditPreview() {
		FeesBO fees = TestObjectFactory.createOneTimeFees("One Time Fee",100.0, Short.valueOf("1"), 1);
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("feeId", fees.getFeeId().toString());
		actionPerform();
		
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("amount", "100.0");
		addRequestParameter("feeStatus.statusId",FeesConstants.STATUS_INACTIVE.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyForward(FeesConstants.EDITPREVIEWSUCCESS);
		
		HttpSession session = request.getSession();
		fees = (FeesBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request.getSession());
		assertEquals("The status should be inactive :", fees.getFeeStatus().getStatusId(),FeesConstants.STATUS_INACTIVE);
		assertEquals("The amount is :", fees.getFeeAmount().toString(),"100.0");
	}
	

	public void testSuccessfulUpdate() {
		FeesBO fees = TestObjectFactory.createOneTimeFees("One Time Fee",100.0, Short.valueOf("1"), 1);
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("feeId", fees.getFeeId().toString());
		actionPerform();
		
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("amount", "100.0");
		addRequestParameter("feeStatus.statusId",FeesConstants.STATUS_INACTIVE.toString());
		actionPerform();
		
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "update");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(FeesConstants.UPDATESUCCESS);
		
		fees = (FeesBO) TestObjectFactory.getObject(FeesBO.class, fees.getFeeId());
		assertEquals("The status should be inactive :", fees.getFeeStatus().getStatusId(),FeesConstants.STATUS_INACTIVE);
		assertEquals("The amount is :", fees.getFeeAmount().toString(),"100.0");
	}

}
