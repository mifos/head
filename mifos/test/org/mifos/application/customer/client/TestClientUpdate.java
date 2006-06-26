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
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;

import servletunit.struts.MockStrutsTestCase;

public class TestClientUpdate extends MockStrutsTestCase {

	public TestClientUpdate() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TestClientUpdate(String arg0) {
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
	//*****************Update status
	public void testSuccessfulEditClientStatus(){
		testSuccessfulGet();
		setRequestPathInfo("/clientCreationAction.do");
		addRequestParameter("method","loadStatus");

		Context  context=(Context)SessionUtils.getAttribute(Constants.CONTEXT,request.getSession());
		Client client=(Client)context.getValueObject();

		addRequestParameter("customerId",client.getCustomerId().toString());
		addRequestParameter("globalCustNum",client.getGlobalCustNum());
		addRequestParameter("versionNo",client.getVersionNo().toString());
		addRequestParameter("statusId",client.getStatusId().toString());
		actionPerform();
		verifyForward("loadStatus_success");
	}


	public void testSuccessfulLoadStatusDetails(){
		testSuccessfulEditClientStatus();
		setRequestPathInfo("/clientStatusAction.do");
		addRequestParameter("method","load");
		actionPerform();
		verifyForward("load_success");

	}

	public void testSuccessfulPreviewEditClientStatus(){
		testSuccessfulLoadStatusDetails();
		setRequestPathInfo("/clientStatusAction.do");
		addRequestParameter("method","preview");

		Client client=(Client)SessionUtils.getAttribute(ClientConstants.OLDCLIENT,request.getSession());
		//Client client=(Client)context.getValueObject();

		addRequestParameter("customerId",client.getCustomerId().toString());
		addRequestParameter("currentStatusId",client.getStatusId().toString());
		addRequestParameter("displayName",client.getDisplayName());
		addRequestParameter("statusId","2");
		addRequestParameter("customerNote.comment","comments");
		addRequestParameter("versionNo",client.getVersionNo().toString());
		actionPerform();
		verifyForward("preview_success");
	}

	public void testSuccessfullUpdateStatus(){
		testSuccessfulPreviewEditClientStatus();
		setRequestPathInfo("/clientStatusAction.do");
		addRequestParameter("method","update");
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


}
