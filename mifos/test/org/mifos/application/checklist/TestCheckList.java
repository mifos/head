package org.mifos.application.checklist;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.checklist.util.valueobjects.CheckList;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;

import servletunit.struts.MockStrutsTestCase;

public class TestCheckList extends MockStrutsTestCase{

	public TestCheckList(String testName){
		super(testName);
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


	//success
	public void testSuccessfulLoad(){
		setRequestPathInfo("/checkListAction.do");
		addRequestParameter("method","load");
        actionPerform();
		verifyForward("load_success");
	}

	//success
	public void testSuccessfulLoadCheckListType(){
		testSuccessfulLoad();
		setRequestPathInfo("/checkListAction.do");
		addRequestParameter("method","loadParent");
		addRequestParameter("typeId", "0");
		addRequestParameter("categoryId", "3");
		addRequestParameter("input","create");
		addRequestParameter("typeName", "");
		addRequestParameter("displayedStatus", "");
		addRequestParameter("checklistName", "dummyList");
		addRequestParameter("type", "0");
		addRequestParameter("typeId", "0");
		addRequestParameter("categoryId", "3");
		addRequestParameter("status", "14");
		addRequestParameter("text", "");
		addRequestParameter("value(0)","Item1");
		addRequestParameter("value(1)","Item2");
		actionPerform();
	}

	//success
	public void testSuccessfulPreview(){
		testSuccessfulLoadCheckListType();
		setRequestPathInfo("/checkListAction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","create");
		actionPerform();
		verifyForward("preview_success");
	}

	//success
	public void testSuccessfulCreate(){
		testSuccessfulPreview();
		setRequestPathInfo("/checkListAction.do");
		addRequestParameter("method","create");
		actionPerform();
		verifyForward("create_success");
	}

	//success
	public void testSuccessfulViewAllCheckList(){
		testSuccessfulLoad();
		setRequestPathInfo("/checkListAction.do");
		addRequestParameter("method","loadall");
		actionPerform();
		verifyForward("forward_view");
	}

	//success
	public void testSuccessfulGetCheckList(){
		Short checklistId=retriveValueObject("dummyList");
		testSuccessfulLoad();
		setRequestPathInfo("/checkListAction.do");
		addRequestParameter("method","get");
		addRequestParameter("checklistId" ,String.valueOf(checklistId));
		addRequestParameter("statusOfCheckList" ,"active");
		addRequestParameter("typeId" ,"0");
		addRequestParameter("categoryId" ,"3");
		actionPerform();
		verifyForward("get_success");
	}

	//success
	public void testSuccessfulCheckListEdit(){
		testSuccessfulGetCheckList();
		setRequestPathInfo("/checkListAction.do");
		addRequestParameter("method","manage");
		addRequestParameter("input","manage");
		actionPerform();
		verifyForward("manage_success");
	}

	//success
	public void testSuccessfulCheckListEditPreview(){
		testSuccessfulCheckListEdit();
		setRequestPathInfo("/checkListAction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","manage");
		addRequestParameter("checklistName","dummyList1");
		addRequestParameter("typeId", "0");
		addRequestParameter("categoryId", "3");
		addRequestParameter("typeName", "");
		addRequestParameter("displayedStatus", "");
		addRequestParameter("type", "0");
		addRequestParameter("typeId", "0");
		addRequestParameter("categoryId", "3");
		addRequestParameter("status", "12");
		addRequestParameter("text", "");
		addRequestParameter("value(0)","Item1");
		addRequestParameter("value(1)","Item2");
		addRequestParameter("previousStatusId","13");
		addRequestParameter("checklistStatus","0");
		actionPerform();
		verifyForward("manage_preview");
	}


	//success
	public void testSuccessfulCheckListEditUpdate(){
		testSuccessfulCheckListEditPreview();
		setRequestPathInfo("/checkListAction.do");
		addRequestParameter("method","update");
		actionPerform();
		verifyForward("update_success");
	}

	private Short retriveValueObject(String checklistName)
	{
		Session session = null;
		try
		{
		 session = HibernateUtil.getSession();

	    Query query = session.createQuery("select max(checkList.checklistId) from org.mifos.application.checklist.util.valueobjects.CheckList checkList where checkList.checklistName = ?" );
		query.setString(0,checklistName);
		Short  checklistId= (Short)query.uniqueResult();

		return checklistId;
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
