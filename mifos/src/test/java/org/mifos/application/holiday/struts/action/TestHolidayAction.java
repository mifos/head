package org.mifos.application.holiday.struts.action;

import org.mifos.application.holiday.util.resources.HolidayConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class TestHolidayAction extends MifosMockStrutsTestCase {

	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UserContext userContext = TestUtils.makeUser();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		//addRequestParameter("recordLoanOfficerId", "1");
		//addRequestParameter("recordOfficeId", "1");
		//ActivityContext ac = new ActivityContext((short) 0, userContext
		//		.getBranchId().shortValue(), userContext.getId().shortValue());
		//request.getSession(false).setAttribute("ActivityContext", ac);
		flowKey = createFlow(request, HolidayAction.class);
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}
 
	public void testLoad() throws Exception {
		setRequestPathInfo("/holidayAction");
		addRequestParameter("method", "load");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(
				HolidayConstants.REPAYMENTRULETYPES, request)); //HOLIDAY_MASTERDATA
	}

	public void testGetHolidays() throws Exception {
		setRequestPathInfo("/holidayAction");
		addRequestParameter("method", "getHolidays");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.get_success.toString());		
		assertNull(SessionUtils.getAttribute("noOfYears", request));		
	}

}
