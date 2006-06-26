package org.mifos.application.customer.client;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.group.util.valueobjects.Group;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;

import servletunit.struts.MockStrutsTestCase;

public class TestClientEdit extends MockStrutsTestCase {

	public TestClientEdit() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TestClientEdit(String arg0) {
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

	public void testBranchOfficeExist(){
		setRequestPathInfo("/OfficeAction.do");
		addRequestParameter("method","load");
        actionPerform();
		verifyForward("load_success");
		assertEquals(4,((ArrayList)request.getAttribute("OfficeLevelList")).size());
	}

	//***************Get a client
	public void testSuccessfulGet(){
		setRequestPathInfo("/clientCreationAction.do");
		addRequestParameter("method","get");
		Client client=retriveClientValueObject("1234567");
		addRequestParameter("globalCustNum",client.getGlobalCustNum());


		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("get_success");
	}


	//******************Edit MFI info
	public void testSuccessfulEditMFIInfo(){
		testSuccessfulGet();
		setRequestPathInfo("/clientCreationAction.do");
		addRequestParameter("method","editMFIInfo");

		Context  context=(Context)SessionUtils.getAttribute(Constants.CONTEXT,request.getSession());
		Client client=(Client)context.getValueObject();

		addRequestParameter("customerId",client.getCustomerId().toString());
		addRequestParameter("globalCustNum",client.getGlobalCustNum());
		addRequestParameter("versionNo",client.getVersionNo().toString());
		addRequestParameter("statusId",client.getStatusId().toString());
		actionPerform();
		verifyForward("editMfiInfo_success");
	}

	public void testSuccessfulPreviewMFIInfo(){
		testSuccessfulEditMFIInfo();
		setRequestPathInfo("/clientCreationAction.do");
		addRequestParameter("input","editMfiInfo");
		addRequestParameter("method","preview");

		Context  context=(Context)SessionUtils.getAttribute(Constants.CONTEXT,request.getSession());
		Client client=(Client)context.getValueObject();

		Client  oldClient=(Client)SessionUtils.getAttribute(ClientConstants.OLDCLIENT,request.getSession());


		addRequestParameter("externalId",client.getExternalId());
		//assertEquals("1",client.getGroupFlag().toString());
		addRequestParameter("isClientUnderGrp","0");

		addRequestParameter("statusId",client.getStatusId().toString());
		addRequestParameter("trainedDate","12/12/1980");
		actionPerform();
		verifyForward("previewEditMfiInfo_success");
	}
	public void testSuccessfulUpdateMFIInfo(){
		testSuccessfulPreviewMFIInfo();
		setRequestPathInfo("/clientCreationAction.do");
		addRequestParameter("method","updateMfi");
		addRequestParameter("groupFlag","0");
		actionPerform();
		verifyForward("update_success");
	}

	//*************Update Personnel Info
	public void testSuccessfulEditPersonalInfo(){
		testSuccessfulGet();
		setRequestPathInfo("/clientCreationAction.do");
		addRequestParameter("method","editPersonalInfo");

		Context  context=(Context)SessionUtils.getAttribute(Constants.CONTEXT,request.getSession());
		Client client=(Client)context.getValueObject();

		addRequestParameter("customerId",client.getCustomerId().toString());
		addRequestParameter("globalCustNum",client.getGlobalCustNum());
		addRequestParameter("versionNo",client.getVersionNo().toString());
		addRequestParameter("statusId",client.getStatusId().toString());

		 addRequestParameter("input","editPersonalInfo");
		 addRequestParameter("isClientUnderGrp","0");
		 addRequestParameter("groupFlag","0");


		 addRequestParameter("customerAddressDetail.customerAddressId" ,client.getCustomerAddressDetail().getCustomerAddressId().toString());
		 addRequestParameter("customerDetail.customer.customerId",client.getCustomerDetail().getCustomerId().toString() );


		 addRequestParameter("office.officeId",String.valueOf(client.getOffice().getOfficeId()));
		 addRequestParameter("office.version",client.getOffice().getVersionNo().toString());
		 //addRequestParameter("globalCustNum","0002-000000003");
		 //addRequestParameter("versionNo","3");
		 //addRequestParameter("statusId","2");
		 addRequestParameter("trained",client.getTrained().toString());
		 //addRequestParameter("loanOfficerId","3");

		 addRequestParameter("trainedDate","");
		 addRequestParameter("externalId","dfasdsdf");

		 addRequestParameter("clientConfidential","0");
		 addRequestParameter("customerNameDetail[0].salutation","47");
		addRequestParameter("customerNameDetail[0].firstName","Client");
		addRequestParameter("customerNameDetail[0].middleName","Test");
		addRequestParameter("customerNameDetail[0].lastName","Test");
		addRequestParameter("customerNameDetail[0].nameType","3");
		addRequestParameter("governmentId","1234567");
		addRequestParameter("dateOfBirth","12/12/1967");
		addRequestParameter("customerDetail.gender","49");
		addRequestParameter("customerDetail.maritalStatus","66");

		addRequestParameter("customerDetail.numChildren","2");
		addRequestParameter("customerDetail.citizenship","130");
		addRequestParameter("customerDetail.ethinicity","132");
		addRequestParameter("customerDetail.handicapped","138");
		addRequestParameter("customerNameDetail[1].nameType","1");

		addRequestParameter("customerNameDetail[1].firstName","ClientFather");
		addRequestParameter("customerNameDetail[1].middleName","TestFather");
		addRequestParameter("customerNameDetail[1].lastName","TestFather");

		addRequestParameter("customerAddressDetail.line1","12/2-3");
		addRequestParameter("customerAddressDetail.line2","Sadashivanagar");
		addRequestParameter("customerAddressDetail.city","Bangalore");
		addRequestParameter("customerAddressDetail.state","Karnataka");
		addRequestParameter("customerAddressDetail.country","India");
		addRequestParameter("customerAddressDetail.zip","12");
		//addRequestParameter("office.officeId","2");

		actionPerform();
		verifyForward("editPersonalInfo_success");

	}

	public void testSuccessfulUpdatePersonalInfo(){
		testSuccessfulEditPersonalInfo();
		setRequestPathInfo("/clientCreationAction.do");
		addRequestParameter("method","update");
		addRequestParameter("editPersonalInfo","1");
		actionPerform();
		verifyForward("update_success");
	}



	private Client retriveClientValueObject(String governmentId)
	{
		Session session = null;
		try
		{
		 session = HibernateUtil.getSession();
		 //Query query = session.createQuery("from org.mifos.application.customer.util.valueobjects.Customer center where center.displayName = ? and center.customerLevel.levelId=3" );
	    Query query = session.createQuery("from org.mifos.application.customer.util.valueobjects.Customer c where c.governmentId = ? and c.customerLevel.levelId=1");
		query.setString(0,governmentId);
		Client group = (Client)query.uniqueResult();

		return group;
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

	private Group retriveGroupValueObject(String groupName)
	{
		Session session = null;
		try
		{
		 session = HibernateUtil.getSession();
		 //Query query = session.createQuery("from org.mifos.application.customer.util.valueobjects.Customer center where center.displayName = ? and center.customerLevel.levelId=3" );
	    Query query = session.createQuery("from org.mifos.application.customer.util.valueobjects.Customer c where c.displayName = ? and c.customerLevel.levelId=2");
		query.setString(0,groupName);
		Group group = (Group)query.uniqueResult();

		return group;
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



}
