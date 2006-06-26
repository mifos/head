package org.mifos.application.customer.group;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.customer.center.util.valueobjects.Center;
import org.mifos.application.customer.group.util.valueobjects.Group;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;

import servletunit.struts.MockStrutsTestCase;

public class TestGroupEdit extends MockStrutsTestCase{

	public TestGroupEdit() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TestGroupEdit(String name) {
		super(name);
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

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	public void testSuccessfulHierarchyCheck(){
		setRequestPathInfo("/GroupAction.do");
		addRequestParameter("method","hierarchyCheck");
		addRequestParameter("recordOfficeId","0");
		addRequestParameter("recordLoanOfficerId","0");
        actionPerform();

	}

	public void testSuccessfulGet(){
		setRequestPathInfo("/GroupAction.do");
		addRequestParameter("method","get");
		Group group=retriveGroupValueObject("Group");
		addRequestParameter("globalCustNum",String.valueOf(group.getGlobalCustNum()));
		actionPerform();
		verifyForward("get_success");
	}
	//************************Edit Group
	public void testSuccessfulEditGroup(){
		testSuccessfulGet();
		setRequestPathInfo("/GroupAction.do");
		addRequestParameter("method","manage");
		actionPerform();
		verifyForward("manage_success");
	}


	public void testSuccessfulEditGroupPreview(){
		testSuccessfulEditGroup();
		setRequestPathInfo("/GroupAction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","ManageGroup");

		addRequestParameter("externalId","ABC");


		Context  context=(Context)SessionUtils.getAttribute(Constants.CONTEXT,request.getSession());
		Group group=(Group)context.getValueObject();

		addRequestParameter("displayName",group.getDisplayName());
		addRequestParameter("customerAddressDetail.customerAddressId",group.getCustomerAddressDetail().getCustomerAddressId().toString());
		addRequestParameter("versionNo",group.getVersionNo().toString());
		addRequestParameter("statusId",group.getStatusId().toString());
		addRequestParameter("office.officeId",group.getOffice().getOfficeId().toString());
		addRequestParameter("office.officeName",group.getOffice().getOfficeName());
		addRequestParameter("office.globalOfficeNum",group.getOffice().getGlobalOfficeNum());
		addRequestParameter("office.version",group.getOffice().getVersionNo().toString());
		actionPerform();
		verifyForward("PreviewManageGroup");
	}


	public void testSuccessfulEditGroupUpdate(){
		testSuccessfulEditGroupPreview();
		setRequestPathInfo("/GroupAction.do");
		addRequestParameter("method","update");
		addRequestParameter("input","PreviewManageGroup");
		actionPerform();
		verifyForward("update_success");
	}

	//****************************Edit Status
	public void testSuccessfullLoadEditStatus(){
		testSuccessfulGet();
		setRequestPathInfo("/GroupAction.do");
		addRequestParameter("method","loadStatus");
		actionPerform();
		verifyForward("loadStatus");
	}

	public void testSuccessfullLoadEditStatusPreview(){
		testSuccessfullLoadEditStatus();
		setRequestPathInfo("/GroupAction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","ChangeGroupStatus");

		addRequestParameter("customerNote.comment","Status Change");
		addRequestParameter("statusId","8");

		Context  context=(Context)SessionUtils.getAttribute(Constants.CONTEXT,request.getSession());
		Group group=(Group)context.getValueObject();

		addRequestParameter("versionNo",group.getVersionNo().toString());
		addRequestParameter("globalCustNum",group.getGlobalCustNum() );
		addRequestParameter("customerId",group.getCustomerId().toString());
		addRequestParameter("displayName",group.getDisplayName());



		actionPerform();
		verifyForward("PreviewChangeGroupStatus");
	}


	public void testSuccessfullUpdateStatus(){
		testSuccessfullLoadEditStatusPreview();
		setRequestPathInfo("/GroupAction.do");
		addRequestParameter("method","updateStatus");
		addRequestParameter("input","PreviewChangeGroupStatus");
		actionPerform();
		verifyForward("updateStatus_success");
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


	private Center retriveCenterValueObject(String centerName)
	{
		Session session = null;
		try
		{
		 session = HibernateUtil.getSession();

	    Query query = session.createQuery("from org.mifos.application.customer.util.valueobjects.Customer center where center.displayName = ? and center.customerLevel.levelId=3" );
		query.setString(0,centerName);
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
}
