/**
 *
 */
package org.mifos.application.personnel;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.application.productdefinition.util.valueobjects.ProductCategory;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.valueobjects.Context;

import servletunit.struts.MockStrutsTestCase;

/**
 * @author mohammedn
 *
 */
public class TestPersonnel extends MockStrutsTestCase {

	/**
	 *
	 */
	public TestPersonnel() {
		super();

	}

	/**
	 * @param testName
	 */
	public TestPersonnel(String testName) {
		super(testName);

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
		setRequestPathInfo("/PersonnelAction.do");
		addRequestParameter("method","chooseOffice");

		actionPerform();

		verifyForward("chooseOffice_success");
	}


	public void testSuccessfulLoad(){
		setRequestPathInfo("/PersonnelAction.do");
		addRequestParameter("method","load");
		Office office=retriveOfficeValueObject("AditiTech");
		addRequestParameter("office.officeId",String.valueOf(office.getOfficeId()));
		addRequestParameter("office.officeName",office.getOfficeName());
		actionPerform();

		assertEquals((((EntityMaster)request.getAttribute("titleList")).getLookUpMaster().size()),3);
		assertEquals((((EntityMaster)request.getAttribute("maritalStatusList")).getLookUpMaster().size()),2);

		verifyForward("load_success");
	}

	public void testSuccessfulPreview() {
		setRequestPathInfo("/PersonnelAction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","CreateUser");


		Office office=retriveOfficeValueObject("AditiTech");
		request.setAttribute("personnelOffice",office);


		addRequestParameter("personnelDetails.firstName","Aditi");
		addRequestParameter("personnelDetails.middleName","Technologies");
		addRequestParameter("personnelDetails.secondLastName","Pvt.");
		addRequestParameter("personnelDetails.lastName","Ltd.");
		addRequestParameter("office.officeId",String.valueOf(office.getOfficeId()));

		addRequestParameter("personnelDetails.governmentIdNumber","1234567890");
		addRequestParameter("emailId","aditi@aditi.com");
		addRequestParameter("dob","12/12/1967");
		addRequestParameter("personnelDetails.maritalStatus","66");
		addRequestParameter("personnelDetails.gender","49");
		addRequestParameter("preferredLocale.localeId","1");
		addRequestParameter("dateOfJoiningMFI","12/12/1994");
		addRequestParameter("personnelDetails.address1","218/10,Ramana");
		addRequestParameter("personnelDetails.address2","Sadashiva Nagar");
		addRequestParameter("personnelDetails.address3","Bellary Road");
		addRequestParameter("personnelDetails.city","Bangalore");
		addRequestParameter("personnelDetails.state","Karnataka");
		addRequestParameter("personnelDetails.country","India");
		addRequestParameter("personnelDetails.postalCode","560080");
		addRequestParameter("personnelDetails.telephone","560080");
		addRequestParameter("title","58");
		addRequestParameter("level.levelId","1");
		addRequestParameter("personnelRoles","2");
		addRequestParameter("userName","Aditi");

		addRequestParameter("password","Aditii");
		addRequestParameter("passwordRepeat","Aditii");

		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("PreviewCreateUser");
	}

	public void testSuccessfulCreate() {
		testSuccessfulLoad();
		testSuccessfulPreview();

		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		Personnel personnel=null;
		if(null != context) {
			personnel =(Personnel)context.getValueObject();
		}
		if(null != personnel) {
			setRequestPathInfo("/PersonnelAction.do");
			addRequestParameter("method","create");
		}


		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("create_success");

	}

	public void testSuccessfulGet() {
		Personnel person = retriveValueObject("Aditi");

		setRequestPathInfo("/PersonnelAction.do");
		addRequestParameter("method","get");
		addRequestParameter("globalPersonnelNum",person.getGlobalPersonnelNum());
		Office office=retriveOfficeValueObject("AditiTech");
		request.setAttribute("personnelOffice",office);

		actionPerform();

		Personnel personnel=(Personnel)request.getAttribute("PersonnelVO");

		assertEquals(personnel.getDisplayName(),"Aditi Technologies Pvt. Ltd.");
		assertEquals(personnel.getEmailId(),"aditi@aditi.com");
		assertEquals(personnel.getPersonnelDetails().getDob(),Date.valueOf("1967-12-12"));

		verifyForward("get_success");
	}

	public void testSuccessfulManage() {
		testSuccessfulGet();
		setRequestPathInfo("/PersonnelAction.do");
		addRequestParameter("method","manage");

		actionPerform();

		Personnel personnel=(Personnel)request.getAttribute("PersonnelVO");

		assertEquals(personnel.getDisplayName(),"Aditi Technologies Pvt. Ltd.");
		assertEquals(personnel.getEmailId(),"aditi@aditi.com");
		assertEquals(personnel.getPersonnelDetails().getDob(),Date.valueOf("1967-12-12"));
		assertEquals(personnel.getUserName(),"Aditi");

		verifyForward("manage_success");
	}

	public void testSuccessfulManagePreview() {
		testSuccessfulManage();


		setRequestPathInfo("/PersonnelAction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","ManageUser");

		addRequestParameter("personnelDetails.firstName","Aditi");
		addRequestParameter("personnelDetails.middleName","Technologies");
		addRequestParameter("personnelDetails.secondLastName","Private");
		addRequestParameter("personnelDetails.lastName","Limited");
		Office office=retriveOfficeValueObject("AditiTech");
		addRequestParameter("office.officeId",String.valueOf(office.getOfficeId()));


		addRequestParameter("emailId","aditi@aditi.com");
		addRequestParameter("dob","12/12/1967");
		addRequestParameter("personnelDetails.maritalStatus","66");
		addRequestParameter("personnelDetails.gender","49");
		addRequestParameter("preferredLocale.localeId","1");
		addRequestParameter("dateOfJoiningMFI","12/12/1994");
		addRequestParameter("personnelDetails.address1","218/10,Ramana");
		addRequestParameter("personnelDetails.address2","Sadashiva Nagar");
		addRequestParameter("personnelDetails.address3","Bellary Road");
		addRequestParameter("personnelDetails.city","Bangalore");
		addRequestParameter("personnelDetails.state","Karnataka");
		addRequestParameter("personnelDetails.country","India");
		addRequestParameter("personnelDetails.postalCode","560080");
		addRequestParameter("personnelDetails.telephone","560080");
		addRequestParameter("title","58");
		addRequestParameter("level.levelId","1");
		addRequestParameter("personnelRoles","2");

		addRequestParameter("userName","Aditi");
		addRequestParameter("personnelDetails.governmentIdNumber","1234567890");
		addRequestParameter("password","Aditii");
		addRequestParameter("passwordRepeat","Aditii");

		addRequestParameter("personnelStatus","1");


		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("PreviewManageUser");
	}

	public void testSuccessfulUpdate() {
		testSuccessfulManagePreview();

		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		Personnel personnel=null;
		if(null != context) {
			personnel =(Personnel)context.getValueObject();
		}
		if(null != personnel) {
			setRequestPathInfo("/PersonnelAction.do");
			addRequestParameter("method","update");
		}
		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("update_success");
	}

	private Personnel retriveValueObject(String userName)
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
