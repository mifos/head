/**
 *
 */
package org.mifos.application.office;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;

import servletunit.struts.MockStrutsTestCase;

public class TestOffice extends MockStrutsTestCase{

	public TestOffice(String testName) {
		super(testName);
	}

	//success
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


       //success
	public void testSuccessfulLoad(){
		setRequestPathInfo("/OfficeAction.do");
		addRequestParameter("method","load");
        actionPerform();
		verifyForward("load_success");
		assertEquals(4,((ArrayList)request.getAttribute("OfficeLevelList")).size());
	}

	//success
	public void testSuccessfulOfficePreview() {

		setRequestPathInfo("/OfficeAction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","create");
		addRequestParameter("officeName", "AditiTech");
		addRequestParameter("shortName", "Software");
		addRequestParameter("formOfficeType", "5");
		addRequestParameter("formParentOffice", "1");
		addRequestParameter("address.address1", "A2");
		addRequestParameter("address.address2", "A2");
		addRequestParameter("address.address3", "A2");
		addRequestParameter("address.city", "C2");
		addRequestParameter("address.state", "S2");
		addRequestParameter("address.country", "C2");
		addRequestParameter("address.postalCode", "22");
		addRequestParameter("address.telephoneNo", "22");
		addRequestParameter("formOfficeStatus", "1");
		actionPerform();
		verifyForward("preview_success");

	}

	//success
	public void testSuccessfulOfficeCreation() {
		testSuccessfulLoad();
		testSuccessfulOfficePreview();
		setRequestPathInfo("/OfficeAction.do");
		addRequestParameter("method","create");
		addRequestParameter("input","create");
		addRequestParameter("officeId", "");
		addRequestParameter("formOfficeStatus", "1");

		actionPerform();

		verifyForward("create_success");
	}

	//success
	public void testSuccessfulOfficeGet() {
		Office office=retriveValueObject("AditiTech");
		setRequestPathInfo("/OfficeAction.do");
		addRequestParameter("method","get");
		addRequestParameter("input","create");
		addRequestParameter("officeId",String.valueOf(office.getOfficeId()));
		actionPerform();
		verifyForward("get_success");
		//assertEquals(1,((ArrayList)request.getAttribute("customFields")).size());
		//assertEquals(5,((ArrayList)request.getAttribute("OfficeLevelList")).size());

	}

//	success
	public void testSuccessfulOfficeEdit() {
		testSuccessfulOfficeGet();
		Office office=retriveValueObject("AditiTech");
		setRequestPathInfo("/OfficeAction.do");
		addRequestParameter("method","manage");
		addRequestParameter("input","manage");
		addRequestParameter("officeId", String.valueOf(office.getOfficeId()));
		addRequestParameter("formOfficeStatus", "1");
		addRequestParameter("address.officeAdressId", "1");
		addRequestParameter("version", String.valueOf(office.getVersionNo()));
		addRequestParameter("formOfficeType", "1");
		verifyNoActionErrors() ;
		verifyNoActionMessages();
		actionPerform();
		verifyForward("manage_success");

	}

//	success
	public void testSuccessfulOfficeEditPreview() {
		testSuccessfulOfficeEdit();
		Office office=retriveValueObject("AditiTech");
		setRequestPathInfo("/OfficeAction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","manageedit");
		addRequestParameter("officeId", String.valueOf(office.getOfficeId()));
		addRequestParameter("officeName", "AditiTech");
		addRequestParameter("shortName", "Pvt");
		addRequestParameter("formOfficeType", "5");
		addRequestParameter("formParentOffice", "1");
		addRequestParameter("address.address1", "A2");
		addRequestParameter("address.address2", "A2");
		addRequestParameter("address.address3", "A2");
		addRequestParameter("address.city", "C2");
		addRequestParameter("address.state", "S2");
		addRequestParameter("address.country", "C2");
		addRequestParameter("address.postalCode", "22");
		addRequestParameter("address.telephoneNo", "22");
		addRequestParameter("formOfficeStatus", "1");
		addRequestParameter("address.officeAdressId", String.valueOf(office.getAddress().getOfficeAdressId()));
		addRequestParameter("version", String.valueOf(office.getVersionNo()));
		verifyNoActionErrors() ;
		verifyNoActionMessages();
		actionPerform();
		verifyForward("managePreview_success");
	}

//	success
	public void testSuccessfulOfficeUpdate() {
		Office office=retriveValueObject("AditiTech");
		//testSuccessfulOfficeEdit();
		testSuccessfulOfficeEditPreview();
		setRequestPathInfo("/OfficeAction.do");
		addRequestParameter("method","update");
		addRequestParameter("input","manage");
		addRequestParameter("officeId", String.valueOf(office.getOfficeId()));
		actionPerform();
		verifyForward("update_success");
		//assertEquals(1,((ArrayList)request.getAttribute("customFields")).size());
		//assertEquals(5,((ArrayList)request.getAttribute("OfficeLevelList")).size());
	}

	private Office retriveValueObject(String officeName)
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
