package org.mifos.application.holiday.struts.action;

import java.util.List;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.util.resources.HolidayConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestHolidayAction extends MifosMockStrutsTestCase {

	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/holiday/struts-config.xml")
				.getPath());
		UserContext userContext = TestObjectFactory.getUserContext();
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
		assertNotNull(SessionUtils.getAttribute(HolidayConstants.HOLIDAYLIST1,
				request));
		assertNotNull(SessionUtils.getAttribute(HolidayConstants.HOLIDAYLIST2,
				request));
		assertEquals(0, ((List<HolidayBO>) SessionUtils.getAttribute(
				HolidayConstants.HOLIDAYLIST1, request)).size());
		assertEquals(0, ((List<HolidayBO>) SessionUtils.getAttribute(
				HolidayConstants.HOLIDAYLIST2, request)).size());
	}

}
