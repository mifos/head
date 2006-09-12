package org.mifos.application.productdefinition.struts.action;

import java.net.URISyntaxException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsPrdActionTest extends MifosMockStrutsTestCase {

	private SavingsOfferingBO savingsOffering;
	
	private String flowKey;

	UserContext userContext = null;

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(savingsOffering);
		super.tearDown();
		
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/application/productdefinition/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		userContext = TestObjectFactory.getUserContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow);
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);
	}

	public void testLoad() throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "load");

		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());

		List<ProductCategoryBO> productCategories = (List<ProductCategoryBO>) SessionUtils
				.getAttribute(
						ProductDefinitionConstants.SAVINGSPRODUCTCATEGORYLIST,
						request);
		assertEquals("The size of master data for categories", 1,
				productCategories.size());
		for (ProductCategoryBO productCategory : productCategories) {
			assertNotNull(productCategory.getProductType());
		}
		assertEquals(
				"The size of applicable list",
				3,
				((List<MasterDataEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.SAVINGSAPPLFORLIST, request))
						.size());
		assertEquals("The size of applicable list", 2,
				((List<MasterDataEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.SAVINGSTYPELIST, request))
						.size());
		assertEquals("The size of applicable list", 2,
				((List<MasterDataEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.RECAMNTUNITLIST, request))
						.size());
		assertEquals("The size of applicable list", 2,
				((List<MasterDataEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.INTCALCTYPESLIST, request))
						.size());
		assertEquals("The size of applicable list", 2,
				((List<MasterDataEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.SAVINGSRECURRENCETYPELIST,
						request)).size());
		assertEquals("The size of applicable list", 6,
				((List<GLCodeEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.SAVINGSDEPOSITGLCODELIST,
						request)).size());
		assertEquals("The size of applicable list", 2,
				((List<GLCodeEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.SAVINGSINTERESTGLCODELIST,
						request)).size());
	}

	public void testPreviewWithOutData() throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		actionPerform();
		verifyActionErrors(new String[] {
				"Please specify the Product instance name.",
				"Please specify the Short name.",
				"Please select the Product category.",
				"Please specify the Start date.",
				"Please select the Applicable for.",
				"Please select the Type of deposits.",
				"Please specify the Interest rate.",
				"Please select the Balance used for Interest calculation.",
				"Please specify the Time period for interest calculation.",
				"Please specify the Frequency of interest posting to accounts.",
				"Please select the GL code for deposits.",
				"Please select the GL code for interest." });
		verifyInputForward();
	}

	public void testPreviewWithPrdApplToGroupAndAppliesToNotEntered()
			throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Savings Offering");
		addRequestParameter("prdOfferingShortName", "SAVP");
		addRequestParameter("prdCategory", "2");
		addRequestParameter("startDate", DateHelper.getCurrentDate(userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "2");
		addRequestParameter("savingsType", "2");
		addRequestParameter("interestRate", "1");
		addRequestParameter("interestCalcType", "1");
		addRequestParameter("timeForInterestCacl", "1");
		addRequestParameter("recurTypeFortimeForInterestCacl", "2");
		addRequestParameter("freqOfInterest", "1");
		addRequestParameter("depositGLCode", "42");
		addRequestParameter("interestGLCode", "57");

		actionPerform();
		verifyActionErrors(new String[] { "Please select the Amount Applies to." });
		verifyInputForward();
	}

	public void testPreviewWithMandPrdAndAmountNotEntered() throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Savings Offering");
		addRequestParameter("prdOfferingShortName", "SAVP");
		addRequestParameter("prdCategory", "2");
		addRequestParameter("startDate", DateHelper.getCurrentDate(userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("savingsType", "1");
		addRequestParameter("interestRate", "1");
		addRequestParameter("interestCalcType", "1");
		addRequestParameter("timeForInterestCacl", "1");
		addRequestParameter("recurTypeFortimeForInterestCacl", "2");
		addRequestParameter("freqOfInterest", "1");
		addRequestParameter("depositGLCode", "42");
		addRequestParameter("interestGLCode", "57");

		actionPerform();
		verifyActionErrors(new String[] { ProductDefinitionConstants.ERRORMANDAMOUNT });
		verifyInputForward();
	}

	public void testPreviewWithMandPrdAndZeroAmountEntered() throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Savings Offering");
		addRequestParameter("prdOfferingShortName", "SAVP");
		addRequestParameter("prdCategory", "2");
		addRequestParameter("startDate", DateHelper.getCurrentDate(userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("savingsType", "1");
		addRequestParameter("interestRate", "1");
		addRequestParameter("interestCalcType", "1");
		addRequestParameter("timeForInterestCacl", "1");
		addRequestParameter("recurTypeFortimeForInterestCacl", "2");
		addRequestParameter("freqOfInterest", "1");
		addRequestParameter("depositGLCode", "42");
		addRequestParameter("interestGLCode", "57");
		addRequestParameter("recommendedAmount", "0.0");
		addRequestParameter("description", "Savings");
		addRequestParameter("maxAmntWithdrawl", "10.0");
		addRequestParameter("minAmntForInt", "10.0");

		actionPerform();
		verifyActionErrors(new String[] { ProductDefinitionConstants.ERRORMANDAMOUNT });
		verifyInputForward();
	}

	public void testPreviewWithInterestRateGreaterThanHundred()
			throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Savings Offering");
		addRequestParameter("prdOfferingShortName", "SAVP");
		addRequestParameter("prdCategory", "2");
		addRequestParameter("startDate", DateHelper.getCurrentDate(userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("savingsType", "1");
		addRequestParameter("interestRate", "100.1");
		addRequestParameter("interestCalcType", "1");
		addRequestParameter("timeForInterestCacl", "1");
		addRequestParameter("recurTypeFortimeForInterestCacl", "2");
		addRequestParameter("freqOfInterest", "1");
		addRequestParameter("depositGLCode", "42");
		addRequestParameter("interestGLCode", "57");
		addRequestParameter("recommendedAmount", "120.0");

		actionPerform();
		verifyActionErrors(new String[] { ProductDefinitionConstants.ERRORINTRATE });
		verifyInputForward();
	}

	public void testPreviewWithStartDateLessThanCurrentDate() throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Savings Offering");
		addRequestParameter("prdOfferingShortName", "SAVP");
		addRequestParameter("prdCategory", "2");
		addRequestParameter("startDate", offSetCurrentDate(-1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("savingsType", "1");
		addRequestParameter("interestRate", "100.0");
		addRequestParameter("interestCalcType", "1");
		addRequestParameter("timeForInterestCacl", "1");
		addRequestParameter("recurTypeFortimeForInterestCacl", "2");
		addRequestParameter("freqOfInterest", "1");
		addRequestParameter("depositGLCode", "42");
		addRequestParameter("interestGLCode", "57");
		addRequestParameter("recommendedAmount", "120.0");

		actionPerform();
		verifyActionErrors(new String[] { ProductDefinitionConstants.INVALIDSTARTDATE });
		verifyInputForward();
	}

	public void testPreviewWithEndDateLessThanStartDate() throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Savings Offering");
		addRequestParameter("prdOfferingShortName", "SAVP");
		addRequestParameter("prdCategory", "2");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(-1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("savingsType", "1");
		addRequestParameter("interestRate", "100.0");
		addRequestParameter("interestCalcType", "1");
		addRequestParameter("timeForInterestCacl", "1");
		addRequestParameter("recurTypeFortimeForInterestCacl", "2");
		addRequestParameter("freqOfInterest", "1");
		addRequestParameter("depositGLCode", "42");
		addRequestParameter("interestGLCode", "57");
		addRequestParameter("recommendedAmount", "120.0");

		actionPerform();
		verifyActionErrors(new String[] { ProductDefinitionConstants.INVALIDENDDATE });
		verifyInputForward();
	}

	public void testPreview() throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Savings Offering");
		addRequestParameter("prdOfferingShortName", "SAVP");
		addRequestParameter("prdCategory", "2");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(+1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("savingsType", "1");
		addRequestParameter("interestRate", "100.0");
		addRequestParameter("interestCalcType", "1");
		addRequestParameter("timeForInterestCacl", "1");
		addRequestParameter("recurTypeFortimeForInterestCacl", "2");
		addRequestParameter("freqOfInterest", "1");
		addRequestParameter("depositGLCode", "42");
		addRequestParameter("interestGLCode", "57");
		addRequestParameter("recommendedAmount", "120.0");

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.preview_success.toString());
	}

	public void testCreate() throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "load");
		actionPerform();

		flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Savings Offering");
		addRequestParameter("prdOfferingShortName", "SAVP");
		addRequestParameter("prdCategory", "2");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(+1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("savingsType", "1");
		addRequestParameter("interestRate", "100.0");
		addRequestParameter("interestCalcType", "1");
		addRequestParameter("timeForInterestCacl", "1");
		addRequestParameter("recurTypeFortimeForInterestCacl", "2");
		addRequestParameter("freqOfInterest", "1");
		addRequestParameter("depositGLCode", "42");
		addRequestParameter("interestGLCode", "57");
		addRequestParameter("recommendedAmount", "120.0");
		actionPerform();

		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.create_success.toString());

		assertNotNull(request
				.getAttribute(ProductDefinitionConstants.SAVINGSPRODUCTID));
		TestObjectFactory
				.removeObject((SavingsOfferingBO) TestObjectFactory
						.getObject(
								SavingsOfferingBO.class,
								(Short) request
										.getAttribute(ProductDefinitionConstants.SAVINGSPRODUCTID)));
	}

	public void testCreateForPrdApplicableToGroups() throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "load");
		actionPerform();

		flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Savings Offering");
		addRequestParameter("prdOfferingShortName", "SAVP");
		addRequestParameter("prdCategory", "2");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(+1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "2");
		addRequestParameter("savingsType", "1");
		addRequestParameter("interestRate", "100.0");
		addRequestParameter("interestCalcType", "1");
		addRequestParameter("timeForInterestCacl", "1");
		addRequestParameter("recurTypeFortimeForInterestCacl", "2");
		addRequestParameter("freqOfInterest", "1");
		addRequestParameter("depositGLCode", "42");
		addRequestParameter("interestGLCode", "57");
		addRequestParameter("recommendedAmount", "120.0");
		addRequestParameter("recommendedAmntUnit", "1");
		actionPerform();

		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.create_success.toString());

		assertNotNull(request
				.getAttribute(ProductDefinitionConstants.SAVINGSPRODUCTID));
		TestObjectFactory
				.removeObject((SavingsOfferingBO) TestObjectFactory
						.getObject(
								SavingsOfferingBO.class,
								(Short) request
										.getAttribute(ProductDefinitionConstants.SAVINGSPRODUCTID)));
	}

	public void testCreateForVolProducts() throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "load");
		actionPerform();

		flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Savings Offering");
		addRequestParameter("prdOfferingShortName", "SAVP");
		addRequestParameter("prdCategory", "2");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(+1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "2");
		addRequestParameter("savingsType", "2");
		addRequestParameter("interestRate", "100.0");
		addRequestParameter("interestCalcType", "1");
		addRequestParameter("timeForInterestCacl", "1");
		addRequestParameter("recurTypeFortimeForInterestCacl", "2");
		addRequestParameter("freqOfInterest", "1");
		addRequestParameter("depositGLCode", "42");
		addRequestParameter("interestGLCode", "57");
		addRequestParameter("recommendedAmntUnit", "1");
		actionPerform();

		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.create_success.toString());

		assertNotNull(request
				.getAttribute(ProductDefinitionConstants.SAVINGSPRODUCTID));
		TestObjectFactory
				.removeObject((SavingsOfferingBO) TestObjectFactory
						.getObject(
								SavingsOfferingBO.class,
								(Short) request
										.getAttribute(ProductDefinitionConstants.SAVINGSPRODUCTID)));
	}

	public void testCancelCreate() throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "cancelCreate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.cancelCreate_success.toString());
	}

	public void testPrevious() throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "previous");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.previous_success.toString());
	}
	
	public void testValidate() throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.preview_success.toString());
	}


	public void testValidateForPreview() throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute(ProductDefinitionConstants.METHODCALLED,
				Methods.preview.toString());

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.preview_failure.toString());
	}

	public void testVaildateForCreate() throws Exception {
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute(ProductDefinitionConstants.METHODCALLED,
				Methods.create.toString());

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.create_failure.toString());
	}
	
	public void testGet() throws Exception {
		String prdName ="Savings_Kendra";
		String prdShortName ="SSK";
		createSavingsOfferingBO( prdName,prdShortName);
		setRequestPathInfo("/savingsproductaction.do");
		addRequestParameter("method", "get");
		addRequestParameter("prdOfferingId", savingsOffering.getPrdOfferingId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.get_success.toString());
		assertEquals(prdName , savingsOffering.getPrdOfferingName());
		assertEquals(prdShortName , savingsOffering.getPrdOfferingShortName());
		assertEquals(prdShortName , savingsOffering.getPrdOfferingShortName());
		assertEquals(PrdStatus.SAVINGSACTIVE.getValue(), savingsOffering
				.getPrdStatus().getOfferingStatusId());
		assertEquals(2, savingsOffering.getSavingsType().getId().shortValue());
	}

	private String offSetCurrentDate(int noOfDays, Locale locale) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
		java.sql.Date currentDate = new java.sql.Date(currentDateCalendar
				.getTimeInMillis());
		SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance(
				DateFormat.SHORT, locale);
		String userfmt = DateHelper
				.convertToCurrentDateFormat(((SimpleDateFormat) sdf)
						.toPattern());
		return DateHelper.convertDbToUserFmt(currentDate.toString(), userfmt);
	}
	
	private SavingsOfferingBO createSavingsOfferingBO(String prdOfferingName,
			String shortName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		savingsOffering = TestObjectFactory.createSavingsOffering(prdOfferingName,
				shortName, (short) 1, new Date(System.currentTimeMillis()),
				(short) 2, 300.0, (short) 1, 1.2, 200.0, 200.0, (short) 2,
				(short) 1, meetingIntCalc, meetingIntPost);
		return savingsOffering;
	}
}
