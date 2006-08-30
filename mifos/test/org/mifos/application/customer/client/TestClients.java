/**
 *
 */
package org.mifos.application.customer.client;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.group.util.valueobjects.Group;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;

import servletunit.struts.MockStrutsTestCase;

public class TestClients extends MockStrutsTestCase {

	public TestClients() {
		super();
	}

	public TestClients(String testName) {
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


	public void testSuccessfulLoad(){
		setRequestPathInfo("/clientCreationAction.do");
		addRequestParameter("method","load");
		Group group=retriveGroupValueObject("Group");
		addRequestParameter("parentGroupId",String.valueOf(group.getCustomerId()));
		addRequestParameter("isClientUnderGrp","1");
        actionPerform();
        verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("load_success");
	}

	public void testSuccessfulNext(){
		testSuccessfulLoad();

		setRequestPathInfo("/clientCreationAction.do");
		addRequestParameter("method","next");


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
		addRequestParameter("customerAddressDetail.zip","2334423");

		Office office=retriveOfficeValueObject("AditiTech");
		addRequestParameter("office.officeId",String.valueOf(office.getOfficeId()));

		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("next_success");
	}

	public void testSuccessfulPreview(){
		testSuccessfulNext();

		setRequestPathInfo("/clientCreationAction.do");
		addRequestParameter("method","preview");


		addRequestParameter("externalId","5345435");
		addRequestParameter("adminFee[0].rateOrAmount","999.0");
		addRequestParameter("adminFee[0].feeId","1");
		addRequestParameter("adminFee[0].feeName","Mainatanence fees");
		addRequestParameter("adminFee[0].feeFrequencyTypeId","1");
		addRequestParameter("selectedFee[0].feeId","2");
		addRequestParameter("selectedFee[0].rateOrAmount","599");

		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("prevMfiInfo_success");
	}

	public void testSuccessfulCreateSaveForLater(){
		testSuccessfulPreview();

		setRequestPathInfo("/clientCreationAction.do");
		addRequestParameter("method","create");
		addRequestParameter("statusId","1");

		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("create_success");
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
