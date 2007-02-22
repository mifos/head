package org.mifos.application.meeting.struts.action;

import java.util.Date;

import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.struts.actionforms.MeetingActionForm;
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;



public class MeetingActionTest extends MifosMockStrutsTestCase{
	private String flowKey;
	private CenterBO center;
	private GroupBO group;
	private ClientBO client1;
	private ClientBO client2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/meeting/struts-config.xml")
				.getPath());
		UserContext userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");

		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, MeetingAction.class);
	}

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoadForCenter()throws Exception{
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerLevel", CustomerLevel.CENTER.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("input", "create");
		actionPerform();
		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(MeetingConstants.WEEKDAYSLIST,request));
		assertNotNull(SessionUtils.getAttribute(MeetingConstants.WEEKRANKLIST,request));
		verifyNoActionErrors();
		verifyNoActionMessages();
		MeetingActionForm actionForm = (MeetingActionForm)request.getSession().getAttribute("meetingActionForm");
		assertEquals(CustomerLevel.CENTER,actionForm.getCustomerLevelValue());
	}

	public void testLoadForGroup()throws Exception{
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerLevel", CustomerLevel.GROUP.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("input", "create");
		actionPerform();

		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(MeetingConstants.WEEKDAYSLIST,request));
		assertNotNull(SessionUtils.getAttribute(MeetingConstants.WEEKRANKLIST,request));
		verifyNoActionErrors();
		verifyNoActionMessages();
		MeetingActionForm actionForm = (MeetingActionForm)request.getSession().getAttribute("meetingActionForm");
		assertEquals(CustomerLevel.GROUP,actionForm.getCustomerLevelValue());
	}

	public void testLoadForClient()throws Exception{
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerLevel", CustomerLevel.CLIENT.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("input", "create");
		actionPerform();

		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(MeetingConstants.WEEKDAYSLIST,request));
		assertNotNull(SessionUtils.getAttribute(MeetingConstants.WEEKRANKLIST,request));
		verifyNoActionErrors();
		verifyNoActionMessages();
		MeetingActionForm actionForm = (MeetingActionForm)request.getSession().getAttribute("meetingActionForm");
		assertEquals(CustomerLevel.CLIENT,actionForm.getCustomerLevelValue());
	}

	public void testLoad_WeeklyMeetingExists()throws Exception{
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		actionPerform();
		Short recurAfter = Short.valueOf("1");
		MeetingBO meeting = createWeeklyMeeting(WeekDay.MONDAY, recurAfter, new  Date());
		SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, meeting, request);

		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerLevel", CustomerLevel.CENTER.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("input", "create");
		actionPerform();

		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(MeetingConstants.WEEKDAYSLIST,request));
		assertNotNull(SessionUtils.getAttribute(MeetingConstants.WEEKRANKLIST,request));
		verifyNoActionErrors();
		verifyNoActionMessages();

		MeetingActionForm actionForm = (MeetingActionForm)request.getSession().getAttribute("meetingActionForm");

		assertEquals(CustomerLevel.CENTER,actionForm.getCustomerLevelValue());
		assertEquals(WeekDay.MONDAY,actionForm.getWeekDayValue());
		assertEquals(recurAfter, actionForm.getRecurWeekValue());
		assertEquals(RecurrenceType.WEEKLY, actionForm.getRecurrenceType());
	}

	public void testLoad_MonthlyOnDateMeetingExists()throws Exception{
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		actionPerform();
		Short recurAfter = Short.valueOf("2");
		Short dayNumber = Short.valueOf("5");
		MeetingBO meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, new  Date());
		SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, meeting, request);

		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerLevel", CustomerLevel.CENTER.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("input", "create");
		actionPerform();

		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(MeetingConstants.WEEKDAYSLIST,request));
		assertNotNull(SessionUtils.getAttribute(MeetingConstants.WEEKRANKLIST,request));
		verifyNoActionErrors();
		verifyNoActionMessages();

		MeetingActionForm actionForm = (MeetingActionForm)request.getSession().getAttribute("meetingActionForm");

		assertEquals(CustomerLevel.CENTER,actionForm.getCustomerLevelValue());
		assertEquals("1",actionForm.getMonthType());
		assertEquals(dayNumber,actionForm.getMonthDayValue());
		assertEquals(recurAfter, actionForm.getDayRecurMonthValue());
		assertEquals(RecurrenceType.MONTHLY, actionForm.getRecurrenceType());
	}

	public void testLoad_MonthlyOnWeekMeetingExists()throws Exception{
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		actionPerform();
		Short recurAfter = Short.valueOf("2");

		MeetingBO meeting = createMonthlyMeetingOnWeekDay(WeekDay.FRIDAY, RankType.SECOND, recurAfter, new  Date());
		SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, meeting, request);

		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerLevel", CustomerLevel.CENTER.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("input", "create");
		actionPerform();

		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(MeetingConstants.WEEKDAYSLIST,request));
		assertNotNull(SessionUtils.getAttribute(MeetingConstants.WEEKRANKLIST,request));
		verifyNoActionErrors();
		verifyNoActionMessages();

		MeetingActionForm actionForm = (MeetingActionForm)request.getSession().getAttribute("meetingActionForm");

		assertEquals(CustomerLevel.CENTER,actionForm.getCustomerLevelValue());
		assertEquals("2",actionForm.getMonthType());
		assertEquals(WeekDay.FRIDAY,actionForm.getMonthWeekValue());
		assertEquals(RankType.SECOND,actionForm.getMonthRankValue());
		assertEquals(recurAfter, actionForm.getRecurMonthValue());
		assertEquals(RecurrenceType.MONTHLY, actionForm.getRecurrenceType());
	}


	public void testFailureCreateMeeting_RecurrenceIsNull()throws Exception{
		loadMeetingPage();
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("frequency", "");
		addRequestParameter("meetingPlace", "Delhi");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("input", "create");
		actionPerform();

		assertEquals("RecurrenceType", 1, getErrrorSize(MeetingConstants.INVALID_RECURRENCETYPE));
		verifyInputForward();
		MeetingBO meeting = (MeetingBO)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		assertNull(meeting);
	}

	public void testFailureCreateWeeklyMeeting_WeekDayAndRecurAfterIsNull()throws Exception{
		loadMeetingPage();
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("frequency", RecurrenceType.WEEKLY.getValue().toString());
		addRequestParameter("weekDay", "");
		addRequestParameter("recurWeek", "");
		addRequestParameter("meetingPlace", "");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("input", "create");

		actionPerform();
		assertEquals("Week Recurrence", 1, getErrrorSize(MeetingConstants.ERRORS_SPECIFY_WEEKDAY_AND_RECURAFTER));
		assertEquals("Meeting Place", 1, getErrrorSize(MeetingConstants.INVALID_MEETINGPLACE));
		verifyInputForward();
		MeetingBO meeting = (MeetingBO)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		assertNull(meeting);
	}

	public void testFailureCreateWeeklyMeeting_WeekDayIsNull()throws Exception{
		loadMeetingPage();
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("frequency", RecurrenceType.WEEKLY.getValue().toString());
		addRequestParameter("weekDay", "");
		addRequestParameter("recurWeek", "2");
		addRequestParameter("meetingPlace", "");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("input", "create");
		actionPerform();

		assertEquals("Week Recurrence", 1, getErrrorSize(MeetingConstants.ERRORS_SPECIFY_WEEKDAY_AND_RECURAFTER));
		assertEquals("Meeting Place", 1, getErrrorSize(MeetingConstants.INVALID_MEETINGPLACE));
		verifyInputForward();
		MeetingBO meeting = (MeetingBO)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		assertNull(meeting);
	}

	public void testFailureCreateWeeklyMeeting_MeetingPlaceIsNull()throws Exception{
		loadMeetingPage();
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("frequency", RecurrenceType.WEEKLY.getValue().toString());
		addRequestParameter("weekDay", WeekDay.MONDAY.getValue().toString());
		addRequestParameter("recurWeek", "2");
		addRequestParameter("meetingPlace", "");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("input", "create");
		actionPerform();

		assertEquals("Meeting Place", 1, getErrrorSize(MeetingConstants.INVALID_MEETINGPLACE));
		verifyInputForward();
		MeetingBO meeting = (MeetingBO)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		assertNull(meeting);
	}

	public void testSuccessfulCreateWeeklyMeeting()throws Exception{
		loadMeetingPage();
		String meetingPlace = "Delhi";
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("frequency", RecurrenceType.WEEKLY.getValue().toString());
		addRequestParameter("weekDay", WeekDay.MONDAY.getValue().toString());
		addRequestParameter("recurWeek", "2");
		addRequestParameter("meetingPlace", meetingPlace);
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("input", "create");
		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadCreateCenter.toString());
		MeetingBO meeting = (MeetingBO)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		assertTrue(meeting.isWeekly());
		assertEquals(meetingPlace, meeting.getMeetingPlace());
		assertEquals(Short.valueOf("2"), meeting.getMeetingDetails().getRecurAfter());
		assertEquals(WeekDay.MONDAY,meeting.getMeetingDetails().getWeekDay());
	}

	public void testFailureCreateMonthlyMeetingOnDate_DayNumAndRecurAfterIsNull()throws Exception{
		loadMeetingPage();
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("frequency", RecurrenceType.MONTHLY.getValue().toString());
		addRequestParameter("monthType", MeetingConstants.MONTHLY_ON_DATE);
		addRequestParameter("monthDay", "");
		addRequestParameter("dayRecurMonth", "");
		addRequestParameter("meetingPlace", "Delhi");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("input", "create");
		actionPerform();

		assertEquals("Month Recurrence On Date", 1, getErrrorSize(MeetingConstants.ERRORS_SPECIFY_DAYNUM_AND_RECURAFTER));
		verifyInputForward();
		MeetingBO meeting = (MeetingBO)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		assertNull(meeting);
	}

	public void testFailureCreateMonthlyMeetingOnDate_RecurAfterIsNull()throws Exception{
		loadMeetingPage();
		Short dayNumber = Short.valueOf("1");
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("frequency", RecurrenceType.MONTHLY.getValue().toString());
		addRequestParameter("monthType", MeetingConstants.MONTHLY_ON_DATE);
		addRequestParameter("monthDay", dayNumber.toString());
		addRequestParameter("dayRecurMonth", "");
		addRequestParameter("meetingPlace", "Delhi");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("input", "create");
		actionPerform();

		assertEquals("Month Recurrence On Date", 1, getErrrorSize(MeetingConstants.ERRORS_SPECIFY_DAYNUM_AND_RECURAFTER));
		verifyInputForward();
		MeetingBO meeting = (MeetingBO)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		assertNull(meeting);
	}

	public void testFailureCreateMonthlyMeetingOnDate_MeetingPlaceIsNull()throws Exception{
		loadMeetingPage();
		Short dayNumber = Short.valueOf("5");
		Short recurAfter = Short.valueOf("1");
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("frequency", RecurrenceType.MONTHLY.getValue().toString());
		addRequestParameter("monthType", MeetingConstants.MONTHLY_ON_DATE);
		addRequestParameter("monthDay", dayNumber.toString());
		addRequestParameter("dayRecurMonth", recurAfter.toString());
		addRequestParameter("meetingPlace", "");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals("Month Recurrence On Date", 1, getErrrorSize(MeetingConstants.INVALID_MEETINGPLACE));
		verifyInputForward();
		MeetingBO meeting = (MeetingBO)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		assertNull(meeting);
	}


	public void testSuccessfulCreateMonthlyMeetingOnDate()throws Exception{
		loadMeetingPage();
		String meetingPlace = "Delhi";
		Short dayNumber = Short.valueOf("5");
		Short recurAfter = Short.valueOf("1");
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("frequency", RecurrenceType.MONTHLY.getValue().toString());
		addRequestParameter("monthType", MeetingConstants.MONTHLY_ON_DATE);
		addRequestParameter("monthDay", dayNumber.toString());
		addRequestParameter("dayRecurMonth", recurAfter.toString());
		addRequestParameter("meetingPlace", meetingPlace);
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("input", "create");
		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadCreateCenter.toString());
		MeetingBO meeting = (MeetingBO)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		assertTrue(meeting.isMonthly());
		assertTrue(meeting.isMonthlyOnDate());
		assertEquals(meetingPlace, meeting.getMeetingPlace());
		assertEquals(recurAfter, meeting.getMeetingDetails().getRecurAfter());
		assertEquals(dayNumber,meeting.getMeetingDetails().getDayNumber());
	}

	public void testFailureCreateMonthlyMeetingOnWeekDay_AllNull()throws Exception{
		loadMeetingPage();
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("frequency", RecurrenceType.MONTHLY.getValue().toString());
		addRequestParameter("monthType", MeetingConstants.MONTHLY_ON_WEEK_DAY);
		addRequestParameter("monthWeek", "");
		addRequestParameter("monthRank", "");
		addRequestParameter("recurMonth", "");
		addRequestParameter("meetingPlace", "Delhi");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("input", "create");
		actionPerform();

		assertEquals("Month Recurrence On WeekDay", 1, getErrrorSize(MeetingConstants.ERRORS_SPECIFY_MONTHLY_MEETING_ON_WEEKDAY));
		verifyInputForward();
		MeetingBO meeting = (MeetingBO)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		assertNull(meeting);
	}

	public void testFailureCreateMonthlyMeetingOnWeekDay_RankANdRecurAfterNull()throws Exception{
		loadMeetingPage();
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("frequency", RecurrenceType.MONTHLY.getValue().toString());
		addRequestParameter("monthType", MeetingConstants.MONTHLY_ON_WEEK_DAY);
		addRequestParameter("monthWeek", WeekDay.MONDAY.getValue().toString());
		addRequestParameter("monthRank", "");
		addRequestParameter("recurMonth", "");
		addRequestParameter("meetingPlace", "Delhi");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("input", "create");
		actionPerform();

		assertEquals("Month Recurrence On WeekDay", 1, getErrrorSize(MeetingConstants.ERRORS_SPECIFY_MONTHLY_MEETING_ON_WEEKDAY));
		verifyInputForward();
		MeetingBO meeting = (MeetingBO)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		assertNull(meeting);
	}

	public void testFailureCreateMonthlyMeetingOnWeekDay_RecurAfterNull()throws Exception{
		loadMeetingPage();
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("frequency", RecurrenceType.MONTHLY.getValue().toString());
		addRequestParameter("monthType", MeetingConstants.MONTHLY_ON_WEEK_DAY);
		addRequestParameter("monthWeek", WeekDay.MONDAY.getValue().toString());
		addRequestParameter("monthRank", RankType.FOURTH.getValue().toString());
		addRequestParameter("recurMonth", "");
		addRequestParameter("meetingPlace", "");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("input", "create");
		actionPerform();

		assertEquals("Month Recurrence On WeekDay", 1, getErrrorSize(MeetingConstants.ERRORS_SPECIFY_MONTHLY_MEETING_ON_WEEKDAY));
		assertEquals("Month Recurrence On Date", 1, getErrrorSize(MeetingConstants.INVALID_MEETINGPLACE));
		verifyInputForward();
		MeetingBO meeting = (MeetingBO)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		assertNull(meeting);
	}

	public void testSuccessfulCreateMonthlyMeetingOnWeekDay()throws Exception{
		loadMeetingPage();
		String meetingPlace = "Delhi";
		Short recurAfter = Short.valueOf("1");
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("frequency", RecurrenceType.MONTHLY.getValue().toString());
		addRequestParameter("monthType", MeetingConstants.MONTHLY_ON_WEEK_DAY);
		addRequestParameter("monthWeek", WeekDay.MONDAY.getValue().toString());
		addRequestParameter("monthRank", RankType.FOURTH.getValue().toString());
		addRequestParameter("recurMonth", recurAfter.toString());
		addRequestParameter("meetingPlace", meetingPlace);
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("input", "create");
		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadCreateCenter.toString());
		MeetingBO meeting = (MeetingBO)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		assertTrue(meeting.isMonthly());
		assertFalse(meeting.isMonthlyOnDate());
		assertEquals(meetingPlace, meeting.getMeetingPlace());
		assertEquals(recurAfter, meeting.getMeetingDetails().getRecurAfter());
		assertEquals(WeekDay.MONDAY,meeting.getMeetingDetails().getWeekDay());
		assertEquals(RankType.FOURTH,meeting.getMeetingDetails().getWeekRank());
	}

	public void testSuccessfulCancelCreate()throws Exception{
		loadMeetingPage();
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "cancelCreate");
		actionPerform();
		verifyForward(ActionForwards.loadCreateCenter.toString());
		MeetingBO meeting = (MeetingBO)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		assertNull(meeting);
	}

	public void testSuccessfulCancelCreate_WithMeetingInSession()throws Exception{
		loadMeetingPage();
		MeetingBO meeting = createWeeklyMeeting(WeekDay.MONDAY, Short.valueOf("5"), new  Date());
		SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, meeting, request);
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "cancelCreate");
		actionPerform();
		verifyForward(ActionForwards.loadCreateCenter.toString());
		meeting = (MeetingBO)SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
		assertNotNull(meeting);
	}

	public void testEditForCenter()throws Exception{
		MeetingBO meeting = createWeeklyMeeting(WeekDay.WEDNESDAY, Short.valueOf("5"), new  Date());
		center = createCenter(meeting);
		HibernateUtil.closeSession();

		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalCustNum", center.getGlobalCustNum());
		actionPerform();

		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "edit");
		addRequestParameter("customerLevel", CustomerLevel.CENTER.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(ActionForwards.edit_success.toString());

		assertNotNull(SessionUtils.getAttribute(MeetingConstants.WEEKDAYSLIST,request));
		assertNotNull(SessionUtils.getAttribute(MeetingConstants.WEEKRANKLIST,request));
		verifyNoActionErrors();
		verifyNoActionMessages();
		MeetingActionForm actionForm = (MeetingActionForm)request.getSession().getAttribute("meetingActionForm");
		assertEquals(CustomerLevel.CENTER,actionForm.getCustomerLevelValue());
		center = TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
	}

	public void testSuccessfulUpdateMeetingForCenter()throws Exception{
		MeetingBO meeting = createWeeklyMeeting(WeekDay.WEDNESDAY, Short.valueOf("5"), new  Date());
		center = createCenter(meeting);
		group = TestObjectFactory.createGroupUnderCenter("myGroup", CustomerStatus.GROUP_ACTIVE, center);
		HibernateUtil.closeSession();
		String meetingPlace = "Delhi";

		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalCustNum", center.getGlobalCustNum());
		actionPerform();

		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "edit");
		addRequestParameter("customerLevel", CustomerLevel.CENTER.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();

		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("frequency", meeting.getMeetingDetails().getRecurrenceType().getRecurrenceId().toString());
		addRequestParameter("weekDay", WeekDay.MONDAY.getValue().toString());
		addRequestParameter("recurWeek", meeting.getMeetingDetails().getRecurAfter().toString());
		addRequestParameter("meetingPlace", meetingPlace);
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.center_detail_page.toString());
		HibernateUtil.closeSession();

		center = TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());

		MeetingBO updatedMeeting = center.getCustomerMeeting().getUpdatedMeeting();
		assertTrue(updatedMeeting.isWeekly());
		assertEquals(meetingPlace, updatedMeeting.getMeetingPlace());
		assertEquals(meeting.getMeetingDetails().getRecurAfter(), updatedMeeting.getMeetingDetails().getRecurAfter());
		assertEquals(WeekDay.MONDAY,updatedMeeting.getMeetingDetails().getWeekDay());

		updatedMeeting = group.getCustomerMeeting().getUpdatedMeeting();
		assertTrue(updatedMeeting.isWeekly());
		assertEquals(meetingPlace, updatedMeeting.getMeetingPlace());
		assertEquals(meeting.getMeetingDetails().getRecurAfter(), updatedMeeting.getMeetingDetails().getRecurAfter());
		assertEquals(WeekDay.MONDAY,updatedMeeting.getMeetingDetails().getWeekDay());
	}

	public void testSuccessfulCreateMeetingFromGroupDetail()throws Exception{
		group = createGroupUnderBranch(null);
		client1 = createClient("client1", group, CustomerStatus.CLIENT_PARTIAL);
		client2 = createClient("client2", group, CustomerStatus.CLIENT_PENDING);
		HibernateUtil.closeSession();
		assertNull(group.getCustomerMeeting());
		assertNull(client1.getCustomerMeeting());
		assertNull(client2.getCustomerMeeting());

		String meetingPlace = "Delhi";
		Short recurAfter = Short.valueOf("4");

		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalCustNum", group.getGlobalCustNum());
		actionPerform();

		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "edit");
		addRequestParameter("customerLevel", CustomerLevel.GROUP.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();

		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("frequency", RecurrenceType.WEEKLY.getValue().toString());
		addRequestParameter("weekDay", WeekDay.MONDAY.getValue().toString());
		addRequestParameter("recurWeek", recurAfter.toString());
		addRequestParameter("meetingPlace", meetingPlace);
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.group_detail_page.toString());
		HibernateUtil.closeSession();

		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		client1 = TestObjectFactory.getObject(ClientBO.class, client1.getCustomerId());
		client2 = TestObjectFactory.getObject(ClientBO.class, client2.getCustomerId());

		assertNotNull(group.getCustomerMeeting());
		assertNotNull(client1.getCustomerMeeting());
		assertNotNull(client2.getCustomerMeeting());

		assertTrue(group.getCustomerMeeting().getMeeting().isWeekly());
		assertTrue(client1.getCustomerMeeting().getMeeting().isWeekly());
		assertTrue(client2.getCustomerMeeting().getMeeting().isWeekly());

		assertEquals(WeekDay.MONDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.MONDAY, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.MONDAY, client2.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());

		assertEquals(meetingPlace, group.getCustomerMeeting().getMeeting().getMeetingPlace());
		assertEquals(meetingPlace, client1.getCustomerMeeting().getMeeting().getMeetingPlace());
		assertEquals(meetingPlace, client2.getCustomerMeeting().getMeeting().getMeetingPlace());

		assertEquals(recurAfter, group.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter());
		assertEquals(recurAfter, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter());
		assertEquals(recurAfter, client2.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter());
	}

	public void testSuccessfulUpdateMeetingFromGroupDetail()throws Exception{
		Short recurAfter = Short.valueOf("5");
		MeetingBO meeting = createWeeklyMeeting(WeekDay.WEDNESDAY, recurAfter, new  Date());
		group = createGroupUnderBranch(meeting);
		client1 = createClient("client1", group, CustomerStatus.CLIENT_PARTIAL);
		client2 = createClient("client2", group, CustomerStatus.CLIENT_PENDING);
		HibernateUtil.closeSession();

		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		client1 = TestObjectFactory.getObject(ClientBO.class, client1.getCustomerId());
		client2 = TestObjectFactory.getObject(ClientBO.class, client2.getCustomerId());

		assertNotNull(group.getCustomerMeeting());
		assertNotNull(client1.getCustomerMeeting());
		assertNotNull(client2.getCustomerMeeting());

		String meetingPlace = "Delhi";

		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalCustNum", group.getGlobalCustNum());
		actionPerform();

		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "edit");
		addRequestParameter("customerLevel", CustomerLevel.GROUP.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();

		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("frequency", RecurrenceType.WEEKLY.getValue().toString());
		addRequestParameter("weekDay", WeekDay.MONDAY.getValue().toString());
		addRequestParameter("recurWeek", recurAfter.toString());
		addRequestParameter("meetingPlace", meetingPlace);
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.group_detail_page.toString());
		HibernateUtil.closeSession();

		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		client1 = TestObjectFactory.getObject(ClientBO.class, client1.getCustomerId());
		client2 = TestObjectFactory.getObject(ClientBO.class, client2.getCustomerId());

		assertTrue(group.getCustomerMeeting().getMeeting().isWeekly());
		assertTrue(client1.getCustomerMeeting().getMeeting().isWeekly());
		assertTrue(client2.getCustomerMeeting().getMeeting().isWeekly());

		assertEquals(WeekDay.MONDAY, group.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.MONDAY, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.MONDAY, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());

		assertEquals(meetingPlace, group.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
		assertEquals(meetingPlace, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
		assertEquals(meetingPlace, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());

		assertEquals(recurAfter, group.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getRecurAfter());
		assertEquals(recurAfter, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getRecurAfter());
		assertEquals(recurAfter, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getRecurAfter());
	}

	public void testSuccessfulEditCancel()throws Exception{
		MeetingBO meeting = createWeeklyMeeting(WeekDay.WEDNESDAY, Short.valueOf("5"), new  Date());
		center = createCenter(meeting);
		HibernateUtil.closeSession();
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalCustNum", center.getGlobalCustNum());
		actionPerform();

		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "edit");
		addRequestParameter("meetingId", center.getCustomerMeeting().getMeeting().getMeetingId().toString());
		addRequestParameter("customerLevel", CustomerLevel.CENTER.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();

		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "cancelUpdate");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(ActionForwards.center_detail_page.toString());
		HibernateUtil.closeSession();
		center = TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		assertNotNull(center.getCustomerMeeting().getMeeting());
	}

	public void testSuccessfulCreateMeetingFromClientDetail()throws Exception{
		client1 = createClient(null);
		HibernateUtil.closeSession();
		
		assertNull(client1.getCustomerMeeting());
		
		String meetingPlace = "Delhi";
		Short recurAfter = Short.valueOf("4");
		
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalCustNum", client1.getGlobalCustNum());
		actionPerform();
		
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "edit");
		addRequestParameter("customerLevel", CustomerLevel.CLIENT.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("frequency", RecurrenceType.WEEKLY.getValue().toString());
		addRequestParameter("weekDay", WeekDay.MONDAY.getValue().toString());
		addRequestParameter("recurWeek", recurAfter.toString());
		addRequestParameter("meetingPlace", meetingPlace);
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.client_detail_page.toString());
		HibernateUtil.closeSession();
		
		client1 = TestObjectFactory.getObject(ClientBO.class, client1.getCustomerId());
		
		assertNotNull(client1.getCustomerMeeting());
		assertTrue(client1.getCustomerMeeting().getMeeting().isWeekly());
		assertEquals(WeekDay.MONDAY, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(meetingPlace, client1.getCustomerMeeting().getMeeting().getMeetingPlace());
		assertEquals(recurAfter, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter());
	}
	
	public void testSuccessfulUpdateMeetingForClient()throws Exception{
		MeetingBO meeting = createWeeklyMeeting(WeekDay.WEDNESDAY, Short.valueOf("5"), new  Date());
		client1 = createClient(meeting);
		HibernateUtil.closeSession();
		String meetingPlace = "Delhi";
	
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalCustNum", client1.getGlobalCustNum());
		actionPerform();
		
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "edit");
		addRequestParameter("meetingId", client1.getCustomerMeeting().getMeeting().getMeetingId().toString());
		addRequestParameter("customerLevel", CustomerLevel.CLIENT.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		
		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("frequency", meeting.getMeetingDetails().getRecurrenceType().getRecurrenceId().toString());
		addRequestParameter("weekDay", WeekDay.FRIDAY.getValue().toString());
		addRequestParameter("recurWeek", meeting.getMeetingDetails().getRecurAfter().toString());
		addRequestParameter("meetingPlace", meetingPlace);
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.client_detail_page.toString());
		HibernateUtil.closeSession();
		
		client1 = TestObjectFactory.getObject(ClientBO.class, client1.getCustomerId());
		
		MeetingBO updatedMeeting = client1.getCustomerMeeting().getUpdatedMeeting();
		assertTrue(updatedMeeting.isWeekly());
		assertEquals(meetingPlace, updatedMeeting.getMeetingPlace());
		assertEquals(meeting.getMeetingDetails().getRecurAfter(), updatedMeeting.getMeetingDetails().getRecurAfter());
		assertEquals(WeekDay.FRIDAY,updatedMeeting.getMeetingDetails().getWeekDay());
	}
	
	private CenterBO createCenter(MeetingBO meeting)throws Exception{
		return TestObjectFactory.createCenter("Center1", meeting, Short.valueOf("3"), Short.valueOf("1"));
	}

	private GroupBO createGroupUnderBranch(MeetingBO meeting)throws Exception{
		return TestObjectFactory.createGroupUnderBranch("group1", 
				CustomerStatus.GROUP_PENDING, Short.valueOf("1"), meeting, 
				Short.valueOf("1"));
	}

	private ClientBO createClient(MeetingBO meeting){
		return TestObjectFactory.createClient("myclient1", meeting, 
				CustomerStatus.CLIENT_PENDING);
	}

	private ClientBO createClient(String clientName, GroupBO group, 
			CustomerStatus clientStatus) {
		return TestObjectFactory.createClient(clientName, clientStatus,
				group, new Date());
	}

	private void loadMeetingPage(){
		setRequestPathInfo("/centerCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		actionPerform();

		setRequestPathInfo("/meetingAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerLevel", CustomerLevel.CENTER.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
	}

	private MeetingBO createWeeklyMeeting(WeekDay weekDay, Short recurAfer, Date startDate) throws MeetingException{
		return new MeetingBO(weekDay, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
	}

	private MeetingBO createMonthlyMeetingOnDate(Short dayNumber, Short recurAfer, Date startDate) throws MeetingException{
		return new MeetingBO(dayNumber, recurAfer, startDate, MeetingType.CUSTOMER_MEETING,"MeetingPlace");
	}

	private MeetingBO createMonthlyMeetingOnWeekDay(WeekDay weekDay, RankType rank, Short recurAfer, Date startDate) throws MeetingException{
		return new MeetingBO(weekDay, rank, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
	}
}
