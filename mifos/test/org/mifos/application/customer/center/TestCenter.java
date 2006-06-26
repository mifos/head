package org.mifos.application.customer.center;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.customer.center.util.valueobjects.Center;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;

import servletunit.struts.MockStrutsTestCase;

public class TestCenter extends MockStrutsTestCase{

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


	public TestCenter(String testName){
		super(testName);
	}
	//*****************************Center creation starts*******************************
	public void testSuccessfulChooseOffice(){
		setRequestPathInfo("/centerAction.do");
		addRequestParameter("method","chooseOffice");
        actionPerform();
		verifyForward("chooseOffice_success");
	}

	//success
	public void testSuccessfulLoadCenter(){
		testSuccessfulChooseOffice();
		setRequestPathInfo("/centerAction.do");
		addRequestParameter("method","load");
		addRequestParameter("input","chooseOffice");

		Office office=retriveOfficeValueObject("AditiTech");
		addRequestParameter("office.officeId",String.valueOf(office.getOfficeId())); // Need to pass the respective office id
		addRequestParameter("office.officeName",office.getOfficeName()); // Need to pass the respective office name
        actionPerform();
		verifyForward("load_success");
	}

	//success
	public void testSuccessfulLoadMeeting(){
		testSuccessfulLoadCenter();
		setRequestPathInfo("/centerAction.do");
		addRequestParameter("method","loadMeeting");
		addRequestParameter("input","Center");
		actionPerform();
		verifyForward("loadMeeting_success" );
	}

	//success
	public void testSuccessfulSaveMeeting(){
		testSuccessfulLoadMeeting();
		setRequestPathInfo("/MeetingAction.do");
		addRequestParameter("method","create");
       	addRequestParameter("frequency","1");
		addRequestParameter("recurWeek","2");
		addRequestParameter("weekDay","1");
		addRequestParameter("meetingPlace","Bangalore");
		actionPerform();
		verifyForward("center_success");
	}


	//success
	public void testSuccessfulCreateMeeting(){
		testSuccessfulSaveMeeting();
		setRequestPathInfo("/centerAction.do");
		addRequestParameter("method","previous");
       	addRequestParameter("input","CreateMeeting");
		actionPerform();
	}

	//success
	public void testSuccessfulCenterPreview(){
		testSuccessfulCreateMeeting();
		setRequestPathInfo("/centerAction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","create");
		addRequestParameter("displayName","center"); // Change the name as per the requirement

		Personnel personnel=retriveValueLoanOfficerObject("Aditi");
		addRequestParameter("loanOfficerId",String.valueOf(personnel.getPersonnelId())); // Pass the correct loan officer's id
		actionPerform();
		verifyNoActionErrors() ;
		verifyNoActionMessages();
	    verifyForward("previewCreate_success");
	}

    //success
	public void testSuccessfulCenterCreate(){
		testSuccessfulCenterPreview();
		setRequestPathInfo("/centerAction.do");
		addRequestParameter("method","create");
		addRequestParameter("input","create");
		actionPerform();
		verifyNoActionErrors() ;
		verifyNoActionMessages();
		verifyForward("create_success");
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

	private Office retriveOfficeValueObject(String officeName)
	{
		Session session = null;
		try
		{
		 session = HibernateUtil.getSession();

	    Query query = session.createQuery("from org.mifos.application.office.util.valueobjects.Office office where office.officeName = ?" );
		query.setString(0,officeName);
		Office office = (Office)query.uniqueResult();

		return office;
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

	private Personnel retriveValueLoanOfficerObject(String userName)
	{
		Session session = null;
		try
		{
		 session = HibernateUtil.getSession();

	    Query query = session.createQuery("from org.mifos.application.personnel.util.valueobjects.Personnel personnel where personnel.userName = ?" );
		query.setString(0,userName);
		Personnel personnel = (Personnel)query.uniqueResult();

		return personnel;
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
