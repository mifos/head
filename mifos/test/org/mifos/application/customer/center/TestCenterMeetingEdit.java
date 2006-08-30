package org.mifos.application.customer.center;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.customer.center.util.valueobjects.Center;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;

import servletunit.struts.MockStrutsTestCase;

public class TestCenterMeetingEdit extends MockStrutsTestCase {

	public TestCenterMeetingEdit() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TestCenterMeetingEdit(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("org/mifos/META-INF/web.xml").getPath());
			setConfigFile(ResourceLoader.getURI("org/mifos/META-INF/struts-config.xml").getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		UserContext userContext=new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		Set set= new HashSet();
		set.add(Short.valueOf("1"));
		userContext.setRoles(set);
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		userContext.setPereferedLocale(new Locale("en","US"));
		userContext.setBranchId(new Short("1"));
        request.getSession().setAttribute(Constants.USERCONTEXT,userContext);
        addRequestParameter("recordLoanOfficerId","1");
		addRequestParameter("recordOfficeId","1");
		ActivityContext ac = new ActivityContext((short)0,userContext.getBranchId().shortValue(),userContext.getId().shortValue());
		request.getSession(false).setAttribute(LoginConstants.ACTIVITYCONTEXT,ac);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public void testSuccessfulChooseOffice(){
		setRequestPathInfo("/centerAction.do");
		addRequestParameter("method","chooseOffice");
        actionPerform();
		verifyForward("chooseOffice_success");
	}
//	*****************************Get Success**************************
	public void testSuccessfulCenterGet(){
		setRequestPathInfo("/centerAction.do");
		addRequestParameter("method","get");

		Center center=retriveValueObject("center");
		addRequestParameter("globalCustNum",center.getGlobalCustNum()); // Please enter the center's globalcustmernumber that got created last
		actionPerform();
		verifyNoActionErrors() ;
		verifyNoActionMessages();
		verifyForward("get_success");
	}

//	***********************************Meeting updation starts*******************************
	public void testSuccessfulEditCenterMeetings(){
		testSuccessfulCenterGet();
		setRequestPathInfo("/MeetingAction.do");
		addRequestParameter("method","get");
		addRequestParameter("input","CenterDetails");
		Meeting meeting=retriveValueMeetingObject("Bangalore");
		addRequestParameter("meetingId",String.valueOf(meeting.getMeetingId()));
		actionPerform();
		//verifyNoActionErrors() ;
		//verifyNoActionMessages();
		//verifyForward("loadEditMeeting_success");
	}


	//success
	public void testSuccessfulEditCenterMeetingsLoad(){
		testSuccessfulEditCenterMeetings();
		setRequestPathInfo("/MeetingAction.do");
		addRequestParameter("method","load");
		addRequestParameter("input","CenterDetails");
		Meeting meeting=retriveValueMeetingObject("Bangalore");
		addRequestParameter("meetingId",String.valueOf(meeting.getMeetingId()));
		actionPerform();
		//verifyNoActionErrors() ;
		//verifyNoActionMessages();
		//verifyForward("loadEditMeeting_success");
	}


	//success
	public void testSuccessfulEditCenterMeetingsUpdate(){
		testSuccessfulEditCenterMeetingsLoad();
		setRequestPathInfo("/MeetingAction.do");
		addRequestParameter("method","update");
		Meeting meeting=retriveValueMeetingObject("Bangalore");
		addRequestParameter("meetingId",String.valueOf(meeting.getMeetingId()));
		addRequestParameter("versionNo",String.valueOf(meeting.getVersionNo()));
		addRequestParameter("frequency","1");
		addRequestParameter("recurWeek","3");
		addRequestParameter("weekDay","4");
		addRequestParameter("meetingPlace","Bangalore");
		actionPerform();
		verifyForward("editCenterMeeting_success");
	}

	private Center retriveValueObject(String customerName)
	{
		Session session = null;
		try
		{
		 session = HibernateUtil.getSession();

	    Query query = session.createQuery("from org.mifos.application.customer.util.valueobjects.Customer center where center.displayName = ? and center.customerLevel.levelId=3" );
		query.setString(0,customerName);
		Center center = (Center)query.uniqueResult();
		System.out.println("Center---------"+center);

		return center;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
			 HibernateUtil.closeSession(session);
			}
			catch(Exception e)
			{

			}
		}

	}


	private Meeting retriveValueMeetingObject(String meetingPlace)
	{
		Session session = null;
		try
		{
		 session = HibernateUtil.getSession();

	    Query query = session.createQuery("from org.mifos.application.meeting.util.valueobjects.Meeting m where m.meetingPlace = ?" );
		query.setString(0,meetingPlace);
		Meeting meeting = (Meeting)query.uniqueResult();

		return meeting;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
			 HibernateUtil.closeSession(session);
			}
			catch(Exception e)
			{

			}
		}

	}

}
