package org.mifos.application.configuration.struts.action;

import java.util.List;

import org.mifos.application.accounts.loan.struts.action.MultipleLoanAccountsCreationAction;
import org.mifos.application.configuration.struts.actionform.LookupOptionsActionForm;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LookupOptionsActionTest extends MifosMockStrutsTestCase{

	private UserContext userContext;
	private String flowKey;

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/configuration/struts-config.xml")
				.getPath());
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, MultipleLoanAccountsCreationAction.class);
		
	}

	private boolean compareLists(List<CustomValueListElement> first, String[] second, int expectedLength) {
		
		assertEquals(first.size(),expectedLength);
		for (int index = 0; index < second.length; ++index) {
			assertEquals(first.get(index).getLookUpValue(), second[index]);
		}
		return true;
	}
	
	public void testLoad() throws Exception {
		setRequestPathInfo("/lookupOptionsAction.do");
		addRequestParameter("method", "load");
		performNoErrors();
		verifyForward(ActionForwards.load_success.toString());
		LookupOptionsActionForm lookupOptionsActionForm = 
			(LookupOptionsActionForm) request
				.getSession().getAttribute("lookupoptionsactionform");
		
		String[] EXPECTED_SALUTATIONS = {"Mr","Mrs","Ms"};
		assertTrue(compareLists(lookupOptionsActionForm.getSalutations(), EXPECTED_SALUTATIONS, 3));

		String[] EXPECTED_PERSONNEL_TITLES = {"Cashier","Center Manager","Branch Manager"};
		assertTrue(compareLists(lookupOptionsActionForm.getUserTitles(), EXPECTED_PERSONNEL_TITLES, 9));
		
		String[] EXPECTED_MARITAL_STATUS = {"Married","UnMarried","Widowed"};
		assertTrue(compareLists(lookupOptionsActionForm.getMaritalStatuses(), EXPECTED_MARITAL_STATUS, 3));

		String[] EXPECTED_ETHNICITY = {"SC","BC","ST"};
		assertTrue(compareLists(lookupOptionsActionForm.getEthnicities(), EXPECTED_ETHNICITY, 5));

		String[] EXPECTED_EDUCATION_LEVEL = {"Only Client","Only Husband","Both Literate"};
		assertTrue(compareLists(lookupOptionsActionForm.getEducationLevels(), EXPECTED_EDUCATION_LEVEL, 4));

		String[] EXPECTED_CITIZENSHIP = {"Hindu","Muslim","Christian"};
		assertTrue(compareLists(lookupOptionsActionForm.getCitizenships(), EXPECTED_CITIZENSHIP, 3));

		String[] EXPECTED_LOAN_PURPOSES = {"0000-Animal Husbandry","0001-Cow Purchase","0002-Buffalo Purchase"};
		assertTrue(compareLists(lookupOptionsActionForm.getPurposesOfLoan(), EXPECTED_LOAN_PURPOSES, 129));

		String[] EXPECTED_COLLATERAL_TYPES = {"Type 1","Type 2"};
		assertTrue(compareLists(lookupOptionsActionForm.getCollateralTypes(), EXPECTED_COLLATERAL_TYPES, 2));

		String[] EXPECTED_HANDICAPPED = {"Yes","No"};
		assertTrue(compareLists(lookupOptionsActionForm.getHandicappeds(), EXPECTED_HANDICAPPED, 2));

		String[] EXPECTED_ATTENDENCE = {"P","Ab","AL"};
		assertTrue(compareLists(lookupOptionsActionForm.getAttendances(), EXPECTED_ATTENDENCE, 4));

		String[] EXPECTED_OFFICER_TITLES = {"President","Vice President"};
		assertTrue(compareLists(lookupOptionsActionForm.getOfficerTitles(), EXPECTED_OFFICER_TITLES, 2));
		
	}
	
	public void testCancel() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/lookupOptionsAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancel_success.toString());
	}
	
	
}
