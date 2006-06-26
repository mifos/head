/**
 *
 */
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

/**
 * @author mohammedn
 *
 */
public class TestCenterEdit extends MockStrutsTestCase {

	/**
	 *
	 */
	public TestCenterEdit() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public TestCenterEdit(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

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

	//*********************Center details updation starts*******************************
	public void testSuccessfulEditCenterInfo(){

		testSuccessfulCenterGet();
		setRequestPathInfo("/centerAction.do");
		addRequestParameter("method","manage");
		actionPerform();
		verifyNoActionErrors() ;
		verifyNoActionMessages();
		verifyForward("manage_success");
	}

	//success
	public void testSuccessfulEditCenterInfoPreview(){
		testSuccessfulEditCenterInfo();
		setRequestPathInfo("/centerAction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","manage");
		addRequestParameter("customerAddressDetail.line1","Ramana Maharshi Road");

		Personnel personnel=retriveValueLoanOfficerObject("Aditi");
		addRequestParameter("loanOfficerId",String.valueOf(personnel.getPersonnelId()));
		Office office=retriveOfficeValueObject("AditiTech");
		addRequestParameter("office.officeId",String.valueOf(office.getOfficeId())); // Need to pass the respective office id
		addRequestParameter("office.officeName",office.getOfficeName());
		addRequestParameter("office.version",String.valueOf(office.getVersionNo()));
		Center center=retriveValueObject("center");
		addRequestParameter("globalCustNum",center.getGlobalCustNum());
		addRequestParameter("versionNo",String.valueOf(center.getVersionNo()));
		addRequestParameter("customerId",String.valueOf(center.getCustomerId()));
		addRequestParameter("customerAddressDetail.customerAddressId" ,String.valueOf(center.getCustomerAddressDetail().getCustomerAddressId()));
		addRequestParameter("displayName","center");
		addRequestParameter("statusId","13");
		actionPerform();
		verifyNoActionErrors() ;
		verifyNoActionMessages();
		verifyForward("previewManage_success");
	}


	//success
	public void testSuccessfulEditCenterInfoUpdate(){
		testSuccessfulEditCenterInfoPreview();
		setRequestPathInfo("/centerAction.do");
		addRequestParameter("method","update");
		addRequestParameter("input","manage");
		actionPerform();
		verifyNoActionErrors() ;
		verifyNoActionMessages();
		verifyForward("update_success");
	}

	//************************Center Status updation starts*********************************
	public void testSuccessfulEditCenterStatus(){
		testSuccessfulCenterGet();
		setRequestPathInfo("/centerAction.do");
		addRequestParameter("method","loadStatus");
		actionPerform();
		verifyNoActionErrors() ;
		verifyNoActionMessages();
		verifyForward("loadStatus_success");
	}


	//success
	public void testSuccessfulEditCenterStatusPreview(){
		testSuccessfulEditCenterStatus();
		setRequestPathInfo("/centerAction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","status");

		Center center=retriveValueObject("center");
		addRequestParameter("globalCustNum",center.getGlobalCustNum());
		addRequestParameter("customerId",String.valueOf(center.getCustomerId()));////Change the customerid according to customer table
		addRequestParameter("versionNo",String.valueOf(center.getVersionNo()));//Change the globalcust number according to  customer table
		Office office=retriveOfficeValueObject("AditiTech");
		addRequestParameter("office.version",String.valueOf(office.getVersionNo()));
		addRequestParameter("displayName","center");//change the certer name as with what it was created
		addRequestParameter("customerMeeting.meetingPlace","ssdf");
		addRequestParameter("office.officeId",String.valueOf(office.getOfficeId())); // Need to pass the respective office id
		addRequestParameter("office.officeName",office.getOfficeName()); // pass the correct office id according to personnel's table
		addRequestParameter("statusId","13"); // pass the status id (you can change this)
		addRequestParameter("customerNote.comment" ,"Status is active"); // pass the description and you can see these in customer_note table
		actionPerform();
		verifyNoActionErrors() ;
		verifyNoActionMessages();
		verifyForward("previewStatus_success");
	}

	//success
	public void testSuccessfulEditCenterStatusStatus(){
		testSuccessfulEditCenterStatusPreview();
		setRequestPathInfo("/centerAction.do");
		addRequestParameter("method","updateStatus");
		addRequestParameter("input","status");
		actionPerform();
		verifyNoActionErrors() ;
		verifyNoActionMessages();
		verifyForward("updateStatus_success");
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
